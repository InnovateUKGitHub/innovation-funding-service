package org.innovateuk.ifs.commons.validation;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import static org.junit.Assert.assertEquals;

public final class ValidatorTestUtil {
	
	private ValidatorTestUtil(){}

    public static void verifyFieldError(BindingResult bindingResult, String errorCode, int errorIndex, String fieldName, Object... expectedArguments) {
        FieldError actualError = (FieldError)bindingResult.getAllErrors().get(errorIndex);

        String expectedFieldName = String.format(fieldName, expectedArguments);

        assertEquals(errorCode, actualError.getCode());
        assertEquals(errorCode, actualError.getDefaultMessage());
        assertEquals(expectedFieldName, actualError.getField());
    }
}
