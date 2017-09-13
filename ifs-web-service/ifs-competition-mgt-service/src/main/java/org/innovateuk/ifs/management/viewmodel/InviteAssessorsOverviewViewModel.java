package org.innovateuk.ifs.management.viewmodel;

import org.innovateuk.ifs.category.resource.InnovationAreaResource;

import java.util.ArrayList;
import java.util.List;

/**
 * Holder of model attributes for the Invite assessors 'Overview' view.
 */
public class InviteAssessorsOverviewViewModel extends InviteAssessorsViewModel<OverviewAssessorRowViewModel> {

    private List<InnovationAreaResource> innovationAreaOptions = new ArrayList<>();
    private boolean selectAllDisabled;

    public List<InnovationAreaResource> getInnovationAreaOptions() {
        return innovationAreaOptions;
    }

    public void setInnovationAreaOptions(List<InnovationAreaResource> innovationAreaOptions) {
        this.innovationAreaOptions = innovationAreaOptions;
    }

    public boolean isSelectAllDisabled() {
        return selectAllDisabled;
    }

    public void setSelectAllDisabled(boolean selectAllDisabled) {
        this.selectAllDisabled = selectAllDisabled;
    }
}
