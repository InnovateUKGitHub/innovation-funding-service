package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationAssessmentSummaryResource;
import org.innovateuk.ifs.application.resource.ApplicationAssessorResource;
import org.junit.Test;

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

    @Override
    protected ApplicationAssessmentSummaryController supplyControllerUnderTest() {
        return new ApplicationAssessmentSummaryController();
    }

    @Test
    public void getAssessors() throws Exception {
        List<ApplicationAssessorResource> expected = newApplicationAssessorResource()
                .build(2);

        Long applicationId = 1L;

        when(applicationAssessmentSummaryServiceMock.getAssessors(applicationId)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/applicationAssessmentSummary/{id}/assessors", applicationId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expected)));

        verify(applicationAssessmentSummaryServiceMock, only()).getAssessors(applicationId);
    }

    @Test
    public void getApplicationAssessmentSummary() throws Exception {
        ApplicationAssessmentSummaryResource expected = newApplicationAssessmentSummaryResource()
                .build();

        Long applicationId = 1L;

        when(applicationAssessmentSummaryServiceMock.getApplicationAssessmentSummary(applicationId)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/applicationAssessmentSummary/{id}", applicationId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expected)));

        verify(applicationAssessmentSummaryServiceMock, only()).getApplicationAssessmentSummary(applicationId);
    }
}