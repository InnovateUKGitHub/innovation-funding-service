package org.innovateuk.ifs.assessment.overview.populator;

import org.innovateuk.ifs.assessment.common.service.AssessmentService;
import org.innovateuk.ifs.assessment.overview.viewmodel.AssessmentTermsAndConditionsViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AssessmentTermsAndConditionsModelPopulator {

    @Autowired
    private final CompetitionRestService competitionRestService;

    @Autowired
    private final AssessmentService assessmentService;

    public AssessmentTermsAndConditionsModelPopulator(CompetitionRestService competitionRestService,
                                                      AssessmentService assessmentService) {
        this.competitionRestService = competitionRestService;
        this.assessmentService = assessmentService;
    }

    public AssessmentTermsAndConditionsViewModel populate(long assessmentId) {
        long competitionId = assessmentService.getById(assessmentId).getCompetition();
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();

        return new AssessmentTermsAndConditionsViewModel(
                assessmentId,
                competition.getTermsAndConditions().getTemplate(),
                competition.getAssessmentDaysLeft(),
                competition.getAssessmentDaysLeftPercentage()
        );
    }
}