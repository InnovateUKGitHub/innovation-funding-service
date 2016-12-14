package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class ViabilityDocs {

    public static final FieldDescriptor[] viabilityResourceFields = {
            fieldWithPath("viability").description("The Viability for a given Project and Organisation"),
            fieldWithPath("viabilityStatus").description("The Viability Status for a given Project and Organisation")
    };
}

