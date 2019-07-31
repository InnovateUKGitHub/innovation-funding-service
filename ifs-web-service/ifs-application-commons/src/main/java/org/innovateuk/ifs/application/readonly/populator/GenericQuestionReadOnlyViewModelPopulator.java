package org.innovateuk.ifs.application.readonly.populator;

import org.innovateuk.ifs.application.readonly.ApplicationReadOnlyData;
import org.innovateuk.ifs.application.readonly.viewmodel.GenericQuestionReadOnlyViewModel;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import static org.hibernate.validator.internal.util.CollectionHelper.asSet;
import static org.innovateuk.ifs.form.resource.FormInputType.FILEUPLOAD;
import static org.innovateuk.ifs.form.resource.FormInputType.TEMPLATE_DOCUMENT;
import static org.innovateuk.ifs.form.resource.FormInputType.TEXTAREA;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.*;

@Component
public class GenericQuestionReadOnlyViewModelPopulator implements QuestionReadOnlyViewModelPopulator<GenericQuestionReadOnlyViewModel> {

    @Override
    public GenericQuestionReadOnlyViewModel populate(QuestionResource question, ApplicationReadOnlyData data) {
        Collection<FormInputResource> formInputs = data.getQuestionIdToFormInputs().get(question.getId());
        Optional<FormInputResource> textInput = formInputs.stream().filter(formInput -> formInput.getType().equals(TEXTAREA))
                .findAny();

        Optional<FormInputResource> appendix = formInputs.stream().filter(formInput -> formInput.getType().equals(FILEUPLOAD))
                .findAny();

        Optional<FormInputResource> templateDocument = formInputs.stream().filter(formInput -> formInput.getType().equals(TEMPLATE_DOCUMENT))
                .findAny();

        Optional<FormInputResponseResource> textResponse = textInput
                .map(input -> data.getFormInputIdToFormInputResponses().get(input.getId()));

        Optional<FormInputResponseResource> appendixResponse = appendix
                .map(input -> data.getFormInputIdToFormInputResponses().get(input.getId()));

        Optional<FormInputResponseResource> templateDocumentResponse = templateDocument
                .map(input -> data.getFormInputIdToFormInputResponses().get(input.getId()));

        return new GenericQuestionReadOnlyViewModel(data, question, questionName(question),
                question.getName(),
                textResponse.map(FormInputResponseResource::getValue).orElse(null),
                appendixResponse.map(FormInputResponseResource::getFilename).orElse(null),
                appendixResponse.map(FormInputResponseResource::getFormInput).orElse(null),
                templateDocumentResponse.map(FormInputResponseResource::getFilename).orElse(null),
                templateDocument.map(FormInputResource::getDescription).orElse(null),
                templateDocumentResponse.map(FormInputResponseResource::getFormInput).orElse(null)
            );
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
