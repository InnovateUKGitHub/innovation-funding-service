package org.innovateuk.ifs.management.assessmentperiod.populator;

import org.innovateuk.ifs.commons.resource.PageResource;
import org.innovateuk.ifs.competition.resource.AssessmentPeriodResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.assessmentperiod.model.ManageAssessmentPeriodsViewModel;
import org.innovateuk.ifs.pagination.PaginationViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ManageAssessmentPeriodsPopulator {

    @Autowired
    private CompetitionRestService competitionRestService;

    public ManageAssessmentPeriodsViewModel populateModel(long competitionId, PageResource<AssessmentPeriodResource> pageResult) {

        CompetitionResource competitionResource = competitionRestService.getCompetitionById(competitionId).getSuccess();

        return new ManageAssessmentPeriodsViewModel(competitionResource, new PaginationViewModel(pageResult, false));
    }



}
