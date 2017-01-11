package org.innovateuk.ifs.category.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

public class InnovationSectorResource extends CategoryResource {

    private List<InnovationAreaResource> children;

    public List<InnovationAreaResource> getChildren() {
        return children;
    }

    public void setChildren(List<InnovationAreaResource> children) {
        this.children = children;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        InnovationSectorResource that = (InnovationSectorResource) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(children, that.children)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(children)
                .toHashCode();
    }
}
