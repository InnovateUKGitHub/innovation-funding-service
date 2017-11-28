package org.innovateuk.ifs.validator;

import org.innovateuk.ifs.form.domain.FormInputResponse;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import static org.innovateuk.ifs.form.builder.FormInputBuilder.newFormInput;
import static org.innovateuk.ifs.form.builder.FormInputResponseBuilder.newFormInputResponse;
import static org.innovateuk.ifs.validator.ValidatorTestUtil.getBindingResult;
import static org.junit.Assert.*;

public class AssessorScopeValidatorTest {

    private Validator validator;

    private FormInputResponse formInputResponse;
    private BindingResult bindingResult;

    @Before
    public void setUp() {
        validator = new AssessorScopeValidator();

        formInputResponse = newFormInputResponse()
                .withFormInputs(newFormInput()
                        .withType(FormInputType.ASSESSOR_APPLICATION_IN_SCOPE)
                        .build())
                .build();
        bindingResult = getBindingResult(formInputResponse);
    }

    @Test
    public void validate_true() {
        formInputResponse.setValue("true");
        validator.validate(formInputResponse, bindingResult);
        assertFalse(bindingResult.hasErrors());
    }

    @Test
    public void validate_true_uppercase() {
        formInputResponse.setValue("TRUE");
        validator.validate(formInputResponse, bindingResult);
        assertFalse(bindingResult.hasErrors());
    }

    @Test
    public void validate_false() {
        formInputResponse.setValue("false");
        validator.validate(formInputResponse, bindingResult);
        assertFalse(bindingResult.hasErrors());
    }

    @Test
    public void validate_false_uppercase() {
        formInputResponse.setValue("FALSE");
        validator.validate(formInputResponse, bindingResult);
        assertFalse(bindingResult.hasErrors());
    }

    @Test
    public void validate_none() {
        formInputResponse.setValue("none");
        validator.validate(formInputResponse, bindingResult);
        assertTrue(bindingResult.hasErrors());
        assertEquals(1, bindingResult.getAllErrors().size());
        assertEquals("validation.assessor.scope.invalidScope", bindingResult.getAllErrors().get(0).getDefaultMessage());
    }

    @Test
    public void validate_null() {
        formInputResponse.setValue(null);
        validator.validate(formInputResponse, bindingResult);
        assertTrue(bindingResult.hasErrors());
        assertEquals(1, bindingResult.getAllErrors().size());
        assertEquals("validation.assessor.scope.invalidScope", bindingResult.getAllErrors().get(0).getDefaultMessage());
    }
}
