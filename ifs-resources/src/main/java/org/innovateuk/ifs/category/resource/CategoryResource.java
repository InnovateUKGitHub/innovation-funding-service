package org.innovateuk.ifs.category.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Resource Class for a generic {@link org.innovateuk.ifs.category.domain.Category}
 */
public abstract class CategoryResource {
    private Long id;
    private String name;
    private CategoryType type;

    private Long parent;

    public CategoryResource() {
    }

    protected CategoryResource(Long id, String name, CategoryType type, Long parent) {
        this.id = id;
        this.name= name;
        this.type = type;
        this.parent = parent;
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
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(name)
                .append(type)
                .append(parent)
                .toHashCode();
    }
}
