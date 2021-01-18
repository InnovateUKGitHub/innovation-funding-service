package org.innovateuk.ifs.application.readonly.populator;

import com.google.common.collect.ImmutableSet;
import org.innovateuk.ifs.application.readonly.ApplicationReadOnlyData;
import org.innovateuk.ifs.application.readonly.ApplicationReadOnlySettings;
import org.innovateuk.ifs.application.readonly.viewmodel.GenericQuestionFileViewModel;
import org.innovateuk.ifs.application.readonly.viewmodel.GenericQuestionReadOnlyViewModel;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.assessment.resource.ApplicationAssessmentResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.user.resource.Role;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.form.resource.FormInputType.*;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.*;
import static org.innovateuk.ifs.user.resource.Role.*;

@Component
public class GenericQuestionReadOnlyViewModelPopulator implements QuestionReadOnlyViewModelPopulator<GenericQuestionReadOnlyViewModel> {

    @Override
    public GenericQuestionReadOnlyViewModel populate(CompetitionResource competition, QuestionResource question, ApplicationReadOnlyData data, ApplicationReadOnlySettings settings) {
        Collection<FormInputResource> formInputs = data.getQuestionIdToApplicationFormInputs().get(question.getId());
        Optional<FormInputResource> answerInput = formInputs.stream().filter(formInput -> formInput.getType().equals(TEXTAREA)
                || formInput.getType().equals(MULTIPLE_CHOICE))
                .findAny();

        Optional<FormInputResource> appendix = formInputs.stream().filter(formInput -> formInput.getType().equals(FILEUPLOAD))
                .findAny();

        Optional<FormInputResource> templateDocument = formInputs.stream().filter(formInput -> formInput.getType().equals(TEMPLATE_DOCUMENT))
                .findAny();

        Optional<FormInputResponseResource> textResponse = answerInput
                .map(input -> data.getFormInputIdToFormInputResponses().get(input.getId()));

        Optional<FormInputResponseResource> appendixResponse = appendix
                .map(input -> data.getFormInputIdToFormInputResponses().get(input.getId()));

        Optional<FormInputResponseResource> templateDocumentResponse = templateDocument
                .map(input -> data.getFormInputIdToFormInputResponses().get(input.getId()));

        return new GenericQuestionReadOnlyViewModel(data, question, questionName(question),
                question.getName(),
                answerInput.map(input -> input.getType().equals(FormInputType.MULTIPLE_CHOICE)
                        ? textResponse.map(FormInputResponseResource::getMultipleChoiceOptionText).orElse(null)
                        : textResponse.map(FormInputResponseResource::getValue).orElse(null)).orElse(null),
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
        boolean isKta = data.getUsersProcessRole().map(pr -> pr.getRole() == KNOWLEDGE_TRANSFER_ADVISER).orElse(false);
        boolean isAssessor = data.getUsersProcessRole().map(pr -> pr.getRole() == ASSESSOR).orElse(false);
        boolean isInterviewAssessor = data.getUsersProcessRole().map(pr -> pr.getRole() == INTERVIEW_ASSESSOR).orElse(false);
        if (isApplicant || isKta || isInterviewAssessor || data.getUser().hasRole(Role.MONITORING_OFFICER) || data.getUser().hasRole(SUPPORTER)) {
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
        return ImmutableSet.of(ASSESSED_QUESTION, SCOPE, PUBLIC_DESCRIPTION, PROJECT_SUMMARY, EQUALITY_DIVERSITY_INCLUSION, KTP_ASSESSMENT);
    }
}
