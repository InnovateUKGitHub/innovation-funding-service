package com.worth.ifs.validator;

import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.form.domain.FormInputResponse;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;

abstract class AbstractValidatorTest implements ValidatorTest {
    BindingResult getBindingResult(FormInputResponse formInputResponse) {
        return new DataBinder(formInputResponse).getBindingResult();
    }

    BindingResult getBindingResult(CostItem costItem) {
        return new DataBinder(costItem).getBindingResult();
    }
}
