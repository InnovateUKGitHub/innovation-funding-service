package org.innovateuk.ifs.category.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Represents an Innovation Area. {@link InnovationArea}s have a sector {@link InnovationSector}
 */
@Entity
@DiscriminatorValue("INNOVATION_AREA")
public class InnovationArea extends Category implements Serializable {

    private static final long serialVersionUID = -8432114987515968128L;

    public static final long NONE = 67;
    public static final long DIGITAL_MANUFACTORING_ID = 22L;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name="parent_id")
    private InnovationSector sector;

    public InnovationArea() {
        // default constructor
    }

    public InnovationArea(String name, InnovationSector sector) {
        super(name);
        if (sector == null) {
            throw new NullPointerException("sector cannot be null");
        }
    }
    public InnovationSector getSector() {
        return sector;
    }

    public void setSector(InnovationSector sector) {
        this.sector = sector;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        InnovationArea that = (InnovationArea) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(sector, that.sector)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(sector)
                .toHashCode();
    }
}
