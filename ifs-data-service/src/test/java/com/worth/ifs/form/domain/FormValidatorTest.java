package com.worth.ifs.form.domain;

import com.worth.ifs.validator.EmailValidator;
import com.worth.ifs.validator.NotEmptyValidator;
import org.junit.Assert;
import org.junit.Test;

public class FormValidatorTest {

    @Test
    public void test_formInputTypeBasicAttributes() throws Exception {
        String title = "Email Validator";
        Class clazz = EmailValidator.class;

        FormValidator formValidator = new FormValidator();
        formValidator.setTitle(title);

        Assert.assertEquals(formValidator.getTitle(), title);
    }
    @Test
    public void test_formInputTypeSetValidatorClazz() throws Exception {
        String title = "Email Validator";
        Class clazz = EmailValidator.class;

        FormValidator formValidator = new FormValidator();
        formValidator.setTitle(title);
        formValidator.setClazz(clazz);

        Assert.assertEquals(formValidator.getTitle(), title);
        Assert.assertEquals(formValidator.getClazz(), clazz);
        Assert.assertEquals(formValidator.getClazzName(), clazz.getName());


        formValidator.setClazzName(NotEmptyValidator.class.getName());
        Assert.assertEquals(formValidator.getClazzName(), NotEmptyValidator.class.getName());
        Assert.assertEquals(formValidator.getClazz(), NotEmptyValidator.class);
    }
}