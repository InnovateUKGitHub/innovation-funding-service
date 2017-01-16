package org.innovateuk.ifs.category.domain;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.category.resource.CategoryType;

import javax.persistence.*;

import java.util.List;

import static org.innovateuk.ifs.category.resource.CategoryType.INNOVATION_SECTOR;

/**
 * An Innovation Sector. {@link InnovationSector}s contain a List of {@link InnovationArea}s.
 */
@Entity
@DiscriminatorValue("INNOVATION_SECTOR")
public class InnovationSector extends ParentCategory<InnovationArea> {

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
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
        children.forEach(innovationArea -> innovationArea.setParent(this));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        InnovationSector that = (InnovationSector) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
//                .append(children, that.children)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
//                .append(children)
                .toHashCode();
    }
}
