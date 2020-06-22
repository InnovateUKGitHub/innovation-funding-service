package org.innovateuk.ifs.question.documentation;

import org.innovateuk.ifs.BaseFileControllerMockMVCTest;
import org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionResource;
import org.innovateuk.ifs.question.controller.QuestionSetupCompetitionController;
import org.innovateuk.ifs.question.transactional.QuestionFileSetupCompetitionService;
import org.innovateuk.ifs.question.transactional.QuestionSetupCompetitionService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import javax.servlet.http.HttpServletRequest;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.CompetitionSetupQuestionResourceDocs.competitionSetupQuestionResourceBuilder;
import static org.innovateuk.ifs.documentation.CompetitionSetupQuestionResourceDocs.competitionSetupQuestionResourceFields;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class QuestionSetupCompetitionControllerDocumentation extends BaseFileControllerMockMVCTest<QuestionSetupCompetitionController> {

    private static final String baseUrl = "/question-setup";

    @Mock
    private QuestionSetupCompetitionService questionSetupCompetitionService;

    @Mock
    private QuestionFileSetupCompetitionService questionFileSetupCompetitionService;

    @Override
    protected QuestionSetupCompetitionController supplyControllerUnderTest() {
        return new QuestionSetupCompetitionController();
    }

    @Test
    public void getByQuestionId() throws Exception {
        final long questionId = 1L;
        when(questionSetupCompetitionService.getByQuestionId(questionId)).thenReturn(serviceSuccess(competitionSetupQuestionResourceBuilder.build()));

        mockMvc.perform(get(baseUrl + "/get-by-id/{id}", questionId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("question-setup-competition/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("id of the question to be retrieved")
                        ),
                        responseFields(competitionSetupQuestionResourceFields)
                ));
    }

    @Test
    public void save() throws Exception {
        CompetitionSetupQuestionResource resource = competitionSetupQuestionResourceBuilder.build();
        when(questionSetupCompetitionService.update(resource)).thenReturn(serviceSuccess(resource));

        mockMvc.perform(put(baseUrl + "/save")
                .header("IFS_AUTH_TOKEN", "123abc")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resource)))
                .andExpect(status().isOk())
                .andDo(document("question-setup-competition/{method-name}",
                        requestFields(competitionSetupQuestionResourceFields)
                ));
    }

    @Test
    public void addDefaultToCompetition() throws Exception {
        final long competitionId = 1L;
        CompetitionSetupQuestionResource resource = competitionSetupQuestionResourceBuilder.build();
        when(questionSetupCompetitionService.createByCompetitionId(competitionId)).thenReturn(serviceSuccess(resource));

        mockMvc.perform(post(baseUrl + "/add-default-to-competition/{id}", competitionId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isCreated())
                .andDo(document("question-setup-competition/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("id of the competition to which the question will be added")
                        ),
                        responseFields(competitionSetupQuestionResourceFields)
                ));
    }

    @Test
    public void deleteById() throws Exception {
        final long questionId = 1L;
        when(questionSetupCompetitionService.delete(questionId)).thenReturn(serviceSuccess());

        mockMvc.perform(delete(baseUrl + "/delete-by-id/{id}", questionId)
                .header("IFS_AUTH_TOKEN", "123abc")).
                andExpect(status().isNoContent())
                .andDo(document("question-setup-competition/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("id of the question to be removed")
                        )
                ));
    }

    @Test
    public void addResearchCategoryQuestionToCompetition() throws Exception {
        final long competitionId = 1L;

        when(questionSetupCompetitionService.addResearchCategoryQuestionToCompetition(competitionId)).thenReturn(serviceSuccess());

        mockMvc.perform(post(baseUrl + "/add-research-category-question-to-competition/{id}", competitionId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isCreated())
                .andDo(document("question-setup-competition/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("id of the competition to which the " +
                                        "research category question will be added")
                        )
                ));
    }

    @Test
    public void deleteTemplateFile() throws Exception {
        final long questionId = 22L;
        when(questionFileSetupCompetitionService.deleteTemplateFile(questionId)).thenReturn(serviceSuccess());

        mockMvc.perform(RestDocumentationRequestBuilders.delete(baseUrl + "/template-file/{questionId}", questionId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isNoContent())
                .andDo(document("question-setup-competition/{method-name}",
                        pathParameters(parameterWithName("questionId").description("Id of the question to have template file deleted")))
                );

        verify(questionFileSetupCompetitionService).deleteTemplateFile(questionId);
    }

    @Test
    public void uploadTemplateFile() throws Exception {
        final long questionId = 77L;
        when(questionFileSetupCompetitionService.uploadTemplateFile(eq("application/pdf"), eq("1234"), eq("randomFile.pdf"),
                eq(questionId), any(HttpServletRequest.class))).thenReturn(serviceSuccess());

        mockMvc.perform(post(baseUrl + "/template-file/{questionId}", questionId)
                .header("IFS_AUTH_TOKEN", "123abc")
                .param("filename", "randomFile.pdf")
                .headers(createFileUploadHeader("application/pdf", 1234)))
                .andExpect(status().isCreated())
                .andDo(document("question-setup-competition/{method-name}",
                        pathParameters(parameterWithName("questionId").description("The question in which the template file will be attached.")),
                        requestParameters(parameterWithName("filename").description("The filename of the file being uploaded")),
                        requestHeaders(
                                headerWithName("Content-Type").description("The Content Type of the file being uploaded e.g. application/pdf")
                        )
                ));

        verify(questionFileSetupCompetitionService).uploadTemplateFile(eq("application/pdf"), eq("1234"), eq("randomFile.pdf"),
                eq(questionId), any(HttpServletRequest.class));
    }

    private HttpHeaders createFileUploadHeader(String contentType, long contentLength) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentLength(contentLength);
        headers.setAccept(singletonList(MediaType.parseMediaType("application/json")));
        return headers;
    }

}
