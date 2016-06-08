package com.worth.ifs.validator;

import com.worth.ifs.application.domain.Application;
import org.junit.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Validator;
import java.time.LocalDate;
import static org.junit.Assert.*;

/**
 * Mark as complete validator test class for application details section
 */
public class ApplicationMarkAsCompleteValidatorTest extends AbstractValidatorTest {
    @Test
    public void validate() throws Exception {

    }

    public Validator getValidator() { return new ApplicationMarkAsCompleteValidator(); }

    BindingResult bindingResult;

    LocalDate currentDate = LocalDate.now();
    Application application;

    @Override
    @Test
    public void testInvalid() throws Exception {

        application  = new Application();

        application.setName("");
        application.setStartDate(currentDate.minusDays(1));
        application.setDurationInMonths(-5L);

        DataBinder binder = new DataBinder(application);
        bindingResult = binder.getBindingResult();
        getValidator().validate(application, bindingResult);

        assertTrue(bindingResult.hasErrors());
        assertEquals(3, bindingResult.getErrorCount());

        application.setName(null);
        application.setStartDate(currentDate.minusDays(1));
        application.setDurationInMonths(100L);

        binder = new DataBinder(application);
        bindingResult = binder.getBindingResult();

        getValidator().validate(application, bindingResult);
        assertTrue(bindingResult.hasErrors());
        assertEquals(3, bindingResult.getErrorCount());
    }

    @Test
    public void testValid() throws Exception {

        application  = new Application();

        application.setName("IFS TEST  DEV Project");
        application.setStartDate(currentDate.plusDays(1));
        application.setDurationInMonths(99L);

        DataBinder binder = new DataBinder(application);
        bindingResult = binder.getBindingResult();
        getValidator().validate(application, bindingResult);

        assertFalse(bindingResult.hasErrors());
    }
}
