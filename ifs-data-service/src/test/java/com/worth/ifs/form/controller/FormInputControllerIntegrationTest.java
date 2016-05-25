package com.worth.ifs.form.controller;

import com.worth.ifs.BaseControllerIntegrationTest;
import com.worth.ifs.form.resource.FormInputResource;
import com.worth.ifs.form.resource.FormInputResponseResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;

import static com.worth.ifs.security.SecuritySetter.addBasicSecurityUser;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class FormInputControllerIntegrationTest  extends BaseControllerIntegrationTest<FormInputController> {
    @Override
    @Autowired
    protected void setControllerUnderTest(FormInputController controller) {
        this.controller = controller;
    }

    @Before
    public void setup(){
        addBasicSecurityUser();
    }

    @Test
    @Rollback
    public void test_findResponsesByApplication(){
        Long questionId = 1L;
        Long formInputId = 1L;
        List<FormInputResource> formInputs = controller.findByQuestionId(questionId).getSuccessObject();

        assertThat(formInputs, hasSize(1));

        Optional<FormInputResource> formInput = formInputs.stream().filter(f -> f.getId().equals(formInputId)).findFirst();

        assertTrue(formInput.isPresent());
        assertThat(formInput.get().getDescription(), containsString("1. What is the business opportunity that your project addresses?"));
    }
}
