package com.worth.ifs.category.resource;

import java.util.Set;

/**
 * Resource Class for a generic {@link com.worth.ifs.category.domain.Category}
 */
public class CategoryResource {
    private Long id;
    private String name;
    private CategoryType type;

    private Long parent;
    private Set<Long> children;

    public CategoryResource() {
    }

    public CategoryResource(Long id, String name, CategoryType type, Long parent, Set<Long> children) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.parent = parent;
        this.children = children;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CategoryType getType() {
        return type;
    }

    public void setType(CategoryType type) {
        this.type = type;
    }

    public Long getParent() {
        return parent;
    }

    public void setParent(Long parent) {
        this.parent = parent;
    }

    public Set<Long> getChildren() {
        return children;
    }

    public void setChildren(Set<Long> children) {
        this.children = children;
    }
}
