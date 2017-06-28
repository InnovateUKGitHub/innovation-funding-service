package org.innovateuk.ifs.management.viewmodel;

import org.innovateuk.ifs.category.resource.InnovationSectorResource;

import java.util.ArrayList;
import java.util.List;

/**
 * Holder of model attributes for the Invite assessors 'Find' view.
 */
public class InviteAssessorsFindViewModel extends InviteAssessorsViewModel<AvailableAssessorRowViewModel> {

    private List<InnovationSectorResource> innovationSectorOptions = new ArrayList<>();
    private boolean selectAllDisabled;

    public List<InnovationSectorResource> getInnovationSectorOptions() {
        return innovationSectorOptions;
    }

    public void setInnovationSectorOptions(List<InnovationSectorResource> innovationSectorOptions) {
        this.innovationSectorOptions = innovationSectorOptions;
    }

    public boolean isSelectAllDisabled() {
        return selectAllDisabled;
    }

    public void setSelectAllDisabled(boolean selectAllDisabled) {
        this.selectAllDisabled = selectAllDisabled;
    }
}