package com.bootcamp.mini_project.views.pages;

import com.bootcamp.mini_project.dto.reports.*;
import com.bootcamp.mini_project.services.ReportService;
import com.bootcamp.mini_project.views.layouts.MainLayout;
import com.github.sebhoss.warnings.CompilerWarnings;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.*;
import com.vaadin.flow.component.grid.*;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinSession;
import java.math.BigDecimal;
import java.util.Objects;
import org.springframework.data.domain.PageRequest;

/**
 * Main Analytics Dashboard combining real-time metrics, quick action CTAs,
 * sales summary reports, and stock audit logs.
 */
@PageTitle("Dashboard | Mini POS")
@Route(value = "dashboard", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@SuppressWarnings(CompilerWarnings.UNUSED)
public class Dashboard extends VerticalLayout implements BeforeEnterObserver {
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String token = (String) VaadinSession.getCurrent().getAttribute("jwt_token");

        if (token == null || token.isBlank()) {
            event.forwardTo("login");
        }
    }

    private final ReportService reportService;
    private final Grid<SalesReportResponse> salesGrid = new Grid<>(SalesReportResponse.class, false);
    private final Grid<StockLogResponse> stockLogGrid = new Grid<>(StockLogResponse.class, false);
    private final Span revenueMetric = new Span("Rp 0");
    private final Span transactionsMetric = new Span("0");

    public Dashboard(ReportService reportService) {
        this.reportService = reportService;

        setPadding(false);
        setSpacing(false);
        addClassNames("p-5", "max-w-7xl", "mx-auto", "w-full", "gap-4", "box-border", "overflow-x-hidden");

        HorizontalLayout topBar = createTopBar();
        Div metricsGrid = createMetricsSection();

        tables();

        H3 salesTitle = new H3("Recent Sales Summary");
        salesTitle.addClassNames("m-0", "text-xs", "font-semibold", "text-foreground", "pt-1");

        H3 logsTitle = new H3("Stock Audit Activity Log");
        logsTitle.addClassNames("m-0", "text-xs", "font-semibold", "text-foreground", "pt-1");

        add(topBar, metricsGrid, salesTitle, salesGrid, logsTitle, stockLogGrid);
        refreshDashboardData();
    }

    private HorizontalLayout createTopBar() {
        VerticalLayout headerText = getHeaderText();

        Button addProductBtn = new Button("Manage Products", _ -> UI.getCurrent().navigate("products"));
        addProductBtn.addClassNames("border", "border-border", "bg-card", "hover:bg-accent", "font-medium", "px-3", "py-1.5", "rounded-md", "text-xs");

        Button newTransactionBtn = new Button("+ New Transaction", _ -> UI.getCurrent().navigate("products"));
        newTransactionBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        newTransactionBtn.addClassNames("bg-primary", "text-primary-foreground", "hover:bg-primary/90", "font-medium", "px-3", "py-1.5", "rounded-md", "text-xs");

        HorizontalLayout ctaBar = new HorizontalLayout(addProductBtn, newTransactionBtn);
        ctaBar.addClassNames("items-center", "gap-2", "shrink-0");

        HorizontalLayout topBar = new HorizontalLayout(headerText, ctaBar);
        topBar.addClassNames("w-full", "justify-between", "items-center", "gap-4");
        return topBar;
    }

    private VerticalLayout getHeaderText() {
        H2 title = new H2("Dashboard");
        title.addClassNames("m-0", "text-lg", "font-bold", "tracking-tight", "text-foreground");

        Paragraph description = new Paragraph("Overview of daily sales performance, stock activity, and quick operations.");
        description.addClassNames("m-0", "text-xs", "text-muted-foreground");

        VerticalLayout headerText = new VerticalLayout(title, description);
        headerText.setPadding(false);
        headerText.setSpacing(false);
        headerText.addClassNames("gap-0.5");
        return headerText;
    }

    private Div createMetricsSection() {
        Div container = new Div();
        container.addClassNames("grid", "grid-cols-1", "md:grid-cols-3", "gap-3", "w-full");

        container.add(
                createCard("Total Revenue", revenueMetric, "Real-time accumulated revenue"),
                createCard("Transactions Completed", transactionsMetric, "Processed sales count"),
                createCard("System Status", new Span("Kafka Online"), "Real-time push connection active")
        );

        return container;
    }

    private Div createCard(String title, Span valueSpan, String subtext) {
        Span cardTitle = new Span(title);
        cardTitle.addClassNames("text-[11px]", "font-medium", "text-muted-foreground");

        valueSpan.addClassNames("text-base", "font-bold", "tracking-tight", "text-foreground");

        Span cardSub = new Span(subtext);
        cardSub.addClassNames("text-[10px]", "text-muted-foreground");

        Div card = new Div(cardTitle, valueSpan, cardSub);
        card.addClassNames("flex", "flex-col", "gap-0.5", "p-4", "bg-card", "border", "border-border", "rounded-md", "shadow-sm");
        return card;
    }

    private void tables() {
        salesGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        salesGrid.addClassNames("border", "border-border", "rounded-md", "bg-card", "text-card-foreground", "w-full", "text-xs");

        salesGrid.addColumn(SalesReportResponse::getTransactionId).setHeader("Transaction ID").setAutoWidth(true).setTextAlign(ColumnTextAlign.CENTER);
        salesGrid.addColumn(report -> report.getTotalAmount() != null ? "Rp " + report.getTotalAmount() : "Rp 0").setHeader("Total Revenue").setAutoWidth(true).setTextAlign(ColumnTextAlign.CENTER);
        salesGrid.addColumn(SalesReportResponse::getTransactionDate).setHeader("Date").setAutoWidth(true).setTextAlign(ColumnTextAlign.CENTER);

        stockLogGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        stockLogGrid.addClassNames("border", "border-border", "rounded-md", "bg-card", "text-card-foreground", "w-full", "text-xs");

        stockLogGrid.addColumn(StockLogResponse::getProductName).setHeader("Product").setAutoWidth(true).setTextAlign(ColumnTextAlign.CENTER);
        stockLogGrid.addColumn(StockLogResponse::getQuantity).setHeader("Quantity").setAutoWidth(true).setTextAlign(ColumnTextAlign.CENTER);
        stockLogGrid.addColumn(StockLogResponse::getChangeType).setHeader("Change Type").setAutoWidth(true).setTextAlign(ColumnTextAlign.CENTER);
        stockLogGrid.addColumn(StockLogResponse::getRemark).setHeader("Remark").setAutoWidth(true).setTextAlign(ColumnTextAlign.CENTER);
        stockLogGrid.addColumn(StockLogResponse::getCreatedAt).setHeader("Recorded At").setAutoWidth(true).setTextAlign(ColumnTextAlign.CENTER);
    }

    public void refreshDashboardData() {
        var salesPage = reportService.getSalesReport(PageRequest.of(0, 10));
        var stockLogPage = reportService.getStockLogReport(PageRequest.of(0, 10));

        BigDecimal totalRevenue = salesPage.getContent().stream()
                .map(SalesReportResponse::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        salesGrid.setItems(salesPage.getContent());
        stockLogGrid.setItems(stockLogPage.getContent());
        transactionsMetric.setText(String.valueOf(salesPage.getTotalElements()));
        revenueMetric.setText("Rp " + totalRevenue);
    }
}