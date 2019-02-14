package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.invite.builder.ProjectUserInviteResourceBuilder;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.springframework.restdocs.payload.FieldDescriptor;

import java.util.UUID;

import static org.innovateuk.ifs.documentation.RestDocsUtil.createTopLevelArrayOfDocumentation;
import static org.innovateuk.ifs.invite.builder.ProjectUserInviteResourceBuilder.newProjectUserInviteResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class ProjectInviteDocs {

    public static final FieldDescriptor[] projectInviteFields = {
            fieldWithPath("id").description("Id of the ProjectInvite"),
            fieldWithPath("status").description("The current InviteStatus of the ProjectInvite (e.g. CREATED)"),
            fieldWithPath("user").description("Id of the User for which the Invite is intended (if they exist in the system)"),
            fieldWithPath("project").description("Id of the Project for which the Invite is intended"),
            fieldWithPath("projectName").description("Name of the Project for which the Invite is intended"),
            fieldWithPath("applicationId").description("Id of the Application that the Project belongs to"),
            fieldWithPath("competitionName").description("Name of the Competition that the Project belongs to"),
            fieldWithPath("email").description("The email address of the Invite recipient"),
            fieldWithPath("hash").description("The hash used to access the Invite"),
            fieldWithPath("leadApplicant").description("Name of the Lead Applicant of the Application"),
            fieldWithPath("leadOrganisationId").description("Id of the Lead Organisation of the Application"),
            fieldWithPath("leadOrganisation").description("Name of the Lead Organisation of the Application"),
            fieldWithPath("name").description("Name of the Invite recipient"),
            fieldWithPath("nameConfirmed").description("Confirmed name of the Invite recipient"),
            fieldWithPath("organisation").description("Id of the Organisation that the Invite recipient is being invited into"),
            fieldWithPath("organisationName").description("Name of the Organisation that the Invite recipient is being invited into")
    };

    public static final FieldDescriptor[] projectInviteFieldsList = createTopLevelArrayOfDocumentation(projectInviteFields);

    public static final ProjectUserInviteResourceBuilder PROJECT_USER_INVITE_RESOURCE_BUILDER = newProjectUserInviteResource().
            withStatus(InviteStatus.CREATED).
            withUser(654L).
            withProject(123L).
            withProjectName("My Project").
            withApplicationId(456L).
            withCompetitionName("My Competition").
            withEmail("test@example.com").
            withHash(UUID.randomUUID().toString()).
            withLeadApplicant("Steve Smith").
            withLeadOrganisation(789L).
            withName("Jessica Doe").
            withNameConfirmed("Jessica Doe").
            withOrganisation(987L).
            withOrganisationName("Empire Ltd");

}