package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class ProfileResourceDocs {

    public static final FieldDescriptor[] profileResourceFields = {
            fieldWithPath("innovationAreas").description("Innovation areas of the profile"),
            fieldWithPath("businessType").description("Business type of the profile"),
            fieldWithPath("skillsAreas").description("Skills areas of the profile"),
            fieldWithPath("affiliations").description("Affiliations of the profile"),
            fieldWithPath("address").description("Address of the profile"),
    };
}
