package org.innovateuk.ifs.management.viewmodel;

import org.innovateuk.ifs.category.resource.InnovationSectorResource;

import java.util.List;

/**
 * Holder of model attributes for the Invite assessors 'Invite' view.
 */
public class CompetitionInviteAssessorsInviteViewModel extends InviteAssessorsViewModel<InvitedAssessorRowViewModel> {

    private List<InnovationSectorResource> innovationSectorOptions;

    public List<InnovationSectorResource> getInnovationSectorOptions() {
        return innovationSectorOptions;
    }

    public void setInnovationSectorOptions(List<InnovationSectorResource> innovationSectorOptions) {
        this.innovationSectorOptions = innovationSectorOptions;
    }
}
