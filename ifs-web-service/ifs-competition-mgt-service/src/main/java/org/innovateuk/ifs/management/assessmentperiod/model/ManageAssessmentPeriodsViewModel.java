package org.innovateuk.ifs.management.assessmentperiod.model;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.pagination.PaginationViewModel;

public class ManageAssessmentPeriodsViewModel {

    private long competitionId;
    private PaginationViewModel paginationViewModel;

    public ManageAssessmentPeriodsViewModel(CompetitionResource competitionResource, PaginationViewModel paginationViewModel) {
        this.competitionId = competitionResource.getId();
        this.paginationViewModel = paginationViewModel;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public PaginationViewModel getPaginationViewModel() {
        return paginationViewModel;
    }
}
