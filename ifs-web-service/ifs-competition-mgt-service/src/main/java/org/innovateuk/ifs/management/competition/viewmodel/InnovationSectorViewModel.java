package org.innovateuk.ifs.management.competition.viewmodel;

import org.innovateuk.ifs.category.resource.InnovationAreaResource;

import java.util.List;

/**
 * Model attributes for View for the Assessor Profile view's Innovation Sectors.
 */
public class InnovationSectorViewModel {

    private String name;
    private List<InnovationAreaResource> children;

    public InnovationSectorViewModel(String name, List<InnovationAreaResource> children) {
        this.name = name;
        this.children = children;
    }

    public String getName() {
        return name;
    }

    public List<InnovationAreaResource> getChildren() {
        return children;
    }
}
