package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

/**
 * A utility class for helping with the generation of Rest Docs
 */
public class RestDocsUtil {

    /**
     * A method that, given a set of fields for a top-level object, is able to produce an equivalent set of fields
     * for a top-level Array of that type of object
     */
    static FieldDescriptor[] createTopLevelArrayOfDocumentation(FieldDescriptor[] originalFieldSet) {
        return simpleMap(originalFieldSet, field -> {
            FieldDescriptor copiedForArray = fieldWithPath("[]" + field.getPath()).description(field.getDescription()).type(field.getType());
            return field.isOptional() ? copiedForArray.optional() : copiedForArray;
        }).toArray(new FieldDescriptor[] {});
    }
}
