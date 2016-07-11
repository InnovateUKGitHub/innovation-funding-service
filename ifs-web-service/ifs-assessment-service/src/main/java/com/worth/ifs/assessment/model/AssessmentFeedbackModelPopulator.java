package com.worth.ifs.assessment.model;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.application.service.QuestionService;
import com.worth.ifs.assessment.viewmodel.AssessmentFeedbackViewModel;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.file.controller.viewmodel.FileDetailsViewModel;
import com.worth.ifs.form.resource.FormInputResource;
import com.worth.ifs.form.resource.FormInputResponseResource;
import com.worth.ifs.form.service.FormInputResponseService;
import com.worth.ifs.form.service.FormInputService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.worth.ifs.commons.rest.RestResult.aggregate;
import static com.worth.ifs.util.CollectionFunctions.flattenLists;
import static com.worth.ifs.util.CollectionFunctions.simpleToMap;

/**
 * Build the model for Assessment Feedback view.
 */
@Component
public class AssessmentFeedbackModelPopulator {

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private FormInputService formInputService;

    @Autowired
    private FormInputResponseService formInputResponseService;

    public AssessmentFeedbackViewModel populateModel(final ApplicationResource application, final Long questionId) {
        final CompetitionResource competition = getCompetition(application.getCompetition());
        final QuestionResource question = getQuestion(questionId);
        final boolean requireScore = question.isNeedingAssessorScore();
        final boolean requireFeedback = question.isNeedingAssessorFeedback();
        final boolean requireCategory = "Scope".equals(question.getShortName());
        final boolean requireScopeConfirmation = "Scope".equals(question.getShortName());
        final List<FormInputResource> questionFormInputs = getQuestionFormInputs(questionId);
        final boolean appendixFormInputExists = questionFormInputs.size() > 1;
        final FormInputResource questionFormInput = questionFormInputs.get(0);
        final Map<Long, FormInputResponseResource> questionFormInputResponses = getQuestionFormInputResponsesAsMap(getQuestionFormInputResponses(application.getId(), questionFormInputs));
        final FormInputResponseResource questionResponse = questionFormInputResponses.get(questionFormInput.getId());
        final String questionResponseValue = questionResponse != null ? questionResponse.getValue() : null;
        if (appendixFormInputExists) {
            final FormInputResource appendixFormInput = questionFormInputs.get(1);
            final FormInputResponseResource appendixResponse = questionFormInputResponses.get(appendixFormInput.getId());
            final boolean appendixResponseExists = appendixResponse != null;
            if (appendixResponseExists) {
                final FileDetailsViewModel appendixDetails = new FileDetailsViewModel(appendixResponse.getFilename(), appendixResponse.getFilesizeBytes());
                return new AssessmentFeedbackViewModel(competition.getAssessmentDaysLeft(), competition.getAssessmentDaysLeftPercentage(), competition, application, question.getId(), question.getQuestionNumber(), question.getShortName(), question.getName(), questionResponseValue, requireScore, requireFeedback, requireCategory, requireScopeConfirmation, question.getAssessorGuidanceQuestion(), question.getAssessorGuidanceAnswer(), true, appendixDetails);
            }
        }

        return new AssessmentFeedbackViewModel(competition.getAssessmentDaysLeft(), competition.getAssessmentDaysLeftPercentage(), competition, application, question.getId(), question.getQuestionNumber(), question.getShortName(), question.getName(), questionResponseValue, requireScore, requireFeedback, requireCategory, requireScopeConfirmation, question.getAssessorGuidanceQuestion(), question.getAssessorGuidanceAnswer());
    }

    private CompetitionResource getCompetition(final Long competitionId) {
        return competitionService.getById(competitionId);
    }

    private QuestionResource getQuestion(final Long questionId) {
        return questionService.getById(questionId);
    }

    private List<FormInputResource> getQuestionFormInputs(final Long questionId) {
        return formInputService.findByQuestion(questionId);
    }

    private Map<Long, FormInputResponseResource> getQuestionFormInputResponsesAsMap(final List<FormInputResponseResource> formInputResponses) {
        return simpleToMap(
                formInputResponses,
                response -> response.getFormInput()
        );
    }

    private List<FormInputResponseResource> getQuestionFormInputResponses(final Long applicationId, final List<FormInputResource> formInputs) {
        final RestResult<List<List<FormInputResponseResource>>> questionFormInputResponses = aggregate(formInputs
                .stream()
                .map(formInput -> formInputResponseService.getByFormInputIdAndApplication(formInput.getId(), applicationId))
                .collect(Collectors.toList()));
        return flattenLists(questionFormInputResponses.getSuccessObjectOrThrowException());
    }
}
