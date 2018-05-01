package org.innovateuk.ifs.management.viewmodel;

import java.util.List;

/**
 *  Holder of model attributes for the assessors shown in the 'Allocate applications to assessors' page
 */
public class InterviewAllocateApplicationsViewModel {
    private long competitionId;
    private String competitionName;
    private List<InterviewAllocateApplicationsRowViewModel> assessors;
    private PaginationViewModel pagination;

    public InterviewAllocateApplicationsViewModel(long competitionId,
                                                  String competitionName,
                                                  List<InterviewAllocateApplicationsRowViewModel> assessors,
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

    public List<InterviewAllocateApplicationsRowViewModel> getAssessors() {
        return assessors;
    }

    public void setAssessors(List<InterviewAllocateApplicationsRowViewModel> assessors) {
        this.assessors = assessors;
    }

    public PaginationViewModel getPagination() {
        return pagination;
    }
}
