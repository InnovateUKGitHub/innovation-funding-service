package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryResource;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.workflow.domain.ActivityState;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.innovateuk.ifs.workflow.domain.ActivityType.APPLICATION_ASSESSMENT;
import static org.innovateuk.ifs.workflow.resource.State.*;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApplicationCountSummaryControllerTest extends BaseControllerMockMVCTest<ApplicationCountSummaryController> {

    @Override
    protected ApplicationCountSummaryController supplyControllerUnderTest() {
        return new ApplicationCountSummaryController();
    }

    @Test
    public void applicationCountSummariesByCompetitionId() throws Exception {
        Long competitionId = 1L;

        List<ApplicationCountSummaryResource> applicationCountSummaryResources = asList(new ApplicationCountSummaryResource());

        when(applicationCountSummaryServiceMock.getApplicationCountSummariesByCompetitionId(competitionId)).thenReturn(serviceSuccess(applicationCountSummaryResources));

        mockMvc.perform(get("/applicationCountSummary/findByCompetitionId/{competitionId}", competitionId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(applicationCountSummaryResources)));

        verify(applicationCountSummaryServiceMock, only()).getApplicationCountSummariesByCompetitionId(competitionId);
    }
}
