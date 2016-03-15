package com.worth.ifs.validator;

import com.worth.ifs.form.domain.FormInputResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

/**
 * This class validates the FormInputResponse, it checks if the maximum word count has been exceeded.
 */
@Component
public class WordCountValidator extends BaseValidator {
    private final Log log = LogFactory.getLog(getClass());


    @Override
    public void validate(Object target, Errors errors) {
        log.debug("do WordCount validation ");
        FormInputResponse response = (FormInputResponse) target;

        if (response.getWordCount() > response.getFormInput().getWordCount()) {
            log.debug("NotEmpty validation message for: " + response.getId());
            errors.rejectValue("value", "response.wordCount", "Maximum word count exceeded");
        }
    }
}
