package com.worth.ifs.form.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.form.domain.FormInput;
import com.worth.ifs.form.resource.FormInputResource;
import com.worth.ifs.user.domain.ProcessRole;
import org.junit.Test;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static java.util.Arrays.asList;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FormInputControllerTest extends BaseControllerMockMVCTest<FormInputController> {

    @Override
    protected FormInputController supplyControllerUnderTest() {
        return new FormInputController();
    }

    @Test
    public void applicationControllerShouldReturnApplicationByUserId() throws Exception {
        Long questionId = 1L;

        FormInputResource testFormInputResource1 = newFormInputResource().withId(1L).withFormInputTypeTitle("testFormInputTypeTitle").build();

        when(formInputService.findByQuestionId(testFormInputResource1.getId())).thenReturn(serviceSuccess(asList(testFormInputResource1)));

        mockMvc.perform(get("/forminput/findByQuestionId/{id}", questionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0]formInputTypeTitle", is("testFormInputTypeTitle")))
                .andExpect(jsonPath("[0]id", is(1)));
    }
}
