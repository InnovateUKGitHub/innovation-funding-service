package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentStatus;
import org.innovateuk.ifs.publiccontent.builder.PublicContentResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import java.time.ZonedDateTime;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.publiccontent.builder.ContentEventResourceBuilder.newContentEventResource;
import static org.innovateuk.ifs.publiccontent.builder.ContentGroupResourceBuilder.newContentGroupResource;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentResourceBuilder.newPublicContentResource;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentSectionResourceBuilder.newPublicContentSectionResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class PublicContentResourceDocs {

    public static final PublicContentResourceBuilder publicContentResourceBuilder = newPublicContentResource()
            .withCompetitionId(1L)
            .withEligibilitySummary("summary")
            .withKeywords(asList("keyword1", "keyword2"))
            .withProjectFundingRange("range")
            .withProjectSize("size")
            .withPublishDate(ZonedDateTime.now())
            .withShortDescription("short")
            .withSummary("sum")
            .withInviteOnly(Boolean.FALSE)
            .withContentSections(
                    newPublicContentSectionResource()
                            .withStatus(PublicContentStatus.IN_PROGRESS)
                            .withType(PublicContentSectionType.DATES)
                            .withPublicContent(1L)
                            .withContentGroups(
                                newContentGroupResource()
                                    .withHeading("Heading")
                                    .withContent("Content")
                                    .withPriority(1)
                                .build(1)
                            ).build(1))
            .withContentEvents(newContentEventResource()
                    .withId(1L)
                    .withDate(ZonedDateTime.now())
                    .withPublicContent(2L)
                    .withContent("Content").build(1));
}
