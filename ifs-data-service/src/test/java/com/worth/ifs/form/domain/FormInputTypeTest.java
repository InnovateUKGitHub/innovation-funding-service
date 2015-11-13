package com.worth.ifs.form.domain;

import org.junit.Assert;
import org.junit.Test;

public class FormInputTypeTest {

    @Test
    public void test_formInputTypeBasicAttributes() throws Exception {

        Long id = 1L;
        String title = "formInputTypeTitle";
        FormInputType formInputType = new FormInputType(id, title);

        Assert.assertEquals(formInputType.getId(), id);
        Assert.assertEquals(formInputType.getTitle(), title);
    }

}