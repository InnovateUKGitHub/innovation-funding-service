package org.innovateuk.ifs.management.viewmodel;

import java.util.List;

/**
 *  Holder of model attributes for the assessors shown in the 'Allocate applications to assessors' page
 */
public class InterviewAllocateOverviewViewModel {
    private long competitionId;
    private String competitionName;
    private List<InterviewAllocateOverviewRowViewModel> assessors;
    private PaginationViewModel pagination;

    public InterviewAllocateOverviewViewModel(long competitionId,
                                              String competitionName,
                                              List<InterviewAllocateOverviewRowViewModel> assessors,
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

    public List<InterviewAllocateOverviewRowViewModel> getAssessors() {
        return assessors;
    }

    public void setAssessors(List<InterviewAllocateOverviewRowViewModel> assessors) {
        this.assessors = assessors;
    }

    public PaginationViewModel getPagination() {
        return pagination;
    }
}
