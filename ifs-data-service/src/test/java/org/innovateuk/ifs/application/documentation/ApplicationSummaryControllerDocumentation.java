package org.innovateuk.ifs.application.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.BuilderAmendFunctions;
import org.innovateuk.ifs.application.builder.ApplicationSummaryResourceBuilder;
import org.innovateuk.ifs.application.controller.ApplicationSummaryController;
import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.ApplicationSummaryResource;
import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.application.transactional.ApplicationSummaryService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.idBasedNames;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;

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

        List<ApplicationSummaryResource> applications = ApplicationSummaryResourceBuilder.
                newApplicationSummaryResource().
                with(uniqueIds()).
                with(idBasedNames("Application ")).
                withFundingDecision(FundingDecision.values()).
                withLead("A lead organisation").
                withCompletedPercentage(20,40,60,80,100).
                withDuration(2L,4L,6L,8L,10L).
                withGrantRequested(new BigDecimal("500"), new BigDecimal("1000"), new BigDecimal("1500"),new BigDecimal("2000"),new BigDecimal("2500")).
                withInnovationArea("Earth Observation", "Internet of Things", "Data", "Cyber Security", "User Experience").
                withLeadApplicant("A lead user").
                withManageFundingEmailDate(LocalDateTime.now()).
                withNumberOfPartners(1,2,3,4,5).
                build(size);

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
                        responseFields(APPLICATION_SUMMARY_RESOURCE_FIELDS)));
    }


    public static final FieldDescriptor[] APPLICATION_SUMMARY_RESOURCE_FIELDS = {
            fieldWithPath("totalElements").description("Total size of the unpaged results set"),
            fieldWithPath("totalPages").description("Total number of pages"),
            fieldWithPath("number").description("Page number - zero indexed"),
            fieldWithPath("size").description("Page size"),
            fieldWithPath("content[].id").description("Application id"),
            fieldWithPath("content[].name").description("Application name"),
            fieldWithPath("content[].lead").description("Lead organisation"),
            fieldWithPath("content[].leadApplicant").description("lead applicant"),
            fieldWithPath("content[].status").description("Application status"),
            fieldWithPath("content[].completedPercentage").description("Application completed percentage"),
            fieldWithPath("content[].numberOfPartners").description("Number of partners on the application"),
            fieldWithPath("content[].grantRequested").description("The grant requested on the application"),
            fieldWithPath("content[].totalProjectCost").description("The total project cost of the application"),
            fieldWithPath("content[].duration").description("Application duration in months"),
            fieldWithPath("content[].fundingDecision").description("The funding decision for the application"),
            fieldWithPath("content[].funded").description("Whether the application will be funded"),
            fieldWithPath("content[].innovationArea").description("The innovation area of the application"),
            fieldWithPath("content[].manageFundingEmailDate").description("The date of the last  manage funding email sent")
    };
}
