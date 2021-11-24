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
