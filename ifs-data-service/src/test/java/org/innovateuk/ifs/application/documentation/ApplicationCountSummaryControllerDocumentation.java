package org.innovateuk.ifs.application.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.controller.ApplicationCountSummaryController;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryResource;
import org.junit.Test;

import static java.util.Arrays.asList;
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

        when(applicationCountSummaryServiceMock.getApplicationCountSummariesByCompetitionId(competitionId)).thenReturn(serviceSuccess(asList(applicationCountSummaryResource)));


        mockMvc.perform(get("/applicationCountSummary/findByCompetitionId/{competitionId}", competitionId))
                .andExpect(status().isOk())
                .andDo(document("applicationCountSummary/{method-name}",
                        pathParameters(
                                parameterWithName("competitionId").description("Id of competition")
                        ),
                        responseFields(applicationCountSummaryResourcesFields)));

        verify(applicationCountSummaryServiceMock).getApplicationCountSummariesByCompetitionId(competitionId);
    }
}
