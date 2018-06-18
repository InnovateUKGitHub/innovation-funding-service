package org.innovateuk.ifs.review.viewmodel;

import org.innovateuk.ifs.management.assessor.viewmodel.InviteAssessorsViewModel;

/**
 * Holder of model attributes for the Invite assessors for Assessment Panel 'Find' view.
 */
public class ReviewInviteAssessorsFindViewModel extends InviteAssessorsViewModel<ReviewAvailableAssessorRowViewModel> {

    private boolean selectAllDisabled;

    public boolean isSelectAllDisabled() {
        return selectAllDisabled;
    }

    public void setSelectAllDisabled(boolean selectAllDisabled) {
        this.selectAllDisabled = selectAllDisabled;
    }
}