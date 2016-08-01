package com.worth.ifs.application.finance.view;

import com.worth.ifs.application.finance.model.FinanceFormField;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.rest.ValidationMessages;
import com.worth.ifs.exception.BigDecimalNumberFormatException;
import com.worth.ifs.exception.IntegerNumberFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Base methods for all FinanceFormHandlers. For example methods that handle exceptions or errors that are possibly occurring in all FinanceFormHandlers.
 */
public class BaseFinanceFormHandler {
    @Autowired
    private MessageSource messageSource;

    protected ValidationMessages getValidationMessageFromException(Map.Entry<Long, List<FinanceFormField>> entry, NumberFormatException e) {
        ValidationMessages validationMessages = new ValidationMessages();
        validationMessages.setObjectId(entry.getKey());
        validationMessages.setObjectName("cost");
        List<Error> errors = new ArrayList<>();
        ArrayList<Object> args = new ArrayList<Object>();
        args.add(e.getMessage());
        if(IntegerNumberFormatException.class.equals(e.getClass()) || BigDecimalNumberFormatException.class.equals(e.getClass())){
            errors.add(new Error("", messageSource.getMessage(e.getMessage(), args.toArray(), Locale.UK), args, HttpStatus.INTERNAL_SERVER_ERROR));
        }else{
            errors.add(new Error("", messageSource.getMessage("field.value.not.valid", args.toArray(), Locale.UK), args, HttpStatus.INTERNAL_SERVER_ERROR));
        }
        validationMessages.setErrors(errors);
        return validationMessages;
    }
}
