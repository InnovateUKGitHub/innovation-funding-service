package com.worth.ifs.validator;

import com.worth.ifs.application.domain.Application;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Validator;
import java.time.LocalDate;
import static org.junit.Assert.*;

/**
 * Mark as complete validator test class for application details section
 */
public class ApplicationMarkAsCompleteValidatorTest {

    private Validator validator;

    BindingResult bindingResult;
    LocalDate currentDate;
    Application application;

    @Before
    public void setUp() {
        validator = new ApplicationMarkAsCompleteValidator();
        currentDate = LocalDate.now();
    }

    @Test
    public void testInvalid() throws Exception {

        application  = new Application();

        application.setName("");
        application.setStartDate(currentDate.minusDays(1));
        application.setDurationInMonths(-5L);
        application.setResubmission(null);

        DataBinder binder = new DataBinder(application);
        bindingResult = binder.getBindingResult();
        validator.validate(application, bindingResult);

        assertTrue(bindingResult.hasErrors());
        assertEquals(4, bindingResult.getErrorCount());

        application.setName(null);
        application.setStartDate(currentDate.minusDays(1));
        application.setDurationInMonths(0L);
        application.setResubmission(true);
        application.setPreviousApplicationNumber(null);
        application.setPreviousApplicationTitle(null);

        binder = new DataBinder(application);
        bindingResult = binder.getBindingResult();

        validator.validate(application, bindingResult);
        assertTrue(bindingResult.hasErrors());
        assertEquals(5, bindingResult.getErrorCount());

        application.setDurationInMonths(37L);
        application.setResubmission(false);

        binder = new DataBinder(application);
        bindingResult = binder.getBindingResult();

        validator.validate(application, bindingResult);
        assertTrue(bindingResult.hasErrors());
        assertEquals(3, bindingResult.getErrorCount());
    }

    @Test
    public void testValid() throws Exception {

        application  = new Application();

        application.setName("IFS TEST DEV Project");
        application.setStartDate(currentDate.plusDays(1));
        application.setDurationInMonths(18L);
        application.setResubmission(true);
        application.setPreviousApplicationNumber("A Number");
        application.setPreviousApplicationTitle("Failed com.worth.ifs.Application");

        DataBinder binder = new DataBinder(application);
        bindingResult = binder.getBindingResult();
        validator.validate(application, bindingResult);

        assertFalse(bindingResult.hasErrors());
    }

    @Test
    public void testSupportsApplicationAndSubclasses() {
        assertTrue(validator.supports(Application.class));
        assertTrue(validator.supports(new Application() {
            //empty extension of application;
        }.getClass()));
    }
}
