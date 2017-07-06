package org.innovateuk.ifs.management.viewmodel;

import java.util.List;

/**
 * Holder of model attributes for the Manage applications page
 */
public class ManageAssessorsViewModel {
    private final long competitionId;
    private final String competitionName;
    private final List<ManageAssessorsRowViewModel> assessors;
    private final boolean inAssessment;
    private final String filter;
    private final PaginationViewModel pagination;

    public ManageAssessorsViewModel(long competitionId,
                                    String competitionName,
                                    List<ManageAssessorsRowViewModel> assessors,
                                    boolean inAssessment,
                                    String filter,
                                    PaginationViewModel pagination) {
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.assessors = assessors;
        this.inAssessment = inAssessment;
        this.filter = filter;
        this.pagination = pagination;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public List<ManageAssessorsRowViewModel> getAssessors() {
        return assessors;
    }

    public boolean isInAssessment() {
        return inAssessment;
    }

    public String getFilter() {
        return filter;
    }

    public PaginationViewModel getPagination() {
        return pagination;
    }
}
