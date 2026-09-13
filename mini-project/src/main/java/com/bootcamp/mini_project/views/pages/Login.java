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
 * Standalone Login View page designed with a centered Shadcn card UI layout and ambient glow background.
 */
@PageTitle("Login | Mini POS")
@Route("login")
@SuppressWarnings(CompilerWarnings.UNUSED)
public class Login extends VerticalLayout {
    private final AuthService authService;

    public Login(AuthService authService) {
        this.authService = authService;

        addClassNames("relative", "overflow-hidden", "min-h-screen", "w-full", "flex", "items-center", "justify-center", "bg-background", "p-4");
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        Span glowTopLeft = new Span();
        glowTopLeft.addClassNames("pointer-events-none", "absolute", "-top-32", "-left-32", "size-96", "rounded-full", "bg-indigo-500/10", "blur-3xl", "dark:bg-indigo-500/15");

        Span glowBottomRight = new Span();
        glowBottomRight.addClassNames("pointer-events-none", "absolute", "-right-32", "-bottom-32", "size-96", "rounded-full", "bg-purple-500/10", "blur-3xl", "dark:bg-purple-500/15");

        Div card = new Div();
        card.addClassNames("relative", "z-10", "w-full", "max-w-md", "bg-card", "border", "border-border", "rounded-xl", "p-8", "shadow-sm", "space-y-6");

        VerticalLayout headerLayout = cardHeaderSection();

        TextField emailField = new TextField("Email Address");
        emailField.setRequired(true);
        emailField.setPlaceholder("admin@mail.com");
        emailField.addClassNames("w-full");

        PasswordField passwordField = new PasswordField("Password");
        passwordField.setRequired(true);
        passwordField.setPlaceholder("••••••••");
        passwordField.addClassNames("w-full");

        Button loginBtn = new Button("Sign In", _ -> processLogin(emailField.getValue(), passwordField.getValue()));
        loginBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        loginBtn.addClassNames("w-full", "bg-primary", "text-primary-foreground", "hover:bg-primary/90", "font-medium", "py-2.5", "rounded-md", "transition-colors", "mt-2");

        VerticalLayout formLayout = new VerticalLayout(emailField, passwordField, loginBtn);
        formLayout.setPadding(false);
        formLayout.addClassNames("gap-4", "w-full");

        card.add(headerLayout, formLayout);
        add(glowTopLeft, glowBottomRight, card);
    }

    private VerticalLayout cardHeaderSection() {
        H2 title = new H2("Welcome back");
        title.addClassNames("text-2xl", "font-semibold", "tracking-tight", "text-foreground");

        Paragraph subtitle = new Paragraph("Enter your credentials to sign in to your POS account.");
        subtitle.addClassNames("text-sm", "text-muted-foreground");

        VerticalLayout headerLayout = new VerticalLayout(title, subtitle);
        headerLayout.setPadding(false);
        headerLayout.setSpacing(false);
        headerLayout.addClassNames("gap-1");
        return headerLayout;
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