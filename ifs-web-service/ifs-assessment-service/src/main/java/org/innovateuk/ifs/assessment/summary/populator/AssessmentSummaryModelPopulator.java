package org.innovateuk.ifs.assessment.summary.populator;

import org.innovateuk.ifs.assessment.resource.AssessmentDetailsResource;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.assessment.resource.AssessorFormInputResponseResource;
import org.innovateuk.ifs.assessment.service.AssessorFormInputResponseRestService;
import org.innovateuk.ifs.assessment.summary.viewmodel.AssessmentSummaryQuestionViewModel;
import org.innovateuk.ifs.assessment.summary.viewmodel.AssessmentSummaryViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Build the model for the Assessment Application Summary view.
 */
@Component
public class AssessmentSummaryModelPopulator {

    @Autowired
    private AssessorFormInputResponseRestService assessorFormInputResponseRestService;

    @Autowired
    private CompetitionRestService competitionRestService;

    public AssessmentSummaryViewModel populateModel(AssessmentResource assessment) {
        CompetitionResource competition = getCompetition(assessment.getCompetition());
        List<AssessmentSummaryQuestionViewModel> questionsViewModel = getQuestionsViewModel(assessment.getId());

        return new AssessmentSummaryViewModel(assessment, competition, questionsViewModel);
    }

    private CompetitionResource getCompetition(long competitionId) {
        return competitionRestService.getCompetitionById(competitionId).getSuccess();
    }

    private List<AssessmentSummaryQuestionViewModel> getQuestionsViewModel(long assessmentId) {
        final AssessmentDetailsResource assessmentDetailsResource = assessorFormInputResponseRestService.getAssessmentDetails(assessmentId).getSuccess();

        return simpleMap(assessmentDetailsResource.getQuestions(), question -> {
            final List<FormInputResource> formInputsForQuestion = ofNullable(assessmentDetailsResource.getFormInputsForQuestion(question.getId())).orElse(emptyList());
            final List<AssessorFormInputResponseResource> responsesForQuestion = ofNullable(assessmentDetailsResource.getFormInputResponsesForQuestion(question.getId())).orElse(emptyList());
            return new AssessmentSummaryQuestionViewModel(
                    question,
                    formInputsForQuestion,
                    responsesForQuestion);
        });
    }
}
