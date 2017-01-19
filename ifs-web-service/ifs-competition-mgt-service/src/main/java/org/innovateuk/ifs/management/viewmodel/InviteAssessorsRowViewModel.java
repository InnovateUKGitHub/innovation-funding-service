package org.innovateuk.ifs.management.viewmodel;

import org.innovateuk.ifs.category.resource.InnovationAreaResource;

import java.util.List;

/**
 * Abstract holder of model attributes for the assessors shown in the 'Invite Assessors' view.
 */
abstract class InviteAssessorsRowViewModel {

    private String name;
    private List<InnovationAreaResource> innovationAreas;
    private boolean compliant;

    protected InviteAssessorsRowViewModel(String name, List<InnovationAreaResource> innovationAreas, boolean compliant) {
        this.name = name;
        this.innovationAreas = innovationAreas;
        this.compliant = compliant;
    }

    public String getName() {
        return name;
    }

    public List<InnovationAreaResource> getInnovationAreas() {
        return innovationAreas;
    }

    public boolean isCompliant() {
        return compliant;
    }
}