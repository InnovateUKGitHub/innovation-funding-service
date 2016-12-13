package org.innovateuk.ifs.management.viewmodel;

import java.util.List;

/**
 * Holder of model attributes for the Invite assessors 'Find' view.
 */
public class InviteAssessorsFindViewModel extends InviteAssessorsViewModel {

    private List<AvailableAssessorViewModel> assessors;

    public List<AvailableAssessorViewModel> getAssessors() {
        return assessors;
    }

    public void setAssessors(List<AvailableAssessorViewModel> assessors) {
        this.assessors = assessors;
    }
}
