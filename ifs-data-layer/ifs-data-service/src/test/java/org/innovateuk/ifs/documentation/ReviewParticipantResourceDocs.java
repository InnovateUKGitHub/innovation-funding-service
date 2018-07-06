package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class ReviewParticipantResourceDocs {

    public static final FieldDescriptor[] reviewParticipantFields = {
            fieldWithPath("id").description("Id of the review participant"),
    };


}
