package org.innovateuk.ifs.management.application.list.populator;

import org.innovateuk.ifs.application.resource.ApplicationCountSummaryPageResource;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryResource;
import org.innovateuk.ifs.assessment.service.AssessmentPeriodService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.application.list.viewmodel.ManageApplicationsRowViewModel;
import org.innovateuk.ifs.management.application.list.viewmodel.ManageApplicationsViewModel;
import org.innovateuk.ifs.management.assessment.populator.BaseManageAssessmentsModelPopulator;
import org.innovateuk.ifs.management.navigation.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.competition.resource.CompetitionStatus.IN_ASSESSMENT;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Build the model for the Manage applications page
 */
@Component
public class ManageApplicationsModelPopulator extends BaseManageAssessmentsModelPopulator {


    @Autowired
    private AssessmentPeriodService assessmentPeriodService;

    public ManageApplicationsViewModel populateModel(CompetitionResource competition, ApplicationCountSummaryPageResource applicationCounts, String filter, long assessmentPeriodId) {

        String assessmentPeriodName = assessmentPeriodService.assessmentPeriodName(assessmentPeriodId, competition.getId());

        ManageApplicationsViewModel model = new ManageApplicationsViewModel(
                competition.getId(), competition.getName(), assessmentPeriodId,
                assessmentPeriodName, simpleMap(applicationCounts.getContent(), this::getRowViewModel),
                IN_ASSESSMENT.equals(competition.getCompetitionStatus()), competition.isAlwaysOpen(),
                filter,
                new Pagination(applicationCounts));
        return model;
    }

    private ManageApplicationsRowViewModel getRowViewModel(ApplicationCountSummaryResource applicationCount) {
        return new ManageApplicationsRowViewModel(applicationCount);
    }
}
