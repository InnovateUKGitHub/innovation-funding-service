package org.innovateuk.ifs.workflow.documentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.workflow.controller.ProcessOutcomeController;
import org.innovateuk.ifs.workflow.resource.ProcessOutcomeResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.ProcessOutcomeDocs.processOutcomeFields;
import static org.innovateuk.ifs.documentation.ProcessOutcomeDocs.processOutcomeResourceBuilder;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProcessOutcomeControllerDocumentation extends BaseControllerMockMVCTest<ProcessOutcomeController> {

    private RestDocumentationResultHandler document;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected ProcessOutcomeController supplyControllerUnderTest() {
        return new ProcessOutcomeController();
    }

    @Before
    public void setup() {
        this.document = document("processoutcome/{method-name}",
                preprocessResponse(prettyPrint()));
    }

    @Test
    public void findById() throws Exception {

        Long processOutcomeId = 4L;

        ProcessOutcomeResource processOutcomeResource = processOutcomeResourceBuilder.build();
        when(processOutcomeServiceMock.findOne(processOutcomeId)).thenReturn(serviceSuccess(processOutcomeResource));

        mockMvc.perform(get("/processoutcome/{id}", processOutcomeId))
                .andExpect(status().isOk())
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("id").description("Id of the process outcome that is being requested")
                        ),
                        responseFields(processOutcomeFields)
                ));
    }

    @Test
    public void findLatestByProcess() throws Exception {
        Long processId = 3L;

        ProcessOutcomeResource processOutcomeResource = processOutcomeResourceBuilder.build();
        when(processOutcomeServiceMock.findLatestByProcess(processId)).thenReturn(serviceSuccess(processOutcomeResource));

        mockMvc.perform(get("/processoutcome/process/{id}", processId))
                .andExpect(status().isOk())
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("id").description("Id of the process the outcome is being requested for")
                        ),
                        responseFields(processOutcomeFields)
                ));
    }

    @Test
    public void findLatestByProcessAndOutcomeType() throws Exception {
        Long processId = 3L;
        String outcomeType = "outcomeType";

        ProcessOutcomeResource processOutcomeResource = processOutcomeResourceBuilder.build();
        when(processOutcomeServiceMock.findLatestByProcessAndOutcomeType(processId, outcomeType)).thenReturn(serviceSuccess(processOutcomeResource));

        mockMvc.perform(get("/processoutcome/process/{id}/type/{type}", processId, outcomeType))
                .andExpect(status().isOk())
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("id").description("Id of the process the outcome is being requested for"),
                                parameterWithName("type").description("Type of the process outcome being requested")
                        ),
                        responseFields(processOutcomeFields)
                ));
    }
}
