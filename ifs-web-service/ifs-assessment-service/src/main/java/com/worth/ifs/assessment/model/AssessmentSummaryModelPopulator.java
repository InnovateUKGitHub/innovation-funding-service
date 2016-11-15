package com.worth.ifs.assessment.model;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.application.service.ApplicationService;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.application.service.QuestionService;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.assessment.resource.AssessorFormInputResponseResource;
import com.worth.ifs.assessment.resource.AssessorFormInputType;
import com.worth.ifs.assessment.service.AssessmentService;
import com.worth.ifs.assessment.service.AssessorFormInputResponseService;
import com.worth.ifs.assessment.viewmodel.AssessmentSummaryQuestionViewModel;
import com.worth.ifs.assessment.viewmodel.AssessmentSummaryViewModel;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.form.resource.FormInputResource;
import com.worth.ifs.form.service.FormInputService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.worth.ifs.assessment.resource.AssessorFormInputType.*;
import static com.worth.ifs.util.CollectionFunctions.*;
import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.groupingBy;

/**
 * Build the model for the Assessment com.worth.ifs.Application Summary view.
 */
@Component
public class AssessmentSummaryModelPopulator {

    @Autowired
    private AssessorFormInputResponseService assessorFormInputResponseService;

    @Autowired
    private AssessmentService assessmentService;

    @Autowired
    private FormInputService formInputService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private CompetitionService competitionService;

    public AssessmentSummaryViewModel populateModel(Long assessmentId) {
        AssessmentResource assessment = getAssessment(assessmentId);
        ApplicationResource application = getApplication(assessment.getApplication());
        CompetitionResource competition = getCompetition(application.getCompetition());

        List<AssessmentSummaryQuestionViewModel> questions = getQuestions(assessmentId, competition.getId());
        List<AssessmentSummaryQuestionViewModel> questionsForScoreOverview = getQuestionsForScoreOverview(questions);

        int totalScoreGiven = getTotalScoreGiven(questions);
        int totalScorePossible = getTotalScorePossible(questions);
        int totalScorePercentage = totalScorePossible == 0 ? 0 : Math.round(totalScoreGiven * 100.0f / totalScorePossible);

        return new AssessmentSummaryViewModel(assessmentId, competition.getAssessmentDaysLeft(), competition.getAssessmentDaysLeftPercentage(), competition, application, questionsForScoreOverview, questions, totalScoreGiven, totalScorePossible, totalScorePercentage);
    }

    private CompetitionResource getCompetition(Long competitionId) {
        return competitionService.getById(competitionId);
    }

    private AssessmentResource getAssessment(Long assessmentId) {
        return assessmentService.getById(assessmentId);
    }

    private ApplicationResource getApplication(Long applicationId) {
        return applicationService.getById(applicationId);
    }

    private List<AssessmentSummaryQuestionViewModel> getQuestions(Long assessmentId, Long competitionId) {
        Map<Long, List<FormInputResource>> assessmentFormInputs = getAssessmentFormInputs(competitionId);
        Map<Long, List<AssessorFormInputResponseResource>> assessorResponses = getAssessorResponses(assessmentId);
        List<QuestionResource> questions = simpleFilter(questionService.getQuestionsByAssessment(assessmentId), question -> assessmentFormInputs.containsKey(question.getId()));
        return simpleMap(questions, question -> {
            List<FormInputResource> formInputsForQuestion = ofNullable(assessmentFormInputs.get(question.getId())).orElse(emptyList());
            List<AssessorFormInputResponseResource> responsesForQuestion = ofNullable(assessorResponses.get(question.getId())).orElse(emptyList());
            Map<AssessorFormInputType, String> responsesByFieldType = getAssessorResponsesByFormInputType(formInputsForQuestion, responsesForQuestion);
            String displayLabel = getQuestionDisplayLabel(question);
            String displayLabelShort = getQuestionDisplayLabelShort(question);
            boolean scoreFormInputExists = formInputsForQuestion.stream().anyMatch(formInput -> SCORE.getTitle().equals(formInput.getFormInputTypeTitle()));
            Integer scoreGiven = ofNullable(responsesByFieldType.get(SCORE)).map(Integer::valueOf).orElse(null);
            Integer scorePossible = scoreFormInputExists ? question.getAssessorMaximumScore() : null;
            String feedback = responsesByFieldType.get(FEEDBACK);
            Boolean applicationInScope = ofNullable(responsesByFieldType.get(APPLICATION_IN_SCOPE)).map(Boolean::valueOf).orElse(null);
            boolean complete = isComplete(formInputsForQuestion, responsesForQuestion);
            return new AssessmentSummaryQuestionViewModel(question.getId(), displayLabel, displayLabelShort, scoreFormInputExists, scoreGiven, scorePossible, feedback, applicationInScope, complete);
        });
    }

    private List<AssessmentSummaryQuestionViewModel> getQuestionsForScoreOverview(List<AssessmentSummaryQuestionViewModel> questions) {
        return simpleFilter(questions, AssessmentSummaryQuestionViewModel::isScoreFormInputExists);
    }

    private Map<Long, List<AssessorFormInputResponseResource>> getAssessorResponses(Long assessmentId) {
        List<AssessorFormInputResponseResource> assessorResponses = assessorFormInputResponseService.getAllAssessorFormInputResponses(assessmentId);
        return assessorResponses.stream().collect(groupingBy(AssessorFormInputResponseResource::getQuestion));
    }

    private Map<AssessorFormInputType, String> getAssessorResponsesByFormInputType(List<FormInputResource> formInputs, List<AssessorFormInputResponseResource> responses) {
        Map<Long, FormInputResource> formInputsMap = simpleToMap(formInputs, FormInputResource::getId);
        return simpleToMap(simpleFilter(responses, response -> response.getValue() != null), response -> getFormInputType(formInputsMap.get(response.getFormInput())), AssessorFormInputResponseResource::getValue);
    }

    private Map<Long, List<FormInputResource>> getAssessmentFormInputs(Long competitionId) {
        List<FormInputResource> assessmentFormInputs = formInputService.findAssessmentInputsByCompetition(competitionId);
        return assessmentFormInputs.stream().collect(groupingBy(FormInputResource::getQuestion));
    }

    private AssessorFormInputType getFormInputType(FormInputResource formInput) {
        return AssessorFormInputType.getByTitle(formInput.getFormInputTypeTitle());
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
