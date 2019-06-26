package org.innovateuk.ifs.project.financereviewer.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.project.financereviewer.transactional.FinanceReviewerService;
import org.innovateuk.ifs.user.resource.SimpleUserResource;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.user.builder.SimpleUserResourceBuilder.newSimpleUserResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FinanceReviewerControllerTest extends BaseControllerMockMVCTest<FinanceReviewerController> {

    @Mock
    private FinanceReviewerService financeReviewerService;

    @Test
    public void findAll() throws Exception {
        List<SimpleUserResource> expected = newSimpleUserResource().build(1);

        when(financeReviewerService.findFinanceUsers()).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/finance-reviewer/find-all"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expected)));

        verify(financeReviewerService).findFinanceUsers();
    }

    @Test
    public void getProjectMonitoringOfficer() throws Exception {
        long projectId = 7;
        SimpleUserResource expected = new SimpleUserResource();

        when(financeReviewerService.getFinanceReviewerForProject(projectId)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/finance-reviewer?projectId={projectId}", projectId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expected)));

        verify(financeReviewerService, only()).getFinanceReviewerForProject(projectId);
    }

    @Test
    public void assignProjectToMonitoringOfficer() throws Exception {
        long userId = 11;
        long projectId = 13;

        when(financeReviewerService.assignFinanceReviewer(userId, projectId)).thenReturn(serviceSuccess(1L));

        mockMvc.perform(MockMvcRequestBuilders.post("/finance-reviewer/{userId}/assign/{projectId}", userId, projectId))
                .andExpect(status().is2xxSuccessful());

        verify(financeReviewerService, only()).assignFinanceReviewer(userId, projectId);
    }

    @Override
    protected FinanceReviewerController supplyControllerUnderTest() {
        return new FinanceReviewerController();
    }
}