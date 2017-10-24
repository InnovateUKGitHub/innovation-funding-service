package org.innovateuk.ifs.management.viewmodel;

import org.innovateuk.ifs.category.resource.InnovationAreaResource;

import java.util.ArrayList;
import java.util.List;

/**
 * Holder of model attributes for the Invite assessors 'Overview' view.
 */
public class PanelInviteAssessorsOverviewViewModel extends PanelInviteAssessorsViewModel<PanelOverviewAssessorRowViewModel> {

    private boolean selectAllDisabled;

    public boolean isSelectAllDisabled() {
        return selectAllDisabled;
    }

    public void setSelectAllDisabled(boolean selectAllDisabled) {
        this.selectAllDisabled = selectAllDisabled;
    }
}
