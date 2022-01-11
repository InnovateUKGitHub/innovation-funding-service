package org.innovateuk.ifs.application.validator;

import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.application.domain.FormInputResponse;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import static org.innovateuk.ifs.commons.error.ValidationMessages.rejectValue;

/**
 * This class validates the FormInputResponse, it checks if the maximum word count has been exceeded.
 */
@Slf4j
@Component
public class WordCountValidator extends BaseValidator {

    @Override
    public void validate(Object target, Errors errors) {
        log.debug("do WordCount validation ");
        FormInputResponse response = (FormInputResponse) target;

        int maxWordCount = response.getFormInput().getWordCount();

        if (response.getWordCount() > maxWordCount) {
            log.debug("NotEmpty validation message for: " + response.getId());
            rejectValue(errors, "value", "validation.field.max.word.count", response.getFormInput().getId(), maxWordCount);
        }
    }
}
