package org.innovateuk.ifs.form.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputScope;
import org.innovateuk.ifs.form.transactional.FormInputService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static org.innovateuk.ifs.form.resource.FormInputScope.APPLICATION;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class FormInputControllerTest extends BaseControllerMockMVCTest<FormInputController> {

    @Mock
    private FormInputService formInputServiceMock;

    @Override
    protected FormInputController supplyControllerUnderTest() {
        return new FormInputController();
    }

    @Test
    public void testFindByQuestionId() throws Exception {
        Long questionId = 1L;

        List<FormInputResource> expected = newFormInputResource().build(1);

        when(formInputServiceMock.findByQuestionId(questionId)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/forminput/find-by-question-id/{id}", questionId))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(expected)));

        verify(formInputServiceMock, only()).findByQuestionId(questionId);
    }

    @Test
    public void testFindByQuestionIdAndScope() throws Exception {
        List<FormInputResource> expected = newFormInputResource()
                .build(2);

        Long questionId = 1L;
        FormInputScope scope = APPLICATION;

        when(formInputServiceMock.findByQuestionIdAndScope(questionId, scope)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/forminput/find-by-question-id/{questionId}/scope/{scope}", questionId, scope))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(content().string(objectMapper.writeValueAsString(expected)));

        verify(formInputServiceMock, only()).findByQuestionIdAndScope(questionId, scope);
    }

    @Test
    public void testFindByCompetitionIdAndScope() throws Exception {
        List<FormInputResource> expected = newFormInputResource()
                .build(2);

        Long competitionId = 1L;
        FormInputScope scope = APPLICATION;

        when(formInputServiceMock.findByCompetitionIdAndScope(competitionId, scope)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/forminput/find-by-competition-id/{competitionId}/scope/{scope}", competitionId, scope))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(content().string(objectMapper.writeValueAsString(expected)));

        verify(formInputServiceMock, only()).findByCompetitionIdAndScope(competitionId, scope);
    }

    @Test
    public void testDelete() throws Exception {
        Long formInputId = 1L;

        when(formInputServiceMock.delete(formInputId)).thenReturn(serviceSuccess());

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/forminput/{id}", formInputId))
                .andExpect(status().is2xxSuccessful());
    }
}
