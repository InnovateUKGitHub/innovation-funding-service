package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.competition.builder.MilestoneResourceBuilder;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.springframework.restdocs.payload.FieldDescriptor;

import java.time.ZonedDateTime;

import static org.innovateuk.ifs.competition.builder.MilestoneResourceBuilder.newMilestoneResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class MilestoneResourceDocs {
    public static final FieldDescriptor[] milestoneResourceFields = {
        fieldWithPath("id").description("Id of the milestoneResource"),
        fieldWithPath("type").description("Type of the milestone"),
        fieldWithPath("date").description("Date of the particular milestone"),
        fieldWithPath("competitionId").description("Id of the competitionResource")
    };

    public static final MilestoneResourceBuilder milestoneResourceBuilder = newMilestoneResource()
            .withId(1L)
            .withDate(ZonedDateTime.now())
            .withName(MilestoneType.OPEN_DATE)
            .withCompetitionId(1L);
}
