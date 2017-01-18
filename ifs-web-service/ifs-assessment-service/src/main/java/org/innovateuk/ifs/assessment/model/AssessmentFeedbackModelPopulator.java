package org.innovateuk.ifs.assessment.model;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.CategoryService;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.assessment.service.AssessmentService;
import org.innovateuk.ifs.assessment.viewmodel.AssessmentFeedbackViewModel;
import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.file.controller.viewmodel.FileDetailsViewModel;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputResponseResource;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.service.FormInputResponseService;
import org.innovateuk.ifs.form.service.FormInputService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static org.innovateuk.ifs.commons.rest.RestResult.aggregate;
import static org.innovateuk.ifs.form.resource.FormInputType.*;
import static org.innovateuk.ifs.util.CollectionFunctions.flattenLists;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleToMap;
import static java.util.stream.Collectors.toList;

/**
 * Build the model for Assessment Feedback view.
 */
@Component
public class AssessmentFeedbackModelPopulator {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private AssessmentService assessmentService;

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private FormInputService formInputService;

    @Autowired
    private FormInputResponseService formInputResponseService;

    @Autowired
    private CategoryService categoryService;

    public AssessmentFeedbackViewModel populateModel(Long assessmentId, QuestionResource question) {
        AssessmentResource assessment = getAssessment(assessmentId);
        ApplicationResource application = getApplication(assessment.getApplication());
        CompetitionResource competition = getCompetition(application.getCompetition());
        List<FormInputResource> applicationFormInputs = getApplicationFormInputs(question.getId());
        FormInputResource applicationFormInput = applicationFormInputs.get(0);
        Map<Long, FormInputResponseResource> applicantResponses = getApplicantResponses(application.getId(), applicationFormInputs);
        FormInputResponseResource applicantResponse = applicantResponses.get(applicationFormInput.getId());
        String applicantResponseValue = applicantResponse != null ? applicantResponse.getValue() : null;
        List<FormInputResource> assessmentFormInputs = getAssessmentFormInputs(question.getId());
        boolean appendixFormInputExists = hasFormInputWithType(applicationFormInputs, FILEUPLOAD);
        boolean scoreFormInputExists = hasFormInputWithType(assessmentFormInputs, ASSESSOR_SCORE);
        boolean scopeFormInputExists = hasFormInputWithType(assessmentFormInputs, ASSESSOR_APPLICATION_IN_SCOPE);
        List<ResearchCategoryResource> researchCategories = scopeFormInputExists ? categoryService.getResearchCategories() : null;

        if (appendixFormInputExists) {
            FormInputResource appendixFormInput = applicationFormInputs.get(1);
            FormInputResponseResource applicantAppendixResponse = applicantResponses.get(appendixFormInput.getId());
            boolean applicantAppendixResponseExists = applicantAppendixResponse != null;
            if (applicantAppendixResponseExists) {
                FileDetailsViewModel appendixDetails = new FileDetailsViewModel(applicantAppendixResponse.getFilename(), applicantAppendixResponse.getFilesizeBytes());
                return new AssessmentFeedbackViewModel(competition.getAssessmentDaysLeft(), competition.getAssessmentDaysLeftPercentage(), competition, application, question.getId(), question.getQuestionNumber(), question.getShortName(), question.getName(), question.getAssessorMaximumScore(), applicantResponseValue, assessmentFormInputs, scoreFormInputExists, scopeFormInputExists, true, appendixDetails, researchCategories);
            }
        }

        return new AssessmentFeedbackViewModel(competition.getAssessmentDaysLeft(), competition.getAssessmentDaysLeftPercentage(), competition, application, question.getId(), question.getQuestionNumber(), question.getShortName(), question.getName(), question.getAssessorMaximumScore(), applicantResponseValue, assessmentFormInputs, scoreFormInputExists, scopeFormInputExists, researchCategories);
    }

    private AssessmentResource getAssessment(Long assessmentId) {
        return assessmentService.getById(assessmentId);
    }

    private ApplicationResource getApplication(Long applicationId) {
        return applicationService.getById(applicationId);
    }

    private CompetitionResource getCompetition(Long competitionId) {
        return competitionService.getById(competitionId);
    }

    private QuestionResource getQuestion(Long questionId) {
        return questionService.getById(questionId);
    }

    private List<FormInputResource> getApplicationFormInputs(Long questionId) {
        return formInputService.findApplicationInputsByQuestion(questionId);
    }

    private List<FormInputResource> getAssessmentFormInputs(Long questionId) {
        return formInputService.findAssessmentInputsByQuestion(questionId);
    }

    private Map<Long, FormInputResponseResource> getApplicantResponses(Long applicationId, List<FormInputResource> applicationFormInputs) {
        RestResult<List<List<FormInputResponseResource>>> applicantResponses = aggregate(applicationFormInputs
                .stream()
                .map(formInput -> formInputResponseService.getByFormInputIdAndApplication(formInput.getId(), applicationId))
                .collect(toList()));
        return simpleToMap(
                flattenLists(applicantResponses.getSuccessObjectOrThrowException()),
                FormInputResponseResource::getFormInput
        );
    }

    private boolean hasFormInputWithType(List<FormInputResource> formInputs, FormInputType type) {
        return formInputs.stream().anyMatch(formInput -> type == formInput.getType());
    }
}
