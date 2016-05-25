package com.worth.ifs.validator;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.form.domain.FormInputResponse;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import static org.junit.Assert.*;


/**
 *
 */
public class ApplicationMarkAsCompleteValidatorTest extends AbstractValidatorTest {
    public Validator getValidator() { return new ApplicationMarkAsCompleteValidator(); }

    BindingResult bindingResult;

    @Before


    @Override
    @Test
    public void testInvalid() throws Exception {

        FormInputResponse formInputResponse = new FormInputResponse();
        Application application = formInputResponse.getApplication();
        BindingResult bindingResult = getBindingResult(formInputResponse);

        if (application == null) {
            application = new Application();
        }

        application.setName("");
        // application.setStartDate();
        application.setDurationInMonths(-5L);

        formInputResponse.setApplication(application);
        getValidator().validate(formInputResponse, bindingResult);
        assertTrue(bindingResult.hasErrors());
        assertEquals(2, bindingResult.getErrorCount());

        application.setName(null);
        application.setDurationInMonths(100L);
        formInputResponse.setApplication(application);
        getValidator().validate(formInputResponse, bindingResult);
        assertTrue(bindingResult.hasErrors());

    }

    @Test
    public void testValid() throws Exception {

        FormInputResponse formInputResponse = new FormInputResponse();
        Application application = formInputResponse.getApplication();
        BindingResult bindingResult = getBindingResult(formInputResponse);

        if (application == null) {
            application = new Application();
        }

        application.setName("IFS TEST Project");
        // application.setStartDate();
        application.setDurationInMonths(99L);

        formInputResponse.setApplication(application);
        getValidator().validate(formInputResponse, bindingResult);
        assertFalse(bindingResult.hasErrors());

    }
}
