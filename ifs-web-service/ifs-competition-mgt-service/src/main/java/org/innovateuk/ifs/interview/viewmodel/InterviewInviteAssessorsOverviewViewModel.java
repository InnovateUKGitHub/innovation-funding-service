package org.innovateuk.ifs.interview.viewmodel;

import org.innovateuk.ifs.management.viewmodel.InviteAssessorsViewModel;
import org.innovateuk.ifs.management.viewmodel.OverviewAssessorRowViewModel;

/**
 * Holder of model attributes for the Interview Invite assessors 'Overview' view.
 */
public class InterviewInviteAssessorsOverviewViewModel extends InviteAssessorsViewModel<OverviewAssessorRowViewModel> {

    private boolean selectAllDisabled;

    public boolean isSelectAllDisabled() {
        return selectAllDisabled;
    }

    public void setSelectAllDisabled(boolean selectAllDisabled) {
        this.selectAllDisabled = selectAllDisabled;
    }
}
