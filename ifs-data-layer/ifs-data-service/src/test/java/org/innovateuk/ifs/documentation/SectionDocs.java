package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.form.builder.SectionResourceBuilder;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.form.builder.SectionResourceBuilder.newSectionResource;

public class SectionDocs {

    public static final SectionResourceBuilder sectionResourceBuilder = newSectionResource()
            .withId(1L)
            .withAssessorGuidanceDescription("assessor guidance description")
            .withPriority(1)
            .withQuestionGroup(true)
            .withCompetition(1L)
            .withQuestions(asList(1L, 2L, 3L))
            .withParentSection(3L)
            .withChildSections(asList(2L, 4L));
}
