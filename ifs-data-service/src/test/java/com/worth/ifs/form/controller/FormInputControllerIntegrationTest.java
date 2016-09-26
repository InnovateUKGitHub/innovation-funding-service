package com.worth.ifs.form.controller;

import com.worth.ifs.BaseControllerIntegrationTest;
import com.worth.ifs.form.resource.FormInputResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static com.worth.ifs.form.resource.FormInputScope.APPLICATION;
import static com.worth.ifs.commons.security.SecuritySetter.addBasicSecurityUser;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;

public class FormInputControllerIntegrationTest extends BaseControllerIntegrationTest<FormInputController> {
    @Override
    @Autowired
    protected void setControllerUnderTest(FormInputController controller) {
        this.controller = controller;
    }

    @Before
    public void setup() {
        addBasicSecurityUser();
    }

    @Test
    public void testFindByQuestionId() {
        Long questionId = 1L;
        Long formInputId = 1L;
        List<FormInputResource> formInputs = controller.findByQuestionId(questionId).getSuccessObject();

        assertThat(formInputs, hasSize(3));

        Optional<FormInputResource> formInput = formInputs.stream().filter(f -> f.getId().equals(formInputId)).findFirst();

        assertTrue(formInput.isPresent());
        assertThat(formInput.get().getDescription(), containsString("1. What is the business opportunity that your project addresses?"));
    }

    @Test
    public void testFindByQuestionIdAndScope() {
        Long questionId = 1L;
        Long formInputId = 1L;
        List<FormInputResource> formInputs = controller.findByQuestionIdAndScope(questionId, APPLICATION).getSuccessObject();

        assertThat(formInputs, hasSize(1));

        Optional<FormInputResource> formInput = formInputs.stream().filter(f -> f.getId().equals(formInputId)).findFirst();

        assertTrue(formInput.isPresent());
        assertThat(formInput.get().getDescription(), containsString("1. What is the business opportunity that your project addresses?"));
    }

    @Test
    public void testFindByCompetitionIdAndScope() {
        Long competitionId = 1L;
        List<FormInputResource> formInputs = controller.findByCompetitionIdAndScope(competitionId, APPLICATION).getSuccessObject();

        assertThat(formInputs, hasSize(38));

        Optional<FormInputResource> formInput = formInputs.stream().filter(f -> !competitionId.equals(f.getCompetition())).findAny();
        assertFalse(formInput.isPresent());
    }
}
