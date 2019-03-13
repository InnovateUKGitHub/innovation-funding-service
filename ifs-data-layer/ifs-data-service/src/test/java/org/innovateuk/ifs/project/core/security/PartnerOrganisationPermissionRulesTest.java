package org.innovateuk.ifs.project.core.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.project.builder.PartnerOrganisationResourceBuilder.newPartnerOrganisationResource;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.core.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.PROJECT_PARTNER;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class PartnerOrganisationPermissionRulesTest extends BasePermissionRulesTest<PartnerOrganisationPermissionRules> {

    @Override
    protected PartnerOrganisationPermissionRules supplyPermissionRulesUnderTest() {
        return new PartnerOrganisationPermissionRules();
    }

    @Test
    public void internalUsersCanViewPartnerOrgs() {

        UserResource user = newUserResource().withRolesGlobal(singletonList(Role.COMP_ADMIN)).build();

        PartnerOrganisationResource partnerOrg = newPartnerOrganisationResource().build();

        assertTrue(rules.internalUsersCanViewPartnerOrganisations(partnerOrg, user));
    }

    @Test
    public void externalUsersCannotViewPartnerOrgs() {

        UserResource user = newUserResource().build();

        PartnerOrganisationResource partnerOrg = newPartnerOrganisationResource().build();

        assertFalse(rules.internalUsersCanViewPartnerOrganisations(partnerOrg, user));
    }

    @Test
    public void partnersCannotViewOtherPartnerOrganisations() {

        long projectId = 1L;
        long organisationId = 2L;
        UserResource user = newUserResource().build();

        PartnerOrganisationResource partnerOrg = newPartnerOrganisationResource()
                .withProject(projectId)
                .withOrganisation(organisationId)
                .build();
        when(projectUserRepositoryMock.findOneByProjectIdAndUserIdAndOrganisationIdAndRole(projectId, user.getId(), organisationId, PROJECT_PARTNER)).thenReturn(null);

        assertFalse(rules.partnersCanViewTheirOwnPartnerOrganisation(partnerOrg, user));
    }

    @Test
    public void partnersCanViewTheirOwnPartnerOrganisation() {

        long projectId = 1L;
        long organisationId = 2L;
        UserResource user = newUserResource().build();

        PartnerOrganisationResource partnerOrg = newPartnerOrganisationResource()
                .withProject(projectId)
                .withOrganisation(organisationId)
                .build();
        ProjectUser projectUser = newProjectUser()
                .build();
        when(projectUserRepositoryMock.findOneByProjectIdAndUserIdAndOrganisationIdAndRole(projectId, user.getId(), organisationId, PROJECT_PARTNER)).thenReturn(projectUser);

        assertTrue(rules.partnersCanViewTheirOwnPartnerOrganisation(partnerOrg, user));
    }

    @Test
    public void partnersCanView() {

        UserResource user = newUserResource().withRolesGlobal(singletonList(Role.COMP_ADMIN)).build();
        Project project = newProject().build();
        ProjectUser projectUser = newProjectUser().withProject(project).build();

        when(projectUserRepositoryMock.findByProjectIdAndUserIdAndRole(project.getId(), user.getId(), PROJECT_PARTNER)).thenReturn(singletonList(projectUser));

        PartnerOrganisationResource partnerOrg = newPartnerOrganisationResource().withProject(project.getId()).build();

        assertTrue(rules.partnersOnProjectCanView(partnerOrg, user));
    }

    @Test
    public void nonPartnersCannotView() {

        UserResource user = newUserResource().withRolesGlobal(singletonList(Role.COMP_ADMIN)).build();
        Project project = newProject().build();

        when(projectUserRepositoryMock.findByProjectIdAndUserIdAndRole(project.getId(), user.getId(), PROJECT_PARTNER)).thenReturn(emptyList());

        PartnerOrganisationResource partnerOrg = newPartnerOrganisationResource().withProject(project.getId()).build();

        assertFalse(rules.partnersOnProjectCanView(partnerOrg, user));
    }

    @Test
    public void internalUserCanView() {

        UserResource user = newUserResource().withRolesGlobal(singletonList(Role.COMP_ADMIN)).build();
        Project project = newProject().build();

        PartnerOrganisationResource partnerOrg = newPartnerOrganisationResource().withProject(project.getId()).build();

        assertTrue(rules.internalUsersCanView(partnerOrg, user));
    }

    @Test
    public void monitoringOfficerCanView() {

        UserResource user = newUserResource().withRolesGlobal(singletonList(Role.MONITORING_OFFICER)).build();
        Project project = newProject().build();

        PartnerOrganisationResource partnerOrg = newPartnerOrganisationResource().withProject(project.getId()).build();
        when(projectMonitoringOfficerRepository.existsByProjectIdAndUserId(project.getId(), user.getId())).thenReturn(true);

        assertTrue(rules.monitoringOfficersUsersCanView(partnerOrg, user));
    }

    @Test
    public void externalUsersCannotView() {

        UserResource user = newUserResource().withRolesGlobal(singletonList(Role.PARTNER)).build();
        Project project = newProject().build();

        PartnerOrganisationResource partnerOrg = newPartnerOrganisationResource().withProject(project.getId()).build();

        assertFalse(rules.internalUsersCanView(partnerOrg, user));
    }
}
