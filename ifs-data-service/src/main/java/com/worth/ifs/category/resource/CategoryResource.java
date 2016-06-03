package com.worth.ifs.category.resource;

import com.worth.ifs.category.domain.CategoryType;

import java.util.Set;

public class CategoryResource {
    private Long id;
    private String name;
    private CategoryType type;

    private Long parent;
    private Set<Long> children;


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
