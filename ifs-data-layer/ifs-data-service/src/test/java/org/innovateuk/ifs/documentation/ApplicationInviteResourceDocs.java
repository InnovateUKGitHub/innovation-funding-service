package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class ApplicationInviteResourceDocs {

    public static final FieldDescriptor[] applicationInviteResourceFields = {
            fieldWithPath("leadOrganisationId").description("Lead organisation Id of the invite resource"),
            fieldWithPath("leadOrganisation").description("Lead organisation of the invite resource"),
            fieldWithPath("leadApplicant").description("Lead applicant of the invite resource"),
            fieldWithPath("leadApplicantEmail").description("Lead applicant email of the invite resource"),
            fieldWithPath("id").description("Id of the invite resource"),
            fieldWithPath("user").description("User of the invite resource"),
            fieldWithPath("name").description("Name of the invite resource"),
            fieldWithPath("nameConfirmed").description("Name confirmed of the invite resource"),
            fieldWithPath("email").description("Email of the invite resource"),
            fieldWithPath("application").description("Application of the invite resource"),
            fieldWithPath("competitionId").description("Application Id of the invite resource"),
            fieldWithPath("competitionName").description("Competition name of the invite resource"),
            fieldWithPath("applicationName").description("Application name of the invite resource"),
            fieldWithPath("inviteOrganisation").description("Invite organisation of the invite resource"),
            fieldWithPath("inviteOrganisationName").description("Invite organisation name of the invite resource"),
            fieldWithPath("inviteOrganisationNameConfirmed").description("Invite organisation name confirmed of the invite resource"),
            fieldWithPath("hash").description("Hash of the invite resource"),
            fieldWithPath("status").description("Status of the invite resource"),
            fieldWithPath("sentOn").description("Sent on of the invite resource"),
            fieldWithPath("inviteOrganisationNameConfirmedSafe").description("Invite organisation name confirmed safe of the invite resource"),
    };
}
