package org.innovateuk.ifs.management.assessor.viewmodel;

import org.innovateuk.ifs.category.resource.InnovationSectorResource;
import org.innovateuk.ifs.management.assessor.viewmodel.InviteAssessorsViewModel;
import org.innovateuk.ifs.management.assessor.viewmodel.InvitedAssessorRowViewModel;

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
