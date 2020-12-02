package org.innovateuk.ifs.application.forms.questions.generic.populator;

import org.innovateuk.ifs.applicant.resource.ApplicantFormInputResource;
import org.innovateuk.ifs.applicant.resource.ApplicantQuestionResource;
import org.innovateuk.ifs.application.forms.questions.generic.form.GenericQuestionApplicationForm;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GenericQuestionApplicationFormPopulator {

    public GenericQuestionApplicationForm populate(GenericQuestionApplicationForm form, ApplicantQuestionResource applicantQuestion) {

        Optional<ApplicantFormInputResource> applicantFormInput = applicantQuestion.getApplicantFormInputs().stream()
                .filter(input -> input.getFormInput().getType().equals(FormInputType.TEXTAREA)
                        || input.getFormInput().getType().equals(FormInputType.MULTIPLE_CHOICE))
                .findFirst();

        String value = applicantFormInput
                .filter(input -> input.getFormInput().getType().equals(FormInputType.TEXTAREA))
                .map(input -> input.getApplicantResponses().stream().findAny()
                        .map(response -> {
                            form.setTextAreaActive(true);
                            return response.getResponse().getValue();
                        }).orElse(null))
                .orElse(null);
        form.setAnswer(value);

        Long multipleChoiceOptionId = applicantFormInput
                .filter(input -> input.getFormInput().getType().equals(FormInputType.MULTIPLE_CHOICE))
                .map(input -> input.getApplicantResponses().stream().findAny()
                        .map((response) -> {
                            form.setMultipleChoiceOptionsActive(true);
                            return response.getResponse().getMultipleChoiceOptionId();
                        }).orElse(null))
                .orElse(null);
        form.setMultipleChoiceOptionId(multipleChoiceOptionId);

        return form;
    }
}
