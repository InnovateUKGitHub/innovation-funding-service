package com.worth.ifs.form.domain;

import com.google.common.collect.Sets;
import com.worth.ifs.validator.EmailValidator;
import com.worth.ifs.validator.NotEmptyValidator;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class FormInputTypeTest {

    @Test
    public void test_formInputTypeBasicAttributes() throws Exception {

        Long id = 1L;
        String title = "formInputTypeTitle";
        FormInputType formInputType = new FormInputType(id, title);

        Assert.assertEquals(formInputType.getId(), id);
        Assert.assertEquals(formInputType.getTitle(), title);
    }

    @Test
    public void test_formInputTypeAddValidator() throws Exception {
        Long id = 1L;
        String title = "formInputTypeTitle";
        FormInputType formInputType = new FormInputType(id, title);

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

}