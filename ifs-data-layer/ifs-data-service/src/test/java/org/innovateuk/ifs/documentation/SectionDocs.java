package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.form.builder.SectionResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.form.builder.SectionResourceBuilder.newSectionResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class SectionDocs {
    public static final FieldDescriptor[] sectionResourceFields = {
            fieldWithPath("id").description("Id of the sectionResource").optional(),
            fieldWithPath("name").description("name of the section").optional(),
            fieldWithPath("description").description("description of the section").optional(),
            fieldWithPath("assessorGuidanceDescription").description("description of the section for the assessors").optional(),
            fieldWithPath("priority").description("priority of the section").optional(),
            fieldWithPath("questionGroup").description("group the question belongs to").optional(),
            fieldWithPath("competition").description("competition the section belongs to").optional(),
            fieldWithPath("questions").description("list of questions belonging to the section").optional(),
            fieldWithPath("leadQuestions").description("list of lead questions belonging to the section").optional(),
            fieldWithPath("parentSection").description("parent section of this section").optional(),
            fieldWithPath("childSections").description("list of child sections").optional(),
            fieldWithPath("displayInAssessmentApplicationSummary").description("whether to display this section in the assessment summary").optional(),
            fieldWithPath("type").description("marks the section as a specific type").optional()
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
