package com.worth.ifs.validator;

import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;

import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.form.domain.FormInputResponse;

public final class ValidatorTestUtil {
	
	private ValidatorTestUtil(){}
	
    public static BindingResult getBindingResult(FormInputResponse formInputResponse) {
        return new DataBinder(formInputResponse).getBindingResult();
    }

    public static BindingResult getBindingResult(CostItem costItem) {
        return new DataBinder(costItem).getBindingResult();
    }
}
