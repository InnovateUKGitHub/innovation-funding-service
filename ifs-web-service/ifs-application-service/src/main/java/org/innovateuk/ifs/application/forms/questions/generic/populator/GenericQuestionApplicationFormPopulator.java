package org.innovateuk.ifs.application.forms.questions.generic.populator;

import org.innovateuk.ifs.applicant.resource.ApplicantQuestionResource;
import org.innovateuk.ifs.application.forms.questions.generic.form.GenericQuestionApplicationForm;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.springframework.stereotype.Component;

@Component
public class GenericQuestionApplicationFormPopulator {

    public GenericQuestionApplicationForm populate(GenericQuestionApplicationForm form, ApplicantQuestionResource applicantQuestion) {
        String value = applicantQuestion.getApplicantFormInputs().stream()
                .filter(input -> input.getFormInput().getType().equals(FormInputType.TEXTAREA))
                .findFirst()
                .flatMap(input -> input.getApplicantResponses().stream().findAny())
                .map(response -> response.getResponse().getValue())
                .orElse(null);
        form.setAnswer(value);
        return form;
    }

}
