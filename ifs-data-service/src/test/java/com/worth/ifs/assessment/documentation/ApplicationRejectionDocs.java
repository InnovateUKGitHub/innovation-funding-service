package com.worth.ifs.assessment.documentation;

import com.worth.ifs.assessment.builder.ApplicationRejectionResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import static com.worth.ifs.assessment.builder.ApplicationRejectionResourceBuilder.newApplicationRejectionResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class ApplicationRejectionDocs {
    public static final FieldDescriptor[] applicationRejectionResourceFields = {
            fieldWithPath("rejectReason").description("The reason for rejecting the application."),
            fieldWithPath("rejectComment").description("Any other comments about the reason why this application is being rejected.")
    };

    public static final ApplicationRejectionResourceBuilder applicationRejectionResourceBuilder = newApplicationRejectionResource()
            .withRejectReason("Conflict of interest")
            .withRejectComment("Member of board of directors");
}