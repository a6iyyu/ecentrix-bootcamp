package com.bootcamp.mini_project.views.pages;

import com.bootcamp.mini_project.dto.reports.*;
import com.bootcamp.mini_project.services.ReportService;
import com.bootcamp.mini_project.views.layouts.MainLayout;
import com.github.sebhoss.warnings.CompilerWarnings;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.*;
import com.vaadin.flow.component.grid.Grid;
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

        addClassNames("p-8", "max-w-7xl", "mx-auto", "w-full", "gap-6");

        HorizontalLayout topBar = createTopBar();
        Div metricsGrid = createMetricsSection();

        tables();

        H3 salesTitle = new H3("Recent Sales Summary");
        salesTitle.addClassNames("text-lg", "font-medium", "text-foreground", "mt-4");

        H3 logsTitle = new H3("Stock Audit Activity Log");
        logsTitle.addClassNames("text-lg", "font-medium", "text-foreground", "mt-4");

        add(topBar, metricsGrid, salesTitle, salesGrid, logsTitle, stockLogGrid);
        refreshDashboardData();
    }

    private HorizontalLayout createTopBar() {
        H2 title = new H2("Dashboard");
        title.addClassNames("text-2xl", "font-semibold", "tracking-tight", "text-foreground");

        Paragraph description = new Paragraph("Overview of daily sales performance, stock activity, and quick operations.");
        description.addClassNames("text-sm", "text-muted-foreground");

        VerticalLayout headerText = new VerticalLayout(title, description);
        headerText.setPadding(false);
        headerText.setSpacing(false);

        Button newTransactionBtn = new Button("+ New Transaction", _ -> UI.getCurrent().navigate("products"));
        newTransactionBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        newTransactionBtn.addClassNames("bg-primary", "text-primary-foreground", "hover:bg-primary/90", "font-medium", "px-4", "py-2", "rounded-md");

        return getTopBar(newTransactionBtn, headerText);
    }

    private HorizontalLayout getTopBar(Button newTransactionBtn, VerticalLayout headerText) {
        Button addProductBtn = new Button("Manage Products", _ -> UI.getCurrent().navigate("products"));
        addProductBtn.addClassNames("border", "border-border", "bg-card", "hover:bg-accent", "font-medium", "px-4", "py-2", "rounded-md");

        HorizontalLayout ctaBar = new HorizontalLayout(addProductBtn, newTransactionBtn);
        ctaBar.addClassNames("items-center", "gap-3");

        HorizontalLayout topBar = new HorizontalLayout(headerText, ctaBar);
        topBar.addClassNames("w-full", "justify-between", "items-center");
        return topBar;
    }

    private Div createMetricsSection() {
        Div container = new Div();
        container.addClassNames("grid", "grid-cols-1", "md:grid-cols-3", "gap-4", "w-full");

        container.add(
                createCard("Total Revenue", revenueMetric, "Real-time accumulated revenue"),
                createCard("Transactions Completed", transactionsMetric, "Processed sales count"),
                createCard("System Status", new Span("Kafka Online"), "Real-time push connection active")
        );

        return container;
    }

    private Div createCard(String title, Span valueSpan, String subtext) {
        Span cardTitle = new Span(title);
        cardTitle.addClassNames("text-sm", "font-medium", "text-muted-foreground");

        valueSpan.addClassNames("text-2xl", "font-bold", "tracking-tight", "text-foreground");

        Span cardSub = new Span(subtext);
        cardSub.addClassNames("text-xs", "text-muted-foreground");

        Div card = new Div(cardTitle, valueSpan, cardSub);
        card.addClassNames("flex", "flex-col", "gap-1", "p-5", "bg-card", "border", "border-border", "rounded-lg", "shadow-sm");
        return card;
    }

    private void tables() {
        salesGrid.addClassNames("border", "border-border", "rounded-lg", "bg-card", "text-card-foreground");
        salesGrid.addColumn(SalesReportResponse::getTransactionId).setHeader("Transaction ID").setAutoWidth(true);
        salesGrid.addColumn(report -> report.getTotalAmount() != null ? "Rp " + report.getTotalAmount() : "Rp 0").setHeader("Total Revenue").setAutoWidth(true);
        salesGrid.addColumn(SalesReportResponse::getTransactionDate).setHeader("Date").setAutoWidth(true);

        stockLogGrid.addClassNames("border", "border-border", "rounded-lg", "bg-card", "text-card-foreground");
        stockLogGrid.addColumn(StockLogResponse::getProductName).setHeader("Product").setAutoWidth(true);
        stockLogGrid.addColumn(StockLogResponse::getQuantity).setHeader("Quantity").setAutoWidth(true);
        stockLogGrid.addColumn(StockLogResponse::getChangeType).setHeader("Change Type").setAutoWidth(true);
        stockLogGrid.addColumn(StockLogResponse::getRemark).setHeader("Remark").setAutoWidth(true);
        stockLogGrid.addColumn(StockLogResponse::getCreatedAt).setHeader("Recorded At").setAutoWidth(true);
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