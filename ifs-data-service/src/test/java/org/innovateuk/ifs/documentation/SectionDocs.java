package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.application.builder.SectionResourceBuilder;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.innovateuk.ifs.application.builder.SectionResourceBuilder.newSectionResource;
import static java.util.Arrays.asList;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class SectionDocs {
    public static final FieldDescriptor[] sectionResourceFields = {
            fieldWithPath("id").description("Id of the sectionResource"),
            fieldWithPath("name").description("name of the section"),
            fieldWithPath("description").description("description of the section"),
            fieldWithPath("assessorGuidanceDescription").description("description of the section for the assessors"),
            fieldWithPath("priority").description("priority of the section"),
            fieldWithPath("questionGroup").description("group the question belongs to"),
            fieldWithPath("competition").description("competition the section belongs to"),
            fieldWithPath("questions").description("list of questions belonging to the section"),
            fieldWithPath("parentSection").description("parent section of this section"),
            fieldWithPath("childSections").description("list of child sections"),
            fieldWithPath("displayInAssessmentApplicationSummary").description("whether to display this section in the assessment summary"),
            fieldWithPath("type").description("marks the section as a specific type")
    };

    public static final SectionResourceBuilder sectionResourceBuilder = newSectionResource()
            .withId(1L)
            .withDescription("section description")
            .withAssessorGuidanceDescription("assessor guidance description")
            .withPriority(1)
            .withQuestionGroup(true)
            .withCompetition(1L)
            .withQuestions(asList(1L, 2L, 3L))
            .withParentSection(3L)
            .withChildSections(asList(2L, 4L))
            .withDisplayInAssessmentApplicationSummary(true);
}
