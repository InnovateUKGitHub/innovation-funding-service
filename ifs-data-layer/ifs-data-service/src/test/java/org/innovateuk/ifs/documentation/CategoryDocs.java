package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder;
import org.innovateuk.ifs.category.builder.InnovationSectorResourceBuilder;
import org.innovateuk.ifs.category.builder.ResearchCategoryResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.category.builder.InnovationSectorResourceBuilder.newInnovationSectorResource;
import static org.innovateuk.ifs.category.builder.ResearchCategoryResourceBuilder.newResearchCategoryResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

/**
 * Helper for Spring REST Docs, specifically for category.
 */
public class CategoryDocs {
    public static final InnovationAreaResourceBuilder innovationAreaResourceBuilder = newInnovationAreaResource()
            .withId(2L ,3L)
            .withName("Innovation area name")
            .withSector(1L);

    public static final InnovationSectorResourceBuilder innovationSectorResourceBuilder = newInnovationSectorResource()
            .withId(2L ,3L)
            .withChildren(innovationAreaResourceBuilder.build(1))
            .withName("Innovation sector name");

    public static final ResearchCategoryResourceBuilder researchCategoryResourceBuilder = newResearchCategoryResource()
            .withId(2L ,3L)
            .withName("Research category name");

}
