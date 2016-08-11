package com.worth.ifs.form.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.form.resource.FormInputResource;
import com.worth.ifs.form.resource.FormInputScope;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import java.util.List;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static com.worth.ifs.form.resource.FormInputScope.APPLICATION;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class FormInputControllerTest extends BaseControllerMockMVCTest<FormInputController> {
    @Override
    protected FormInputController supplyControllerUnderTest() {
        return new FormInputController();
    }

    @Test
    public void testFindByQuestionId() throws Exception {
        Long questionId = 1L;

        List<FormInputResource> expected = newFormInputResource()
                .withId(1L)
                .withFormInputTypeTitle("testFormInputTypeTitle")
                .build(1);

        when(formInputServiceMock.findByQuestionId(questionId)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/forminput/findByQuestionId/{id}", questionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0]formInputTypeTitle", is("testFormInputTypeTitle")))
                .andExpect(jsonPath("[0]id", is(1)));

        verify(formInputServiceMock, only()).findByQuestionId(questionId);
    }

    @Test
    public void testFindByQuestionIdAndScope() throws Exception {
        List<FormInputResource> expected = newFormInputResource()
                .build(2);

        Long questionId = 1L;
        FormInputScope scope = APPLICATION;

        when(formInputServiceMock.findByQuestionIdAndScope(questionId, scope)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(RestDocumentationRequestBuilders.get("/forminput/findByQuestionId/{questionId}/scope/{scope}", questionId, scope))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(content().string(new ObjectMapper().writeValueAsString(expected)));

        verify(formInputServiceMock, only()).findByQuestionIdAndScope(questionId, scope);
    }

    @Test
    public void testFindByCompetitionIdAndScope() throws Exception {
        List<FormInputResource> expected = newFormInputResource()
                .build(2);

        Long competitionId = 1L;
        FormInputScope scope = APPLICATION;

        when(formInputServiceMock.findByCompetitionIdAndScope(competitionId, scope)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(RestDocumentationRequestBuilders.get("/forminput/findByCompetitionId/{competitionId}/scope/{scope}", competitionId, scope))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(content().string(new ObjectMapper().writeValueAsString(expected)));

        verify(formInputServiceMock, only()).findByCompetitionIdAndScope(competitionId, scope);
    }

    @Test
    public void testSave() throws Exception {
        FormInputResource expected = newFormInputResource()
                .build();

        Long competitionId = 1L;

        when(formInputServiceMock.save(any(FormInputResource.class))).thenReturn(serviceSuccess(expected));

        mockMvc.perform(RestDocumentationRequestBuilders.put("/forminput/", competitionId)
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(new ObjectMapper().writeValueAsString(expected)))
                .andExpect(status().isOk())
                .andExpect(content().string(new ObjectMapper().writeValueAsString(expected)));
    }

    @Test
    public void testDelete() throws Exception {
        Long formInputId = 1L;

        when(formInputServiceMock.delete(formInputId)).thenReturn(serviceSuccess());

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/forminput/{id}", formInputId))
                .andExpect(status().is2xxSuccessful());
    }
}
