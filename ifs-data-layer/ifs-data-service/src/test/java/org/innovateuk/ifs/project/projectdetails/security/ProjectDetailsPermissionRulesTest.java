package org.innovateuk.ifs.project.projectdetails.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.domain.ProjectUser;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.invite.domain.ProjectParticipantRole.PROJECT_PARTNER;
import static org.innovateuk.ifs.project.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.RoleBuilder.newRole;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.LEADAPPLICANT;
import static org.innovateuk.ifs.user.resource.UserRoleType.PARTNER;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ProjectDetailsPermissionRulesTest extends BasePermissionRulesTest<ProjectDetailsPermissionRules> {

    @Override
    protected ProjectDetailsPermissionRules supplyPermissionRulesUnderTest() {
        return new ProjectDetailsPermissionRules();
    }

    @Test
    public void testLeadPartnersCanUpdateTheBasicProjectDetails() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setupUserAsLeadPartner(project, user);

        assertTrue(rules.leadPartnersCanUpdateTheBasicProjectDetails(project, user));
    }

    @Test
    public void testLeadPartnersCanUpdateTheBasicProjectDetailsButUserNotLeadPartner() {

        Application originalApplication = newApplication().build();
        ProjectResource project = newProjectResource().build();
        Project projectEntity = newProject().withApplication(originalApplication).build();
        UserResource user = newUserResource().build();
        Role leadApplicantRole = newRole().build();
        Organisation leadOrganisation = newOrganisation().build();
        ProcessRole leadApplicantProcessRole = newProcessRole().withOrganisationId(leadOrganisation.getId()).build();

        // find the lead organisation
        when(projectRepositoryMock.findOne(project.getId())).thenReturn(projectEntity);
        when(roleRepositoryMock.findOneByName(LEADAPPLICANT.getName())).thenReturn(leadApplicantRole);
        when(processRoleRepositoryMock.findOneByApplicationIdAndRoleId(projectEntity.getApplication().getId(), leadApplicantRole.getId())).thenReturn(leadApplicantProcessRole);

        // see if the user is a partner on the lead organisation
        when(organisationRepositoryMock.findOne(leadOrganisation.getId())).thenReturn(leadOrganisation);
        when(projectUserRepositoryMock.findOneByProjectIdAndUserIdAndOrganisationIdAndRole(
                project.getId(), user.getId(), leadOrganisation.getId(), PROJECT_PARTNER)).thenReturn(null);

        assertFalse(rules.leadPartnersCanUpdateTheBasicProjectDetails(project, user));
    }

    @Test
    public void testPartnersCanUpdateTheirOwnOrganisationsFinanceContacts() {

        ProjectResource project = newProjectResource().build();
        Organisation organisation = newOrganisation().build();
        ProjectOrganisationCompositeId composite = new ProjectOrganisationCompositeId(project.getId(), organisation.getId());
        UserResource user = newUserResource().build();
        setupUserAsPartner(project, user);
        when(projectUserRepositoryMock.findOneByProjectIdAndUserIdAndOrganisationIdAndRole(project.getId(), user.getId(), organisation.getId(), PROJECT_PARTNER)).thenReturn(new ProjectUser());

        assertTrue(rules.partnersCanUpdateTheirOwnOrganisationsFinanceContacts(composite, user));
    }

    @Test
    public void testPartnersCanUpdateTheirOwnOrganisationsFinanceContactsButUserNotPartner() {

        ProjectResource project = newProjectResource().build();
        Organisation organisation = newOrganisation().build();
        ProjectOrganisationCompositeId composite = new ProjectOrganisationCompositeId(project.getId(), organisation.getId());
        UserResource user = newUserResource().build();
        Role partnerRole = newRole().build();

        when(roleRepositoryMock.findOneByName(PARTNER.getName())).thenReturn(partnerRole);
        when(projectUserRepositoryMock.findByProjectIdAndUserIdAndRole(project.getId(), user.getId(), PROJECT_PARTNER)).thenReturn(emptyList());

        assertFalse(rules.partnersCanUpdateTheirOwnOrganisationsFinanceContacts(composite, user));
    }

    @Test
    public void testPartnersCanUpdateTheirOwnOrganisationsFinanceContactsButUserNotMemberOfOrganisation() {

        ProjectResource project = newProjectResource().build();
        Organisation organisation = newOrganisation().build();
        ProjectOrganisationCompositeId composite = new ProjectOrganisationCompositeId(project.getId(), organisation.getId());
        UserResource user = newUserResource().build();
        setupUserAsPartner(project, user);

        when(projectUserRepositoryMock.findOneByProjectIdAndUserIdAndOrganisationIdAndRole(project.getId(), user.getId(), organisation.getId(), PROJECT_PARTNER)).thenReturn(null);

        assertFalse(rules.partnersCanUpdateTheirOwnOrganisationsFinanceContacts(composite, user));
    }

    @Test
    public void testSubmitIsAllowed() {
        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();
        setupUserAsPartner(project, user);

        assertTrue(rules.submitIsAllowed(project.getId(), user));
    }
}
