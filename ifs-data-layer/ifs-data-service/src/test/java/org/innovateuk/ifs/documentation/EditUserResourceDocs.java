package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class EditUserResourceDocs {

    public static final FieldDescriptor[] editUserResourceFields = {
            fieldWithPath("userToEdit").description("The user being edited"),
            fieldWithPath("userRoleType").description("The new role for the user being edited")
    };
}

