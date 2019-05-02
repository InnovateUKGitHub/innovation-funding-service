package org.innovateuk.ifs.application.summary.populator;

import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.application.summary.ApplicationSummaryData;
import org.innovateuk.ifs.application.summary.viewmodel.GenericQuestionSummaryViewModel;
import org.innovateuk.ifs.application.summary.viewmodel.NewQuestionSummaryViewModel;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import static org.hibernate.validator.internal.util.CollectionHelper.asSet;

@Component
public class GenericQuestionSummaryViewModelPopulator implements QuestionSummaryViewModelPopulator {

    @Override
    public NewQuestionSummaryViewModel populate(QuestionResource question, ApplicationSummaryData data) {
        Collection<FormInputResource> formInputs = data.getQuestionIdToFormInputs().get(question.getId());
        Optional<FormInputResource> textInput = formInputs.stream().filter(formInput -> FormInputType.TEXTAREA.equals(formInput.getType()))
                .findAny();

        Optional<FormInputResource> appendix = formInputs.stream().filter(formInput -> FormInputType.FILEUPLOAD.equals(formInput.getType()))
                .findAny();

        Optional<FormInputResponseResource> textResponse = textInput
                .map(input -> data.getFormInputIdToFormInputResponses().get(input.getId()));

        Optional<FormInputResponseResource> appendixResponse = appendix
                .map(input -> data.getFormInputIdToFormInputResponses().get(input.getId()));

        return new GenericQuestionSummaryViewModel(data, question, questionName(question),
                question.getName(),
                textResponse.map(FormInputResponseResource::getValue).orElse(null),
                appendixResponse.map(FormInputResponseResource::getFilename).orElse(null),
                appendixResponse.map(FormInputResponseResource::getFormInput).orElse(null)
        );
    }

    private String questionName(QuestionResource question) {
        return question.getQuestionSetupType() == QuestionSetupType.ASSESSED_QUESTION ?
                String.format("%s. %s", question.getQuestionNumber(), question.getShortName()) :
                question.getShortName();
    }

    @Override
    public Set<QuestionSetupType> questionTypes() {
        return asSet(QuestionSetupType.ASSESSED_QUESTION, QuestionSetupType.SCOPE, QuestionSetupType.PUBLIC_DESCRIPTION, QuestionSetupType.PROJECT_SUMMARY);
    }
}
