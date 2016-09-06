package com.worth.ifs.validator;

import com.worth.ifs.form.domain.FormInputResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import static com.worth.ifs.commons.rest.ValidationMessages.rejectValue;

/**
 * This class validates the FormInputResponse, it checks if the maximum word count has been exceeded.
 */
@Component
public class WordCountValidator extends BaseValidator {
    private static final Log LOG = LogFactory.getLog(WordCountValidator.class);

    @Override
    public void validate(Object target, Errors errors) {
        LOG.debug("do WordCount validation ");
        FormInputResponse response = (FormInputResponse) target;

        int maxWordCount = response.getFormInput().getWordCount();

        if (response.getWordCount() > maxWordCount) {
            LOG.debug("NotEmpty validation message for: " + response.getId());
            rejectValue(errors, "value", "validation.field.max.word.count", maxWordCount);
        }
    }
}
