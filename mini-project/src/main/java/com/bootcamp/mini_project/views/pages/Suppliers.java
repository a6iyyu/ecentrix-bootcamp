package com.bootcamp.mini_project.views.pages;

import com.bootcamp.mini_project.dto.suppliers.*;
import com.bootcamp.mini_project.services.SupplierService;
import com.bootcamp.mini_project.views.layouts.MainLayout;
import com.vaadin.flow.component.button.*;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.*;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.*;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.component.textfield.*;
import com.vaadin.flow.router.*;
import org.springframework.data.domain.PageRequest;

/**
 * View page for managing product suppliers with Shadcn-inspired UI components.
 */
@PageTitle("Suppliers Management | Mini POS")
@Route(value = "suppliers", layout = MainLayout.class)
public class Suppliers extends VerticalLayout {
    private final SupplierService supplierService;
    private final Grid<SupplierResponse> grid = new Grid<>(SupplierResponse.class, false);

    public Suppliers(SupplierService supplierService) {
        this.supplierService = supplierService;

        setPadding(false);
        setSpacing(false);
        addClassNames("p-5", "max-w-7xl", "mx-auto", "w-full", "gap-4", "box-border", "overflow-x-hidden");

        VerticalLayout headerText = getHeaderText();

        Button addButton = new Button("+ Add Supplier", _ -> modalAction(null));
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.addClassNames("bg-primary", "text-primary-foreground", "hover:bg-primary/90", "font-medium", "px-3", "py-1.5", "rounded-md", "text-xs", "shrink-0");

        HorizontalLayout topBar = new HorizontalLayout(headerText, addButton);
        topBar.addClassNames("w-full", "justify-between", "items-center", "gap-4");

        table();

        add(topBar, grid);
        refreshGridData();
    }

    private VerticalLayout getHeaderText() {
        HorizontalLayout breadcrumb = createBreadcrumb();

        H2 title = new H2("Suppliers");
        title.addClassNames("m-0", "text-lg", "font-bold", "tracking-tight", "text-foreground");

        Paragraph description = new Paragraph("Manage your product vendors, contact details, and locations.");
        description.addClassNames("m-0", "text-xs", "text-muted-foreground");

        VerticalLayout headerText = new VerticalLayout(breadcrumb, title, description);
        headerText.setPadding(false);
        headerText.setSpacing(false);
        headerText.addClassNames("gap-0.5");
        return headerText;
    }

    private HorizontalLayout createBreadcrumb() {
        HorizontalLayout breadcrumb = new HorizontalLayout();
        breadcrumb.addClassNames("items-center", "gap-1.5", "text-[11px]", "text-muted-foreground");

        RouterLink dashLink = new RouterLink("Dashboard", Dashboard.class);
        dashLink.addClassNames("text-muted-foreground", "hover:text-foreground", "no-underline");

        Span sep = new Span("/");
        sep.addClassNames("text-muted-foreground/60");

        Span current = new Span("Suppliers");
        current.addClassNames("font-medium", "text-foreground");

        breadcrumb.add(dashLink, sep, current);
        return breadcrumb;
    }

    private void table() {
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.addClassNames("border", "border-border", "rounded-md", "bg-card", "text-card-foreground", "w-full", "text-xs");
        grid.addColumn(SupplierResponse::getId).setHeader("ID").setAutoWidth(true).setTextAlign(ColumnTextAlign.CENTER);
        grid.addColumn(SupplierResponse::getName).setHeader("Supplier Name").setAutoWidth(true).setTextAlign(ColumnTextAlign.CENTER);
        grid.addColumn(SupplierResponse::getEmail).setHeader("Email").setAutoWidth(true).setTextAlign(ColumnTextAlign.CENTER);
        grid.addColumn(SupplierResponse::getPhoneNumber).setHeader("Phone Number").setAutoWidth(true).setTextAlign(ColumnTextAlign.CENTER);
        grid.addColumn(SupplierResponse::getAddress).setHeader("Address").setAutoWidth(true).setTextAlign(ColumnTextAlign.CENTER);

        grid.addComponentColumn(supplier -> {
            Button editButton = new Button("Edit", _ -> modalAction(supplier));
            editButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            editButton.addClassNames("text-xs", "font-medium", "text-foreground", "hover:underline");

            Button deleteButton = new Button("Delete", _ -> modalDelete(supplier));
            deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
            deleteButton.addClassNames("text-xs", "font-medium", "text-destructive", "hover:underline");

            HorizontalLayout actions = new HorizontalLayout(editButton, deleteButton);
            actions.addClassNames("gap-2", "justify-center", "items-center", "w-full");
            return actions;
        }).setHeader("Actions").setAutoWidth(true).setTextAlign(ColumnTextAlign.CENTER);
    }

    private void refreshGridData() {
        grid.setItems(supplierService.getAllSuppliers(PageRequest.of(0, 100)).getContent());
    }

    private void modalAction(SupplierResponse existingSupplier) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(existingSupplier == null ? "Create New Supplier" : "Edit Supplier");
        dialog.setWidth("520px");

        TextField nameField = new TextField("Supplier Name");
        nameField.setRequired(true);
        nameField.addClassNames("w-full", "text-xs");

        TextField emailField = new TextField("Email Address");
        emailField.setRequired(true);
        emailField.addClassNames("w-full", "text-xs");

        TextField phoneField = new TextField("Phone Number");
        phoneField.addClassNames("w-full", "text-xs");

        HorizontalLayout contactRow = new HorizontalLayout(emailField, phoneField);
        contactRow.addClassNames("w-full", "gap-3");

        TextArea addressField = new TextArea("Address");
        addressField.addClassNames("w-full", "text-xs");

        if (existingSupplier != null) {
            nameField.setValue(existingSupplier.getName() != null ? existingSupplier.getName() : "");
            emailField.setValue(existingSupplier.getEmail() != null ? existingSupplier.getEmail() : "");
            phoneField.setValue(existingSupplier.getPhoneNumber() != null ? existingSupplier.getPhoneNumber() : "");
            addressField.setValue(existingSupplier.getAddress() != null ? existingSupplier.getAddress() : "");
        }

        VerticalLayout formLayout = new VerticalLayout(nameField, contactRow, addressField);
        formLayout.setPadding(false);
        formLayout.setSpacing(false);
        formLayout.addClassNames("w-full", "gap-3", "py-2");
        dialog.add(formLayout);

        Button cancelButton = new Button("Cancel", _ -> dialog.close());
        cancelButton.addClassNames("text-xs", "text-muted-foreground");

        Button saveButton = new Button("Save", _ -> {
            if (nameField.isEmpty() || emailField.isEmpty()) {
                Notification.show("Supplier name and email are required", 3000, Notification.Position.BOTTOM_END).addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            SupplierRequest request = SupplierRequest.builder()
                    .name(nameField.getValue())
                    .email(emailField.getValue())
                    .phoneNumber(phoneField.getValue())
                    .address(addressField.getValue())
                    .build();

            try {
                if (existingSupplier == null) {
                    supplierService.createSupplier(request);
                    Notification.show("Supplier created successfully", 3000, Notification.Position.BOTTOM_END).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                } else {
                    supplierService.updateSupplier(existingSupplier.getId(), request);
                    Notification.show("Supplier updated successfully", 3000, Notification.Position.BOTTOM_END).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                }

                refreshGridData();
                dialog.close();
            } catch (Exception ex) {
                String errorMsg = ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred";
                Notification.show(errorMsg, 3000, Notification.Position.BOTTOM_END).addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClassNames("bg-primary", "text-primary-foreground", "text-xs");

        dialog.getFooter().add(cancelButton, saveButton);
        dialog.open();
    }

    private void modalDelete(SupplierResponse supplier) {
        Dialog confirmDialog = new Dialog();
        confirmDialog.setHeaderTitle("Delete Supplier");

        Paragraph text = new Paragraph("Are you sure you want to delete supplier '" + supplier.getName() + "'?");
        text.addClassNames("text-xs");
        confirmDialog.add(text);

        Button cancelButton = new Button("Cancel", _ -> confirmDialog.close());
        cancelButton.addClassNames("text-xs");

        Button deleteButton = new Button("Delete", _ -> {
            try {
                supplierService.deleteSupplier(supplier.getId());
                Notification.show("Supplier deleted", 3000, Notification.Position.BOTTOM_END).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                refreshGridData();
                confirmDialog.close();
            } catch (Exception ex) {
                String errorMsg = ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred";
                Notification.show(errorMsg, 3000, Notification.Position.BOTTOM_END).addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        deleteButton.addClassNames("bg-destructive", "text-destructive-foreground", "text-xs");

        confirmDialog.getFooter().add(cancelButton, deleteButton);
        confirmDialog.open();
    }
}