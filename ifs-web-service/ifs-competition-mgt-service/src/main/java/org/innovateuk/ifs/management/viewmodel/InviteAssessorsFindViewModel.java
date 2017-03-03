package org.innovateuk.ifs.management.viewmodel;

import org.innovateuk.ifs.category.resource.InnovationAreaResource;

import java.util.ArrayList;
import java.util.List;

/**
 * Holder of model attributes for the Invite assessors 'Find' view.
 */
public class InviteAssessorsFindViewModel extends InviteAssessorsViewModel<AvailableAssessorRowViewModel> {

    private List<InnovationAreaResource> innovationAreaOptions = new ArrayList<>();

    public List<InnovationAreaResource> getInnovationAreaOptions() {
        return innovationAreaOptions;
    }

    public void setInnovationAreaOptions(List<InnovationAreaResource> innovationAreaOptions) {
        this.innovationAreaOptions = innovationAreaOptions;
    }
}
