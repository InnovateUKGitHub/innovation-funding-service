package org.innovateuk.ifs.application.validator;

import org.innovateuk.ifs.application.domain.FormInputResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.ZeroDowntime;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import static org.innovateuk.ifs.commons.rest.ValidationMessages.rejectValue;

/**
 * This class validates the FormInputResponse, it checks if the maximum word count has been exceeded.
 */
@Component
public class WordCountValidator extends BaseValidator {
    @ZeroDowntime(reference = "IFS-3144", description = "Remove old package names and add flyway script to correct them in database.")
    public static final String OLD_PACKAGE_NAME = "org.innovateuk.ifs.validation.validator.WordCountValidator";
    private static final Log LOG = LogFactory.getLog(WordCountValidator.class);

    @Override
    public void validate(Object target, Errors errors) {
        LOG.debug("do WordCount validation ");
        FormInputResponse response = (FormInputResponse) target;

        int maxWordCount = response.getFormInput().getWordCount();

        if (response.getWordCount() > maxWordCount) {
            LOG.debug("NotEmpty validation message for: " + response.getId());
            rejectValue(errors, "value", "validation.field.max.word.count", response.getFormInput().getId(), maxWordCount);
        }
    }
}
