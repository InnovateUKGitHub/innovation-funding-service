package com.worth.ifs.category.resource;

/**
 * Resource to link any class to {@link CategoryResource}
 */
public class CategoryLinkResource {
    private Long id;
    private Long category;
    private String className;
    private Long classPk;

    public CategoryLinkResource() {
    }

    public CategoryLinkResource(Long id, Long category, String className, Long classPk) {
        this.id = id;
        this.category = category;
        this.className = className;
        this.classPk = classPk;
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

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Long getClassPk() {
        return classPk;
    }

    public void setClassPk(Long classPk) {
        this.classPk = classPk;
    }
}
