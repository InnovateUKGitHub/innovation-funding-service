package org.innovateuk.ifs.category.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Represents an Innovation Area. {@link InnovationArea}s have a sector {@link InnovationSector}
 */
@Entity
@DiscriminatorValue("INNOVATION_AREA")
public class InnovationArea extends Category {

    @ManyToOne(optional = true)
    @JoinColumn(name="parent_id")
    private InnovationSector sector;

    // todo this is public just to support the mapper -- can be instantited with reflection
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
        if (!super.equals(o)) return false;

        InnovationArea that = (InnovationArea) o;

        return sector != null ? sector.equals(that.sector) : that.sector == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (sector != null ? sector.hashCode() : 0);
        return result;
    }
}
