package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.setup.builder.SetupStatusResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.innovateuk.ifs.setup.builder.SetupStatusResourceBuilder.newSetupStatusResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class SetupStatusResourceDocs {


    public static final SetupStatusResourceBuilder setupStatusResourceBuilder = newSetupStatusResource()
            .withId(1L)
            .withCompleted(Boolean.FALSE)
            .withClassName(Question.class.getName())
            .withClassPk(23L)
            .withTargetClassName(Competition.class.getName())
            .withTargetId(424L)
            .withParentId(2L);
}
