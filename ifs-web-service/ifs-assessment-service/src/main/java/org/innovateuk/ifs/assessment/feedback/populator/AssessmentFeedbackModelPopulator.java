package org.innovateuk.ifs.assessment.feedback.populator;

import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.assessment.common.service.AssessmentService;
import org.innovateuk.ifs.assessment.feedback.viewmodel.AssessmentFeedbackViewModel;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.GuidanceRowResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.file.controller.viewmodel.FileDetailsViewModel;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.commons.rest.RestResult.aggregate;
import static org.innovateuk.ifs.form.resource.FormInputScope.APPLICATION;
import static org.innovateuk.ifs.form.resource.FormInputScope.ASSESSMENT;
import static org.innovateuk.ifs.form.resource.FormInputType.*;
import static org.innovateuk.ifs.util.CollectionFunctions.*;

/**
 * Build the model for Assessment Feedback view.
 */
@Component
public class AssessmentFeedbackModelPopulator extends AssessmentModelPopulator<AssessmentFeedbackViewModel> {

    @Autowired
    private AssessmentService assessmentService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private FormInputRestService formInputRestService;

    @Autowired
    private FormInputResponseRestService formInputResponseRestService;

    @Autowired
    private CategoryRestService categoryRestService;

    @Override
    public AssessmentFeedbackViewModel populate(long assessmentId, QuestionResource question) {
        AssessmentResource assessment = getAssessment(assessmentId);
        CompetitionResource competition = getCompetition(assessment.getCompetition());

        List<FormInputResource> applicationFormInputs = getApplicationFormInputs(question.getId());
        Map<Long, FormInputResponseResource> applicantResponses = getApplicantResponses(
                assessment.getApplication(),
                applicationFormInputs);
        List<FormInputResource> assessmentFormInputs = getAssessmentFormInputs(question.getId());


        List<ResearchCategoryResource> researchCategories =
                findFormInputWithType(assessmentFormInputs, ASSESSOR_RESEARCH_CATEGORY).
                        map(fi -> categoryRestService.getResearchCategories().getSuccess()).
                        orElse(null);

        String applicantResponseValue = getApplicantResponseValue(applicationFormInputs, applicantResponses);
        List<FileDetailsViewModel> appendixDetails = getAppendixDetails(applicationFormInputs, applicantResponses);
        FileDetailsViewModel templateDocumentDetails = getTemplateDocumentDetails(applicationFormInputs, applicantResponses);
        String templateDocumentTitle = findFormInputWithType(applicationFormInputs, TEMPLATE_DOCUMENT)
                .map(FormInputResource::getDescription)
                .orElse(null);

        return new AssessmentFeedbackViewModel(assessment,
                competition,
                question,
                applicantResponseValue,
                formatGuidanceScores(assessmentFormInputs),
                findFormInputWithType(assessmentFormInputs, ASSESSOR_SCORE).isPresent(),
                findFormInputWithType(assessmentFormInputs, ASSESSOR_APPLICATION_IN_SCOPE).isPresent(),
                appendixDetails,
                templateDocumentDetails,
                templateDocumentTitle,
                researchCategories);
    }


    private AssessmentResource getAssessment(long assessmentId) {
        return assessmentService.getById(assessmentId);
    }

    private CompetitionResource getCompetition(long competitionId) {
        return competitionRestService.getCompetitionById(competitionId).getSuccess();
    }

    private List<FileDetailsViewModel> getAppendixDetails(List<FormInputResource> applicationFormInputs,
                                                    Map<Long, FormInputResponseResource> applicantResponses) {

        return findFormInputWithType(applicationFormInputs, FILEUPLOAD).map(appendixFormInput -> {
            FormInputResponseResource applicantAppendixResponse = applicantResponses.get(appendixFormInput.getId());
            boolean applicantAppendixResponseExists = applicantAppendixResponse != null && !applicantAppendixResponse.getFileEntries().isEmpty();
            if (!applicantAppendixResponseExists) {
                return new ArrayList<FileDetailsViewModel>();
            }
            return applicantAppendixResponse.getFileEntries().stream()
                    .map(file -> new FileDetailsViewModel(appendixFormInput.getId(),
                            file.getId(),
                            file.getName(),
                            file.getFilesizeBytes()))
                    .collect(toList());
        }).orElse(new ArrayList<>());
    }

    private FileDetailsViewModel getTemplateDocumentDetails(List<FormInputResource> applicationFormInputs,
                                                    Map<Long, FormInputResponseResource> applicantResponses) {

        return findFormInputWithType(applicationFormInputs, TEMPLATE_DOCUMENT).map(appendixFormInput -> {
            FormInputResponseResource applicantAppendixResponse = applicantResponses.get(appendixFormInput.getId());
            boolean applicantAppendixResponseExists = applicantAppendixResponse != null && !applicantAppendixResponse.getFileEntries().isEmpty();
            return applicantAppendixResponseExists ? new FileDetailsViewModel(appendixFormInput.getId(),
                    applicantAppendixResponse.getFileEntries().get(0).getId(),
                    applicantAppendixResponse.getFileEntries().get(0).getName(),
                    applicantAppendixResponse.getFileEntries().get(0).getFilesizeBytes()) : null;
        }).orElse(null);
    }

    private String getApplicantResponseValue(List<FormInputResource> applicationFormInputs, Map<Long, FormInputResponseResource> applicantResponses) {
        String applicantResponseValue = applicationFormInputs.stream()
                .filter(formInput -> formInput.getType().equals(TEXTAREA) || formInput.getType().equals(MULTIPLE_CHOICE))
                .map(input -> applicantResponses.entrySet().stream()
                        .filter(applicantResponse -> applicantResponse.getKey().equals(input.getId()))
                        .map(Map.Entry::getValue)
                        .map(formInputResponse -> input.getType().equals(MULTIPLE_CHOICE)
                                ? formInputResponse.getMultipleChoiceOptionText()
                                : formInputResponse.getValue()).findFirst().orElse(null))
                .findFirst()
                .orElse(null);

        return applicantResponseValue;
    }

    private List<FormInputResource> getApplicationFormInputs(Long questionId) {
        return formInputRestService.getByQuestionIdAndScope(questionId, APPLICATION).getSuccess();
    }

    private List<FormInputResource> getAssessmentFormInputs(Long questionId) {
        return formInputRestService.getByQuestionIdAndScope(questionId, ASSESSMENT).getSuccess();
    }

    private Map<Long, FormInputResponseResource> getApplicantResponses(Long applicationId, List<FormInputResource> applicationFormInputs) {
        RestResult<List<List<FormInputResponseResource>>> applicantResponses = aggregate(applicationFormInputs
                .stream()
                .map(formInput -> formInputResponseRestService.getByFormInputIdAndApplication(formInput.getId(), applicationId))
                .collect(toList()));
        return simpleToMap(
                flattenLists(applicantResponses.getSuccess()),
                FormInputResponseResource::getFormInput
        );
    }

    private Optional<FormInputResource> findFormInputWithType(List<FormInputResource> formInputs, FormInputType type) {
        return simpleFindFirst(formInputs, formInput -> type.equals(formInput.getType()));
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
