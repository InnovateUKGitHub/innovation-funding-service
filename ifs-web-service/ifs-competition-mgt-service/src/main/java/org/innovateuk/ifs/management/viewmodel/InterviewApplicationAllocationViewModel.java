package org.innovateuk.ifs.management.viewmodel;

import java.util.List;

/**
 * Holder of model attributes for the Manage applications page
 */
public class InterviewApplicationAllocationViewModel {
    private long competitionId;
    private String competitionName;
    private List<InterviewApplicationAllocationRowViewModel> assessors;
    private PaginationViewModel pagination;

    public InterviewApplicationAllocationViewModel(long competitionId,
                                                   String competitionName,
                                                   List<InterviewApplicationAllocationRowViewModel> assessors,
                                                   PaginationViewModel pagination
    ) {
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.assessors = assessors;
        this.pagination = pagination;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public List<InterviewApplicationAllocationRowViewModel> getAssessors() {
        return assessors;
    }

    public void setAssessors(List<InterviewApplicationAllocationRowViewModel> assessors) {
        this.assessors = assessors;
    }

    public PaginationViewModel getPagination() {
        return pagination;
    }
}
