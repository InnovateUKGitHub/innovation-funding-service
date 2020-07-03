package org.innovateuk.ifs.application.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class FormInputResponseResourceDocs {
    public static final FieldDescriptor[] formInputResponseResourceFields = {
            fieldWithPath("id").description("Id of the FormInputResponseResource"),
            fieldWithPath("updateDate").description("The date of the last update"),
            fieldWithPath("value").description("The value of the response"),
            fieldWithPath("updatedBy").type("Number").description("The id of who last updated the input"),
            fieldWithPath("updatedByUser").description("The id of the user who last updated the input"),
            fieldWithPath("updatedByUserName").type("String").description("The user name of the user who last updated the input"),
            fieldWithPath("question").description("The id of the question for this response"),
            fieldWithPath("formInput").type("Number").description("The id of the form input"),
            fieldWithPath("formInputMaxWordCount").description("The max word count of the form input"),
            fieldWithPath("application").type("Number").description("The id of the application for the application"),
            fieldWithPath("fileEntries[]").description("the file entries"),
            fieldWithPath("fileEntry").description("The file entry of the form input response"),
            fieldWithPath("filename").description("The file name of the form input response"),
            fieldWithPath("filesizeBytes").description("The file size of the form input response")
    };
}
