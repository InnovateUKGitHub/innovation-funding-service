package org.innovateuk.ifs.form.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class FormInputResponseResourceDocs {
    public static final FieldDescriptor[] formInputResponseResourceFields = {
            fieldWithPath("id").description("Id of the FormInputResponseResource"),
            fieldWithPath("updateDate").description("The date of the last update"),
            fieldWithPath("value").description("The value of the response"),
            fieldWithPath("updatedBy").description("The id of who last updated the input"),
            fieldWithPath("updatedByUser").description("The id of the user who last updated the input"),
            fieldWithPath("updatedByUserName").description("The user name of the user who last updated the input"),
            fieldWithPath("question").description("The id of the question for this response"),
            fieldWithPath("formInput").description("The id of the form input"),
            fieldWithPath("formInputMaxWordCount").description("The max word count of the form input"),
            fieldWithPath("application").description("The id of the application for the application"),
            fieldWithPath("fileEntry").description("The id of the file entry"),
            fieldWithPath("filename").description("The name of the file"),
            fieldWithPath("filesizeBytes").description("The size of the file")
    };
}
