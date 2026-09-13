package com.bootcamp.mini_project.views.pages;

import com.bootcamp.mini_project.dto.suppliers.*;
import com.bootcamp.mini_project.services.SupplierService;
import com.bootcamp.mini_project.views.layouts.MainLayout;
import com.vaadin.flow.component.button.*;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
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

        addClassNames("p-8", "max-w-7xl", "mx-auto", "w-full", "gap-6");

        H2 title = new H2("Suppliers");
        title.addClassNames("text-2xl", "font-semibold", "tracking-tight", "text-foreground");

        Paragraph description = new Paragraph("Manage your product vendors, contact details, and locations.");
        description.addClassNames("text-sm", "text-muted-foreground");

        VerticalLayout headerText = new VerticalLayout(title, description);
        headerText.setPadding(false);
        headerText.setSpacing(false);

        Button addButton = new Button("+ Add Supplier", _ -> modalAction(null));
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.addClassNames("bg-primary", "text-primary-foreground", "hover:bg-primary/90", "font-medium", "px-4", "py-2", "rounded-md", "transition-colors");

        HorizontalLayout topBar = new HorizontalLayout(headerText, addButton);
        topBar.addClassNames("w-full", "justify-between", "items-center");

        table();

        add(topBar, grid);
        refreshGridData();
    }

    private void table() {
        grid.addClassNames("border", "border-border", "rounded-lg", "bg-card", "text-card-foreground");

        grid.addColumn(SupplierResponse::getId).setHeader("ID").setAutoWidth(true);
        grid.addColumn(SupplierResponse::getName).setHeader("Supplier Name").setAutoWidth(true);
        grid.addColumn(SupplierResponse::getEmail).setHeader("Email").setAutoWidth(true);
        grid.addColumn(SupplierResponse::getPhoneNumber).setHeader("Phone Number").setAutoWidth(true);
        grid.addColumn(SupplierResponse::getAddress).setHeader("Address").setAutoWidth(true);

        grid.addComponentColumn(supplier -> {
            Button editButton = new Button("Edit", _ -> modalAction(supplier));
            editButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            editButton.addClassNames("text-sm", "font-medium", "text-foreground", "hover:underline");

            Button deleteButton = new Button("Delete", _ -> modalDelete(supplier));
            deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
            deleteButton.addClassNames("text-sm", "font-medium", "text-destructive", "hover:underline");

            HorizontalLayout actions = new HorizontalLayout(editButton, deleteButton);
            actions.addClassNames("gap-2");
            return actions;
        }).setHeader("Actions").setAutoWidth(true);
    }

    private void refreshGridData() {
        grid.setItems(supplierService.getAllSuppliers(PageRequest.of(0, 100)).getContent());
    }

    private void modalAction(SupplierResponse existingSupplier) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(existingSupplier == null ? "Create New Supplier" : "Edit Supplier");

        TextField nameField = new TextField("Supplier Name");
        nameField.setRequired(true);
        nameField.addClassNames("w-full");

        TextField emailField = new TextField("Email Address");
        emailField.setRequired(true);
        emailField.addClassNames("w-full");

        TextField phoneField = new TextField("Phone Number");
        phoneField.addClassNames("w-full");

        TextArea addressField = new TextArea("Address");
        addressField.addClassNames("w-full");

        if (existingSupplier != null) {
            nameField.setValue(existingSupplier.getName() != null ? existingSupplier.getName() : "");
            emailField.setValue(existingSupplier.getEmail() != null ? existingSupplier.getEmail() : "");
            phoneField.setValue(existingSupplier.getPhoneNumber() != null ? existingSupplier.getPhoneNumber() : "");
            addressField.setValue(existingSupplier.getAddress() != null ? existingSupplier.getAddress() : "");
        }

        VerticalLayout formLayout = new VerticalLayout(nameField, emailField, phoneField, addressField);
        formLayout.addClassNames("py-2", "w-96");
        dialog.add(formLayout);

        Button cancelButton = new Button("Cancel", _ -> dialog.close());
        cancelButton.addClassNames("text-muted-foreground");

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
        saveButton.addClassNames("bg-primary", "text-primary-foreground");

        dialog.getFooter().add(cancelButton, saveButton);
        dialog.open();
    }

    private void modalDelete(SupplierResponse supplier) {
        Dialog confirmDialog = new Dialog();
        confirmDialog.setHeaderTitle("Delete Supplier");

        Paragraph text = new Paragraph("Are you sure you want to delete supplier '" + supplier.getName() + "'?");
        confirmDialog.add(text);

        Button cancelButton = new Button("Cancel", _ -> confirmDialog.close());

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
        deleteButton.addClassNames("bg-destructive", "text-destructive-foreground");

        confirmDialog.getFooter().add(cancelButton, deleteButton);
        confirmDialog.open();
    }
}