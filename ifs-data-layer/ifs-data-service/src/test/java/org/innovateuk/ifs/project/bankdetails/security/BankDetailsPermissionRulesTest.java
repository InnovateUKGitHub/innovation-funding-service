package org.innovateuk.ifs.project.bankdetails.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.bankdetails.resource.BankDetailsResource;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectProcess;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.core.repository.ProjectProcessRepository;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static junit.framework.TestCase.assertFalse;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.project.bankdetails.builder.BankDetailsResourceBuilder.newBankDetailsResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.core.builder.ProjectProcessBuilder.newProjectProcess;
import static org.innovateuk.ifs.project.core.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.PROJECT_PARTNER;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.PROJECT_USER_ROLES;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class BankDetailsPermissionRulesTest extends BasePermissionRulesTest<BankDetailsPermissionRules> {

    @Mock
    private ProjectProcessRepository projectProcessRepository;

    @Override
    protected BankDetailsPermissionRules supplyPermissionRulesUnderTest() {
        return new BankDetailsPermissionRules();
    }

    private UserResource user;
    private UserResource projectFinanceUser;
    private ProjectResource project;
    private List<ProjectUser> partnerProjectUser;
    private OrganisationResource organisationResource;
    private BankDetailsResource bankDetailsResource;
    private ProjectProcess projectProcess;

    @Before
    public void setUp() {
        user = newUserResource().build();
        project = newProjectResource().build();
        Project projectProcessProject = newProject().build();
        projectFinanceUser = newUserResource().withRolesGlobal(singletonList(Role.PROJECT_FINANCE)).build();
        partnerProjectUser = newProjectUser().build(1);
        organisationResource = newOrganisationResource().build();
        bankDetailsResource = newBankDetailsResource().withOrganisation(organisationResource.getId()).withProject(project.getId()).build();

        projectProcess = newProjectProcess()
                .withProject(projectProcessProject)
                .withActivityState(ProjectState.SETUP)
                .build();
    }

    @Test
    public void partnersCanSeeBankDetailsOfTheirOwnOrg() {
        when(projectUserRepository.findByProjectIdAndUserIdAndRoleIsIn(project.getId(), user.getId(), PROJECT_USER_ROLES.stream().collect(Collectors.toList()))).thenReturn(partnerProjectUser);
        when(projectUserRepository.findOneByProjectIdAndUserIdAndOrganisationIdAndRoleIn(project.getId(), user.getId(), organisationResource.getId(), PROJECT_USER_ROLES.stream().collect(Collectors.toList()))).thenReturn(partnerProjectUser.get(0));
        assertTrue(rules.partnersCanSeeTheirOwnOrganisationsBankDetails(bankDetailsResource, user));
    }

    @Test
    public void nonPartnersCannotSeeBankDetails() {
        when(projectUserRepository.findByProjectIdAndUserIdAndRole(project.getId(), user.getId(), PROJECT_PARTNER)).thenReturn(Collections.emptyList());
        assertFalse(rules.partnersCanSeeTheirOwnOrganisationsBankDetails(bankDetailsResource, user));
    }

    @Test
    public void partnersCannotSeeBankDetailsOfAnotherOrganisation() {
        when(projectUserRepository.findByProjectIdAndUserIdAndRole(project.getId(), user.getId(), PROJECT_PARTNER)).thenReturn(partnerProjectUser);
        when(projectUserRepository.findFirstByProjectIdAndUserIdAndOrganisationIdAndRoleIn(project.getId(), user.getId(), organisationResource.getId(), PROJECT_USER_ROLES.stream().collect(Collectors.toList()))).thenReturn(null);
        assertFalse(rules.partnersCanSeeTheirOwnOrganisationsBankDetails(bankDetailsResource, user));
    }

    @Test
    public void partnersCanUpdateTheirOwnOrganisationBankDetails() {
        when(projectProcessRepository.findOneByTargetId(project.getId())).thenReturn(projectProcess);
        when(projectUserRepository.findByProjectIdAndUserIdAndRoleIsIn(project.getId(), user.getId(), PROJECT_USER_ROLES.stream().collect(Collectors.toList()))).thenReturn(partnerProjectUser);
        when(projectUserRepository.findFirstByProjectIdAndUserIdAndOrganisationIdAndRoleIn(project.getId(), user.getId(), organisationResource.getId(), PROJECT_USER_ROLES.stream().collect(Collectors.toList()))).thenReturn(partnerProjectUser.get(0));
        assertTrue(rules.partnersCanSubmitTheirOwnOrganisationsBankDetails(bankDetailsResource, user));
    }

    @Test
    public void nonPartnersCannotUpdateBankDetails() {
        when(projectUserRepository.findByProjectIdAndUserIdAndRole(project.getId(), user.getId(), PROJECT_PARTNER)).thenReturn(Collections.emptyList());
        assertFalse(rules.partnersCanSubmitTheirOwnOrganisationsBankDetails(bankDetailsResource, user));
    }

    @Test
    public void partnersCannotUpdateBankDetailsOfAnotherOrg() {
        when(projectUserRepository.findByProjectIdAndUserIdAndRole(project.getId(), user.getId(), PROJECT_PARTNER)).thenReturn(partnerProjectUser);
        when(projectUserRepository.findOneByProjectIdAndUserIdAndOrganisationIdAndRole(project.getId(), user.getId(), organisationResource.getId(), PROJECT_PARTNER)).thenReturn(null);
        assertFalse(rules.partnersCanSubmitTheirOwnOrganisationsBankDetails(bankDetailsResource, user));
    }

    @Test
    public void projectFinanceUserCanSeeAllBankDetailsForAllOrganisations() {
        assertTrue(rules.projectFinanceUsersCanSeeAllBankDetailsOnAllProjects(bankDetailsResource, projectFinanceUser));
    }

    @Test
    public void projectFinanceUserCanUpdateBankDetailsForAllOrganisations() {
        when(projectProcessRepository.findOneByTargetId(project.getId())).thenReturn(projectProcess);
        assertTrue(rules.projectFinanceUsersCanUpdateAnyOrganisationsBankDetails(bankDetailsResource, projectFinanceUser));
    }
}