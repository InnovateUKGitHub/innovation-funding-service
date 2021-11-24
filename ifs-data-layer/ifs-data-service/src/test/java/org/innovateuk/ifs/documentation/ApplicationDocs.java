package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.application.builder.ApplicationResourceBuilder;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

import static java.time.ZonedDateTime.now;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.competition.resource.CollaborationLevel.SINGLE_OR_COLLABORATIVE;

public class ApplicationDocs {

    public static final ApplicationResourceBuilder applicationResourceBuilder = newApplicationResource()
            .withId(1L)
            .withName("application name")
            .withStartDate(LocalDate.now())
            .withSubmittedDate(now())
            .withDurationInMonths(1L)
            .withApplicationState(ApplicationState.OPENED)
            .withCompetition(1L)
            .withCompetitionName("competition name")
            .withCompetitionStatus(CompetitionStatus.PROJECT_SETUP)
            .withCompletion(new BigDecimal(30L))
            .withResearchCategory(new ResearchCategoryResource())
            .withInnovationArea(new InnovationAreaResource())
            .withLeadOrganisationId(1L)
            .withNoInnovationAreaApplicable(false)
            .withCollaborationLevel(SINGLE_OR_COLLABORATIVE)
            .withAssessmentPeriod((Long) null);
}
