package org.innovateuk.ifs.application.readonly.populator;

import org.innovateuk.ifs.application.readonly.ApplicationReadOnlyData;
import org.innovateuk.ifs.application.readonly.ApplicationReadOnlySettings;
import org.innovateuk.ifs.application.readonly.viewmodel.GenericQuestionAnswerRowReadOnlyViewModel;
import org.innovateuk.ifs.application.readonly.viewmodel.GenericQuestionFileViewModel;
import org.innovateuk.ifs.application.readonly.viewmodel.GenericQuestionReadOnlyViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.application.service.QuestionStatusRestService;
import org.innovateuk.ifs.assessment.resource.ApplicationAssessmentResource;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.comparator.BooleanComparator;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toSet;
import static org.innovateuk.ifs.form.resource.FormInputType.*;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.*;
import static org.innovateuk.ifs.user.resource.ProcessRoleType.*;

@Component
public class GenericQuestionReadOnlyViewModelPopulator implements QuestionReadOnlyViewModelPopulator<GenericQuestionReadOnlyViewModel> {

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private QuestionStatusRestService questionStatusRestService;

    @Override
    public GenericQuestionReadOnlyViewModel populate(QuestionResource question, ApplicationReadOnlyData data, ApplicationReadOnlySettings settings) {
        Collection<FormInputResource> formInputs = data.getQuestionIdToApplicationFormInputs().get(question.getId());
        Optional<FormInputResource> answerInput = formInputs.stream().filter(formInput -> formInput.getType().equals(TEXTAREA)
                || formInput.getType().equals(MULTIPLE_CHOICE))
                .findAny();

        Optional<FormInputResource> appendix = formInputs.stream().filter(formInput -> formInput.getType().equals(FILEUPLOAD))
                .findAny();

        Optional<FormInputResource> templateDocument = formInputs.stream().filter(formInput -> formInput.getType().equals(TEMPLATE_DOCUMENT))
                .findAny();

        Map<Long, List<FormInputResponseResource>> formInputIdToFormInputResponses = data.getFormInputIdToFormInputResponses();
        boolean multipleStatuses = Boolean.TRUE.equals(question.hasMultipleStatuses());
        String answer;
        List<GenericQuestionAnswerRowReadOnlyViewModel> answers;

        if (multipleStatuses) {
            answer = null;
            answers = answerMapForMultipleStatuses(question, answerInput, formInputIdToFormInputResponses, data.getApplicationProcessRoles(), data.getApplication());
        } else {
            answers = null;
            answer = answerForNotMultipleStatuses(answerInput, formInputIdToFormInputResponses);
        }

        Optional<FormInputResponseResource> appendixResponse = appendix
                .map(input -> firstOrNull(formInputIdToFormInputResponses.get(input.getId())));

        Optional<FormInputResponseResource> templateDocumentResponse = templateDocument
                .map(input -> firstOrNull(formInputIdToFormInputResponses.get(input.getId())));

        return new GenericQuestionReadOnlyViewModel(data, question, questionName(question),
                question.getName(),
                multipleStatuses,
                answer,
                answers,
                settings.isIncludeStatuses(),
                appendixResponse.map(resp -> files(resp, question, data, settings)).orElse(Collections.emptyList()),
                templateDocumentResponse.flatMap(resp -> files(resp, question, data, settings).stream().findFirst()).orElse(null),
                templateDocument.map(FormInputResource::getDescription).orElse(null),
                allFeedback(data, question, settings),
                allScores(data, question, settings),
                inScope(data, settings),
                totalScope(data, settings),
                hasScope(data, question)
            );
    }

    private List<GenericQuestionAnswerRowReadOnlyViewModel> answerMapForMultipleStatuses(QuestionResource question,
                                                                                         Optional<FormInputResource> answerInput,
                                                                                         Map<Long, List<FormInputResponseResource>> formInputIdToFormInputResponses,
                                                                                         List<ProcessRoleResource> applicationProcessRoles,
                                                                                         ApplicationResource application) {

        Optional<List<FormInputResponseResource>> textResponses = answerInput.map(input -> formInputIdToFormInputResponses.get(input.getId()));

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
                .map(entry -> rowViewModel(question, application, entry.getKey(), entry.getValue(), answerInput, textResponses))
                .sorted((a, b) -> BooleanComparator.TRUE_LOW.compare(a.isLead(), b.isLead()))
                .collect(Collectors.toList());
    }

    private GenericQuestionAnswerRowReadOnlyViewModel rowViewModel(QuestionResource question,
                                                                   ApplicationResource application,
                                                                   Long organisationId,
                                                                   List<ProcessRoleResource> processRoles,
                                                                   Optional<FormInputResource> answerInput,
                                                                   Optional<List<FormInputResponseResource>> textResponses) {
        OrganisationResource org = organisationRestService.getOrganisationById(organisationId).getSuccess();
        String partnerName = org.getName();
        boolean lead = processRoles.stream().anyMatch(ProcessRoleResource::isLeadApplicant);

        List<QuestionStatusResource> questionStatusesForOrg = questionStatusRestService.findByQuestionAndApplicationAndOrganisation(question.getId(), application.getId(), organisationId).getSuccess();

        boolean markedAsComplete = questionStatusesForOrg != null && questionStatusesForOrg.stream().anyMatch(status -> Boolean.TRUE.equals(status.getMarkedAsComplete()));

        String answer = null;

        for (ProcessRoleResource pr: processRoles) {
            if (answerInput.isPresent() && textResponses.isPresent()) {
                Optional<FormInputResponseResource> respForPr = textResponses.get().stream().filter(resp -> resp.getUpdatedBy().equals(pr.getId())).findAny();
                if (respForPr.isPresent()) {
                    answer = getAnswer(answerInput.get(), respForPr.get());
                    break;
                }
            }
        }

        return new GenericQuestionAnswerRowReadOnlyViewModel(partnerName, lead, answer, markedAsComplete);
    }

    private Predicate<ProcessRoleResource> distinctByOrgId() {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return pr -> seen.putIfAbsent(pr.getOrganisationId(), Boolean.TRUE) == null;
    }

    private String answerForNotMultipleStatuses(Optional<FormInputResource> answerInput, Map<Long, List<FormInputResponseResource>> formInputIdToFormInputResponses) {
        Optional<FormInputResponseResource> textResponse = answerInput.map(input -> firstOrNull(formInputIdToFormInputResponses.get(input.getId())));
        if (textResponse.isPresent()) {
            return answerInput.map(input -> getAnswer(input, textResponse.get())).orElse(null);
        }
        return null;
    }

    private String getAnswer(FormInputResource input, FormInputResponseResource textResponse) {
        return input.getType().equals(FormInputType.MULTIPLE_CHOICE)
                ? textResponse.getMultipleChoiceOptionText()
                : textResponse.getValue();
    }

    private FormInputResponseResource firstOrNull(List<FormInputResponseResource> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    private List<String> allFeedback(ApplicationReadOnlyData data, QuestionResource question, ApplicationReadOnlySettings settings) {
        List<String> feedbackList = new ArrayList<>();
        if (settings.isIncludeAssessment()) {
            if (settings.getAssessmentId() != null) {
                ofNullable(data.getAssessmentToApplicationAssessment().get(settings.getAssessmentId()))
                        .map(ApplicationAssessmentResource::getFeedback)
                        .filter(feedbackMap -> feedbackMap.containsKey(question.getId()))
                        .map(feedbackMap -> feedbackMap.get(question.getId()))
                        .ifPresent(feedbackList::add);
            } else {
                data.getAssessmentToApplicationAssessment().values()
                        .stream()
                        .map(ApplicationAssessmentResource::getFeedback)
                        .filter(feedbackMap -> feedbackMap.containsKey(question.getId()))
                        .map(feedbackMap -> feedbackMap.get(question.getId()))
                        .forEach(feedbackList::add);
            }
        }

        return feedbackList;
    }

    private List<BigDecimal> allScores(ApplicationReadOnlyData data, QuestionResource question, ApplicationReadOnlySettings settings) {
        List<BigDecimal> scoresList = new ArrayList<>();

        if (settings.isIncludeAssessment()) {
            if (settings.getAssessmentId() != null) {
                ofNullable(data.getAssessmentToApplicationAssessment().get(settings.getAssessmentId()))
                        .map(ApplicationAssessmentResource::getScores)
                        .filter(scoresMap -> scoresMap.containsKey(question.getId()))
                        .map(scoresMap -> scoresMap.get(question.getId()))
                        .ifPresent(scoresList::add);
            } else {
                data.getAssessmentToApplicationAssessment().values()
                        .stream()
                        .map(ApplicationAssessmentResource::getScores)
                        .filter(scoresMap -> scoresMap.containsKey(question.getId()))
                        .map(scoresMap -> scoresMap.get(question.getId()))
                        .forEach(scoresList::add);
            }
        }

        return scoresList;
    }

    private Boolean hasScope(ApplicationReadOnlyData data, QuestionResource question) {
        return data.getFormInputIdToAssessorFormInput().values().stream().anyMatch(
                fi -> fi.getQuestion().equals(question.getId())
                        && fi.getType().equals(ASSESSOR_APPLICATION_IN_SCOPE)
        );
    }

    private int inScope(ApplicationReadOnlyData data, ApplicationReadOnlySettings settings) {
        int inScopeCount = 0;
        if (settings.isIncludeAssessment()) {
            if (settings.getAssessmentId() != null) {
                inScopeCount = ofNullable(data.getAssessmentToApplicationAssessment().get(settings.getAssessmentId()))
                        .map((ApplicationAssessmentResource::isInScope)).orElse(false) ? 1 : 0;
            } else {
                inScopeCount = (int) data.getAssessmentToApplicationAssessment().values()
                        .stream()
                        .map(ApplicationAssessmentResource::isInScope).filter(is -> is).count();
            }
        }

        return inScopeCount;
    }

    private int totalScope(ApplicationReadOnlyData data, ApplicationReadOnlySettings settings) {
        int totalScope = 0;

        if (settings.isIncludeAssessment()) {
            if (settings.getAssessmentId() != null) {
                totalScope = ofNullable(data.getAssessmentToApplicationAssessment().get(settings.getAssessmentId()))
                        .map((ApplicationAssessmentResource::isInScope)).orElse(false) ? 1 : 0;
            } else {
                totalScope = (int) data.getAssessmentToApplicationAssessment().values()
                        .stream()
                        .map(ApplicationAssessmentResource::isInScope).count();
            }
        }

        return totalScope;
    }

    private List<GenericQuestionFileViewModel> files(FormInputResponseResource response, QuestionResource question, ApplicationReadOnlyData data, ApplicationReadOnlySettings settings) {
        return response.getFileEntries().stream()
                .map(file -> new GenericQuestionFileViewModel(file.getId(),
                        file.getName(),
                        urlForFormInputDownload(response.getFormInput(), file.getId(), question, data, settings)
                )).collect(Collectors.toList());
    }

    private String urlForFormInputDownload(long formInputId, long fileEntryId, QuestionResource question, ApplicationReadOnlyData data, ApplicationReadOnlySettings settings) {
        boolean isApplicant = data.getUsersProcessRole().map(pr -> applicantProcessRoles().contains(pr.getRole())).orElse(false);
        boolean isKta = data.getUsersProcessRole().map(pr -> pr.getRole().isKta()).orElse(false);
        boolean isAssessor = data.getUsersProcessRole().map(pr -> pr.getRole() == ASSESSOR).orElse(false);
        boolean isInterviewAssessor = data.getUsersProcessRole().map(pr -> pr.getRole() == INTERVIEW_ASSESSOR).orElse(false);
        if (isApplicant || isKta || isInterviewAssessor || data.getUser().hasRole(Role.MONITORING_OFFICER) || data.getUser().hasRole(Role.SUPPORTER)) {
            return String.format("/application/%d/form/question/%d/forminput/%d/file/%d/download", data.getApplication().getId(), question.getId(), formInputId, fileEntryId);
        } else if (isAssessor && settings.isIncludeAssessment()) {
            return String.format("/assessment/%d/application/%d/formInput/%d/file/%d/download", settings.getAssessmentId(), data.getApplication().getId(), formInputId, fileEntryId);
        } else {
            return String.format("/management/competition/%d/application/%d/forminput/%d/file/%d/download", data.getCompetition().getId(), data.getApplication().getId(), formInputId, fileEntryId);
        }
    }

    private String questionName(QuestionResource question) {
        return question.getQuestionSetupType() == ASSESSED_QUESTION ?
                String.format("%s. %s", question.getQuestionNumber(), question.getShortName()) :
                question.getShortName();
    }

    @Override
    public Set<QuestionSetupType> questionTypes() {
        return stream(QuestionSetupType.values()).filter(QuestionSetupType::hasFormInputResponses).collect(toSet());
    }
}
