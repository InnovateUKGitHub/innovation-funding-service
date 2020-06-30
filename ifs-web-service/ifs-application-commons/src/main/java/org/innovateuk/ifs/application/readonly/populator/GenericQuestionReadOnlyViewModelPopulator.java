package org.innovateuk.ifs.application.readonly.populator;

import org.innovateuk.ifs.application.readonly.ApplicationReadOnlyData;
import org.innovateuk.ifs.application.readonly.ApplicationReadOnlySettings;
import org.innovateuk.ifs.application.readonly.viewmodel.GenericQuestionReadOnlyViewModel;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.assessment.resource.AssessorFormInputResponseResource;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.MultipleChoiceOptionResource;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.user.resource.Role;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import static org.hibernate.validator.internal.util.CollectionHelper.asSet;
import static org.innovateuk.ifs.form.resource.FormInputType.*;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.*;

@Component
public class GenericQuestionReadOnlyViewModelPopulator implements QuestionReadOnlyViewModelPopulator<GenericQuestionReadOnlyViewModel> {

    @Override
    public GenericQuestionReadOnlyViewModel populate(QuestionResource question, ApplicationReadOnlyData data, ApplicationReadOnlySettings settings) {
        Collection<FormInputResource> formInputs = data.getQuestionIdToApplicationFormInputs().get(question.getId());
        Optional<FormInputResource> textInput = formInputs.stream().filter(formInput -> formInput.getType().equals(TEXTAREA)
                || formInput.getType().equals(MULTIPLE_CHOICE))
                .findAny();

        Optional<FormInputResource> appendix = formInputs.stream().filter(formInput -> formInput.getType().equals(FILEUPLOAD))
                .findAny();

        Optional<FormInputResource> templateDocument = formInputs.stream().filter(formInput -> formInput.getType().equals(TEMPLATE_DOCUMENT))
                .findAny();

        Optional<FormInputResponseResource> textResponse = textInput
                .map(input -> data.getFormInputIdToFormInputResponses().get(input.getId()));

        String answer = null;
        if (textInput.isPresent()) {
            FormInputResource input = textInput.get();
            if (input.getType().equals(TEXTAREA)) {
                answer = textResponse.map(FormInputResponseResource::getValue).orElse(null);
            } else {
                answer = textResponse.map(response -> input.getMultipleChoiceOptions().stream()
                        .filter(multipleChoice -> multipleChoice.getId().equals(Long.getLong(response.getValue())))
                        .findAny()
                        .map(MultipleChoiceOptionResource::getText).orElse(null))
                        .orElse(null);
            }
        }

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
                answer,
                appendixResponse.map(FormInputResponseResource::getFilename).orElse(null),
                appendixResponse.map(response -> urlForFormInputDownload(response.getFormInput(), question, data, settings)).orElse(null),
                appendixResponse.map(FormInputResponseResource::getFormInput).orElse(null),
                templateDocumentResponse.map(FormInputResponseResource::getFilename).orElse(null),
                templateDocumentResponse.map(response -> urlForFormInputDownload(response.getFormInput(), question, data, settings)).orElse(null),
                templateDocument.map(FormInputResource::getDescription).orElse(null),
                templateDocumentResponse.map(FormInputResponseResource::getFormInput).orElse(null),
                feedback.map(AssessorFormInputResponseResource::getValue).orElse(null),
                score.map(AssessorFormInputResponseResource::getValue).orElse(null)
            );
    }

    private String urlForFormInputDownload(long formInputId, QuestionResource question, ApplicationReadOnlyData data, ApplicationReadOnlySettings settings) {
        if (data.getApplicantProcessRole().isPresent()) {
            return String.format("/application/%d/form/question/%d/forminput/%d/download", data.getApplication().getId(), question.getId(), formInputId);
        } else if (data.getUser().hasRole(Role.ASSESSOR) && settings.isIncludeAssessment()) {
            return String.format("/assessment/%d/application/%d/formInput/%d/download", settings.getAssessmentId(), data.getApplication().getId(), formInputId);
        } else {
            return String.format("/management/competition/%d/application/%d/forminput/%d/download", data.getCompetition().getId(), data.getApplication().getId(), formInputId);
        }
    }

    private String questionName(QuestionResource question) {
        return question.getQuestionSetupType() == ASSESSED_QUESTION ?
                String.format("%s. %s", question.getQuestionNumber(), question.getShortName()) :
                question.getShortName();
    }

    @Override
    public Set<QuestionSetupType> questionTypes() {
        return asSet(ASSESSED_QUESTION, SCOPE, PUBLIC_DESCRIPTION, PROJECT_SUMMARY);
    }
}
