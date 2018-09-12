package org.innovateuk.ifs.interview.viewmodel;

import org.innovateuk.ifs.management.assessor.viewmodel.InviteAssessorsViewModel;

/**
 * Holder of model attributes for the Invite assessors for Interview Panel 'Find' view.
 */
public class InterviewInviteAssessorsFindViewModel extends InviteAssessorsViewModel<InterviewAvailableAssessorRowViewModel> {

    private boolean selectAllDisabled;

    public boolean isSelectAllDisabled() {
        return selectAllDisabled;
    }

    public void setSelectAllDisabled(boolean selectAllDisabled) {
        this.selectAllDisabled = selectAllDisabled;
    }
}