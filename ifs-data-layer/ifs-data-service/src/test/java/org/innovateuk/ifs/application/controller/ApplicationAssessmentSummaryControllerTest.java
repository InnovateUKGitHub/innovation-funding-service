package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationAssessmentSummaryResource;
import org.innovateuk.ifs.application.resource.ApplicationAssessorPageResource;
import org.innovateuk.ifs.application.resource.ApplicationAssessorResource;
import org.innovateuk.ifs.application.transactional.ApplicationAssessmentSummaryService;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static org.innovateuk.ifs.application.builder.ApplicationAssessmentSummaryResourceBuilder.newApplicationAssessmentSummaryResource;
import static org.innovateuk.ifs.application.builder.ApplicationAssessorResourceBuilder.newApplicationAssessorResource;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApplicationAssessmentSummaryControllerTest extends BaseControllerMockMVCTest<ApplicationAssessmentSummaryController> {

    @Mock
    private ApplicationAssessmentSummaryService applicationAssessmentSummaryServiceMock;


    @Override
    protected ApplicationAssessmentSummaryController supplyControllerUnderTest() {
        return new ApplicationAssessmentSummaryController();
    }

    @Test
    public void getAssignedAssessors() throws Exception {
        List<ApplicationAssessorResource> expected = newApplicationAssessorResource()
                .build(2);

        Long applicationId = 1L;

        when(applicationAssessmentSummaryServiceMock.getAssignedAssessors(applicationId)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/application-assessment-summary/{id}/assigned-assessors", applicationId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expected)));

        verify(applicationAssessmentSummaryServiceMock, only()).getAssignedAssessors(applicationId);
    }

    @Test
    public void getAvailableAssessors() throws Exception {
        ApplicationAssessorPageResource expected = new ApplicationAssessorPageResource();

        Long applicationId = 1L;

        when(applicationAssessmentSummaryServiceMock.getAvailableAssessors(applicationId, 0, 20, null)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/application-assessment-summary/{id}/available-assessors", applicationId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expected)));

        verify(applicationAssessmentSummaryServiceMock, only()).getAvailableAssessors(applicationId, 0, 20, null);
    }

    @Test
    public void getAvailableAssessors_WithParams() throws Exception {
        ApplicationAssessorPageResource expected = new ApplicationAssessorPageResource();

        Long applicationId = 1L;
        int page = 3;
        int size = 6;
        String assessorNameFilter = "";

        when(applicationAssessmentSummaryServiceMock.getAvailableAssessors(applicationId, page, size, assessorNameFilter)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/application-assessment-summary/{id}/available-assessors?page={page}&size={size}&assessorNameFilter={assessorNameFilter}", applicationId, page, size, assessorNameFilter))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expected)));

        verify(applicationAssessmentSummaryServiceMock, only()).getAvailableAssessors(applicationId, page, size, assessorNameFilter);
    }

    @Test
    public void getApplicationAssessmentSummary() throws Exception {
        ApplicationAssessmentSummaryResource expected = newApplicationAssessmentSummaryResource()
                .build();

        Long applicationId = 1L;

        when(applicationAssessmentSummaryServiceMock.getApplicationAssessmentSummary(applicationId)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/application-assessment-summary/{id}", applicationId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expected)));

        verify(applicationAssessmentSummaryServiceMock, only()).getApplicationAssessmentSummary(applicationId);
    }
}