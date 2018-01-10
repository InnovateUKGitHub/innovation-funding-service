package org.innovateuk.ifs.management.viewmodel;

/**
 * Holder of model attributes for the Invite assessors for Assessment Panel 'Find' view.
 */
public class PanelInviteAssessorsFindViewModel extends InviteAssessorsViewModel<PanelAvailableAssessorRowViewModel> {

    private boolean selectAllDisabled;

    public boolean isSelectAllDisabled() {
        return selectAllDisabled;
    }

    public void setSelectAllDisabled(boolean selectAllDisabled) {
        this.selectAllDisabled = selectAllDisabled;
    }
}