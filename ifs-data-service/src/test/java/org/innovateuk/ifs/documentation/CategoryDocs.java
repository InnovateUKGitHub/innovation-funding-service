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


    public static FieldDescriptor[] categoryResourceFields(String description, String categoryType) {
        return toArray(baseFields(description, categoryType));
    };

    public static FieldDescriptor[] categoryResourceFieldsWithSector(String description, String categoryType, String sectorDescription) {;
        List<FieldDescriptor> fields = baseFields(description, categoryType);
        fields.add(fieldWithPath("[].sector").description(sectorDescription));
        return toArray(fields);
    };

    public static FieldDescriptor[] categoryResourceFieldsWithChildren(String description, String categoryType, String childrenDescription) {
        List<FieldDescriptor> fields = baseFields(description, categoryType);
        fields.add(fieldWithPath("[].children").description(childrenDescription));
        return toArray(fields);
    };

    private static List<FieldDescriptor> baseFields(String description, String categoryType) {
        return newArrayList(
                fieldWithPath("[]").description(description),
                fieldWithPath("[].id").description("id of the " + categoryType),
                fieldWithPath("[].name").description("name of the " + categoryType),
                fieldWithPath("[].type").description("type of the " + categoryType),
                fieldWithPath("[].priority").description("priority of the " + categoryType));
    }

    private static FieldDescriptor[] toArray(List<FieldDescriptor> list) {
        return list.toArray(new FieldDescriptor[list.size()]);
    }
}
