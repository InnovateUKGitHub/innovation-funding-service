package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class MonitoringOfficerDocs {
    public static final FieldDescriptor[] monitoringOfficerResourceFields = {
            fieldWithPath("id").description("Id of the Monitoring Officer"),
            fieldWithPath("firstName").description("First name of the Monitoring Officer"),
            fieldWithPath("lastName").description("Last name of the Monitoring Officer"),
            fieldWithPath("email").description("Email address of the Monitoring Officer"),
            fieldWithPath("phoneNumber").description("Phone number of the Monitoring Officer"),
            fieldWithPath("project").description("Project id of the Project to which the Monitoring Officer is assigned")
    };
}
