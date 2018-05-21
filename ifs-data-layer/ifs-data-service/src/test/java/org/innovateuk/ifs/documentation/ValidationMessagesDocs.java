package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class ValidationMessagesDocs {
    public static final FieldDescriptor[] validationMessagesFields = {
            fieldWithPath("objectName").type("String").description("The name of the validation target e.g. costItem"),
            fieldWithPath("objectId").type("Number").description("The name of the validation target e.g. the id of the costItem being validated"),
            fieldWithPath("errors").type("Array").description("An array of validation errors (or warnings) raised against the validation target"),
    };
}
