package com.worth.ifs.validator;

import static com.worth.ifs.form.builder.FormInputBuilder.newFormInput;
import static com.worth.ifs.form.builder.FormInputResponseBuilder.newFormInputResponse;
import static com.worth.ifs.validator.ValidatorTestUtil.getBindingResult;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import com.worth.ifs.form.domain.FormInput;
import com.worth.ifs.form.domain.FormInputResponse;

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
    public void testInvalid500() {

        String testValue1 = "";
        for(int i=0; i<500; i++) {
            testValue1+=" word";
        }

        formInputResponse.setValue(testValue1);
        validator.validate(formInputResponse, bindingResult);
        assertTrue(bindingResult.hasErrors());
    }
    
    @Test
    public void testInvalid5000() {
        String testValue2 = "";
        for(int i=0; i<=5000; i++) {
            testValue2+=" word";
        }

        formInputResponse.setValue(testValue2);
        validator.validate(formInputResponse, bindingResult);
        assertTrue(bindingResult.hasErrors());
    }

    @Test
    public void testValidEmpty() {
        formInputResponse.setValue("");
        validator.validate(formInputResponse, bindingResult);
        assertTrue(!bindingResult.hasErrors());
    }
    
    @Test
    public void testValidWhitespace() {
        formInputResponse.setValue(" ");
        validator.validate(formInputResponse, bindingResult);
        assertTrue(!bindingResult.hasErrors());
    }
    
    @Test
    public void testValidWords() {
        formInputResponse.setValue(" word word word");
        validator.validate(formInputResponse, bindingResult);
        assertTrue(!bindingResult.hasErrors());
    }
    
    @Test
    public void testValidManyWords() {
        String testValue1 = "";
        for(int i=0; i<499; i++) {
            testValue1+=" word";
        }

        formInputResponse.setValue(testValue1);
        validator.validate(formInputResponse, bindingResult);
        assertTrue(!bindingResult.hasErrors());
    }
}