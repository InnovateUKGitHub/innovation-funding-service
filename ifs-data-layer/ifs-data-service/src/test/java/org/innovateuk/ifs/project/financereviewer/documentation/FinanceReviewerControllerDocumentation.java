package org.innovateuk.ifs.project.financereviewer.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.project.financereviewer.controller.FinanceReviewerController;
import org.innovateuk.ifs.project.financereviewer.transactional.FinanceReviewerService;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.UserDocs.simpleUserResourceFields;
import static org.innovateuk.ifs.user.builder.SimpleUserResourceBuilder.newSimpleUserResource;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FinanceReviewerControllerDocumentation extends BaseControllerMockMVCTest<FinanceReviewerController> {

    @Mock
    private FinanceReviewerService financeReviewerService;

    @Override
    protected FinanceReviewerController supplyControllerUnderTest() {
        return new FinanceReviewerController();
    }

    @Test
    public void findFinanceUsers() throws Exception {

        when(financeReviewerService.findFinanceUsers()).thenReturn(serviceSuccess(newSimpleUserResource().build(1)));

        mockMvc.perform(get("/finance-reviewer/find-all"))
                .andExpect(status().isOk())
                .andDo(document("finance-reviewer/{method-name}",
                                responseFields(fieldWithPath("[]").description("List of finance users"))
                                        .andWithPrefix("[].", simpleUserResourceFields)
                ));

        verify(financeReviewerService).findFinanceUsers();
    }

    @Test
    public void getFinanceReviewerForProject() throws Exception {
        long projectId = 7;
        when(financeReviewerService.getFinanceReviewerForProject(projectId)).thenReturn(serviceSuccess(newSimpleUserResource().build()));

        mockMvc.perform(get("/finance-reviewer?projectId={projectId}", projectId))
                .andExpect(status().isOk())
                .andDo(document("finance-reviewer/{method-name}",
                        requestParameters(
                                parameterWithName("project").description("id of the project")
                        ),
                        responseFields(simpleUserResourceFields)
                ));

        verify(financeReviewerService, only()).getFinanceReviewerForProject(projectId);
    }

    @Test
    public void assignFinanceReviewer() throws Exception {
        long userId = 11;
        long projectId = 13;

        when(financeReviewerService.assignFinanceReviewer(userId, projectId)).thenReturn(serviceSuccess(1L));

        mockMvc.perform(post("/finance-reviewer/{userId}/assign/{projectId}", userId, projectId))
                .andExpect(status().is2xxSuccessful())
                .andDo(document("finance-reviewer/{method-name}",
                        pathParameters(
                                parameterWithName("userId").description("Id of the finance reviewer user"),
                                parameterWithName("projectId").description("Id of the project to assign")
                        )
                ));

        verify(financeReviewerService, only()).assignFinanceReviewer(userId, projectId);
    }
}