package org.innovateuk.ifs.validator;

import org.innovateuk.ifs.form.domain.FormInputResponse;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import static org.innovateuk.ifs.validator.ValidatorTestUtil.getBindingResult;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NonNegativeIntegerValidatorTest {
    
	private Validator validator;
	
	private FormInputResponse formInputResponse;
	private BindingResult bindingResult;
	
	@Before
	public void setUp() {
        validator = new NonNegativeIntegerValidator();
        formInputResponse = new FormInputResponse();
        bindingResult = getBindingResult(formInputResponse);
    }

    @Test
    public void testNegativeNumber() {
        formInputResponse.setValue("-1");
        validator.validate(formInputResponse, bindingResult);
        assertTrue(bindingResult.hasErrors());
    }

    @Test
    public void testDecimal() {
        formInputResponse.setValue("1.1");
        validator.validate(formInputResponse, bindingResult);
        assertTrue(bindingResult.hasErrors());
    }

    @Test
    public void testGreaterThanMAX_VALUE() {
        String greaterThatMaxValue = Integer.MAX_VALUE + "0";
        formInputResponse.setValue("1.1");
        validator.validate(formInputResponse, bindingResult);
        assertTrue(bindingResult.hasErrors());
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
