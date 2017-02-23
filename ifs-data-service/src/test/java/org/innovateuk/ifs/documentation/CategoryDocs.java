package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.category.builder.*;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.category.builder.InnovationSectorResourceBuilder.newInnovationSectorResource;
import static org.innovateuk.ifs.category.builder.ResearchCategoryResourceBuilder.newResearchCategoryResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

/**
 * Helper for Spring REST Docs, specifically for category.
 */
public class CategoryDocs {

public static final FieldDescriptor[] categoryResourceFields = {
            fieldWithPath("id").description("id of the category"),
            fieldWithPath("name").description("name of the category"),
            fieldWithPath("type").description("type of the category"),
            fieldWithPath("parent").description("parent category if exists"),
            fieldWithPath("children").description("child categories if exists")
    };

    public static final InnovationAreaResourceBuilder innovationAreaResourceBuilder = newInnovationAreaResource()
            .withId(2L ,3L)
            .withName("Innovation area name")
            .withSector(1L);

    public static final InnovationSectorResourceBuilder innovationSectorResourceBuilder = newInnovationSectorResource()
            .withId(2L ,3L)
            .withName("Innovation sector name");

    public static final ResearchCategoryResourceBuilder researchCategoryResourceBuilder = newResearchCategoryResource()
            .withId(2L ,3L)
            .withName("Research category name");
}
