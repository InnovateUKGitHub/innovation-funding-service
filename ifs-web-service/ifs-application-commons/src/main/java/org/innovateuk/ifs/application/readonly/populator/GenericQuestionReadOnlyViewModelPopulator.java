package org.innovateuk.ifs.application.readonly.populator;

import org.innovateuk.ifs.application.readonly.ApplicationReadOnlyData;
import org.innovateuk.ifs.application.readonly.ApplicationReadOnlySettings;
import org.innovateuk.ifs.application.readonly.viewmodel.GenericQuestionFileViewModel;
import org.innovateuk.ifs.application.readonly.viewmodel.GenericQuestionReadOnlyViewModel;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.assessment.resource.AssessorFormInputResponseResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.user.resource.Role;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static org.hibernate.validator.internal.util.CollectionHelper.asSet;
import static org.innovateuk.ifs.form.resource.FormInputType.*;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.*;

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

        Optional<AssessorFormInputResponseResource> feedback = Optional.empty();
        Optional<AssessorFormInputResponseResource> score = Optional.empty();
        if (settings.isIncludeAssessment()) {
            Optional<Collection<AssessorFormInputResponseResource>> responses = Optional.ofNullable(data.getQuestionToAssessorResponse().get(question.getId()));

            feedback = responses.flatMap(resps ->
                    resps.stream()
                            .filter(resp -> data.getFormInputIdToAssessorFormInput().get(resp.getFormInput()).getType().equals(TEXTAREA))
                            .findAny());

            score = responses.flatMap(resps ->
                    resps.stream()
                            .filter(resp -> data.getFormInputIdToAssessorFormInput().get(resp.getFormInput()).getType().equals(ASSESSOR_SCORE))
                            .findAny());
        }

        return new GenericQuestionReadOnlyViewModel(data, question, questionName(question),
                question.getName(),
                answerInput.map(input -> input.getType().equals(FormInputType.MULTIPLE_CHOICE)
                        ? textResponse.map(FormInputResponseResource::getMultipleChoiceOptionText).orElse(null)
                        : textResponse.map(FormInputResponseResource::getValue).orElse(null)).orElse(null),
                appendixResponse.map(resp -> files(resp, question, data, settings)).orElse(Collections.emptyList()),
                templateDocumentResponse.flatMap(resp -> files(resp, question, data, settings).stream().findFirst()).orElse(null),
                templateDocument.map(FormInputResource::getDescription).orElse(null),
                feedback.map(AssessorFormInputResponseResource::getValue).orElse(null),
                score.map(AssessorFormInputResponseResource::getValue).orElse(null)
            );
    }

    private List<GenericQuestionFileViewModel> files(FormInputResponseResource response, QuestionResource question, ApplicationReadOnlyData data, ApplicationReadOnlySettings settings) {
        return response.getFileEntries().stream()
                .map(file -> new GenericQuestionFileViewModel(file.getId(),
                        file.getName(),
                        urlForFormInputDownload(response.getFormInput(), file.getId(), question, data, settings)
                )).collect(Collectors.toList());
    }

    private String urlForFormInputDownload(long formInputId, long fileEntryId, QuestionResource question, ApplicationReadOnlyData data, ApplicationReadOnlySettings settings) {
        if (data.getApplicantProcessRole().isPresent() || data.getUser().hasRole(Role.MONITORING_OFFICER)) {
            return String.format("/application/%d/form/question/%d/forminput/%d/file/%d/download", data.getApplication().getId(), question.getId(), formInputId, fileEntryId);
        } else if (data.getUser().hasRole(Role.ASSESSOR) && settings.isIncludeAssessment()) {
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
        return asSet(ASSESSED_QUESTION, SCOPE, PUBLIC_DESCRIPTION, PROJECT_SUMMARY, EQUALITY_DIVERSITY_INCLUSION);
    }
}
