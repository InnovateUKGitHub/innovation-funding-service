package org.innovateuk.ifs.application.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.controller.ApplicationCountSummaryController;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryPageResource;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryResource;
import org.junit.Test;

import static java.util.Collections.singletonList;
import static java.util.Optional.empty;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.ApplicationCountDocs.applicationCountSummaryResourceBuilder;
import static org.innovateuk.ifs.documentation.ApplicationCountDocs.applicationCountSummaryResourcesFields;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApplicationCountSummaryControllerDocumentation extends BaseControllerMockMVCTest<ApplicationCountSummaryController> {
    @Override
    protected ApplicationCountSummaryController supplyControllerUnderTest() {
        return new ApplicationCountSummaryController();
    }

    @Test
    public void getApplicationCountSummariesByCompetitionId() throws Exception {
        Long competitionId = 1L;
        ApplicationCountSummaryResource applicationCountSummaryResource = applicationCountSummaryResourceBuilder.build();
        ApplicationCountSummaryPageResource pageResource = new ApplicationCountSummaryPageResource();
        pageResource.setContent(singletonList(applicationCountSummaryResource));

        when(applicationCountSummaryServiceMock.getApplicationCountSummariesByCompetitionId(competitionId, 0, 20, empty())).thenReturn(serviceSuccess(pageResource));


        mockMvc.perform(get("/applicationCountSummary/findByCompetitionId/{competitionId}", competitionId))
                .andExpect(status().isOk())
                .andDo(document("applicationCountSummary/{method-name}",
                        pathParameters(
                                parameterWithName("competitionId").description("Id of competition")
                        ),
                        responseFields(applicationCountSummaryResourcesFields)));

        verify(applicationCountSummaryServiceMock).getApplicationCountSummariesByCompetitionId(competitionId, 0, 20, empty());
    }

    @Test
    public void getApplicationCountSummariesByCompetitionIdAndInnovationArea() throws Exception {
        Long competitionId = 1L;
        ApplicationCountSummaryResource applicationCountSummaryResource = applicationCountSummaryResourceBuilder.build();
        ApplicationCountSummaryPageResource pageResource = new ApplicationCountSummaryPageResource();
        pageResource.setContent(singletonList(applicationCountSummaryResource));

        when(applicationCountSummaryServiceMock.getApplicationCountSummariesByCompetitionIdAndInnovationArea(competitionId, 0, 20, empty(), "")).thenReturn(serviceSuccess(pageResource));


        mockMvc.perform(get("/applicationCountSummary/findByCompetitionIdAndInnovationArea/{competitionId}", competitionId))
                .andExpect(status().isOk())
                .andDo(document("applicationCountSummary/{method-name}",
                        pathParameters(
                                parameterWithName("competitionId").description("Id of competition")
                        ),
                        responseFields(applicationCountSummaryResourcesFields)));

        verify(applicationCountSummaryServiceMock).getApplicationCountSummariesByCompetitionIdAndInnovationArea(competitionId, 0, 20, empty(), "");
    }

}
