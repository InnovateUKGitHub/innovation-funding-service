package org.innovateuk.ifs.application.validator;

import org.innovateuk.ifs.application.domain.FormInputResponse;
import org.innovateuk.ifs.form.domain.MultipleChoiceOption;
import org.innovateuk.ifs.form.repository.MultipleChoiceOptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.util.Optional;

import static org.innovateuk.ifs.commons.error.ValidationMessages.rejectValue;

/**
 * This class validates the FormInputResponse, it checks if there is a value present.
 */
@Component
public class RequiredMultipleChoiceValidator extends BaseValidator {
    @Autowired
    private MultipleChoiceOptionRepository multipleChoiceOptionRepository;

    @Override
    public void validate(Object target, Errors errors) {
        FormInputResponse response = (FormInputResponse) target;

        if (response.getValue() == null) {
            rejectValue(errors, "value", "validation.multiple.choice.required");
            return;
        }

        Optional<MultipleChoiceOption> option = multipleChoiceOptionRepository.findById(Long.valueOf(response.getValue()));

        if (!(option.isPresent() && option.get().getFormInput().getId().equals(response.getFormInput().getId()))) {
            rejectValue(errors, "value", "validation.multiple.choice.invalid");
        }

    }
}
