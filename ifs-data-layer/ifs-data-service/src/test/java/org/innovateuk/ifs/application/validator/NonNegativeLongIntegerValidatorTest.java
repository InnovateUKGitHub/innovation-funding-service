package org.innovateuk.ifs.application.validator;

import org.innovateuk.ifs.application.domain.FormInputResponse;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class NonNegativeLongIntegerValidatorTest {
    
	private Validator validator;
	
	private FormInputResponse formInputResponse;
	private BindingResult bindingResult;
	
	@Before
	public void setUp() {
        validator = new NonNegativeLongIntegerValidator();
        formInputResponse = new FormInputResponse();
        bindingResult = ValidatorTestUtil.getBindingResult(formInputResponse);
    }

    @Test
    public void testNegativeNumber() {
        formInputResponse.setValue("-1");
        validator.validate(formInputResponse, bindingResult);
        assertTrue(bindingResult.hasErrors());
        assertEquals(1, bindingResult.getAllErrors().size());
        assertEquals("validation.standard.non.negative.integer.non.negative.format", bindingResult.getAllErrors().get(0).getDefaultMessage());
    }
}
