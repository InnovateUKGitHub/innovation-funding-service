package org.innovateuk.ifs.util;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

/**
 * Base class to enable the spring security to apply type information when applying security rules to entity ids.
 */
public abstract class CompositeId implements Serializable {
    private final Long id;

    protected CompositeId(Long id) {
        this.id = id;
    }

    public Long id() {
        return id;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CompositeId that = (CompositeId) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .isEquals();
    }

    @Override
    public final int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .toHashCode();
    }
}
