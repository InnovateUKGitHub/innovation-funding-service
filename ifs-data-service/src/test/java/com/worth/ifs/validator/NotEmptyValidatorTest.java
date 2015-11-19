package com.worth.ifs.validator;

import com.worth.ifs.form.domain.FormInputResponse;
import org.junit.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NotEmptyValidatorTest {
    private NotEmptyValidator getValidator() {
        return new NotEmptyValidator();
    }

    @Test
    public void testInvalid() throws Exception {
        FormInputResponse formInputResponse = new FormInputResponse();

        BindingResult bindingResult = this.getBindingResult(formInputResponse);


        formInputResponse.setValue("");
        getValidator().validate(formInputResponse, bindingResult);
        assertTrue(bindingResult.hasErrors());

        formInputResponse.setValue(null);
        getValidator().validate(formInputResponse, bindingResult);
        assertTrue(bindingResult.hasErrors());

        formInputResponse.setValue(" ");
        getValidator().validate(formInputResponse, bindingResult);
        assertTrue(bindingResult.hasErrors());

        formInputResponse.setValue(" ");
        getValidator().validate(formInputResponse, bindingResult);
        assertTrue(bindingResult.hasErrors());

    }

    @Test
    public void testValid() throws Exception {
        FormInputResponse formInputResponse = new FormInputResponse();

        BindingResult bindingResult = this.getBindingResult(formInputResponse);

        formInputResponse.setValue("asdf");
        getValidator().validate(formInputResponse, bindingResult);
        assertFalse(bindingResult.hasErrors());

        formInputResponse.setValue("a");
        getValidator().validate(formInputResponse, bindingResult);
        assertFalse(bindingResult.hasErrors());

        formInputResponse.setValue("-");
        getValidator().validate(formInputResponse, bindingResult);
        assertFalse(bindingResult.hasErrors());
    }

    private BindingResult getBindingResult(FormInputResponse formInputResponse) {
        DataBinder binder = new DataBinder(formInputResponse);
        BindingResult bindingResult = binder.getBindingResult();
        return bindingResult;
    }
}