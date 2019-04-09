package org.innovateuk.ifs.application.summary.populator;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.application.summary.viewmodel.GenericQuestionSummaryViewModel;
import org.innovateuk.ifs.application.summary.viewmodel.NewQuestionSummaryViewModel;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputScope;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.hibernate.validator.internal.util.CollectionHelper.asSet;

@Component
public class GenericQuestionSummaryViewModelPopulator implements QuestionSummaryViewModelPopulator {

    @Autowired
    private FormInputRestService formInputRestService;

    @Autowired
    private FormInputResponseRestService formInputResponseRestService;

    @Override
    public NewQuestionSummaryViewModel populate(QuestionResource question, ApplicationResource application) {
        List<FormInputResource> formInputs = formInputRestService.getByQuestionIdAndScope(question.getId(), FormInputScope.APPLICATION).getSuccess();
        Optional<FormInputResource> textInput = formInputs.stream().filter(formInput -> FormInputType.TEXTAREA.equals(formInput.getType()))
                .findAny();

        Optional<FormInputResource> appendix = formInputs.stream().filter(formInput -> FormInputType.FILEUPLOAD.equals(formInput.getType()))
                .findAny();


        Optional<FormInputResponseResource> textResponse = textInput
                .flatMap(input -> formInputResponseRestService.getByFormInputIdAndApplication(input.getId(), application.getId()).getOptionalSuccessObject())
                .flatMap(inputs -> inputs.isEmpty() ? Optional.empty() : Optional.ofNullable(inputs.get(0)));

        Optional<FormInputResponseResource> appendixResponse = appendix
                .flatMap(input -> formInputResponseRestService.getByFormInputIdAndApplication(input.getId(), application.getId()).getOptionalSuccessObject())
                .flatMap(inputs -> inputs.isEmpty() ? Optional.empty() : Optional.ofNullable(inputs.get(0)));


        return new GenericQuestionSummaryViewModel(questionName(question),
                question.getName(),
                textResponse.map(FormInputResponseResource::getValue).orElse(null),
                appendixResponse.map(FormInputResponseResource::getFilename).orElse(null),
                application.getId(),
                question.getId(),
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
