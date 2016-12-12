package org.innovateuk.ifs.validator;

import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;

import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.form.domain.FormInputResponse;

public final class ValidatorTestUtil {
	
	private ValidatorTestUtil(){}
	
    public static BindingResult getBindingResult(FormInputResponse formInputResponse) {
        return new DataBinder(formInputResponse).getBindingResult();
    }

    public static BindingResult getBindingResult(FinanceRowItem costItem) {
        return new DataBinder(costItem).getBindingResult();
    }
}
