package org.innovateuk.ifs.assessment.feedback.populator;

import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.assessment.common.service.AssessmentService;
import org.innovateuk.ifs.assessment.feedback.viewmodel.AssessmentFeedbackViewModel;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.GuidanceRowResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.file.controller.viewmodel.FileDetailsViewModel;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.ProcessRoleRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.comparator.BooleanComparator;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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

    @Autowired
    private ProcessRoleRestService processRoleRestService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Override
    public AssessmentFeedbackViewModel populate(long assessmentId, QuestionResource question) {
        AssessmentResource assessment = getAssessment(assessmentId);
        CompetitionResource competition = getCompetition(assessment.getCompetition());

        List<FormInputResource> applicationFormInputs = getApplicationFormInputs(question.getId());
        Map<Long, List<FormInputResponseResource>> applicantResponses = getApplicantResponses(
                assessment.getApplication(),
                applicationFormInputs);
        List<FormInputResource> assessmentFormInputs = getAssessmentFormInputs(question.getId());
        List<ProcessRoleResource> applicationProcessRoles = processRoleRestService.findProcessRole(assessment.getApplication()).getSuccess();

        List<ResearchCategoryResource> researchCategories =
                findFormInputWithType(assessmentFormInputs, ASSESSOR_RESEARCH_CATEGORY).
                        map(fi -> categoryRestService.getResearchCategories().getSuccess()).
                        orElse(null);

        boolean multipleStatuses = Boolean.TRUE.equals(question.hasMultipleStatuses());
        String applicantResponseValue;
        List<AssessmentFeedbackViewModel.ApplicantResponseViewModel> applicantResponseValues;
        if (multipleStatuses) {
            applicantResponseValue = null;
            applicantResponseValues = getApplicantResponseValues(applicationProcessRoles, applicationFormInputs, applicantResponses);
        } else {
            applicantResponseValue = getApplicantResponseValue(applicationFormInputs, applicantResponses);
            applicantResponseValues = Collections.emptyList();
        }
        List<FileDetailsViewModel> appendixDetails = getAppendixDetails(applicationFormInputs, applicantResponses);
        FileDetailsViewModel templateDocumentDetails = getTemplateDocumentDetails(applicationFormInputs, applicantResponses);
        String templateDocumentTitle = findFormInputWithType(applicationFormInputs, TEMPLATE_DOCUMENT)
                .map(FormInputResource::getDescription)
                .orElse(null);

        return new AssessmentFeedbackViewModel(assessment,
                competition,
                question,
                multipleStatuses,
                applicantResponseValue,
                applicantResponseValues,
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
                                                    Map<Long, List<FormInputResponseResource>> applicantResponses) {

        return findFormInputWithType(applicationFormInputs, FILEUPLOAD).map(appendixFormInput -> {
            List<FormInputResponseResource> applicantAppendixResponse = applicantResponses.get(appendixFormInput.getId());
            boolean applicantAppendixResponseExists = applicantAppendixResponse != null && !applicantAppendixResponse.isEmpty() && !applicantAppendixResponse.get(0).getFileEntries().isEmpty();
            if (!applicantAppendixResponseExists) {
                return new ArrayList<FileDetailsViewModel>();
            }
            return applicantAppendixResponse.get(0).getFileEntries().stream()
                    .map(file -> new FileDetailsViewModel(appendixFormInput.getId(),
                            file.getId(),
                            file.getName(),
                            file.getFilesizeBytes()))
                    .collect(toList());
        }).orElse(new ArrayList<>());
    }

    private FileDetailsViewModel getTemplateDocumentDetails(List<FormInputResource> applicationFormInputs,
                                                    Map<Long, List<FormInputResponseResource>> applicantResponses) {

        return findFormInputWithType(applicationFormInputs, TEMPLATE_DOCUMENT).map(appendixFormInput -> {
            List<FormInputResponseResource> applicantAppendixResponse = applicantResponses.get(appendixFormInput.getId());
            boolean applicantAppendixResponseExists = applicantAppendixResponse != null && !applicantAppendixResponse.isEmpty() && !applicantAppendixResponse.get(0).getFileEntries().isEmpty();
            return applicantAppendixResponseExists ? new FileDetailsViewModel(appendixFormInput.getId(),
                    applicantAppendixResponse.get(0).getFileEntries().get(0).getId(),
                    applicantAppendixResponse.get(0).getFileEntries().get(0).getName(),
                    applicantAppendixResponse.get(0).getFileEntries().get(0).getFilesizeBytes()) : null;
        }).orElse(null);
    }

    private String getApplicantResponseValue(List<FormInputResource> applicationFormInputs, Map<Long, List<FormInputResponseResource>> applicantResponses) {
        String applicantResponseValue = applicationFormInputs.stream()
                .filter(formInput -> formInput.getType().equals(TEXTAREA) || formInput.getType().equals(MULTIPLE_CHOICE))
                .map(input -> applicantResponses.entrySet().stream()
                        .filter(applicantResponse -> applicantResponse.getKey().equals(input.getId()))
                        .map(Map.Entry::getValue)
                        .map(formInputResponse -> input.getType().equals(MULTIPLE_CHOICE)
                                ? formInputResponse.get(0).getMultipleChoiceOptionText()
                                : formInputResponse.get(0).getValue()).findFirst().orElse(null))
                .findFirst()
                .orElse(null);

        return applicantResponseValue;
    }

    private List<AssessmentFeedbackViewModel.ApplicantResponseViewModel> getApplicantResponseValues(List<ProcessRoleResource> applicationProcessRoles,
                                                                                                    List<FormInputResource> applicationFormInputs,
                                                                                                    Map<Long, List<FormInputResponseResource>> applicantResponses) {
        List<ProcessRoleResource> applicantProcessRoles = applicationProcessRoles.stream()
                .filter(pr -> pr.getRole().isCollaborator() || pr.getRole().isLeadApplicant())
                .collect(Collectors.toList());

        List<Long> applicantOrgIds = applicantProcessRoles.stream()
                .filter(distinctByOrgId()).map(ProcessRoleResource::getOrganisationId)
                .collect(Collectors.toList());

        Map<Long, List<ProcessRoleResource>> processRoleResourcesGroupedByOrgId = applicantOrgIds.stream()
                .collect(Collectors.toMap(Function.identity(), orgId -> applicantProcessRoles.stream()
                        .filter(apr -> apr.getOrganisationId().equals(orgId))
                        .collect(Collectors.toList()))
                );

        return processRoleResourcesGroupedByOrgId.entrySet().stream()
                .map(entry -> {
                    Long orgId = entry.getKey();
                    List<ProcessRoleResource> orgProcessRoles = entry.getValue();
                    OrganisationResource organisation = organisationRestService.getOrganisationById(orgId).getSuccess();
                    boolean lead = orgProcessRoles.stream().anyMatch(pr -> pr.isLeadApplicant());
                    String answer = answerForOrg(orgProcessRoles, applicationFormInputs, applicantResponses);
                    return new AssessmentFeedbackViewModel.ApplicantResponseViewModel(organisation.getName(), lead, answer);
                }).sorted((a, b) -> BooleanComparator.TRUE_LOW.compare(a.isLead(), b.isLead()))
                .collect(Collectors.toList());
    }

    private String answerForOrg(List<ProcessRoleResource> orgProcessRoles,
                                List<FormInputResource> applicationFormInputs,
                                Map<Long, List<FormInputResponseResource>> applicantResponses) {

        return applicationFormInputs.stream()
                .filter(formInput -> formInput.getType().equals(TEXTAREA) || formInput.getType().equals(MULTIPLE_CHOICE))
                .map(input -> applicantResponses.entrySet().stream()
                        .filter(applicantResponse -> applicantResponse.getKey().equals(input.getId()))
                        .map(applicantResponse ->
                             applicantResponse.getValue().stream()
                                    .filter(resp -> orgProcessRoles.stream()
                                            .anyMatch(orgProcessRole -> orgProcessRole.getUser().equals(resp.getUpdatedByUser())))
                                    .findAny())
                        .filter(Optional::isPresent)
                        .map(formInputResponse -> input.getType().equals(MULTIPLE_CHOICE)
                                ? formInputResponse.get().getMultipleChoiceOptionText()
                                : formInputResponse.get().getValue()).findFirst().orElse(null))
                .findFirst()
                .orElse(null);
    }

    private Predicate<ProcessRoleResource> distinctByOrgId() {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return pr -> seen.putIfAbsent(pr.getOrganisationId(), Boolean.TRUE) == null;
    }

    private List<FormInputResource> getApplicationFormInputs(Long questionId) {
        return formInputRestService.getByQuestionIdAndScope(questionId, APPLICATION).getSuccess();
    }

    private List<FormInputResource> getAssessmentFormInputs(Long questionId) {
        return formInputRestService.getByQuestionIdAndScope(questionId, ASSESSMENT).getSuccess();
    }

    private Map<Long, List<FormInputResponseResource>> getApplicantResponses(Long applicationId, List<FormInputResource> applicationFormInputs) {
        List<List<FormInputResponseResource>> applicantResponses = aggregate(applicationFormInputs
                .stream()
                .map(formInput -> formInputResponseRestService.getByFormInputIdAndApplication(formInput.getId(), applicationId))
                .collect(toList())).getSuccess();
        return flattenLists(applicantResponses).stream().collect(Collectors.groupingBy(FormInputResponseResource::getFormInput));
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
