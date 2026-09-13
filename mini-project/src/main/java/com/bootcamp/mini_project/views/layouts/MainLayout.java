package com.bootcamp.mini_project.views.layouts;

import com.bootcamp.mini_project.views.pages.*;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.*;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.page.*;
import com.vaadin.flow.router.RouterLink;

/**
 * Root App Layout for the Inventory POS System.
 * Integrates Tailwind/Shadcn UI styling tokens, top navigation bar,
 * and Server Push support for real-time Kafka metrics updates.
 */
public class MainLayout extends AppLayout {
    public MainLayout() {
        Span brandText = new Span("Mini POS");
        brandText.addClassNames("text-lg", "font-bold", "tracking-tight", "text-foreground");

        Div logoBadge = new Div(brandText);
        logoBadge.addClassNames("flex", "items-center", "gap-2", "mr-6");

        HorizontalLayout navLinks = new HorizontalLayout();
        navLinks.addClassNames("gap-1", "items-center");

        navLinks.add(
                navigation("Dashboard", Dashboard.class),
                navigation("Products", Products.class),
                navigation("Categories", Categories.class),
                navigation("Suppliers", Suppliers.class)
        );

        Button logoutBtn = new Button("Logout", _ -> UI.getCurrent().navigate("login"));
        logoutBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        logoutBtn.addClassNames("px-3", "py-1.5", "text-sm", "font-medium", "text-muted-foreground", "hover:text-foreground", "hover:bg-accent", "rounded-md", "transition-colors");

        HorizontalLayout rightContainer = new HorizontalLayout(logoutBtn);
        rightContainer.addClassNames("ml-auto", "items-center");

        HorizontalLayout headerBar = new HorizontalLayout(logoBadge, navLinks, rightContainer);
        headerBar.addClassNames("w-full", "px-6", "py-3", "bg-card", "border-b", "border-border", "items-center", "justify-between");

        addToNavbar(headerBar);
    }

    private RouterLink navigation(String text, Class<? extends Component> navigationTarget) {
        RouterLink link = new RouterLink(text, navigationTarget);
        link.addClassNames("px-3", "py-2", "text-sm", "font-medium", "rounded-md", "text-muted-foreground", "hover:text-foreground", "hover:bg-accent", "transition-colors", "no-underline");
        return link;
    }
}