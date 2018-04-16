package org.innovateuk.ifs.application.validator;

import static org.innovateuk.ifs.application.validator.ValidatorTestUtil.getBindingResult;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import org.innovateuk.ifs.application.domain.FormInputResponse;

public class NotEmptyValidatorTest {
	
	private Validator validator;

	private FormInputResponse formInputResponse;
	private BindingResult bindingResult;
	
	@Before
	public void setUp() {
        validator = new NotEmptyValidator();
        
        formInputResponse = new FormInputResponse();
        bindingResult = ValidatorTestUtil.getBindingResult(formInputResponse);
    }

    @Test
    public void testInvalidEmpty() {
        formInputResponse.setValue("");
        validator.validate(formInputResponse, bindingResult);
        assertTrue(bindingResult.hasErrors());
    }
    @Test
    public void testInvalidNull() {
        formInputResponse.setValue(null);
        validator.validate(formInputResponse, bindingResult);
        assertTrue(bindingResult.hasErrors());
    }
    @Test
    public void testInvalidWhiteSpace() {
        formInputResponse.setValue(" ");
        validator.validate(formInputResponse, bindingResult);
        assertTrue(bindingResult.hasErrors());
    }
    @Test
    public void testValid() {
        formInputResponse.setValue("asdf");
        validator.validate(formInputResponse, bindingResult);
        assertFalse(bindingResult.hasErrors());
    }
    @Test
    public void testValidSingleChar() {
        formInputResponse.setValue("a");
        validator.validate(formInputResponse, bindingResult);
        assertFalse(bindingResult.hasErrors());
    }
    @Test
    public void testValidNotAlpha() {
        formInputResponse.setValue("-");
        validator.validate(formInputResponse, bindingResult);
        assertFalse(bindingResult.hasErrors());
    }
}
