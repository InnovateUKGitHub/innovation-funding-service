package org.innovateuk.ifs.application.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.controller.ApplicationSummaryController;
import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.ApplicationSummaryResource;
import org.innovateuk.ifs.application.transactional.ApplicationSummaryService;
import org.innovateuk.ifs.documentation.ApplicationSummaryDocs;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.ApplicationSummaryDocs.APPLICATION_SUMMARY_RESOURCE_BUILDER;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;

public class ApplicationSummaryControllerDocumentation extends BaseControllerMockMVCTest<ApplicationSummaryController> {
    @Mock
    private ApplicationSummaryService applicationSummaryService;

    private static String baseUrl = "/applicationSummary";

    @Override
    protected ApplicationSummaryController supplyControllerUnderTest() {
        return new ApplicationSummaryController();
    }

    @Test
    public void getWithFundingDecisionApplicationSummariesByCompetitionId() throws Exception {
        final Long competitionId = 1L;
        final String sort = "id";
        final Integer pageIndex = 0;
        final Integer size = 20;
        final Integer totalPages = pageIndex + 2; // In this test we are giving a full page of results before the last page.

        List<ApplicationSummaryResource> applications = APPLICATION_SUMMARY_RESOURCE_BUILDER.build(5);

        ApplicationSummaryPageResource pageResource = new ApplicationSummaryPageResource(totalPages * size, totalPages, applications, pageIndex, size);

        when(applicationSummaryService.getWithFundingDecisionApplicationSummariesByCompetitionId(competitionId, sort, pageIndex, size)).thenReturn(serviceSuccess(pageResource));

        mockMvc.perform(
                get(baseUrl + "/findByCompetition/{competitionId}/with-funding-decision", competitionId).
                        param("page", pageIndex + "").
                        param("sort", sort).
                        param("size", size + "").
                        contentType(APPLICATION_JSON)).
                andDo(document("application-summary/{method-name}",
                        pathParameters(parameterWithName("competitionId").description("The competition id")),
                        requestParameters(
                                parameterWithName("sort").description("Sort on entity field name"),
                                parameterWithName("page").description("Page number - zero indexed"),
                                parameterWithName("size").description("Page size")
                        ),
                        responseFields(ApplicationSummaryDocs.APPLICATION_SUMMARY_RESOURCE_FIELDS)));
    }
}
