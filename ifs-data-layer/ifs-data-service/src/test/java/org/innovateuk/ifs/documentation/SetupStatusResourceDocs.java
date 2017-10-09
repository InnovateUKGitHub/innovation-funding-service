package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.setup.builder.SetupStatusResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.innovateuk.ifs.setup.builder.SetupStatusResourceBuilder.newSetupStatusResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class SetupStatusResourceDocs {
    public static final FieldDescriptor[] setupStatusResourceFields = {
            fieldWithPath("id").description("Id of the Setup status resource"),
            fieldWithPath("completed").description("Indicator it is complete"),
            fieldWithPath("className").description("Name of the class linked to completed field"),
            fieldWithPath("classPk").description("Primary key of the class linked to completed field"),
            fieldWithPath("targetClassName").description("Name of the context class (for instance the Competition)"),
            fieldWithPath("targetId").description("Primary key of the context class (for instance the Competition)"),
            fieldWithPath("parentId").description("Foreign key to the parent setup status")
    };

    public static final FieldDescriptor[] setupStatusListFields = {
            fieldWithPath("[].id").description("Id of the Setup status resource"),
            fieldWithPath("[].completed").description("Indicator it is complete"),
            fieldWithPath("[].className").description("Name of the class linked to completed field"),
            fieldWithPath("[].classPk").description("Primary key of the class linked to completed field"),
            fieldWithPath("[].targetClassName").description("Name of the context class (for instance the Competition)"),
            fieldWithPath("[].targetId").description("Primary key of the context class (for instance the Competition)"),
            fieldWithPath("[].parentId").description("Foreign key to the parent setup status")
    };

    public static final SetupStatusResourceBuilder setupStatusResourceBuilder = newSetupStatusResource()
            .withId(1L)
            .withCompleted(Boolean.FALSE)
            .withClassName(Question.class.getName())
            .withClassPk(23L)
            .withTargetClassName(Competition.class.getName())
            .withTargetId(424L)
            .withParentId(2L);
}
