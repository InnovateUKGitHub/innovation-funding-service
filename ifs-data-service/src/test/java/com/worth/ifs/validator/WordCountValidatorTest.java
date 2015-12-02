package com.worth.ifs.validator;

import com.worth.ifs.form.domain.FormInput;
import com.worth.ifs.form.domain.FormInputResponse;
import org.junit.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;

import static com.worth.ifs.form.builder.FormInputBuilder.newFormInput;
import static com.worth.ifs.form.builder.FormInputResponseBuilder.newFormInputResponse;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class WordCountValidatorTest {
    private WordCountValidator getValidator() {
        return new WordCountValidator();
    }

    @Test
    public void testInvalid() throws Exception {

        FormInput formInput = newFormInput().withWordCount(500).build();
        FormInputResponse formInputResponse = newFormInputResponse().withFormInputs(formInput).build();

        BindingResult bindingResult = this.getBindingResult(formInputResponse);

        String testValue1 = "";
        for(int i=0; i<500; i++) {
            testValue1+=" word";
        }

        formInputResponse.setValue(testValue1);
        getValidator().validate(formInputResponse, bindingResult);
        assertTrue(bindingResult.hasErrors());

        String testValue2 = "";
        for(int i=0; i<=5000; i++) {
            testValue2+=" word";
        }

        formInputResponse.setValue(testValue2);
        getValidator().validate(formInputResponse, bindingResult);
        assertTrue(bindingResult.hasErrors());

    }

    @Test
    public void testValid() throws Exception {
        FormInput formInput = newFormInput().withWordCount(500).build();
        FormInputResponse formInputResponse = newFormInputResponse().withFormInputs(formInput).build();

        BindingResult bindingResult = this.getBindingResult(formInputResponse);

        formInputResponse.setValue("");
        getValidator().validate(formInputResponse, bindingResult);
        assertTrue(!bindingResult.hasErrors());

        formInputResponse.setValue(" ");
        getValidator().validate(formInputResponse, bindingResult);
        assertTrue(!bindingResult.hasErrors());

        formInputResponse.setValue(" word word word");
        getValidator().validate(formInputResponse, bindingResult);
        assertTrue(!bindingResult.hasErrors());

        String testValue1 = "";
        for(int i=0; i<499; i++) {
            testValue1+=" word";
        }

        formInputResponse.setValue(testValue1);
        getValidator().validate(formInputResponse, bindingResult);
        assertTrue(!bindingResult.hasErrors());
    }

    private BindingResult getBindingResult(FormInputResponse formInputResponse) {
        DataBinder binder = new DataBinder(formInputResponse);
        BindingResult bindingResult = binder.getBindingResult();
        return bindingResult;
    }
}