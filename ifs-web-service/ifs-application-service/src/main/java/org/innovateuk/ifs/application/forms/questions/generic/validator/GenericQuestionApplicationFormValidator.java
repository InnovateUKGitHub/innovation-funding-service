package org.innovateuk.ifs.application.forms.questions.generic.validator;

import org.innovateuk.ifs.application.forms.questions.generic.form.GenericQuestionApplicationForm;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

@Component
public class GenericQuestionApplicationFormValidator {

    public void validate(GenericQuestionApplicationForm form, BindingResult bindingResult) {
        validateTextArea(form, bindingResult);
        validateMultipleChoiceOptions(form, bindingResult);
    }

    private void validateTextArea(GenericQuestionApplicationForm form, BindingResult bindingResult) {
        if (form.isTextAreaActive() && form.getAnswer().trim().length() <= 0) {
            bindingResult.rejectValue("answer", "validation.field.please.enter.some.text");
        }
    }

    private void validateMultipleChoiceOptions(GenericQuestionApplicationForm form, BindingResult bindingResult) {
        if (form.isMultipleChoiceOptionsActive() && form.getAnswer().isEmpty()) {
            bindingResult.rejectValue("answer", "validation.multiple.choice.required");
        }
    }
}