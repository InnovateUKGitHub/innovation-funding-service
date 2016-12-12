package org.innovateuk.ifs.form.documentation;

import org.innovateuk.ifs.form.builder.FormInputResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.innovateuk.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static org.innovateuk.ifs.form.resource.FormInputType.TEXTAREA;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class FormInputResourceDocs {
    public static final FieldDescriptor[] formInputResourceFields = {
        fieldWithPath("id").description("Id of the formInputResource"),
        fieldWithPath("wordCount").description("amount of words in the response"),
        fieldWithPath("type").description("type of the form input"),
        fieldWithPath("question").description("id of the question this input belongs to"),
        fieldWithPath("competition").description("id of the competition the form input belongs to"),
        fieldWithPath("inputValidators").description("list of inputValidator ids"),
        fieldWithPath("description").description("description"),
        fieldWithPath("includedInApplicationSummary").description("whether the input should be included in the application summary"),
        fieldWithPath("formValidators").description("list of formValidator ids"),
        fieldWithPath("guidanceTitle").description("question on which the guidance for this input is based"),
        fieldWithPath("guidanceAnswer").description("answer to the guidanceTitle"),
        fieldWithPath("guidanceRows").description("breakdown of guidance by score"),
        fieldWithPath("priority").description("priority of the input, used for rendering purposes only"),
        fieldWithPath("scope").description("the scope for which the input should be rendered")
    };

    public static final FormInputResourceBuilder formInputResourceBuilder = newFormInputResource()
        .withId(1L)
        .withType(TEXTAREA)
        .withWordCount(140);
}
