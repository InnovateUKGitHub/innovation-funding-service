package org.innovateuk.ifs.assessment.summary.populator;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.assessment.resource.AssessorFormInputResponseResource;
import org.innovateuk.ifs.assessment.resource.AssessmentDetailsResource;
import org.innovateuk.ifs.assessment.service.AssessorFormInputResponseRestService;
import org.innovateuk.ifs.assessment.summary.viewmodel.AssessmentSummaryQuestionViewModel;
import org.innovateuk.ifs.assessment.summary.viewmodel.AssessmentSummaryViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.form.resource.FormInputType.*;
import static org.innovateuk.ifs.util.CollectionFunctions.*;

/**
 * Build the model for the Assessment Application Summary view.
 */
@Component
public class AssessmentSummaryModelPopulator {

    @Autowired
    private AssessorFormInputResponseRestService assessorFormInputResponseRestService;

    @Autowired
    private CompetitionService competitionService;

    public AssessmentSummaryViewModel populateModel(AssessmentResource assessment) {
        CompetitionResource competition = getCompetition(assessment.getCompetition());
        List<AssessmentSummaryQuestionViewModel> questionsViewModel = getQuestionsViewModel(assessment.getId());

        return new AssessmentSummaryViewModel(assessment, competition, questionsViewModel);
    }

    private CompetitionResource getCompetition(long competitionId) {
        return competitionService.getById(competitionId);
    }

    private List<AssessmentSummaryQuestionViewModel> getQuestionsViewModel(long assessmentId) {
        final AssessmentDetailsResource assessmentDetailsResource = assessorFormInputResponseRestService.getAssessmentDetails(assessmentId).getSuccessObjectOrThrowException();

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
