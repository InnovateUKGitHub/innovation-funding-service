package org.innovateuk.ifs.application.forms.questions.generic.populator;

import org.innovateuk.ifs.applicant.resource.ApplicantQuestionResource;
import org.innovateuk.ifs.application.forms.questions.generic.form.GenericQuestionApplicationForm;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GenericQuestionApplicationFormPopulator {

    public GenericQuestionApplicationForm populate(GenericQuestionApplicationForm form, ApplicantQuestionResource applicantQuestion) {

        String value = applicantQuestion.getApplicantFormInputs().stream()
                .filter(input -> input.getFormInput().getType().equals(FormInputType.TEXTAREA) ||
                        input.getFormInput().getType().equals(FormInputType.MULTIPLE_CHOICE))
                .findFirst()
                .map(input -> input.getApplicantResponses().stream().findAny()
                        .map(response -> {
                            String answer;
                            if (input.getFormInput().getType().equals(FormInputType.MULTIPLE_CHOICE)) {
                                form.setMultipleChoiceOptionsActive(true);
                                answer = response.getResponse().getMultipleChoiceOptionText();
                            } else {
                                form.setTextAreaActive(true);
                                answer = response.getResponse().getValue();
                            }
                            return answer;
                        }).orElse(null))
                .orElse(null);

        form.setAnswer(value);

        return form;
    }
}
