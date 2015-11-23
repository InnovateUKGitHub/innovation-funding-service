package com.worth.ifs.application.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.domain.Application;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FormInputResponseControllerTest extends BaseControllerMockMVCTest<FormInputResponseController>{
    @Override
    protected FormInputResponseController supplyControllerUnderTest() {
        return new FormInputResponseController();
    }

    @Test
    public void testFindResponsesByApplication() throws Exception {

    }

    @Test
    public void testSaveQuestionResponse() throws Exception {
//        String contentString = "{\"userId\":1,\"applicationId\":1,\"formInputId\":1,\"value\":\"\"}";
//        mockMvc.perform(post("/forminputresponse/saveQuestionResponse/")
//                    .content(contentString)
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .accept(MediaType.APPLICATION_JSON)
//                )
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("name", is("testApplication1Name")))
//                .andExpect(jsonPath("id", is(1)));
    }
}