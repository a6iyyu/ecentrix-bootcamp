package com.bootcamp.mini_project.views.pages;

import com.bootcamp.mini_project.dto.auth.*;
import com.bootcamp.mini_project.services.AuthService;
import com.github.sebhoss.warnings.CompilerWarnings;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.*;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.*;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.*;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinSession;

/**
 * Standalone Login View page with compact card width, precise grid-centered footer,
 * consistent rounded-md borders, and ambient glow background.
 */
@PageTitle("Login | Mini POS")
@Route("login")
@SuppressWarnings(CompilerWarnings.UNUSED)
public class Login extends VerticalLayout {
    private final AuthService authService;

    public Login(AuthService authService) {
        this.authService = authService;

        addClassNames("relative", "flex", "min-h-screen", "w-full", "items-center", "justify-center", "overflow-hidden", "bg-background", "p-4", "md:p-8");

        setPadding(false);
        setSpacing(false);
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        Span glowTopLeft = new Span();
        glowTopLeft.addClassNames("pointer-events-none", "absolute", "-top-32", "-left-32", "size-80", "md:size-[420px]", "xl:size-[520px]", "rounded-full", "bg-indigo-500/20", "blur-[120px]", "dark:bg-indigo-500/25");

        Span glowBottomRight = new Span();
        glowBottomRight.addClassNames("pointer-events-none", "absolute", "-right-32", "-bottom-32", "size-80", "md:size-[420px]", "xl:size-[520px]", "rounded-full", "bg-purple-500/20", "blur-[120px]", "dark:bg-purple-500/25");

        Div content = new Div();
        content.addClassNames("relative", "z-10", "w-full", "max-w-sm", "md:max-w-[380px]", "space-y-6");

        Div header = createHeader();

        Div card = new Div();
        card.addClassNames("w-full", "rounded-md", "border", "border-border", "bg-card/80", "p-6", "md:p-8", "shadow-sm", "backdrop-blur-md");

        Div cardHeader = createCardHeader();

        TextField emailField = new TextField("Email Address");
        emailField.setRequired(true);
        emailField.setPlaceholder("admin@mail.com");
        emailField.addClassNames("w-full", "text-xs");

        PasswordField passwordField = new PasswordField("Password");
        passwordField.setRequired(true);
        passwordField.setPlaceholder("••••••••");
        passwordField.addClassNames("w-full", "text-xs");

        Button loginButton = new Button("Sign In", _ -> processLogin(emailField.getValue(), passwordField.getValue()));
        loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        loginButton.addClassNames("h-10", "w-full", "rounded-md", "bg-primary", "px-4", "py-3", "text-xs", "font-semibold", "text-primary-foreground", "shadow-sm", "transition-colors", "hover:bg-primary/90", "focus-visible:outline-none", "focus-visible:ring-2", "focus-visible:ring-ring", "focus-visible:ring-offset-2");

        VerticalLayout formLayout = new VerticalLayout(emailField, passwordField, loginButton);
        formLayout.setPadding(false);
        formLayout.setSpacing(false);
        formLayout.addClassNames("w-full", "gap-4");

        Div footer = new Div();
        footer.addClassNames("w-full", "grid", "place-items-center", "pt-2");

        Paragraph footerText = new Paragraph("© 2026 Rafi Abiyyu Airlangga. All rights reserved.");
        footerText.addClassNames("m-0", "text-center", "text-[11px]", "md:text-xs", "text-muted-foreground");

        footer.add(footerText);
        card.add(cardHeader, formLayout);
        content.add(header, card, footer);
        add(glowTopLeft, glowBottomRight, content);
    }

    private Div createCardHeader() {
        Div cardHeader = new Div();
        cardHeader.addClassNames("flex", "flex-col", "gap-1", "pb-6");

        H2 title = new H2("Welcome back");
        title.addClassNames("m-0", "text-xl", "md:text-2xl", "font-bold", "tracking-tight", "text-foreground");

        Paragraph subtitle = new Paragraph("Enter your credentials to sign in to your POS account.");
        subtitle.addClassNames("m-0", "text-xs", "md:text-sm", "leading-relaxed", "text-muted-foreground");
        cardHeader.add(title, subtitle);
        return cardHeader;
    }

    private Div createHeader() {
        Div header = new Div();
        header.addClassNames("flex", "flex-col", "items-center", "justify-center", "text-center", "gap-1", "pb-2");

        H1 brandTitle = new H1("Mini POS");
        brandTitle.addClassNames("m-0", "text-2xl", "md:text-3xl", "font-bold", "tracking-tight", "text-foreground");

        Paragraph brandSubtitle = new Paragraph("Manage your point of sale with ease.");
        brandSubtitle.addClassNames("m-0", "text-xs", "md:text-sm", "leading-relaxed", "text-muted-foreground");

        header.add(brandTitle, brandSubtitle);
        return header;
    }

    private void processLogin(String email, String password) {
        if (email.isBlank() || password.isBlank()) {
            Notification.show("Please fill in both email and password", 3000, Notification.Position.BOTTOM_END).addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        try {
            LoginRequest request = LoginRequest.builder().email(email).password(password).build();
            AuthResponse response = authService.login(request);

            if (response != null && response.getAccessToken() != null) {
                VaadinSession.getCurrent().setAttribute("jwt_token", response.getAccessToken());
            }

            Notification.show("Login successful!", 2000, Notification.Position.BOTTOM_END).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            UI.getCurrent().navigate("dashboard");
        } catch (Exception ex) {
            String errorMsg = ex.getMessage() != null ? ex.getMessage() : "Invalid credentials";
            Notification.show(errorMsg, 3000, Notification.Position.BOTTOM_END).addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}