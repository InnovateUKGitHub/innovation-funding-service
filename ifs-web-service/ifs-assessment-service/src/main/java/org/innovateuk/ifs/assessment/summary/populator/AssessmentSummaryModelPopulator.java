package org.innovateuk.ifs.assessment.summary.populator;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.assessment.resource.AssessorFormInputResponseResource;
import org.innovateuk.ifs.assessment.common.service.AssessorFormInputResponseService;
import org.innovateuk.ifs.assessment.summary.viewmodel.AssessmentSummaryQuestionViewModel;
import org.innovateuk.ifs.assessment.summary.viewmodel.AssessmentSummaryViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.service.FormInputService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.groupingBy;
import static org.innovateuk.ifs.form.resource.FormInputType.*;
import static org.innovateuk.ifs.util.CollectionFunctions.*;

/**
 * Build the model for the Assessment Application Summary view.
 */
@Component
public class AssessmentSummaryModelPopulator {

    @Autowired
    private AssessorFormInputResponseService assessorFormInputResponseService;

    @Autowired
    private FormInputService formInputService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private CompetitionService competitionService;

    public AssessmentSummaryViewModel populateModel(AssessmentResource assessment) {
        CompetitionResource competition = getCompetition(assessment.getCompetition());
        List<AssessmentSummaryQuestionViewModel> questions = getQuestions(assessment.getId(), assessment.getCompetition());

        int totalScoreGiven = getTotalScoreGiven(questions);
        int totalScorePossible = getTotalScorePossible(questions);
        int totalScorePercentage = totalScorePossible == 0 ? 0 : Math.round(totalScoreGiven * 100.0f / totalScorePossible);

        return new AssessmentSummaryViewModel(assessment.getId(), assessment.getApplication(),
                assessment.getApplicationName(), competition.getAssessmentDaysLeft(),
                competition.getAssessmentDaysLeftPercentage(), questions, totalScoreGiven, totalScorePossible, totalScorePercentage);
    }

    private CompetitionResource getCompetition(Long competitionId) {
        return competitionService.getById(competitionId);
    }

    private List<AssessmentSummaryQuestionViewModel> getQuestions(Long assessmentId, Long competitionId) {
        Map<Long, List<FormInputResource>> assessmentFormInputs = getAssessmentFormInputs(competitionId);
        Map<Long, List<AssessorFormInputResponseResource>> assessorResponses = getAssessorResponses(assessmentId);
        List<QuestionResource> questions = simpleFilter(questionService.getQuestionsByAssessment(assessmentId), question -> assessmentFormInputs.containsKey(question.getId()));
        return simpleMap(questions, question -> {
            List<FormInputResource> formInputsForQuestion = ofNullable(assessmentFormInputs.get(question.getId())).orElse(emptyList());
            List<AssessorFormInputResponseResource> responsesForQuestion = ofNullable(assessorResponses.get(question.getId())).orElse(emptyList());
            Map<FormInputType, String> responsesByFieldType = getAssessorResponsesByFormInputType(formInputsForQuestion, responsesForQuestion);
            String displayLabel = getQuestionDisplayLabel(question);
            String displayLabelShort = getQuestionDisplayLabelShort(question);
            boolean scoreFormInputExists = formInputsForQuestion.stream().anyMatch(formInput -> ASSESSOR_SCORE == formInput.getType());
            Integer scoreGiven = ofNullable(responsesByFieldType.get(ASSESSOR_SCORE)).map(Integer::valueOf).orElse(null);
            Integer scorePossible = scoreFormInputExists ? question.getAssessorMaximumScore() : null;
            String feedback = responsesByFieldType.get(TEXTAREA);
            Boolean applicationInScope = ofNullable(responsesByFieldType.get(ASSESSOR_APPLICATION_IN_SCOPE)).map(Boolean::valueOf).orElse(null);
            boolean complete = isComplete(formInputsForQuestion, responsesForQuestion);
            return new AssessmentSummaryQuestionViewModel(question.getId(), displayLabel, displayLabelShort, scoreFormInputExists, scoreGiven, scorePossible, feedback, applicationInScope, complete);
        });
    }

    private Map<Long, List<AssessorFormInputResponseResource>> getAssessorResponses(Long assessmentId) {
        List<AssessorFormInputResponseResource> assessorResponses = assessorFormInputResponseService.getAllAssessorFormInputResponses(assessmentId);
        return assessorResponses.stream().collect(groupingBy(AssessorFormInputResponseResource::getQuestion));
    }

    private Map<FormInputType, String> getAssessorResponsesByFormInputType(List<FormInputResource> formInputs, List<AssessorFormInputResponseResource> responses) {
        Map<Long, FormInputResource> formInputsMap = simpleToMap(formInputs, FormInputResource::getId);
        return simpleToMap(simpleFilter(responses, response -> response.getValue() != null), response -> formInputsMap.get(response.getFormInput()).getType(), AssessorFormInputResponseResource::getValue);
    }

    private Map<Long, List<FormInputResource>> getAssessmentFormInputs(Long competitionId) {
        List<FormInputResource> assessmentFormInputs = formInputService.findAssessmentInputsByCompetition(competitionId);
        return assessmentFormInputs.stream().collect(groupingBy(FormInputResource::getQuestion));
    }

    private String getQuestionDisplayLabel(QuestionResource question) {
        return ofNullable(question.getQuestionNumber()).map(questionNumber -> format("%s. %s", questionNumber, question.getShortName())).orElse(question.getShortName());
    }

    private String getQuestionDisplayLabelShort(QuestionResource question) {
        return ofNullable(question.getQuestionNumber()).map(questionNumber -> format("Q%s", questionNumber)).orElse(StringUtils.EMPTY);
    }

    private boolean isComplete(List<FormInputResource> formInputs, List<AssessorFormInputResponseResource> responses) {
        Map<Long, AssessorFormInputResponseResource> responsesMap = simpleToMap(responses, AssessorFormInputResponseResource::getFormInput);
        return formInputs.stream().allMatch(formInput ->
                ofNullable(responsesMap.get(formInput.getId())).map(response -> StringUtils.isNotBlank(response.getValue())).orElse(false));
    }

    private int getTotalScoreGiven(List<AssessmentSummaryQuestionViewModel> questions) {
        return questions.stream()
                .filter(AssessmentSummaryQuestionViewModel::isScoreFormInputExists)
                .mapToInt(question -> ofNullable(question.getScoreGiven()).orElse(0))
                .sum();
    }

    private int getTotalScorePossible(List<AssessmentSummaryQuestionViewModel> questions) {
        return questions.stream()
                .filter(AssessmentSummaryQuestionViewModel::isScoreFormInputExists)
                .mapToInt(question -> ofNullable(question.getScorePossible()).orElse(0))
                .sum();
    }
}
