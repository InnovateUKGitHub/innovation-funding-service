package com.worth.ifs.application.finance.view;

import com.worth.ifs.application.finance.model.FinanceFormField;
import com.worth.ifs.commons.rest.ValidationMessages;
import com.worth.ifs.exception.BigDecimalNumberFormatException;
import com.worth.ifs.exception.IntegerNumberFormatException;

import java.util.List;
import java.util.Map;

import static com.worth.ifs.commons.error.Error.globalError;
import static java.util.Collections.singletonList;

/**
 * Base methods for all FinanceFormHandlers. For example methods that handle exceptions or errors that are possibly occurring in all FinanceFormHandlers.
 */
public class BaseFinanceFormHandler {

    protected ValidationMessages getValidationMessageFromException(Map.Entry<Long, List<FinanceFormField>> entry, NumberFormatException e) {
        ValidationMessages validationMessages = new ValidationMessages();
        validationMessages.setObjectId(entry.getKey());
        validationMessages.setObjectName("cost");
        List<Object> args = singletonList(e.getMessage());
        if(IntegerNumberFormatException.class.equals(e.getClass()) || BigDecimalNumberFormatException.class.equals(e.getClass())){
            validationMessages.addError(globalError(e.getMessage(), args));
        }else{
            validationMessages.addError(globalError("field.value.not.valid", args));
        }
        return validationMessages;
    }
}
