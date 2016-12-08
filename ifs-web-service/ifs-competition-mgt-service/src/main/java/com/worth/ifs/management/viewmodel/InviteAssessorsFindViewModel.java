package com.worth.ifs.management.viewmodel;

import java.util.List;

/**
 * Holder of model attributes for the Invite assessors 'Find' view.
 */
public class InviteAssessorsFindViewModel extends InviteAssessorsViewModel {

    private List<AssessorViewModel> assessors;

    public List<AssessorViewModel> getAssessors() {
        return assessors;
    }

    public void setAssessors(List<AssessorViewModel> assessors) {
        this.assessors = assessors;
    }
}