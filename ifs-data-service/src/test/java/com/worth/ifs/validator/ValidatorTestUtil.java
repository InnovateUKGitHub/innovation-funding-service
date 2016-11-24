package com.worth.ifs.validator;

import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;

import com.worth.ifs.finance.resource.cost.FinanceRowItem;
import com.worth.ifs.form.domain.FormInputResponse;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public final class ValidatorTestUtil {

	private ValidatorTestUtil(){}
	
    public static BindingResult getBindingResult(FormInputResponse formInputResponse) {
        return new DataBinder(formInputResponse).getBindingResult();
    }

    public static BindingResult getBindingResult(FinanceRowItem costItem) {
        return new DataBinder(costItem).getBindingResult();
    }

    public static void verifyFieldError(BindingResult bindingResult, String errorCode, int errorIndex, String fieldName, Object... expectedArguments) {
        FieldError actualError = (FieldError)bindingResult.getAllErrors().get(errorIndex);

        String expectedFieldName = String.format(fieldName, expectedArguments);

        assertEquals(errorCode, actualError.getCode());
        assertEquals(errorCode, actualError.getDefaultMessage());
        assertEquals(expectedFieldName, actualError.getField());
    }
}
