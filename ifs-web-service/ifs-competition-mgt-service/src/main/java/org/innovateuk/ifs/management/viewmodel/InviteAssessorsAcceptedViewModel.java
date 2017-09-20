package org.innovateuk.ifs.management.viewmodel;

import org.innovateuk.ifs.category.resource.InnovationAreaResource;

import java.util.ArrayList;
import java.util.List;

/**
 * Holder of model attributes for the Invite assessors 'Accepted' view.
 */
public class InviteAssessorsAcceptedViewModel extends InviteAssessorsViewModel<OverviewAssessorRowViewModel> {

    private List<InnovationAreaResource> innovationAreaOptions = new ArrayList<>();

    public List<InnovationAreaResource> getInnovationAreaOptions() {
        return innovationAreaOptions;
    }

    public void setInnovationAreaOptions(List<InnovationAreaResource> innovationAreaOptions) {
        this.innovationAreaOptions = innovationAreaOptions;
    }
}
