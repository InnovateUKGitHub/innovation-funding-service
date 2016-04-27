package com.worth.ifs.validator;

import com.worth.ifs.form.domain.FormInputResponse;
import org.junit.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EmailValidatorTest extends AbstractValidatorTest {
    public Validator getValidator() {
        return new EmailValidator();
    }

    @Test
    public void testInvalid() throws Exception {
        FormInputResponse formInputResponse = new FormInputResponse();

        BindingResult bindingResult = getBindingResult(formInputResponse);


        formInputResponse.setValue("SomeNotValidEmailAdres@.nl");
        getValidator().validate(formInputResponse, bindingResult);
        assertTrue(bindingResult.hasErrors());

        formInputResponse.setValue("info@company");
        getValidator().validate(formInputResponse, bindingResult);
        assertTrue(bindingResult.hasErrors());

        formInputResponse.setValue("a@a.a");
        getValidator().validate(formInputResponse, bindingResult);
        assertTrue(bindingResult.hasErrors());

    }

    @Test
    public void testValid() throws Exception {
        FormInputResponse formInputResponse = new FormInputResponse();

        BindingResult bindingResult = getBindingResult(formInputResponse);

        formInputResponse.setValue("info12@govgov.gov.uk");
        getValidator().validate(formInputResponse, bindingResult);
        assertFalse(bindingResult.hasErrors());

        formInputResponse.setValue("ab@cd.ef");
        getValidator().validate(formInputResponse, bindingResult);
        assertFalse(bindingResult.hasErrors());

        formInputResponse.setValue("SomeLongEmailAdres@SomeLongDomain.test");
        getValidator().validate(formInputResponse, bindingResult);
        assertFalse(bindingResult.hasErrors());
    }
}