package com.bootcamp.mini_project.views.pages;

import com.bootcamp.mini_project.dto.categories.*;
import com.bootcamp.mini_project.services.CategoryService;
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
 * View page for managing product categories with Shadcn-inspired UI components.
 */
@PageTitle("Categories Management | Mini POS")
@Route(value = "categories", layout = MainLayout.class)
public class Categories extends VerticalLayout {
    private final CategoryService categoryService;
    private final Grid<CategoryResponse> grid = new Grid<>(CategoryResponse.class, false);

    public Categories(CategoryService categoryService) {
        this.categoryService = categoryService;

        addClassNames("p-8", "max-w-7xl", "mx-auto", "w-full", "gap-6");

        H2 title = new H2("Categories");
        title.addClassNames("text-2xl", "font-semibold", "tracking-tight", "text-foreground");

        Paragraph description = new Paragraph("Organize your products by managing inventory categories.");
        description.addClassNames("text-sm", "text-muted-foreground");

        VerticalLayout headerText = new VerticalLayout(title, description);
        headerText.setPadding(false);
        headerText.setSpacing(false);

        Button addButton = new Button("+ Add Category", _ -> modalAction(null));
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

        grid.addColumn(CategoryResponse::getId).setHeader("ID").setAutoWidth(true);
        grid.addColumn(CategoryResponse::getName).setHeader("Category Name").setAutoWidth(true);
        grid.addColumn(CategoryResponse::getDescription).setHeader("Description").setAutoWidth(true);

        grid.addComponentColumn(category -> {
            Button editButton = new Button("Edit", _ -> modalAction(category));
            editButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            editButton.addClassNames("text-sm", "font-medium", "text-foreground", "hover:underline");

            Button deleteButton = new Button("Delete", _ -> modalDelete(category));
            deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
            deleteButton.addClassNames("text-sm", "font-medium", "text-destructive", "hover:underline");

            HorizontalLayout actions = new HorizontalLayout(editButton, deleteButton);
            actions.addClassNames("gap-2");
            return actions;
        }).setHeader("Actions").setAutoWidth(true);
    }

    private void refreshGridData() {
        grid.setItems(categoryService.getAllCategories(PageRequest.of(0, 100)).getContent());
    }

    private void modalAction(CategoryResponse existingCategory) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(existingCategory == null ? "Create New Category" : "Edit Category");

        TextField nameField = new TextField("Category Name");
        nameField.setRequired(true);
        nameField.addClassNames("w-full");

        TextArea descriptionField = new TextArea("Description");
        descriptionField.addClassNames("w-full");

        if (existingCategory != null) {
            nameField.setValue(existingCategory.getName() != null ? existingCategory.getName() : "");
            descriptionField.setValue(existingCategory.getDescription() != null ? existingCategory.getDescription() : "");
        }

        VerticalLayout formLayout = new VerticalLayout(nameField, descriptionField);
        formLayout.addClassNames("py-2", "w-80");
        dialog.add(formLayout);

        Button cancelButton = new Button("Cancel", _ -> dialog.close());
        cancelButton.addClassNames("text-muted-foreground");

        Button saveButton = new Button("Save", _ -> {
            if (nameField.isEmpty()) {
                Notification.show("Category name is required", 3000, Notification.Position.BOTTOM_END).addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            CategoryRequest request = CategoryRequest.builder()
                    .name(nameField.getValue())
                    .description(descriptionField.getValue())
                    .build();

            try {
                if (existingCategory == null) {
                    categoryService.createCategory(request);
                    Notification.show("Category created successfully", 3000, Notification.Position.BOTTOM_END).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                } else {
                    categoryService.updateCategory(existingCategory.getId(), request);
                    Notification.show("Category updated successfully", 3000, Notification.Position.BOTTOM_END).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                }

                refreshGridData();
                dialog.close();
            } catch (Exception ex) {
                Notification.show(ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred", 3000, Notification.Position.BOTTOM_END).addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClassNames("bg-primary", "text-primary-foreground");

        dialog.getFooter().add(cancelButton, saveButton);
        dialog.open();
    }

    private void modalDelete(CategoryResponse category) {
        Dialog confirmDialog = new Dialog();
        confirmDialog.setHeaderTitle("Delete Category");

        Paragraph text = new Paragraph("Are you sure you want to delete category '" + category.getName() + "'?");
        confirmDialog.add(text);

        Button cancelButton = new Button("Cancel", _ -> confirmDialog.close());

        Button deleteButton = new Button("Delete", _ -> {
            try {
                categoryService.deleteCategory(category.getId());
                Notification.show("Category deleted", 3000, Notification.Position.BOTTOM_END).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                refreshGridData();
                confirmDialog.close();
            } catch (Exception ex) {
                Notification.show(ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred", 3000, Notification.Position.BOTTOM_END).addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        deleteButton.addClassNames("bg-destructive", "text-destructive-foreground");

        confirmDialog.getFooter().add(cancelButton, deleteButton);
        confirmDialog.open();
    }
}