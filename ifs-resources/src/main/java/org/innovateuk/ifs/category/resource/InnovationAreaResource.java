package org.innovateuk.ifs.category.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static org.innovateuk.ifs.category.resource.CategoryType.INNOVATION_AREA;

/**
 * DTO for <code>InnovationArea</code>s.
 */
public class InnovationAreaResource extends CategoryResource {

    private Long parent;

    @Override
    public CategoryType getType() {
        return INNOVATION_AREA;
    }

    public Long getParent() {
        return parent;
    }

    public void setParent(Long parent) {
        this.parent = parent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        InnovationAreaResource that = (InnovationAreaResource) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(parent, that.parent)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(parent)
                .toHashCode();
    }
}
