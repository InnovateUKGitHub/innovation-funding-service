package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class AvailableAssessorResourceDocs {

    public static final FieldDescriptor[] availableAssessorResourceFields = {
            fieldWithPath("name").description("Name of the assessor"),
            fieldWithPath("innovationAreas").description("Innovation areas of the assessor"),
            fieldWithPath("compliant").description("Flag to signify if the assessor is compliant. An assessor is compliant if, and only if they’ve completed their Skills and Business Type, and they’ve completed their DoI, and they’ve signed a Agreement."),
            fieldWithPath("email").description("E-mail address of the assessor"),
            fieldWithPath("businessType").description("Assessor type (business or academic)"),
    };
}