package com.worth.ifs.validator;

import com.worth.ifs.form.domain.FormInputResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


@Component
public class ResponseValidator implements Validator {
    private final Log log = LogFactory.getLog(getClass());

    @Override
    public boolean supports(Class<?> clazz) {
        return FormInputResponse.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        FormInputResponse response = (FormInputResponse) target;

//        List<com.worth.ifs.validator.domain.Validator> validators = response.getQuestion().getQuestionType().getValidators();
//        log.info("Validators : "+  validators.size());
        log.info("Response id: "+ response.getId());
        log.info("Response value: "+ response.getValue());

        log.warn("ResponseValidator!");

        if(response.getValue().equals("123")){
            errors.reject("response.invalidInput", "Not a valid response 123");
        }
    }
}
