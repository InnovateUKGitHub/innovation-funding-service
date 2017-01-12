package org.innovateuk.ifs.category.domain;

import org.innovateuk.ifs.category.resource.CategoryType;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import static org.innovateuk.ifs.category.resource.CategoryType.INNOVATION_AREA;

/**
 * Represents an Innovation Area. {@link InnovationArea}s have a parent {@link InnovationSector}
 */
@Entity
@DiscriminatorValue("INNOVATION_AREA")
public class InnovationArea extends Category {

    @ManyToOne(optional = true)
    @JoinColumn(name="parent_id")
    private InnovationSector parent;

    // todo this is public just to support the mapper -- can be instantited with reflection
    public InnovationArea() {
        // default constructor
    }

    @Override
    public CategoryType getType() {
        return INNOVATION_AREA;
    }

    public InnovationArea(String name, InnovationSector sector) {
        super(name);
        if (sector == null) {
            throw new NullPointerException("sector cannot be null");
        }
    }
    public InnovationSector getParent() {
        return parent;
    }

    public void setParent(InnovationSector parent) {
        this.parent = parent;
    }
}
