package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.competition.builder.MilestoneResourceBuilder;
import org.innovateuk.ifs.competition.resource.MilestoneType;

import java.time.ZonedDateTime;

import static org.innovateuk.ifs.competition.builder.MilestoneResourceBuilder.newMilestoneResource;

public class MilestoneResourceDocs {

    public static final MilestoneResourceBuilder milestoneResourceBuilder = newMilestoneResource()
            .withId(1L)
            .withDate(ZonedDateTime.now())
            .withName(MilestoneType.OPEN_DATE)
            .withCompetitionId(1L)
            .withAssessmentPeriod((Long) null);
}
