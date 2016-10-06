package com.worth.ifs.project.finance.documentation;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.project.controller.FinanceCheckController;
import com.worth.ifs.project.finance.workflow.financechecks.resource.FinanceCheckProcessResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;

import java.time.LocalDateTime;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.documentation.FinanceCheckDocs.financeCheckApprovalStatusFields;
import static com.worth.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static com.worth.ifs.project.controller.FinanceCheckController.*;
import static com.worth.ifs.project.finance.builder.FinanceCheckProcessResourceBuilder.newFinanceCheckProcessResource;
import static com.worth.ifs.project.finance.resource.FinanceCheckState.READY_TO_APPROVE;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static com.worth.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FinanceCheckControllerDocumentation extends BaseControllerMockMVCTest<FinanceCheckController> {

    private RestDocumentationResultHandler document;

    @Before
    public void setup(){
        this.document = document("project/{method-name}",
                preprocessResponse(prettyPrint()));
    }

    @Test
    public void testGenerateFinanceCheck(){
        // TODO RP
    }

    @Test
    public void testSaveFinanceCheck() {
        // TODO RP
    }

    @Test
    public void testGetFinanceCheck() {
        // TODO RP
    }

    @Test
    public void testApproveFinanceCheck() throws Exception {

        when(financeCheckServiceMock.approve(123L, 456L)).thenReturn(serviceSuccess());

        String url = FINANCE_CHECK_BASE_URL + "/{projectId}" + FINANCE_CHECK_ORGANISATION_PATH + "/{organisationId}" +
                FINANCE_CHECK_PATH + "/approve";

        mockMvc.perform(post(url, 123L, 456L)).
                andExpect(status().isOk()).
                andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project to which the Finance Check is linked"),
                                parameterWithName("organisationId").description("Id of the organisation to which the Finance Check is linked")
                        )
                ));

        verify(financeCheckServiceMock).approve(123L, 456L);
    }

    @Test
    public void testGetFinanceCheckApprovalStatus() throws Exception {

        FinanceCheckProcessResource status = newFinanceCheckProcessResource().
                withCanApprove(true).
                withInternalParticipant(newUserResource().build()).
                withParticipant(newProjectUserResource().build()).
                withState(READY_TO_APPROVE).
                withModifiedDate(LocalDateTime.of(2016, 10, 04, 12, 10, 02)).
                build();

        when(financeCheckServiceMock.getFinanceCheckApprovalStatus(123L, 456L)).thenReturn(serviceSuccess(status));

        String url = FINANCE_CHECK_BASE_URL + "/{projectId}" + FINANCE_CHECK_ORGANISATION_PATH + "/{organisationId}" +
                FINANCE_CHECK_PATH + "/status";

        mockMvc.perform(get(url, 123L, 456L)).
                andExpect(status().isOk()).
                andExpect(content().json(toJson(status))).
                andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project to which the Finance Check is linked"),
                                parameterWithName("organisationId").description("Id of the organisation to which the Finance Check is linked")
                        ),
                        responseFields(financeCheckApprovalStatusFields)
                ));

        verify(financeCheckServiceMock).getFinanceCheckApprovalStatus(123L, 456L);
    }

    @Override
    protected FinanceCheckController supplyControllerUnderTest() {
        return new FinanceCheckController();
    }
}
