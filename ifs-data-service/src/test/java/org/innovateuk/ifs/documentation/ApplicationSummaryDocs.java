package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.application.builder.ApplicationSummaryResourceBuilder;
import org.innovateuk.ifs.application.resource.FundingDecision;
import org.springframework.restdocs.payload.FieldDescriptor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.idBasedNames;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class ApplicationSummaryDocs {

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
            fieldWithPath("content[].manageFundingEmailDate").description("The date of the last  manage funding email sent"),
            fieldWithPath("content[].ineligibleInformed").description("Whether the applicant has been informed the application is ineligible")
    };

    public static final ApplicationSummaryResourceBuilder APPLICATION_SUMMARY_RESOURCE_BUILDER =
            ApplicationSummaryResourceBuilder.newApplicationSummaryResource().
                    with(uniqueIds()).
                    with(idBasedNames("Application ")).
                    withFundingDecision(FundingDecision.values()).
                    withLead("A lead organisation").
                    withCompletedPercentage(20, 40, 60, 80, 100).
                    withDuration(2L, 4L, 6L, 8L, 10L).
                    withGrantRequested(new BigDecimal("500"), new BigDecimal("1000"), new BigDecimal("1500"), new BigDecimal("2000"), new BigDecimal("2500")).
                    withInnovationArea("Earth Observation", "Internet of Things", "Data", "Cyber Security", "User Experience").
                    withLeadApplicant("A lead user").
                    withManageFundingEmailDate(ZonedDateTime.now()).
                    withNumberOfPartners(1, 2, 3, 4, 5).
                    withIneligibleInformed(true, true, true, false, false);
}
