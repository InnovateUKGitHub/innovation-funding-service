package com.worth.ifs.form.documentation;

import com.worth.ifs.form.builder.FormInputResourceBuilder;

import org.springframework.restdocs.payload.FieldDescriptor;

import static com.worth.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class FormInputResourceDocs {
    public static final FieldDescriptor[] formInputResourceFields = {
        fieldWithPath("id").description("Id of the formInputResource"),
        fieldWithPath("wordCount").description("amount of words in the response"),
        fieldWithPath("formInputType").description("forminputType"),
        fieldWithPath("formInputTypeTitle").description("formInputTypeTitle"),
        fieldWithPath("question").description("id of the question this input belongs to"),
        fieldWithPath("competition").description("id of the competition the form input belongs to"),
        fieldWithPath("inputValidators").description("list of inputValidator ids"),
        fieldWithPath("description").description("description"),
        fieldWithPath("includedInApplicationSummary").description("whether the input should be included in the application summary"),
        fieldWithPath("formValidators").description("list of formValidator ids"),
        fieldWithPath("guidanceQuestion").description("question on which the guidance for this input is based"),
        fieldWithPath("guidanceAnswer").description("answer to the guidanceQuestion"),
        fieldWithPath("priority").description("priority of the input, used for rendering purposes only"),
        fieldWithPath("scope").description("the scope for which the input should be rendered")
    };

    public static final FormInputResourceBuilder formInputResourceBuilder = newFormInputResource()
        .withId(1L)
        .withWordCount(140);
}
