package org.innovateuk.ifs.management.viewmodel;

import org.innovateuk.ifs.category.resource.CategoryResource;

import java.util.List;

/**
 * Holder of model attributes for the Invite assessors 'Invite' view.
 */
public class InviteAssessorsInviteViewModel extends InviteAssessorsViewModel<InvitedAssessorRowViewModel> {

    private List<CategoryResource> innovationSectorOptions;

    public List<CategoryResource> getInnovationSectorOptions() {
        return innovationSectorOptions;
    }

    public void setInnovationSectorOptions(List<CategoryResource> innovationSectorOptions) {
        this.innovationSectorOptions = innovationSectorOptions;
    }
}
