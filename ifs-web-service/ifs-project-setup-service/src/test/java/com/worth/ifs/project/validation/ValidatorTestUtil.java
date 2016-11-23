package com.worth.ifs.project.validation;

import com.worth.ifs.finance.resource.cost.FinanceRowItem;
import com.worth.ifs.form.domain.FormInputResponse;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
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

    public static void verifyError(BindingResult bindingResult, String errorCode, int errorIndex, Object... expectedArguments) {
        ObjectError actualError = bindingResult.getAllErrors().get(errorIndex);

        assertEquals(errorCode, actualError.getCode());
        assertEquals(errorCode, actualError.getDefaultMessage());
        assertArrayEquals(expectedArguments, actualError.getArguments());
    }
}
