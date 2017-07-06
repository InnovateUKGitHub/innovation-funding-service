package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.application.resource.AssessorCountSummaryPageResource;
import org.innovateuk.ifs.application.resource.AssessorCountSummaryResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.viewmodel.ManageAssessorsRowViewModel;
import org.innovateuk.ifs.management.viewmodel.ManageAssessorsViewModel;
import org.innovateuk.ifs.management.viewmodel.PaginationViewModel;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.competition.resource.CompetitionStatus.IN_ASSESSMENT;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

@Component
public class ManageAssessorsModelPopulator extends BaseManageAssessmentsModelPopulator<AssessorCountSummaryResource, AssessorCountSummaryPageResource, ManageAssessorsViewModel> {

    public ManageAssessorsViewModel populateModel(CompetitionResource competition, AssessorCountSummaryPageResource assessorCounts, String origin) {
        return new ManageAssessorsViewModel(
                competition.getId(), competition.getName(),
                simpleMap(assessorCounts.getContent(), this::getRowViewModel),
                IN_ASSESSMENT.equals(competition.getCompetitionStatus()),
                new PaginationViewModel(assessorCounts, origin));
    }

    private ManageAssessorsRowViewModel getRowViewModel(AssessorCountSummaryResource assessorCount) {
        return new ManageAssessorsRowViewModel(assessorCount);
    }
}