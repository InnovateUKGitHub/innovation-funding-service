package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.category.builder.CategoryResourceBuilder;
import org.innovateuk.ifs.category.resource.CategoryResource;
import org.innovateuk.ifs.category.resource.CategoryType;
import org.springframework.restdocs.payload.FieldDescriptor;

import java.util.ArrayList;

import static com.google.common.primitives.Longs.asList;
import static org.innovateuk.ifs.category.builder.CategoryResourceBuilder.newCategoryResource;
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

    public static final CategoryResourceBuilder categoryResourceBuilder = newCategoryResource()
            .withId(2L ,3L)
            .withName("Category name")
            .withType(CategoryType.INNOVATION_AREA)
            .withParent(1L);
}
