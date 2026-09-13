package com.bootcamp.mini_project.views.pages;

import com.bootcamp.mini_project.dto.categories.*;
import com.bootcamp.mini_project.services.CategoryService;
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
 * View page for managing product categories with Shadcn-inspired UI components.
 */
@PageTitle("Categories Management | Mini POS")
@Route(value = "categories", layout = MainLayout.class)
public class Categories extends VerticalLayout {
    private final CategoryService categoryService;
    private final Grid<CategoryResponse> grid = new Grid<>(CategoryResponse.class, false);

    public Categories(CategoryService categoryService) {
        this.categoryService = categoryService;

        setPadding(false);
        setSpacing(false);
        addClassNames("p-5", "max-w-7xl", "mx-auto", "w-full", "gap-4", "box-border", "overflow-x-hidden");

        VerticalLayout headerText = getHeaderText();

        Button addButton = new Button("+ Add Category", _ -> modalAction(null));
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

        H2 title = new H2("Categories");
        title.addClassNames("m-0", "text-lg", "font-bold", "tracking-tight", "text-foreground");

        Paragraph description = new Paragraph("Organize your products by managing inventory categories.");
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

        Span current = new Span("Categories");
        current.addClassNames("font-medium", "text-foreground");

        breadcrumb.add(dashLink, sep, current);
        return breadcrumb;
    }

    private void table() {
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.addClassNames("border", "border-border", "rounded-md", "bg-card", "text-card-foreground", "w-full", "text-xs");
        grid.addColumn(CategoryResponse::getId).setHeader("ID").setAutoWidth(true).setTextAlign(ColumnTextAlign.CENTER);
        grid.addColumn(CategoryResponse::getName).setHeader("Category Name").setAutoWidth(true).setTextAlign(ColumnTextAlign.CENTER);
        grid.addColumn(CategoryResponse::getDescription).setHeader("Description").setAutoWidth(true).setTextAlign(ColumnTextAlign.CENTER);

        grid.addComponentColumn(category -> {
            Button editButton = new Button("Edit", _ -> modalAction(category));
            editButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            editButton.addClassNames("text-xs", "font-medium", "text-foreground", "hover:underline");

            Button deleteButton = new Button("Delete", _ -> modalDelete(category));
            deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
            deleteButton.addClassNames("text-xs", "font-medium", "text-destructive", "hover:underline");

            HorizontalLayout actions = new HorizontalLayout(editButton, deleteButton);
            actions.addClassNames("gap-2", "justify-center", "items-center", "w-full");
            return actions;
        }).setHeader("Actions").setAutoWidth(true).setTextAlign(ColumnTextAlign.CENTER);
    }

    private void refreshGridData() {
        grid.setItems(categoryService.getAllCategories(PageRequest.of(0, 100)).getContent());
    }

    private void modalAction(CategoryResponse existingCategory) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(existingCategory == null ? "Create New Category" : "Edit Category");
        dialog.setWidth("480px");

        TextField nameField = new TextField("Category Name");
        nameField.setRequired(true);
        nameField.addClassNames("w-full", "text-xs");

        TextArea descriptionField = new TextArea("Description");
        descriptionField.addClassNames("w-full", "text-xs");

        if (existingCategory != null) {
            nameField.setValue(existingCategory.getName() != null ? existingCategory.getName() : "");
            descriptionField.setValue(existingCategory.getDescription() != null ? existingCategory.getDescription() : "");
        }

        VerticalLayout formLayout = new VerticalLayout(nameField, descriptionField);
        formLayout.setPadding(false);
        formLayout.setSpacing(false);
        formLayout.addClassNames("w-full", "gap-3", "py-2");
        dialog.add(formLayout);

        Button cancelButton = new Button("Cancel", _ -> dialog.close());
        cancelButton.addClassNames("text-xs", "text-muted-foreground");

        Button saveButton = new Button("Save", _ -> {
            if (nameField.isEmpty()) {
                Notification.show("Category name is required", 3000, Notification.Position.BOTTOM_END).addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            CategoryRequest request = CategoryRequest.builder().name(nameField.getValue()).description(descriptionField.getValue()).build();

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
        saveButton.addClassNames("bg-primary", "text-primary-foreground", "text-xs");

        dialog.getFooter().add(cancelButton, saveButton);
        dialog.open();
    }

    private void modalDelete(CategoryResponse category) {
        Dialog confirmDialog = new Dialog();
        confirmDialog.setHeaderTitle("Delete Category");

        Paragraph text = new Paragraph("Are you sure you want to delete category '" + category.getName() + "'?");
        text.addClassNames("text-xs");
        confirmDialog.add(text);

        Button cancelButton = new Button("Cancel", _ -> confirmDialog.close());
        cancelButton.addClassNames("text-xs");

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
        deleteButton.addClassNames("bg-destructive", "text-destructive-foreground", "text-xs");

        confirmDialog.getFooter().add(cancelButton, deleteButton);
        confirmDialog.open();
    }
}