package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.application.builder.ApplicationCountSummaryResourceBuilder;

import static org.innovateuk.ifs.application.builder.ApplicationCountSummaryResourceBuilder.newApplicationCountSummaryResource;

public class ApplicationCountDocs {

    public static final ApplicationCountSummaryResourceBuilder applicationCountSummaryResourceBuilder = newApplicationCountSummaryResource()
            .withId(1L)
            .withName("application name")
            .withLeadOrganisation("lead organisation name")
            .withAssessors(4L)
            .withAccepted(2L)
            .withSubmitted(1L);
}
