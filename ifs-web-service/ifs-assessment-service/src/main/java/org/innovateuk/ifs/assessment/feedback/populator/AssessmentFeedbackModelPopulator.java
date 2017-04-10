package org.innovateuk.ifs.assessment.feedback.populator;

import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.service.CategoryService;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.assessment.service.AssessmentService;
import org.innovateuk.ifs.assessment.feedback.viewmodel.AssessmentFeedbackViewModel;
import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.GuidanceRowResource;
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

import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.commons.rest.RestResult.aggregate;
import static org.innovateuk.ifs.form.resource.FormInputType.*;
import static org.innovateuk.ifs.util.CollectionFunctions.flattenLists;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleToMap;

/**
 * Build the model for Assessment Feedback view.
 */
@Component
public class AssessmentFeedbackModelPopulator {

    @Autowired
    private AssessmentService assessmentService;

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private FormInputService formInputService;

    @Autowired
    private FormInputResponseService formInputResponseService;

    @Autowired
    private CategoryService categoryService;

    public AssessmentFeedbackViewModel populateModel(Long assessmentId, QuestionResource question) {
        AssessmentResource assessment = getAssessment(assessmentId);
        CompetitionResource competition = getCompetition(assessment.getCompetition());
        List<FormInputResource> applicationFormInputs = getApplicationFormInputs(question.getId());
        Map<Long, FormInputResponseResource> applicantResponses = getApplicantResponses(assessment.getApplication(),
                applicationFormInputs);
        List<FormInputResource> assessmentFormInputs = getAssessmentFormInputs(question.getId());
        List<ResearchCategoryResource> researchCategories = hasFormInputWithType(assessmentFormInputs, ASSESSOR_RESEARCH_CATEGORY)
                ? categoryService.getResearchCategories()
                : null;

        String applicantResponseValue = getApplicantResponseValue(applicationFormInputs, applicantResponses);
        FileDetailsViewModel appendixDetails = getAppendixDetails(applicationFormInputs, applicantResponses);

        return new AssessmentFeedbackViewModel(assessment.getId(),
                competition.getAssessmentDaysLeft(),
                competition.getAssessmentDaysLeftPercentage(),
                assessment.getApplication(),
                assessment.getApplicationName(),
                question.getId(),
                question.getQuestionNumber(),
                question.getShortName(),
                question.getName(),
                question.getAssessorMaximumScore(),
                applicantResponseValue,
                formatGuidanceScores(assessmentFormInputs),
                hasFormInputWithType(assessmentFormInputs, ASSESSOR_SCORE),
                hasFormInputWithType(assessmentFormInputs, ASSESSOR_APPLICATION_IN_SCOPE),
                appendixDetails != null,
                appendixDetails,
                researchCategories);
    }


    private AssessmentResource getAssessment(Long assessmentId) {
        return assessmentService.getById(assessmentId);
    }

    private CompetitionResource getCompetition(Long competitionId) {
        return competitionService.getById(competitionId);
    }

    private FileDetailsViewModel getAppendixDetails(List<FormInputResource> applicationFormInputs,
                                                    Map<Long, FormInputResponseResource> applicantResponses) {
        FileDetailsViewModel appendixDetails = null;
        if (hasFormInputWithType(applicationFormInputs, FILEUPLOAD)) {

            FormInputResource appendixFormInput = applicationFormInputs.get(1);
            FormInputResponseResource applicantAppendixResponse = applicantResponses.get(appendixFormInput.getId());
            boolean applicantAppendixResponseExists = applicantAppendixResponse != null;
            if (applicantAppendixResponseExists) {
                appendixDetails = new FileDetailsViewModel(appendixFormInput.getId(),
                        applicantAppendixResponse.getFilename(),
                        applicantAppendixResponse.getFilesizeBytes());
            }
        }
        return appendixDetails;
    }

    private String getApplicantResponseValue(List<FormInputResource> applicationFormInputs, Map<Long, FormInputResponseResource> applicantResponses) {
        FormInputResponseResource applicantResponse = applicantResponses.get(applicationFormInputs.get(0).getId());
        return applicantResponse != null ? applicantResponse.getValue() : null;
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

    private List<FormInputResource> formatGuidanceScores(List<FormInputResource> assessorInputs) {
        if (assessorInputs != null) {
            for (FormInputResource input : assessorInputs) {
                if (TEXTAREA.equals(input.getType()) && input.getGuidanceRows() != null) {
                    for (GuidanceRowResource row : input.getGuidanceRows()) {
                        row.setSubject(row.getSubject().replace(",", " to "));
                    }
                }
            }
        }

        return assessorInputs;
    }
}
