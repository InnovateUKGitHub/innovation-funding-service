package org.innovateuk.ifs.category.resource;

/**
 * Resource to link any class to {@link CategoryResource}
 */
public class CategoryLinkResource {
    private Long id;
    private Long category;

    public CategoryLinkResource() {
    }

    public CategoryLinkResource(Long id, Long category, String className, Long classPk) {
        this.id = id;
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCategory() {
        return category;
    }

    public void setCategory(Long category) {
        this.category = category;
    }
}
