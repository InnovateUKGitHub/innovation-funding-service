package org.innovateuk.ifs.category.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public abstract class CategoryLinkResource <T, C extends CategoryResource>{

    private Long id;
    private C category;
    private String className;
    public CategoryLinkResource() {
    }

    protected CategoryLinkResource(C category) {
        if (category == null) {
            throw new NullPointerException("category cannot be null");
        }
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public C getCategory() {
        return category;
    }

    public void setCategory(C category) {
        this.category = category;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public abstract T getEntity();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CategoryLinkResource<?, ?> that = (CategoryLinkResource<?, ?>) o;

        return new EqualsBuilder()
                .append(category, that.category)
                .append(className, that.className)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(category)
                .append(className)
                .toHashCode();
    }
}
