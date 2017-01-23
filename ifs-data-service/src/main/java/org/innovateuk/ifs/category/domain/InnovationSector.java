package org.innovateuk.ifs.category.domain;

import javax.persistence.*;

import java.util.List;


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
    public List<InnovationArea> getChildren() {
        return children;
    }

    public void setChildren(List<InnovationArea> children) {
        this.children = children;
        children.forEach(innovationArea -> innovationArea.setParent(this));
    }
}
