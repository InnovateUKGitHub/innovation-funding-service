package com.worth.ifs.form.documentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.form.controller.FormInputController;
import com.worth.ifs.form.resource.FormInputResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;

import java.util.List;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.form.documentation.FormInputResourceDocs.formInputResourceBuilder;
import static com.worth.ifs.form.documentation.FormInputResourceDocs.formInputResourceFields;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

public class FormInputControllerDocumentation extends BaseControllerMockMVCTest<FormInputController> {
    private static final String baseURI = "/forminput";

    private RestDocumentationResultHandler document;

    @Override
    protected FormInputController supplyControllerUnderTest() {
        return new FormInputController();
    }

    @Before
    public void setup() {
        this.document = document("forminput/{method-name}",
            preprocessResponse(prettyPrint()));
    }

    @Test
    public void documentFindById() throws Exception {
        FormInputResource testResource = formInputResourceBuilder.build();
        when(formInputServiceMock.findFormInput(1L)).thenReturn(serviceSuccess(testResource));

        mockMvc.perform(get(baseURI + "/{id}", 1L))
            .andDo(this.document.snippets(
                pathParameters(
                    parameterWithName("id").description("id of the forminput to be fetched")
                ),
                responseFields(
                    formInputResourceFields
                )
            ));
    }

    @Test
    public void documentFindByQuestionId() throws Exception {
        List<FormInputResource> testResource = formInputResourceBuilder.build(1);
        when(formInputServiceMock.findByQuestionId(1L)).thenReturn(serviceSuccess(testResource));

        mockMvc.perform(get(baseURI + "/findByQuestionId/{id}", 1L))
            .andDo(this.document.snippets(
                pathParameters(
                    parameterWithName("id").description("id of the question")
                ),
                responseFields(
                    fieldWithPath("[]").description("List of formInputs the user is allowed to see")
                )
            ));
    }

    @Test
    public void documentFindByCompetitionId() throws Exception {
        List<FormInputResource> testResource = formInputResourceBuilder.build(1);
        when(formInputServiceMock.findByCompetitionId(1L)).thenReturn(serviceSuccess(testResource));

        mockMvc.perform(get(baseURI + "/findByCompetitionId/{id}", 1L))
            .andDo(this.document.snippets(
                pathParameters(
                    parameterWithName("id").description("id of the competition")
                ),
                responseFields(
                    fieldWithPath("[]").description("List of formInputs the user is allowed to see")
                )
            ));
    }

    @Test
    public void documentSave() throws Exception {
        FormInputResource testResource = formInputResourceBuilder.build();
        when(formInputServiceMock.save(any())).thenReturn(serviceSuccess(testResource));

        ObjectMapper mapper = new ObjectMapper();

        mockMvc.perform(put(baseURI + "/")
                    .contentType(APPLICATION_JSON)
                    .content(mapper.writeValueAsString(testResource)))
                .andDo(this.document.snippets(
                        responseFields(formInputResourceFields)
                ));
    }

    @Test
    public void documentDelete() throws Exception {
        when(formInputServiceMock.delete(1L)).thenReturn(serviceSuccess());

        mockMvc.perform(delete(baseURI + "/{id}", 1L))
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("id").description("id of the forminput")
                        )
                ));
    }
}