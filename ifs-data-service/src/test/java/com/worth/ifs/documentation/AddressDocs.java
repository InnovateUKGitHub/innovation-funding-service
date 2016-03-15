package com.worth.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

/**
 * Created by worthsystems on 15/03/16.
 */
public class AddressDocs {
    public static final FieldDescriptor[] addressResourceFields = {
        fieldWithPath("id").description("id of the address"),
        fieldWithPath("addressLine1").description("first addressLine"),
        fieldWithPath("addressLine2").description("second addressLine"),
        fieldWithPath("addressLine3").description("third addressLine"),
        fieldWithPath("town").description("fourth addressLine"),
        fieldWithPath("county").description("county where requested address is located"),
        fieldWithPath("postcode").description("postcode of the requested address")
    };

}
