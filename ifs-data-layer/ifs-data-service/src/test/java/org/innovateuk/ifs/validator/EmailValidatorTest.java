package org.innovateuk.ifs.validator;

import static org.innovateuk.ifs.validator.ValidatorTestUtil.getBindingResult;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import org.innovateuk.ifs.form.domain.FormInputResponse;

public class EmailValidatorTest {
    
	private Validator validator;
	
	private FormInputResponse formInputResponse;
	private BindingResult bindingResult;
	
	@Before
	public void setUp() {
        validator = new EmailValidator();
        
        formInputResponse = new FormInputResponse();
        bindingResult = getBindingResult(formInputResponse);
    }

    @Test
    public void testInvalidNoDomainBeforeExtension() {
        formInputResponse.setValue("SomeNotValidEmailAdres@.nl");
        validator.validate(formInputResponse, bindingResult);
        assertTrue(bindingResult.hasErrors());
    }
    @Test
    public void testValidNoDomainExtension() {
        formInputResponse.setValue("info@company");
        validator.validate(formInputResponse, bindingResult);
        assertFalse(bindingResult.hasErrors());
    }
    @Test
    public void testValidShortDomain() {
        formInputResponse.setValue("a@a.a");
        validator.validate(formInputResponse, bindingResult);
        assertFalse(bindingResult.hasErrors());
    }
    @Test
    public void testValidWithSubdomain() throws Exception {
        formInputResponse.setValue("info12@govgov.gov.uk");
        validator.validate(formInputResponse, bindingResult);
        assertFalse(bindingResult.hasErrors());
    }
    @Test
    public void testValid() throws Exception {
        formInputResponse.setValue("ab@cd.ef");
        validator.validate(formInputResponse, bindingResult);
        assertFalse(bindingResult.hasErrors());
    }
    @Test
    public void testValidLongAddress() throws Exception {
        formInputResponse.setValue("SomeLongEmailAdres@SomeLongDomain.test");
        validator.validate(formInputResponse, bindingResult);
        assertFalse(bindingResult.hasErrors());
    }
}
