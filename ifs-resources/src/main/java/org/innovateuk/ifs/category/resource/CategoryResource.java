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

    public CategoryResource() {
    }

    protected CategoryResource(Long id, String name, CategoryType type) {
        this.id = id;
        this.name= name;
        this.type = type;
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

    public abstract CategoryType getType();

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
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(name)
                .append(type)
                .toHashCode();
    }
}
