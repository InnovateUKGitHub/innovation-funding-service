package org.innovateuk.ifs.project.projectdetails.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.domain.ProjectProcess;
import org.innovateuk.ifs.project.domain.ProjectUser;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.workflow.domain.ActivityState;
import org.junit.Before;
import org.junit.Test;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.invite.domain.ProjectParticipantRole.PROJECT_PARTNER;
import static org.innovateuk.ifs.project.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.builder.ProjectProcessBuilder.newProjectProcess;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.workflow.domain.ActivityType.PROJECT_SETUP;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ProjectDetailsPermissionRulesTest extends BasePermissionRulesTest<ProjectDetailsPermissionRules> {
    private ProjectProcess projectProcess;
    private ProjectResource project;

    @Before
    public void setUp() throws Exception {
        projectProcess = newProjectProcess().withActivityState(new ActivityState(PROJECT_SETUP, ProjectState.SETUP.getBackingState())).build();
        project = newProjectResource().withProjectState(ProjectState.SETUP).build();
    }

    @Override
    protected ProjectDetailsPermissionRules supplyPermissionRulesUnderTest() {
        return new ProjectDetailsPermissionRules();
    }

    @Test
    public void testLeadPartnersCanUpdateTheBasicProjectDetails() {
        UserResource user = newUserResource().build();

        setupUserAsLeadPartner(project, user);

        when(projectProcessRepositoryMock.findOneByTargetId(project.getId())).thenReturn(projectProcess);
        assertTrue(rules.leadPartnersCanUpdateTheBasicProjectDetails(project, user));
    }

    @Test
    public void testLeadPartnersCanUpdateTheBasicProjectDetailsButUserNotLeadPartner() {

        Application originalApplication = newApplication().build();
        Project projectEntity = newProject().withApplication(originalApplication).build();
        UserResource user = newUserResource().build();
        Organisation leadOrganisation = newOrganisation().build();
        ProcessRole leadApplicantProcessRole = newProcessRole().withOrganisationId(leadOrganisation.getId()).build();

        // find the lead organisation
        when(projectRepositoryMock.findOne(project.getId())).thenReturn(projectEntity);
        when(processRoleRepositoryMock.findOneByApplicationIdAndRole(projectEntity.getApplication().getId(),  Role.LEADAPPLICANT)).thenReturn(leadApplicantProcessRole);

        // see if the user is a partner on the lead organisation
        when(organisationRepositoryMock.findOne(leadOrganisation.getId())).thenReturn(leadOrganisation);
        when(projectUserRepositoryMock.findOneByProjectIdAndUserIdAndOrganisationIdAndRole(
                project.getId(), user.getId(), leadOrganisation.getId(), PROJECT_PARTNER)).thenReturn(null);

        assertFalse(rules.leadPartnersCanUpdateTheBasicProjectDetails(project, user));
    }

    @Test
    public void testPartnersCanUpdateTheirOwnOrganisationsFinanceContacts() {
        Organisation organisation = newOrganisation().build();
        ProjectOrganisationCompositeId composite = new ProjectOrganisationCompositeId(project.getId(), organisation.getId());
        UserResource user = newUserResource().build();
        setupUserAsPartner(project, user);

        when(projectUserRepositoryMock.findOneByProjectIdAndUserIdAndOrganisationIdAndRole(project.getId(), user.getId(), organisation.getId(), PROJECT_PARTNER)).thenReturn(new ProjectUser());
        when(projectProcessRepositoryMock.findOneByTargetId(project.getId())).thenReturn(projectProcess);

        assertTrue(rules.partnersCanUpdateTheirOwnOrganisationsFinanceContacts(composite, user));
    }

    @Test
    public void testPartnersCanUpdateTheirOwnOrganisationsFinanceContactsButUserNotPartner() {
        Organisation organisation = newOrganisation().build();
        ProjectOrganisationCompositeId composite = new ProjectOrganisationCompositeId(project.getId(), organisation.getId());
        UserResource user = newUserResource().build();

        when(projectUserRepositoryMock.findByProjectIdAndUserIdAndRole(project.getId(), user.getId(), PROJECT_PARTNER)).thenReturn(emptyList());

        assertFalse(rules.partnersCanUpdateTheirOwnOrganisationsFinanceContacts(composite, user));
    }

    @Test
    public void testPartnersCanUpdateTheirOwnOrganisationsFinanceContactsButUserNotMemberOfOrganisation() {
        Organisation organisation = newOrganisation().build();
        ProjectOrganisationCompositeId composite = new ProjectOrganisationCompositeId(project.getId(), organisation.getId());
        UserResource user = newUserResource().build();
        setupUserAsPartner(project, user);

        when(projectUserRepositoryMock.findOneByProjectIdAndUserIdAndOrganisationIdAndRole(project.getId(), user.getId(), organisation.getId(), PROJECT_PARTNER)).thenReturn(null);
        when(projectProcessRepositoryMock.findOneByTargetId(project.getId())).thenReturn(projectProcess);

        assertFalse(rules.partnersCanUpdateTheirOwnOrganisationsFinanceContacts(composite, user));
    }

    @Test
    public void testPartnersCanUpdateProjectLocationForTheirOwnOrganisationWhenUserDoesNotBelongToGivenOrganisation() {

        Long projectId = 1L;
        Long organisationId = 2L;
        ProjectOrganisationCompositeId composite = new ProjectOrganisationCompositeId(projectId, organisationId);
        UserResource user = newUserResource().build();

        when(projectUserRepositoryMock.findOneByProjectIdAndUserIdAndOrganisationIdAndRole(projectId, user.getId(), organisationId, PROJECT_PARTNER)).thenReturn(null);

        assertFalse(rules.partnersCanUpdateProjectLocationForTheirOwnOrganisation(composite, user));
    }

    @Test
    public void testPartnersCanUpdateProjectLocationForTheirOwnOrganisationSuccess() {

        Long projectId = 1L;
        Long organisationId = 2L;
        ProjectOrganisationCompositeId composite = new ProjectOrganisationCompositeId(projectId, organisationId);
        UserResource user = newUserResource().build();

        when(projectUserRepositoryMock.findOneByProjectIdAndUserIdAndOrganisationIdAndRole(projectId, user.getId(), organisationId, PROJECT_PARTNER)).thenReturn(new ProjectUser());

        assertTrue(rules.partnersCanUpdateProjectLocationForTheirOwnOrganisation(composite, user));
    }
}
