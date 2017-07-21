package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class EditUserResourceDocs {

    public static final FieldDescriptor[] editUserResourceFields = {
            fieldWithPath("userId").description("The id of the user being edited"),
            fieldWithPath("firstName").description("The first name the user being edited"),
            fieldWithPath("lastName").description("The last name of the user being edited"),
            fieldWithPath("userRoleType").description("The new role for the user being edited")
    };
}

