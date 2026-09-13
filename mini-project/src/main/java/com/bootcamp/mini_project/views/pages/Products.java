package com.bootcamp.mini_project.views.pages;

import com.bootcamp.mini_project.dto.categories.CategoryResponse;
import com.bootcamp.mini_project.dto.products.*;
import com.bootcamp.mini_project.dto.suppliers.SupplierResponse;
import com.bootcamp.mini_project.services.*;
import com.bootcamp.mini_project.views.layouts.MainLayout;
import com.vaadin.flow.component.button.*;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.*;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.component.textfield.*;
import com.vaadin.flow.router.*;
import java.math.BigDecimal;
import java.util.*;
import org.springframework.data.domain.PageRequest;

/**
 * View page for managing inventory products with relations to categories and suppliers.
 */
@PageTitle("Products Management | Mini POS")
@Route(value = "products", layout = MainLayout.class)
public class Products extends VerticalLayout {
    private final CategoryService categoryService;
    private final ProductService productService;
    private final SupplierService supplierService;

    private final Grid<ProductResponse> grid = new Grid<>(ProductResponse.class, false);

    public Products(ProductService productService, CategoryService categoryService, SupplierService supplierService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.supplierService = supplierService;

        addClassNames("p-8", "max-w-7xl", "mx-auto", "w-full", "gap-6");

        H2 title = new H2("Products");
        title.addClassNames("text-2xl", "font-semibold", "tracking-tight", "text-foreground");

        Paragraph description = new Paragraph("Manage inventory items, pricing, stock levels, and vendor mappings.");
        description.addClassNames("text-sm", "text-muted-foreground");

        VerticalLayout headerText = new VerticalLayout(title, description);
        headerText.setPadding(false);
        headerText.setSpacing(false);

        Button addButton = new Button("+ Add Product", _ -> modalAction(null));
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

        grid.addColumn(ProductResponse::getId).setHeader("ID").setAutoWidth(true);
        grid.addColumn(ProductResponse::getName).setHeader("Product Name").setAutoWidth(true);
        grid.addColumn(product -> product.getPrice() != null ? "Rp " + product.getPrice() : "-").setHeader("Price").setAutoWidth(true);
        grid.addColumn(ProductResponse::getStock).setHeader("Stock").setAutoWidth(true);
        grid.addColumn(product -> product.getCategoryName() != null ? product.getCategoryName() : "-").setHeader("Category").setAutoWidth(true);
        grid.addColumn(product -> product.getSupplierName() != null ? product.getSupplierName() : "-").setHeader("Supplier").setAutoWidth(true);

        grid.addComponentColumn(product -> {
            Button editButton = new Button("Edit", _ -> modalAction(product));
            editButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            editButton.addClassNames("text-sm", "font-medium", "text-foreground", "hover:underline");

            Button deleteButton = new Button("Delete", _ -> modalDelete(product));
            deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
            deleteButton.addClassNames("text-sm", "font-medium", "text-destructive", "hover:underline");

            HorizontalLayout actions = new HorizontalLayout(editButton, deleteButton);
            actions.addClassNames("gap-2");
            return actions;
        }).setHeader("Actions").setAutoWidth(true);
    }

    private void refreshGridData() {
        grid.setItems(productService.getAllProducts(PageRequest.of(0, 100)).getContent());
    }

    private void modalAction(ProductResponse existingProduct) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(existingProduct == null ? "Create New Product" : "Edit Product");

        TextField nameField = new TextField("Product Name");
        nameField.setRequired(true);
        nameField.addClassNames("w-full");

        BigDecimalField priceField = new BigDecimalField("Price (IDR)");
        priceField.setRequired(true);
        priceField.addClassNames("w-full");

        IntegerField stockField = new IntegerField("Stock Quantity");
        stockField.setRequired(true);
        stockField.setMin(0);
        stockField.addClassNames("w-full");

        List<CategoryResponse> categories = categoryService.getAllCategories(PageRequest.of(0, 200)).getContent();
        ComboBox<CategoryResponse> categorySelect = new ComboBox<>("Category");
        categorySelect.setItems(categories);
        categorySelect.setItemLabelGenerator(CategoryResponse::getName);
        categorySelect.setRequired(true);
        categorySelect.addClassNames("w-full");

        List<SupplierResponse> suppliers = supplierService.getAllSuppliers(PageRequest.of(0, 200)).getContent();
        ComboBox<SupplierResponse> supplierSelect = new ComboBox<>("Supplier (Optional)");
        supplierSelect.setItems(suppliers);
        supplierSelect.setItemLabelGenerator(SupplierResponse::getName);
        supplierSelect.setClearButtonVisible(true);
        supplierSelect.addClassNames("w-full");

        TextArea descriptionField = new TextArea("Description");
        descriptionField.addClassNames("w-full");

        if (existingProduct != null) {
            nameField.setValue(existingProduct.getName() != null ? existingProduct.getName() : "");
            priceField.setValue(existingProduct.getPrice() != null ? existingProduct.getPrice() : BigDecimal.ZERO);
            stockField.setValue(existingProduct.getStock() != null ? existingProduct.getStock() : 0);
            descriptionField.setValue(existingProduct.getDescription() != null ? existingProduct.getDescription() : "");

            categories.stream()
                    .filter(c -> Objects.equals(c.getId(), existingProduct.getCategoryId()))
                    .findFirst()
                    .ifPresent(categorySelect::setValue);

            suppliers.stream()
                    .filter(s -> Objects.equals(s.getId(), existingProduct.getSupplierId()))
                    .findFirst()
                    .ifPresent(supplierSelect::setValue);
        }

        VerticalLayout formLayout = new VerticalLayout(nameField, priceField, stockField, categorySelect, supplierSelect, descriptionField);
        formLayout.addClassNames("py-2", "w-96");
        dialog.add(formLayout);

        Button cancelButton = new Button("Cancel", _ -> dialog.close());
        cancelButton.addClassNames("text-muted-foreground");

        Button saveButton = new Button("Save", _ -> {
            if (nameField.isEmpty() || priceField.isEmpty() || stockField.isEmpty() || categorySelect.isEmpty()) {
                Notification.show("Please fill in all required fields", 3000, Notification.Position.BOTTOM_END).addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            Long selectedCategoryId = categorySelect.getValue().getId();
            Long selectedSupplierId = supplierSelect.getValue() != null ? supplierSelect.getValue().getId() : null;

            ProductRequest request = ProductRequest.builder()
                    .name(nameField.getValue())
                    .price(priceField.getValue())
                    .stock(stockField.getValue())
                    .categoryId(selectedCategoryId)
                    .supplierId(selectedSupplierId)
                    .description(descriptionField.getValue())
                    .build();

            try {
                if (existingProduct == null) {
                    productService.createProduct(request);
                    Notification.show("Product created successfully", 3000, Notification.Position.BOTTOM_END).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                } else {
                    productService.updateProduct(existingProduct.getId(), request);
                    Notification.show("Product updated successfully", 3000, Notification.Position.BOTTOM_END).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
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

    private void modalDelete(ProductResponse product) {
        Dialog confirmDialog = new Dialog();
        confirmDialog.setHeaderTitle("Delete Product");

        Paragraph text = new Paragraph("Are you sure you want to delete product '" + product.getName() + "'?");
        confirmDialog.add(text);

        Button cancelButton = new Button("Cancel", _ -> confirmDialog.close());

        Button deleteButton = new Button("Delete", _ -> {
            try {
                productService.deleteProduct(product.getId());
                Notification.show("Product deleted", 3000, Notification.Position.BOTTOM_END).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
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