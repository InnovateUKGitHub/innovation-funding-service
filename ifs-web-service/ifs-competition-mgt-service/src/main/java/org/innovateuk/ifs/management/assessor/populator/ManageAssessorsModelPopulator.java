package org.innovateuk.ifs.management.assessor.populator;

import org.innovateuk.ifs.application.resource.AssessorCountSummaryPageResource;
import org.innovateuk.ifs.application.resource.AssessorCountSummaryResource;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.assessment.populator.BaseManageAssessmentsModelPopulator;
import org.innovateuk.ifs.management.assessor.viewmodel.ManageAssessorsRowViewModel;
import org.innovateuk.ifs.management.assessor.viewmodel.ManageAssessorsViewModel;
import org.innovateuk.ifs.management.navigation.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.competition.resource.CompetitionStatus.IN_ASSESSMENT;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

@Component
public class ManageAssessorsModelPopulator extends BaseManageAssessmentsModelPopulator {

    @Autowired
    private CategoryRestService categoryRestService;

    public ManageAssessorsViewModel populateModel(CompetitionResource competition, AssessorCountSummaryPageResource assessorCounts) {
        return new ManageAssessorsViewModel(
                competition.getId(), competition.getName(),
                simpleMap(assessorCounts.getContent(), this::getRowViewModel),
                competition.getCompetitionStatus() == IN_ASSESSMENT,
                categoryRestService.getInnovationSectors().getSuccess(),
                new Pagination(assessorCounts));
    }

    private ManageAssessorsRowViewModel getRowViewModel(AssessorCountSummaryResource assessorCount) {
        return new ManageAssessorsRowViewModel(assessorCount);
    }
}