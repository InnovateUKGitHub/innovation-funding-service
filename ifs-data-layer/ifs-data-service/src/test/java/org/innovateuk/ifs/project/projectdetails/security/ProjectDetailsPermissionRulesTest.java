package org.innovateuk.ifs.project.projectdetails.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectProcess;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.core.repository.ProjectProcessRepository;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.core.builder.ProjectProcessBuilder.newProjectProcess;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.PROJECT_PARTNER;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.PROJECT_USER_ROLES;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.IFS_ADMINISTRATOR;
import static org.innovateuk.ifs.user.resource.Role.LEADAPPLICANT;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ProjectDetailsPermissionRulesTest extends BasePermissionRulesTest<ProjectDetailsPermissionRules> {
    private ProjectProcess projectProcess;
    private ProjectResource project;

    @Mock
    private ProjectProcessRepository projectProcessRepository;

    @Before
    public void setUp() throws Exception {
        projectProcess = newProjectProcess().withActivityState(ProjectState.SETUP).build();
        project = newProjectResource().withProjectState(ProjectState.SETUP).build();
    }

    @Override
    protected ProjectDetailsPermissionRules supplyPermissionRulesUnderTest() {
        return new ProjectDetailsPermissionRules();
    }

    @Test
    public void leadPartnersCanUpdateTheBasicProjectDetails() {
        UserResource user = newUserResource().build();

        setupUserAsLeadPartner(project, user);

        when(projectProcessRepository.findOneByTargetId(project.getId())).thenReturn(projectProcess);
        assertTrue(rules.leadPartnersCanUpdateTheBasicProjectDetails(project, user));
    }

    @Test
    public void leadPartnersCanUpdateTheBasicProjectDetailsButUserNotLeadPartner() {

        Application originalApplication = newApplication().build();
        Project projectEntity = newProject().withApplication(originalApplication).build();
        UserResource user = newUserResource().build();
        Organisation leadOrganisation = newOrganisation().build();
        ProcessRole leadApplicantProcessRole = newProcessRole().withOrganisationId(leadOrganisation.getId()).build();

        // find the lead organisation
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(projectEntity));
        when(processRoleRepository.findOneByApplicationIdAndRole(projectEntity.getApplication().getId(), LEADAPPLICANT)).thenReturn(leadApplicantProcessRole);

        // see if the user is a partner on the lead organisation
        when(organisationRepository.findById(leadOrganisation.getId())).thenReturn(Optional.of(leadOrganisation));
        when(projectUserRepository.findOneByProjectIdAndUserIdAndOrganisationIdAndRole(
                project.getId(), user.getId(), leadOrganisation.getId(), PROJECT_PARTNER)).thenReturn(null);

        assertFalse(rules.leadPartnersCanUpdateTheBasicProjectDetails(project, user));
    }

    @Test
    public void ifsAdministratorCanUpdateTheProjectStartDate() {
        UserResource user = newUserResource().withRoleGlobal(IFS_ADMINISTRATOR).build();

        when(projectProcessRepository.findOneByTargetId(project.getId())).thenReturn(projectProcess);
        assertTrue(rules.ifsAdministratorCanUpdateTheProjectStartDate(project, user));
    }

    @Test
    public void partnersCanUpdateTheirOwnOrganisationsFinanceContacts() {
        Organisation organisation = newOrganisation().build();
        ProjectOrganisationCompositeId composite = new ProjectOrganisationCompositeId(project.getId(), organisation.getId());
        UserResource user = newUserResource().build();
        setupUserAsPartner(project, user);

        when(projectUserRepository.findFirstByProjectIdAndUserIdAndOrganisationIdAndRoleIn(project.getId(), user.getId(), organisation.getId(), PROJECT_USER_ROLES.stream().collect(Collectors.toList()))).thenReturn(new ProjectUser());
        when(projectProcessRepository.findOneByTargetId(project.getId())).thenReturn(projectProcess);

        assertTrue(rules.partnersCanUpdateTheirOwnOrganisationsFinanceContacts(composite, user));
    }

    @Test
    public void partnersCanUpdateTheirOwnOrganisationsFinanceContactsButUserNotPartner() {
        Organisation organisation = newOrganisation().build();
        ProjectOrganisationCompositeId composite = new ProjectOrganisationCompositeId(project.getId(), organisation.getId());
        UserResource user = newUserResource().build();

        when(projectUserRepository.findByProjectIdAndUserIdAndRoleIsIn(project.getId(), user.getId(), PROJECT_USER_ROLES.stream().collect(Collectors.toList()))).thenReturn(emptyList());

        assertFalse(rules.partnersCanUpdateTheirOwnOrganisationsFinanceContacts(composite, user));
    }

    @Test
    public void partnersCanUpdateTheirOwnOrganisationsFinanceContactsButUserNotMemberOfOrganisation() {
        Organisation organisation = newOrganisation().build();
        ProjectOrganisationCompositeId composite = new ProjectOrganisationCompositeId(project.getId(), organisation.getId());
        UserResource user = newUserResource().build();
        setupUserAsPartner(project, user);

        when(projectUserRepository.findOneByProjectIdAndUserIdAndOrganisationIdAndRole(project.getId(), user.getId(), organisation.getId(), PROJECT_PARTNER)).thenReturn(null);
        when(projectProcessRepository.findOneByTargetId(project.getId())).thenReturn(projectProcess);

        assertFalse(rules.partnersCanUpdateTheirOwnOrganisationsFinanceContacts(composite, user));
    }

    @Test
    public void partnersCanUpdateProjectLocationForTheirOwnOrganisationWhenUserDoesNotBelongToGivenOrganisation() {

        long projectId = 1L;
        long organisationId = 2L;
        ProjectOrganisationCompositeId composite = new ProjectOrganisationCompositeId(projectId, organisationId);
        UserResource user = newUserResource().build();

        when(projectUserRepository.findFirstByProjectIdAndUserIdAndOrganisationIdAndRoleIn(projectId, user.getId(), organisationId, PROJECT_USER_ROLES.stream().collect(Collectors.toList()))).thenReturn(null);

        assertFalse(rules.partnersCanUpdateProjectLocationForTheirOwnOrganisation(composite, user));
    }

    @Test
    public void partnersCanUpdateProjectLocationForTheirOwnOrganisationSuccess() {

        long projectId = 1L;
        long organisationId = 2L;
        ProjectOrganisationCompositeId composite = new ProjectOrganisationCompositeId(projectId, organisationId);
        UserResource user = newUserResource().build();

        when(projectUserRepository.findFirstByProjectIdAndUserIdAndOrganisationIdAndRoleIn(projectId, user.getId(), organisationId, PROJECT_USER_ROLES.stream().collect(Collectors.toList()))).thenReturn(new ProjectUser());

        assertTrue(rules.partnersCanUpdateProjectLocationForTheirOwnOrganisation(composite, user));
    }
}