package org.innovateuk.ifs.validator;

import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.domain.FormInputResponse;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import static org.innovateuk.ifs.form.builder.FormInputBuilder.newFormInput;
import static org.innovateuk.ifs.form.builder.FormInputResponseBuilder.newFormInputResponse;
import static org.innovateuk.ifs.validator.ValidatorTestUtil.getBindingResult;
import static java.util.Collections.nCopies;
import static org.junit.Assert.*;

public class WordCountValidatorTest {

    private Validator validator;

    private FormInputResponse formInputResponse;
    private BindingResult bindingResult;

    @Before
    public void setUp() {
        validator = new WordCountValidator();

        FormInput formInput = newFormInput().withWordCount(500).build();
        formInputResponse = newFormInputResponse().withFormInputs(formInput).build();
        bindingResult = getBindingResult(formInputResponse);
    }

    @Test
    public void testValid_lessThanLimit() {
        String testValue = String.join(" ", nCopies(499, "word"));
        formInputResponse.setValue(testValue);
        validator.validate(formInputResponse, bindingResult);
        assertFalse(bindingResult.hasErrors());
    }

    @Test
    public void testValid_equalsLimit() {
        String testValue = String.join(" ", nCopies(500, "word"));
        formInputResponse.setValue(testValue);
        validator.validate(formInputResponse, bindingResult);
        assertFalse(bindingResult.hasErrors());
    }

    @Test
    public void testInvalid_exceedsLimit() {
        String testValue = String.join(" ", nCopies(501, "word"));
        formInputResponse.setValue(testValue);
        validator.validate(formInputResponse, bindingResult);
        assertTrue(bindingResult.hasErrors());
        assertEquals(1, bindingResult.getErrorCount());
        assertTrue(bindingResult.hasFieldErrors("value"));
        assertEquals("validation.field.max.word.count", bindingResult.getFieldError("value").getCode());
        assertEquals(500, bindingResult.getFieldError("value").getArguments()[1]);
    }

    @Test
    public void testValid_null() {
        formInputResponse.setValue(null);
        validator.validate(formInputResponse, bindingResult);
        assertFalse(bindingResult.hasErrors());
    }

    @Test
    public void testValid_empty() {
        formInputResponse.setValue("");
        validator.validate(formInputResponse, bindingResult);
        assertFalse(bindingResult.hasErrors());
    }

    @Test
    public void testValid_whitespace() {
        formInputResponse.setValue(" ");
        validator.validate(formInputResponse, bindingResult);
        assertFalse(bindingResult.hasErrors());
    }
}
