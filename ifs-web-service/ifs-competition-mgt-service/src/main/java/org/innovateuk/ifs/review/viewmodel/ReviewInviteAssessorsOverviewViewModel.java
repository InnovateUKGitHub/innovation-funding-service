package org.innovateuk.ifs.review.viewmodel;

import org.innovateuk.ifs.management.assessor.viewmodel.InviteAssessorsViewModel;
import org.innovateuk.ifs.management.assessor.viewmodel.OverviewAssessorRowViewModel;

/**
 * Holder of model attributes for the Invite assessors 'Overview' view.
 */
public class ReviewInviteAssessorsOverviewViewModel extends InviteAssessorsViewModel<OverviewAssessorRowViewModel> {

    private boolean selectAllDisabled;

    public boolean isSelectAllDisabled() {
        return selectAllDisabled;
    }

    public void setSelectAllDisabled(boolean selectAllDisabled) {
        this.selectAllDisabled = selectAllDisabled;
    }
}
