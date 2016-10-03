package com.worth.ifs;

import com.worth.ifs.invite.domain.ProjectParticipantRole;
import com.worth.ifs.project.domain.Project;
import com.worth.ifs.project.domain.ProjectUser;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.resource.RoleResource;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.resource.UserRoleType;
import org.junit.Before;
import org.mockito.InjectMocks;

import java.util.List;

import static com.worth.ifs.application.builder.ApplicationBuilder.newApplication;
import static com.worth.ifs.invite.domain.ProjectParticipantRole.PROJECT_PARTNER;
import static com.worth.ifs.project.builder.ProjectBuilder.newProject;
import static com.worth.ifs.project.builder.ProjectUserBuilder.newProjectUser;
import static com.worth.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static com.worth.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static com.worth.ifs.user.builder.RoleBuilder.newRole;
import static com.worth.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static com.worth.ifs.user.resource.UserRoleType.*;
import static com.worth.ifs.util.CollectionFunctions.simpleFilter;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.mockito.Mockito.when;

/**
 * A base class for testing @PermissionRules-annotated classes
 */
public abstract class BasePermissionRulesTest<T> extends BaseUnitTestMocksTest {

    @InjectMocks
    protected T rules = supplyPermissionRulesUnderTest();

    protected List<Role> allRoles;

    protected List<RoleResource> allRolesResources;

    protected List<UserResource> allGlobalRoleUsers;

    protected RoleResource compAdminRole() {
        return getRoleResource(COMP_ADMIN);
    }

    protected RoleResource assessorRole() { return getRoleResource(ASSESSOR); }

    protected UserResource compAdminUser() {
        return getUserWithRole(COMP_ADMIN);
    }

    protected UserResource projectFinanceUser() {
        return getUserWithRole(PROJECT_FINANCE);
    }

    protected UserResource assessorUser() {
        return getUserWithRole(ASSESSOR);
    }

    protected RoleResource systemRegistrationRole() {
        return getRoleResource(SYSTEM_REGISTRATION_USER);
    }

    protected UserResource systemRegistrationUser() {
        return getUserWithRole(SYSTEM_REGISTRATION_USER);
    }

    @Before
    public void setupSetsOfData() {
        allRoles = newRole().withType(UserRoleType.values()).build(UserRoleType.values().length);
        allRolesResources = allRoles.stream().map(role -> newRoleResource().withType(UserRoleType.fromName(role.getName())).build()).collect(toList());
        allGlobalRoleUsers = simpleMap(allRolesResources, role -> newUserResource().withRolesGlobal(singletonList(role)).build());

        // Set up global role method mocks
        for (Role role : allRoles) {
            when(roleRepositoryMock.findOneByName(role.getName())).thenReturn(role);
        }
    }

    private UserResource createUserWithRoles(UserRoleType... types) {
        List<RoleResource> roles = simpleMap(asList(types), this::getRoleResource);
        return newUserResource().withRolesGlobal(roles).build();
    }

    protected UserResource getUserWithRole(UserRoleType type) {
        return simpleFilter(allGlobalRoleUsers, user -> simpleMap(user.getRoles(), RoleResource::getName).contains(type.getName())).get(0);
    }

    private RoleResource getRoleResource(UserRoleType type) {
        return simpleFilter(allRolesResources, role -> role.getName().equals(type.getName())).get(0);
    }

    protected Role getRole(UserRoleType type) {
        return simpleFilter(allRoles, role -> role.getName().equals(type.getName())).get(0);
    }

    protected void setUpUserAsProjectManager(ProjectResource projectResource, UserResource user) {

        List<ProjectUser> projectManagerUser = newProjectUser().build(1);

        when(projectUserRepositoryMock.findByProjectIdAndUserIdAndRole(projectResource.getId(), user.getId(), ProjectParticipantRole.PROJECT_MANAGER ))
                .thenReturn(projectManagerUser);
    }

    protected void setUpUserNotAsProjectManager(UserResource user) {
        List<RoleResource> projectManagerUser = emptyList();
        user.setRoles(projectManagerUser);
    }

    protected void setupUserAsPartner(ProjectResource project, UserResource user) {
        setupPartnerExpectations(project, user, true);
    }

    protected void setupUserNotAsPartner(ProjectResource project, UserResource user) {
        setupPartnerExpectations(project, user, false);
    }

    protected void setUpUserAsCompAdmin(ProjectResource project, UserResource user) {
        List<RoleResource> compAdminRoleResource = newRoleResource().withType(UserRoleType.COMP_ADMIN).build(1);
        user.setRoles(compAdminRoleResource);
    }

    protected void setUpUserNotAsCompAdmin(ProjectResource project, UserResource user) {
        List<RoleResource> compAdminRoleResource = emptyList();
        user.setRoles(compAdminRoleResource);
    }

    protected void setUpUserAsProjectFinanceUser(ProjectResource project, UserResource user) {
        List<RoleResource> projectFinanceUser = newRoleResource().withType(UserRoleType.PROJECT_FINANCE).build(1);
        user.setRoles(projectFinanceUser);
    }

    protected void setUpUserNotAsProjectFinanceUser(ProjectResource project, UserResource user) {
        List<RoleResource> projectFinanaceUser = emptyList();
        user.setRoles(projectFinanaceUser);
    }

    protected void setupPartnerExpectations(ProjectResource project, UserResource user, boolean userIsPartner) {
        Role partnerRole = newRole().build();
        List<ProjectUser> partnerProjectUser = newProjectUser().build(1);

        when(roleRepositoryMock.findOneByName(PARTNER.getName())).thenReturn(partnerRole);
        when(projectUserRepositoryMock.findByProjectIdAndUserIdAndRole(project.getId(), user.getId(), PROJECT_PARTNER)).thenReturn(userIsPartner ? partnerProjectUser : emptyList());
    }
    protected void setupUserAsLeadPartner(ProjectResource project, UserResource user) {
        setupLeadPartnerExpectations(project, user, true);
    }

    protected void setupUserNotAsLeadPartner(ProjectResource project, UserResource user) {
        setupLeadPartnerExpectations(project, user, false);
    }
    private void setupLeadPartnerExpectations(ProjectResource project, UserResource user, boolean userIsLeadPartner) {

        com.worth.ifs.application.domain.Application originalApplication = newApplication().build();
        Project projectEntity = newProject().withApplication(originalApplication).build();
        Role leadApplicantRole = newRole().build();
        Role partnerRole = newRole().build();
        Organisation leadOrganisation = newOrganisation().build();
        ProcessRole leadApplicantProcessRole = newProcessRole().withOrganisation(leadOrganisation).build();

        // find the lead organisation
        when(projectRepositoryMock.findOne(project.getId())).thenReturn(projectEntity);
        when(roleRepositoryMock.findOneByName(LEADAPPLICANT.getName())).thenReturn(leadApplicantRole);
        when(processRoleRepositoryMock.findOneByApplicationIdAndRoleId(projectEntity.getApplication().getId(), leadApplicantRole.getId())).thenReturn(leadApplicantProcessRole);

        // see if the user is a partner on the lead organisation
        when(roleRepositoryMock.findOneByName(PARTNER.getName())).thenReturn(partnerRole);
        when(projectUserRepositoryMock.findOneByProjectIdAndUserIdAndOrganisationIdAndRole(
                project.getId(), user.getId(), leadOrganisation.getId(), PROJECT_PARTNER)).thenReturn(userIsLeadPartner ? newProjectUser().build() : null);
    }

    protected abstract T supplyPermissionRulesUnderTest();

}
