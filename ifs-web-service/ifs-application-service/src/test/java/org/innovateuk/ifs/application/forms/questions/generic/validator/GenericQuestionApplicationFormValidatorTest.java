package org.innovateuk.ifs.application.forms.questions.generic.validator;

import org.innovateuk.ifs.application.forms.questions.generic.form.GenericQuestionApplicationForm;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.FieldError;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GenericQuestionApplicationFormValidatorTest {

    private GenericQuestionApplicationFormValidator validator;

    private BindingResult bindingResult;

    @Before
    public void setup() {
        validator = new GenericQuestionApplicationFormValidator();
        bindingResult = new DataBinder(new GenericQuestionApplicationForm()).getBindingResult();
    }

    @Test
    public void validateTextArea() {
        GenericQuestionApplicationForm form = new GenericQuestionApplicationForm();
        form.setAnswer("");
        form.setTextAreaActive(true);
        form.setMultipleChoiceOptionsActive(false);

        validator.validate(form, bindingResult);
        assertTrue(bindingResult.hasErrors());
        Assert.assertEquals(1, bindingResult.getErrorCount());

        FieldError actualError =  (FieldError) bindingResult.getAllErrors().get(0);
        assertEquals("validation.field.please.enter.some.text", actualError.getCode());
        assertEquals("answer", actualError.getField());
    }

    @Test
    public void validateMultipleChoiceOptions() {
        GenericQuestionApplicationForm form = new GenericQuestionApplicationForm();
        form.setAnswer("");
        form.setTextAreaActive(false);
        form.setMultipleChoiceOptionsActive(true);

        validator.validate(form, bindingResult);
        assertTrue(bindingResult.hasErrors());
        Assert.assertEquals(1, bindingResult.getErrorCount());

        FieldError actualError =  (FieldError) bindingResult.getAllErrors().get(0);
        assertEquals("validation.multiple.choice.required", actualError.getCode());
        assertEquals("multipleChoiceOptionId", actualError.getField());
    }
}
