package org.innovateuk.ifs.category.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * An Innovation Sector. {@link InnovationSector}s contain a List of {@link InnovationArea}s.
 */
@Entity
@DiscriminatorValue("INNOVATION_SECTOR")
public class InnovationSector extends ParentCategory<InnovationArea> implements Serializable {

    private static final long serialVersionUID = -1650198672567248725L;

    public static final long NONE = 66;

    @OneToMany(mappedBy = "sector", cascade = CascadeType.ALL)
    @OrderBy("priority ASC")
    private List<InnovationArea> children;

    public InnovationSector() {
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
        children.forEach(innovationArea -> innovationArea.setSector(this));
    }
}
