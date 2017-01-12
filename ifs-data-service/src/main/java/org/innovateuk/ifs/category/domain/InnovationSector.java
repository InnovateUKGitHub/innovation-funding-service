package org.innovateuk.ifs.category.domain;


import org.innovateuk.ifs.category.resource.CategoryType;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import java.util.List;

import static org.innovateuk.ifs.category.resource.CategoryType.INNOVATION_SECTOR;

/**
 * An Innovation Sector. {@link InnovationSector}s contain a Set of {@link InnovationArea}s.
 */
@Entity
@DiscriminatorValue("INNOVATION_SECTOR")
public class InnovationSector extends ContainerCategory<InnovationArea> {

    @OneToMany(mappedBy = "parent")
    @OrderBy("name ASC")
    private List<InnovationArea> children;

    public InnovationSector() {
        // default constructor
    }

    public InnovationSector(String name) {
        super(name);
    }

    @Override
    public CategoryType getType() {
        return INNOVATION_SECTOR;
    }

    @Override
    public List<InnovationArea> getChildren() {
        return children;
    }

    public void setChildren(List<InnovationArea> children) {
        this.children = children;
    }
}
