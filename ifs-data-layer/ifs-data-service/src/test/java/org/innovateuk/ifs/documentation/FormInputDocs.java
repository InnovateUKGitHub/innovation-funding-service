package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class FormInputDocs {
    public static final FieldDescriptor[] formInputFields = {
            fieldWithPath("id").description("id of the form input"),
            fieldWithPath("wordCount").description("word count of the form input"),
            fieldWithPath("type").description("type of the form input"),
            fieldWithPath("question").description("question of the form input"),
            fieldWithPath("competition").description("competition of the form input"),
            fieldWithPath("inputValidators").description("input validators of the form input"),
            fieldWithPath("description").description("description of the form input"),
            fieldWithPath("includedInApplicationSummary").description("included in application summary of the form input"),
            fieldWithPath("guidanceTitle").description("guidance title of the form input"),
            fieldWithPath("guidanceAnswer").description("guidance answer of the form input"),
            fieldWithPath("guidanceRows").description("guidance rows of the form input"),
            fieldWithPath("priority").description("priority of the form input"),
            fieldWithPath("scope").description("scope of the form input"),
            fieldWithPath("allowedFileTypes").description("allowed file types of the form input"),
            fieldWithPath("formValidators").description("form validators of the form input"),
    };
}
