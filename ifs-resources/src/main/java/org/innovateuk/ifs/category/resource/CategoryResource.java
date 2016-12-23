package org.innovateuk.ifs.category.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Resource Class for a generic {@link org.innovateuk.ifs.category.domain.Category}
 */
public class CategoryResource {
    private Long id;
    private String name;
    private CategoryType type;

    private Long parent;
    private List<CategoryResource> children;

    public CategoryResource() {
    }

    public CategoryResource(Long id, String name, CategoryType type, Long parent) {
        this(id, name, type, parent, new ArrayList<>());
    }

    public CategoryResource(Long id, String name, CategoryType type, Long parent, List<CategoryResource> children) {
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

    public List<CategoryResource> getChildren() {
        return children;
    }

    public void setChildren(List<CategoryResource> children) {
        this.children = children;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CategoryResource that = (CategoryResource) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(name, that.name)
                .append(type, that.type)
                .append(parent, that.parent)
                .append(children, that.children)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(name)
                .append(type)
                .append(parent)
                .append(children)
                .toHashCode();
    }
}
