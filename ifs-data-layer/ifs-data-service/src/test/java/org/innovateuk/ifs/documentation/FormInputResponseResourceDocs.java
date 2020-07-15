package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class FormInputResponseResourceDocs {
    public static final FieldDescriptor[] formInputResponseResourceFields = {
            fieldWithPath("id").description("The response of the form input response"),
            fieldWithPath("updateDate").description("The update date of the form input response"),
            fieldWithPath("value").description("The value of the form input response"),
            fieldWithPath("updatedBy").description("The updated by of the form input response"),
            fieldWithPath("updatedByUser").description("The updated by user of the form input response"),
            fieldWithPath("updatedByUserName").description("The updated by user name of the form input response"),
            fieldWithPath("question").description("The queston of the form input response"),
            fieldWithPath("formInput").description("The form input of the form input response"),
            fieldWithPath("formInputMaxWordCount").description("The max word count of the form input response"),
            fieldWithPath("application").description("The application of the form input response"),
            fieldWithPath("fileEntries[]").description("The file entries of the form input response"),
            fieldWithPath("fileEntry").description("The file entry of the form input response"),
            fieldWithPath("filename").description("The file name of the form input response"),
            fieldWithPath("filesizeBytes").description("The file size of the form input response"),
            fieldWithPath("multipleChoiceOptionId").description("The id of the multiple choice form input response"),
            fieldWithPath("multipleChoiceOptionText").description("The value of the multiple choice form input response")
    };
}
