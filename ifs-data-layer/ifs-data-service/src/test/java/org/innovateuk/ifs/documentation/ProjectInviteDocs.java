package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.invite.builder.ProjectUserInviteResourceBuilder;
import org.innovateuk.ifs.invite.constant.InviteStatus;

import java.util.UUID;

import static org.innovateuk.ifs.invite.builder.ProjectUserInviteResourceBuilder.newProjectUserInviteResource;

public class ProjectInviteDocs {

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