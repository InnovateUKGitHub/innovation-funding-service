package org.innovateuk.ifs.validator;

import org.innovateuk.ifs.form.domain.FormInputResponse;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import static org.innovateuk.ifs.validator.ValidatorTestUtil.getBindingResult;
import static org.junit.Assert.*;

public class SignedLongIntegerValidatorTest {
    
	private Validator validator;
	
	private FormInputResponse formInputResponse;
	private BindingResult bindingResult;
	
	@Before
	public void setUp() {
        validator = new SignedLongIntegerValidator();
        formInputResponse = new FormInputResponse();
        bindingResult = getBindingResult(formInputResponse);
    }

    @Test
    public void testDecimal() {
        formInputResponse.setValue("1.1");
        validator.validate(formInputResponse, bindingResult);
        assertTrue(bindingResult.hasErrors());
        assertEquals(1, bindingResult.getAllErrors().size());
        assertEquals("validation.standard.integer.non.decimal.format", bindingResult.getAllErrors().get(0).getDefaultMessage());
    }

    @Test
    public void testGreaterThanMAX_VALUE() {
        String greaterThanMaxValue = Long.MAX_VALUE + "1";
        formInputResponse.setValue(greaterThanMaxValue);
        validator.validate(formInputResponse, bindingResult);
        assertTrue(bindingResult.hasErrors());
        assertEquals(1, bindingResult.getAllErrors().size());
        assertEquals("validation.standard.integer.max.value.format", bindingResult.getAllErrors().get(0).getDefaultMessage());
    }

    @Test
    public void testMultipleFailures() {
        String multipleFailures = Long.MAX_VALUE + ".1";
        formInputResponse.setValue(multipleFailures);
        validator.validate(formInputResponse, bindingResult);
        assertEquals(2, bindingResult.getAllErrors().size());
        assertEquals("validation.standard.integer.non.decimal.format", bindingResult.getAllErrors().get(0).getDefaultMessage());
        assertEquals("validation.standard.integer.max.value.format", bindingResult.getAllErrors().get(1).getDefaultMessage());
    }

    @Test
    public void testZero() {
        formInputResponse.setValue("0");
        validator.validate(formInputResponse, bindingResult);
        assertFalse(bindingResult.hasErrors());
    }

    @Test
    public void testValid() {
        formInputResponse.setValue("10000");
        validator.validate(formInputResponse, bindingResult);
        assertFalse(bindingResult.hasErrors());
    }
}
