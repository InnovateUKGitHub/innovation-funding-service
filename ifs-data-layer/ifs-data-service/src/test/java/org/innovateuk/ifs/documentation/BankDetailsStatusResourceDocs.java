package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class BankDetailsStatusResourceDocs {

    public static final FieldDescriptor[] bankDetailsStatusResourcesFields = {
            fieldWithPath("organisationId").description("Organisation Id"),
            fieldWithPath("organisationName").description("Organisation name"),
            fieldWithPath("bankDetailsStatus").description("Bank details status"),
    };
}
