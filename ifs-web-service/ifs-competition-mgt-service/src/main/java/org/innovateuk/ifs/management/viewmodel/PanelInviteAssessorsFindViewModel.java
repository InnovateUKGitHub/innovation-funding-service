package org.innovateuk.ifs.management.viewmodel;

import org.innovateuk.ifs.category.resource.InnovationSectorResource;

import java.util.ArrayList;
import java.util.List;

/**
 * Holder of model attributes for the Invite assessors for Assessment Panel 'Find' view.
 */
public class PanelInviteAssessorsFindViewModel extends InviteAssessorsViewModel<AvailableAssessorRowViewModel> {

    private boolean selectAllDisabled;

    public boolean isSelectAllDisabled() {
        return selectAllDisabled;
    }

    public void setSelectAllDisabled(boolean selectAllDisabled) {
        this.selectAllDisabled = selectAllDisabled;
    }
}