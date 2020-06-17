package org.innovateuk.ifs.project.activitylog.populator;

import org.innovateuk.ifs.activitylog.resource.ActivityLogResourceBuilder;
import org.innovateuk.ifs.activitylog.resource.ActivityType;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.threads.resource.FinanceChecksSectionType;
import org.junit.Test;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static org.innovateuk.ifs.activitylog.resource.ActivityLogResourceBuilder.newActivityLogResource;
import static org.innovateuk.ifs.activitylog.resource.ActivityType.*;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.junit.Assert.assertNotNull;

public class ActivityLogUrlHelperTest {

    @Test
    public void url() {
        long projectId = 1L;
        long applicationId = 2L;
        long competitionId = 3L;
        long queryId = 4L;
        long documentId = 5L;
        long organisationId = 6L;

        ProjectResource project = newProjectResource()
                .withId(projectId)
                .withApplication(applicationId)
                .withCompetition(competitionId)
                .build();
        ActivityLogResourceBuilder activity = newActivityLogResource()
                .withQuery(queryId)
                .withQueryType(FinanceChecksSectionType.VIABILITY)
                .withDocumentConfig(documentId)
                .withOrganisation(organisationId);

        //Test all enum entries have a URL.
        stream(ActivityType.values())
                .filter(type -> !asList(NONE,
                        GRANTS_FINANCE_CONTACT_INVITED,
                        GRANTS_MONITORING_OFFICER_INVITED,
                        GRANTS_MONITORING_OFFICER_INVITED)
                        .contains(type))
                .forEach((type) ->
                    assertNotNull(ActivityLogUrlHelper.
                            url(activity.withActivityType(type).build(), project))
                );
    }
}
