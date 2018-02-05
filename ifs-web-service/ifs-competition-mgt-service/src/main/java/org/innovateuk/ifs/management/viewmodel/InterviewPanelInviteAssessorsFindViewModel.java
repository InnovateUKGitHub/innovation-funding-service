package org.innovateuk.ifs.management.viewmodel;

/**
 * Holder of model attributes for the Invite assessors for Interview Panel 'Find' view.
 */
public class InterviewPanelInviteAssessorsFindViewModel extends InviteAssessorsViewModel<InterviewPanelAvailableAssessorRowViewModel> {

    private boolean selectAllDisabled;

    public boolean isSelectAllDisabled() {
        return selectAllDisabled;
    }

    public void setSelectAllDisabled(boolean selectAllDisabled) {
        this.selectAllDisabled = selectAllDisabled;
    }
}