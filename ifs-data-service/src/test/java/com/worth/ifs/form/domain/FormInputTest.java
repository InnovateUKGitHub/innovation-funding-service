package com.worth.ifs.form.domain;

import com.google.common.collect.Sets;
import com.worth.ifs.validator.EmailValidator;
import com.worth.ifs.validator.NotEmptyValidator;
import org.junit.Test;

import java.util.HashSet;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.*;

public class FormInputTest {

    @Test
    public void test_formInputTypeAddValidator() throws Exception {
        Long id = 1L;
        String title = "formInputTypeTitle";
        FormInput formInputType = new FormInput();

        String validatorTitle = "Email Validator";
        Class clazz = EmailValidator.class;

        FormValidator emailValidator = new FormValidator();
        emailValidator.setTitle(validatorTitle);
        emailValidator.setClass(clazz);

        FormValidator notEmptyValidator = new FormValidator();
        notEmptyValidator.setTitle(validatorTitle);
        notEmptyValidator.setClass(NotEmptyValidator.class);

        formInputType.addFormValidator(emailValidator);
        formInputType.addFormValidator(emailValidator);
        formInputType.addFormValidator(notEmptyValidator);

        assertThat(formInputType.getFormValidators(), hasItem(emailValidator));
        assertThat(formInputType.getFormValidators(), hasItem(notEmptyValidator));
        assertEquals(formInputType.getFormValidators(), Sets.newHashSet(emailValidator, notEmptyValidator));
    }

    @Test
    public void test_formInputTypeAddValidatorSet() throws Exception {
        Long id = 1L;
        String title = "formInputTypeTitle";
        FormInput formInputType = new FormInput();

        String validatorTitle = "Email Validator";
        Class clazz = EmailValidator.class;

        FormValidator emailValidator = new FormValidator();
        emailValidator.setTitle(validatorTitle);
        emailValidator.setClass(clazz);

        FormValidator notEmptyValidator = new FormValidator();
        notEmptyValidator.setTitle(validatorTitle);
        notEmptyValidator.setClass(NotEmptyValidator.class);

        HashSet validators = new HashSet();
        validators.add(emailValidator);
        formInputType.setFormValidators(validators);

        assertThat(formInputType.getFormValidators(), hasItem(emailValidator));
        assertEquals(formInputType.getFormValidators(), Sets.newHashSet(emailValidator));
    }

}