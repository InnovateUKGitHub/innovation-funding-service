package org.innovateuk.ifs.assessment.feedback.populator;

import org.innovateuk.ifs.application.common.populator.ApplicationSubsidyBasisModelPopulator;
import org.innovateuk.ifs.assessment.common.service.AssessmentService;
import org.innovateuk.ifs.assessment.feedback.viewmodel.AssessmentFeedbackSubsidyBasisViewModel;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AssessmentFeedbackSubsidyBasisModelPopulator extends AssessmentModelPopulator<AssessmentFeedbackSubsidyBasisViewModel> {

    @Autowired
    private AssessmentService assessmentService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    ApplicationSubsidyBasisModelPopulator applicationSubsidyBasisPopulator;

    @Override
    public AssessmentFeedbackSubsidyBasisViewModel populate(long assessmentId, QuestionResource question) {
        AssessmentResource assessment = assessmentService.getById(assessmentId);
        CompetitionResource competition = competitionRestService.getCompetitionById(assessment.getCompetition()).getSuccess();

        return new AssessmentFeedbackSubsidyBasisViewModel(
                assessment.getApplicationName(),
                competition.getAssessmentDaysLeft(),
                competition.getAssessmentDaysLeftPercentage(),
                question.getShortName(),
                applicationSubsidyBasisPopulator.populate(question, assessment.getApplication()));
    }
}
