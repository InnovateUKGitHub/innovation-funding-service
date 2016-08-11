package com.worth.ifs.invite.security;


import com.worth.ifs.BasePermissionRulesTest;
import com.worth.ifs.invite.domain.ProjectInvite;
import com.worth.ifs.invite.resource.InviteProjectResource;
import com.worth.ifs.project.builder.ProjectBuilder;
import com.worth.ifs.project.domain.Project;
import com.worth.ifs.user.builder.OrganisationBuilder;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import static com.worth.ifs.invite.builder.ProjectInviteBuilder.newInvite;
import static com.worth.ifs.project.builder.ProjectUserBuilder.newProjectUser;
import static com.worth.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static com.worth.ifs.project.builder.ProjectUserBuilder.newProjectUser;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static com.worth.ifs.user.resource.UserRoleType.COLLABORATOR;
import static com.worth.ifs.user.resource.UserRoleType.PARTNER;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ProjectInvitePermissionRulesTest extends BasePermissionRulesTest<ProjectInvitePermissionRules> {

    private UserResource initiatingInvitePartner;
    private UserResource invitedPartner;
    private ProjectInvite projectInvite;
    private InviteProjectResource inviteProjectResource;


    @Override
    protected ProjectInvitePermissionRules supplyPermissionRulesUnderTest() {
        return new ProjectInvitePermissionRules();
    }

    @Before
    public void setup() throws Exception {

        initiatingInvitePartner = newUserResource().withId(10L).build();
        invitedPartner = newUserResource().withId(40L).build();

        {
            final Organisation organisation = OrganisationBuilder.newOrganisation().withId(25L).build();
            final Project project = ProjectBuilder.newProject().withId(1L).build();

            projectInvite = newInvite().withProject(project).withOrganisation(organisation).build();
            inviteProjectResource = new InviteProjectResource();
            inviteProjectResource.setProject(project.getId());
            inviteProjectResource.setOrganisation(organisation.getId());

            when(organisationRepositoryMock.findOne(organisation.getId())).thenReturn(organisation);
            when(inviteProjectRepositoryMock.findOne(projectInvite.getId())).thenReturn(projectInvite);

            when(projectUserRepositoryMock.findByProjectIdAndRoleIdAndUserId(project.getId(),getRole(PARTNER).getId(),
                    initiatingInvitePartner.getId())).thenReturn(newProjectUser().withRole(getRole(PARTNER)).build());

            when(projectUserRepositoryMock.findByProjectIdAndRoleIdAndUserId(project.getId(), getRole(PARTNER).getId(),
                    invitedPartner.getId())).thenReturn(newProjectUser().withRole(getRole(PARTNER)).build());



            when(projectUserRepositoryMock.findOneByProjectIdAndUserIdAndOrganisationIdAndRoleId(project.getId(),
                    invitedPartner.getId(), organisation.getId(), getRole(PARTNER).getId())).
                    thenReturn(newProjectUser().withRole(getRole(PARTNER)).build());

        }

    }

    @Test
    public void testPartnersCanViewOtherPartnersInviteForSameProject() {
         assertFalse(rules.partnersOnProjectCanViewInvite(inviteProjectResource, initiatingInvitePartner));

    }

}
