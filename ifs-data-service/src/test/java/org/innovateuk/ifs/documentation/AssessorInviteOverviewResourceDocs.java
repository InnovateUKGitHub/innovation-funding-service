package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class AssessorInviteOverviewResourceDocs {

    public static final FieldDescriptor[] assessorInviteOverviewResourceFields = {
            fieldWithPath("name").description("Name of the invitee"),
            fieldWithPath("innovationAreas").description("Innovation areas of the invitee"),
            fieldWithPath("compliant").description("Flag to signify if the invitee is compliant. An invitee who is also an existing assessor is compliant if, and only if they’ve completed their Skills and Business Type, and they’ve completed their DoI, and they’ve signed an Agreement."),
            fieldWithPath("status").description("Status of the invite"),
            fieldWithPath("details").description("Additional details, for example, the reason why the invite is declined")
    };
}