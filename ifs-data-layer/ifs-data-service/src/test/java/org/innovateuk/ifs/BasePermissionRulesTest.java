package org.innovateuk.ifs;

import org.innovateuk.ifs.commons.security.evaluator.DefaultPermissionMethodHandler;
import org.innovateuk.ifs.commons.security.evaluator.PermissionedObjectClassToPermissionsToPermissionsMethods;
import org.innovateuk.ifs.invite.domain.ProjectParticipantRole;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.mockito.InjectMocks;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.invite.domain.ProjectParticipantRole.PROJECT_PARTNER;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.core.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.mockito.Mockito.when;

/**
 * A base class for testing @PermissionRules-annotated classes
 */
public abstract class BasePermissionRulesTest<T> extends BaseUnitTestMocksTest {

    @InjectMocks
    protected T rules = supplyPermissionRulesUnderTest();

    protected List<Role> allRoles;

    protected List<UserResource> allGlobalRoleUsers;

    protected List<UserResource> allInternalUsers;

    protected UserResource compAdminUser() {
        return getUserWithRole(COMP_ADMIN);
    }

    protected UserResource projectFinanceUser() {
        return getUserWithRole(PROJECT_FINANCE);
    }

    protected UserResource supportUser() {
        return getUserWithRole(SUPPORT);
    }

    protected UserResource innovationLeadUser() {
        return getUserWithRole(INNOVATION_LEAD);
    }

    protected UserResource assessorUser() {
        return getUserWithRole(ASSESSOR);
    }

    protected UserResource systemRegistrationUser() {
        return getUserWithRole(SYSTEM_REGISTRATION_USER);
    }

    protected UserResource anonymousUser() {
        return (UserResource) ReflectionTestUtils.getField(new DefaultPermissionMethodHandler(new PermissionedObjectClassToPermissionsToPermissionsMethods()), "ANONYMOUS_USER");
    }

    protected UserResource ifsAdminUser(){
        return getUserWithRole(IFS_ADMINISTRATOR);
    }

    @Before
    public void setupSetsOfData() {
        allRoles = asList(Role.values());
        allGlobalRoleUsers = simpleMap(allRoles, role -> newUserResource().withRolesGlobal(singletonList(role)).build());
        allInternalUsers = asList(compAdminUser(), projectFinanceUser(), supportUser(), innovationLeadUser());

    }

    protected UserResource getUserWithRole(Role type) {
        return simpleFilter(allGlobalRoleUsers, user -> user.hasRole(type)).get(0);
    }

    protected void setUpUserAsProjectManager(ProjectResource projectResource, UserResource user) {

        List<ProjectUser> projectManagerUser = newProjectUser().build(1);

        when(projectUserRepositoryMock.findByProjectIdAndUserIdAndRole(projectResource.getId(), user.getId(), ProjectParticipantRole.PROJECT_MANAGER ))
                .thenReturn(projectManagerUser);
    }

    protected void setUpUserNotAsProjectManager(UserResource user) {
        List<Role> projectManagerUser = emptyList();
        user.setRoles(projectManagerUser);
    }

    protected void setupUserAsPartner(ProjectResource project, UserResource user) {
        setupPartnerExpectations(project, user, true);
    }

    protected void setupUserAsPartner(ProjectResource project, UserResource user, OrganisationResource organisation) {
        setupPartnerExpectations(project, user, organisation, true);
    }

    protected void setupUserNotAsPartner(ProjectResource project, UserResource user) {
        setupPartnerExpectations(project, user, false);
    }

    protected void setupUserNotAsPartner(ProjectResource project, UserResource user, OrganisationResource organisation) {
        setupPartnerExpectations(project, user, organisation, false);
    }

    protected void setUpUserAsCompAdmin(ProjectResource project, UserResource user) {
        List<Role> compAdminRoleResource = singletonList(COMP_ADMIN);
        user.setRoles(compAdminRoleResource);
    }

    protected void setUpUserNotAsCompAdmin(ProjectResource project, UserResource user) {
        List<Role> compAdminRoleResource = emptyList();
        user.setRoles(compAdminRoleResource);
    }

    protected void setUpUserAsProjectFinanceUser(ProjectResource project, UserResource user) {
        List<Role> projectFinanceUser = singletonList(Role.PROJECT_FINANCE);
        user.setRoles(projectFinanceUser);
    }

    protected void setUpUserNotAsProjectFinanceUser(ProjectResource project, UserResource user) {
        List<Role> projectFinanceUser = emptyList();
        user.setRoles(projectFinanceUser);
    }

    protected void setupPartnerExpectations(ProjectResource project, UserResource user, boolean userIsPartner) {
        List<ProjectUser> partnerProjectUser = newProjectUser().build(1);

        when(projectUserRepositoryMock.findByProjectIdAndUserIdAndRole(project.getId(), user.getId(), PROJECT_PARTNER)).thenReturn(userIsPartner ? partnerProjectUser : emptyList());
    }

    protected void setupPartnerExpectations(ProjectResource project, UserResource user, OrganisationResource organisation, boolean userIsPartner) {
        ProjectUser partnerProjectUser = newProjectUser().build();

        when(projectUserRepositoryMock.findOneByProjectIdAndUserIdAndOrganisationIdAndRole(project.getId(), user.getId(), organisation.getId(), PROJECT_PARTNER)).thenReturn(userIsPartner ? partnerProjectUser : null);
    }

    protected void setupUserAsLeadPartner(ProjectResource project, UserResource user) {
        setupLeadPartnerExpectations(project, user, true);
    }

    protected void setupUserNotAsLeadPartner(ProjectResource project, UserResource user) {
        setupLeadPartnerExpectations(project, user, false);
    }

    private void setupLeadPartnerExpectations(ProjectResource project, UserResource user, boolean userIsLeadPartner) {

        org.innovateuk.ifs.application.domain.Application originalApplication = newApplication().build();
        Project projectEntity = newProject().withApplication(originalApplication).build();
        Organisation leadOrganisation = newOrganisation().build();
        ProcessRole leadApplicantProcessRole = newProcessRole().withOrganisationId(leadOrganisation.getId()).build();

        // find the lead organisation
        when(projectRepositoryMock.findOne(project.getId())).thenReturn(projectEntity);
        when(processRoleRepositoryMock.findOneByApplicationIdAndRole(projectEntity.getApplication().getId(), Role.LEADAPPLICANT)).thenReturn(leadApplicantProcessRole);

        // see if the user is a partner on the lead organisation
        when(organisationRepositoryMock.findOne(leadOrganisation.getId())).thenReturn(leadOrganisation);
        when(projectUserRepositoryMock.findOneByProjectIdAndUserIdAndOrganisationIdAndRole(
                project.getId(), user.getId(), leadOrganisation.getId(), PROJECT_PARTNER)).thenReturn(userIsLeadPartner ? newProjectUser().build() : null);
    }

    protected abstract T supplyPermissionRulesUnderTest();

}
