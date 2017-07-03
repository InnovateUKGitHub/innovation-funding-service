package org.innovateuk.ifs.validator;

import org.innovateuk.ifs.form.domain.FormInputResponse;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import static java.time.ZonedDateTime.now;
import static java.time.format.DateTimeFormatter.ofPattern;
import static org.innovateuk.ifs.validator.ValidatorTestUtil.getBindingResult;
import static org.junit.Assert.*;

public class PastMonthYearDateValidatorTest {

    private Validator validator;

    private FormInputResponse formInputResponse;
    private BindingResult bindingResult;

    @Before
    public void setUp() {
        validator = new PastMMYYYYValidator();
        formInputResponse = new FormInputResponse();
        bindingResult = getBindingResult(formInputResponse);
    }

    @Test
    public void testNullInput() {
        formInputResponse.setValue(null);
        validator.validate(formInputResponse, bindingResult);
        assertTrue(bindingResult.hasErrors());
        assertEquals(1, bindingResult.getAllErrors().size());
        assertEquals("validation.standard.mm.yyyy.format", bindingResult.getAllErrors().get(0).getDefaultMessage());
    }

    @Test
    public void testArbitraryInput() {
        formInputResponse.setValue("Not a date");
        validator.validate(formInputResponse, bindingResult);
        assertTrue(bindingResult.hasErrors());
        assertEquals(1, bindingResult.getAllErrors().size());
        assertEquals("validation.standard.mm.yyyy.format", bindingResult.getAllErrors().get(0).getDefaultMessage());
    }

    @Test
    public void testIncorrectFormatInput() {
        formInputResponse.setValue("2016-10");
        validator.validate(formInputResponse, bindingResult);
        assertTrue(bindingResult.hasErrors());
        assertEquals(1, bindingResult.getAllErrors().size());
        assertEquals("validation.standard.mm.yyyy.format", bindingResult.getAllErrors().get(0).getDefaultMessage());
    }

    @Test
    public void testInvalidMonth() {
        formInputResponse.setValue("13-2017");
        validator.validate(formInputResponse, bindingResult);
        assertTrue(bindingResult.hasErrors());
        assertEquals(1, bindingResult.getAllErrors().size());
        assertEquals("validation.standard.mm.yyyy.format", bindingResult.getAllErrors().get(0).getDefaultMessage());
    }

    @Test
    public void testInvalidMonth2() {
        formInputResponse.setValue("0-2017");
        validator.validate(formInputResponse, bindingResult);
        assertTrue(bindingResult.hasErrors());
        assertEquals(1, bindingResult.getAllErrors().size());
        assertEquals("validation.standard.mm.yyyy.format", bindingResult.getAllErrors().get(0).getDefaultMessage());
    }

    @Test
    public void testFutureInput() {
        String oneMonthInTheFuture = now().plusMonths(1).format(ofPattern("MM-uuuu"));
        formInputResponse.setValue(oneMonthInTheFuture);
        validator.validate(formInputResponse, bindingResult);
        assertTrue(bindingResult.hasErrors());
        assertEquals(1, bindingResult.getAllErrors().size());
        assertEquals("validation.standard.past.mm.yyyy.not.past.format", bindingResult.getAllErrors().get(0).getDefaultMessage());
    }

    @Test
    public void testNow() {
        String now = now().format(ofPattern("MM-uuuu"));
        formInputResponse.setValue(now);
        validator.validate(formInputResponse, bindingResult);
        assertFalse(bindingResult.hasErrors());
    }

    @Test
    public void testValid() {
        formInputResponse.setValue("01-2000");
        validator.validate(formInputResponse, bindingResult);
        assertFalse(bindingResult.hasErrors());
        formInputResponse.setValue("12-2000");
        validator.validate(formInputResponse, bindingResult);
        assertFalse(bindingResult.hasErrors());
    }

}
