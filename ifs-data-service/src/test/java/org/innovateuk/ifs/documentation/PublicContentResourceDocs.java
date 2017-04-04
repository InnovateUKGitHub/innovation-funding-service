package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentStatus;
import org.innovateuk.ifs.publiccontent.builder.PublicContentResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import java.time.ZonedDateTime;
import java.time.ZonedDateTime;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.publiccontent.builder.ContentGroupResourceBuilder.newContentGroupResource;
import static org.innovateuk.ifs.publiccontent.builder.ContentEventResourceBuilder.newContentEventResource;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentResourceBuilder.newPublicContentResource;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentSectionResourceBuilder.newPublicContentSectionResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class PublicContentResourceDocs {
    public static final FieldDescriptor[] publicContentResourceFields = {
            fieldWithPath("id").description("Id of the public content resource"),
            fieldWithPath("competitionId").description("Id of the competition"),
            fieldWithPath("eligibilitySummary").description("The summary of the eligibility"),
            fieldWithPath("fundingType").description("The type of competition funding"),
            fieldWithPath("keywords[]").description("List of keywords that will match the search on"),
            fieldWithPath("projectFundingRange").description("The range of project funding"),
            fieldWithPath("projectSize").description("The size of the project"),
            fieldWithPath("publishDate").description("The date the public content was last published on"),
            fieldWithPath("shortDescription").description("The short description"),
            fieldWithPath("summary").description("The summary"),
            fieldWithPath("contentSections[]").description("The content sections"),
            fieldWithPath("contentSections[].status").description("The status of each section"),
            fieldWithPath("contentSections[].type").description("The type of each section"),
            fieldWithPath("contentSections[].contentGroups[]").description("The group of content linked to the section"),
            fieldWithPath("contentSections[].contentGroups[].heading").description("The heading of the content group"),
            fieldWithPath("contentSections[].contentGroups[].content").description("The content of the content group"),
            fieldWithPath("contentEvents[]").description("The content events"),
            fieldWithPath("contentEvents[].id").description("The id of the events"),
            fieldWithPath("contentEvents[].date").description("The date of the events"),
            fieldWithPath("contentEvents[].content").description("The content of the events"),
            fieldWithPath("contentEvents[].publicContent").description("The id of the parent public content"),

    };

    public static final PublicContentResourceBuilder publicContentResourceBuilder = newPublicContentResource()
            .withCompetitionId(1L)
            .withEligibilitySummary("summary")
            .withFundingType(FundingType.GRANT)
            .withKeywords(asList("keyword1", "keyword2"))
            .withProjectFundingRange("range")
            .withProjectSize("size")
            .withPublishDate(ZonedDateTime.now())
            .withShortDescription("short")
            .withSummary("sum")
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
