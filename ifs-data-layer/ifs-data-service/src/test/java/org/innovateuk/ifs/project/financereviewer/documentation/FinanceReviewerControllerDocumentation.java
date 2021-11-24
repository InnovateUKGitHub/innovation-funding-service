package org.innovateuk.ifs.project.financereviewer.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.project.financereviewer.controller.FinanceReviewerController;
import org.innovateuk.ifs.project.financereviewer.transactional.FinanceReviewerService;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.user.builder.SimpleUserResourceBuilder.newSimpleUserResource;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
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
                .andExpect(status().isOk());

        verify(financeReviewerService).findFinanceUsers();
    }

    @Test
    public void getFinanceReviewerForProject() throws Exception {
        long projectId = 7;
        when(financeReviewerService.getFinanceReviewerForProject(projectId)).thenReturn(serviceSuccess(newSimpleUserResource().build()));

        mockMvc.perform(get("/finance-reviewer?projectId={projectId}", projectId))
                .andExpect(status().isOk());

        verify(financeReviewerService, only()).getFinanceReviewerForProject(projectId);
    }

    @Test
    public void assignFinanceReviewer() throws Exception {
        long userId = 11;
        long projectId = 13;

        when(financeReviewerService.assignFinanceReviewer(userId, projectId)).thenReturn(serviceSuccess(1L));

        mockMvc.perform(post("/finance-reviewer/{userId}/assign/{projectId}", userId, projectId))
                .andExpect(status().is2xxSuccessful());

        verify(financeReviewerService, only()).assignFinanceReviewer(userId, projectId);
    }
}