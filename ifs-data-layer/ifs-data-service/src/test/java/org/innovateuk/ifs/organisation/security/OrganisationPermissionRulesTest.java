package org.innovateuk.ifs.organisation.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.invite.repository.InviteOrganisationRepository;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationSearchResult;
import org.innovateuk.ifs.project.core.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.monitoring.domain.MonitoringOfficer;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.invite.builder.InviteOrganisationBuilder.newInviteOrganisation;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.project.core.builder.PartnerOrganisationBuilder.newPartnerOrganisation;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.core.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.PROJECT_PARTNER;
import static org.innovateuk.ifs.project.monitoring.builder.MonitoringOfficerBuilder.newMonitoringOfficer;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.MONITORING_OFFICER;
import static org.innovateuk.ifs.user.resource.Role.STAKEHOLDER;
import static org.innovateuk.ifs.util.CollectionFunctions.combineLists;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests the logic within the individual OrganisationRules methods that secures basic Organisation details
 */
public class OrganisationPermissionRulesTest extends BasePermissionRulesTest<OrganisationPermissionRules> {

    @Mock
    private InviteOrganisationRepository inviteOrganisationRepository;

    @Test
    public void systemRegistrationUserCanViewAnOrganisationThatIsNotYetLinkedToAnApplication() {
        allGlobalRoleUsers.forEach(user -> {
            if (user.equals(systemRegistrationUser())) {
                assertTrue(rules.systemRegistrationUserCanSeeOrganisationsNotYetConnectedToApplications(newOrganisationResource().build(), user));
            } else {
                assertFalse(rules.systemRegistrationUserCanSeeOrganisationsNotYetConnectedToApplications(newOrganisationResource().build(), user));
            }
        });
    }

    @Test
    public void internalUsersCanViewAnyOrganisation() {
        allGlobalRoleUsers.forEach(user -> {
            if (allInternalUsers.contains(user)) {
                assertTrue(rules.internalUsersCanSeeAllOrganisations(newOrganisationResource().build(), user));
            } else {
                assertFalse(rules.internalUsersCanSeeAllOrganisations(newOrganisationResource().build(), user));
            }
        });
    }

    @Test
    public void stakeholdersCanSeeAllOrganisations() {
        allGlobalRoleUsers.forEach(user -> {
            if (user.hasRole(STAKEHOLDER)) {
                assertTrue(rules.stakeholdersCanSeeAllOrganisations(newOrganisationResource().build(), user));
            } else {
                assertFalse(rules.stakeholdersCanSeeAllOrganisations(newOrganisationResource().build(), user));
            }
        });
    }

    @Test
    public void monitoringOfficersCanSeeAllOrganisations() {

        long organisationId = 1L;

        Organisation organisation = newOrganisation()
                .withId(organisationId)
                .build();

        List<PartnerOrganisation> partnerOrganisations = newPartnerOrganisation()
                .withOrganisation(organisation)
                .build(1);

        Project project = newProject()
                .withPartnerOrganisations(partnerOrganisations)
                .build();

        List<MonitoringOfficer> projectMonitoringOfficers = newMonitoringOfficer()
                .withProject(project)
                .build(1);

        when(projectMonitoringOfficerRepository.findByUserId(monitoringOfficerUser().getId())).thenReturn(projectMonitoringOfficers);

        allGlobalRoleUsers.forEach(user -> {
            if (user.hasRole(MONITORING_OFFICER)) {
                assertTrue(rules.monitoringOfficersCanSeeAllOrganisations(newOrganisationResource().withId(organisationId).build(), monitoringOfficerUser()));
            } else {
                assertFalse(rules.monitoringOfficersCanSeeAllOrganisations(newOrganisationResource().withId(organisationId).build(), user));
            }
        });
    }

    @Test
    public void projectFinanceUserCanUpdateAllOrganisations() {
        allGlobalRoleUsers.forEach(user -> {
            if (user.equals(projectFinanceUser())) {
                assertTrue(rules.projectFinanceUserCanUpdateAnyOrganisation(newOrganisationResource().build(), user));
            } else {
                assertFalse(rules.projectFinanceUserCanUpdateAnyOrganisation(newOrganisationResource().build(), user));
            }
        });
    }

    @Test
    public void systemRegistrationUsersCanViewAnyOrganisation() {
        allGlobalRoleUsers.forEach(user -> {
            if (user.equals(systemRegistrationUser())) {
                assertTrue(rules.systemRegistrationUserCanSeeAllOrganisations(newOrganisationResource().build(), user));
            } else {
                assertFalse(rules.systemRegistrationUserCanSeeAllOrganisations(newOrganisationResource().build(), user));
            }
        });
    }

    @Test
    public void memberOfOrganisationCanViewOwnOrganisation() {

        UserResource user = newUserResource().build();
        UserResource anotherUser = newUserResource().build();

        OrganisationResource organisation = newOrganisationResource().withUsers(asList(user.getId(), anotherUser.getId())).build();

        assertTrue(rules.memberOfOrganisationCanViewOwnOrganisation(organisation, user));
    }

    @Test
    public void memberOfOrganisationCanViewOwnOrganisationButUserIsNotAMemberOfTheOrganisation() {

        UserResource user = newUserResource().build();
        UserResource anotherUser = newUserResource().build();
        UserResource unrelatedUser = newUserResource().build();

        OrganisationResource organisation = newOrganisationResource().withUsers(asList(user.getId(), anotherUser.getId())).build();

        assertFalse(rules.memberOfOrganisationCanViewOwnOrganisation(organisation, unrelatedUser));
    }

    @Test
    public void memberOfOrganisationCanUpdateOwnOrganisation() {

        UserResource user = newUserResource().build();
        UserResource anotherUser = newUserResource().build();

        OrganisationResource organisation = newOrganisationResource().withUsers(asList(user.getId(), anotherUser.getId())).build();

        assertTrue(rules.memberOfOrganisationCanUpdateOwnOrganisation(organisation, user));
    }

    @Test
    public void memberOfOrganisationCanUpdateOwnOrganisationButUserIsNotAMemberOfTheOrganisation() {

        UserResource user = newUserResource().build();
        UserResource anotherUser = newUserResource().build();
        UserResource unrelatedUser = newUserResource().build();

        OrganisationResource organisation = newOrganisationResource().withUsers(asList(user.getId(), anotherUser.getId())).build();

        assertFalse(rules.memberOfOrganisationCanUpdateOwnOrganisation(organisation, unrelatedUser));
    }

    @Test
    public void usersCanViewOrganisationsOnTheirOwnApplications() {

        Organisation organisation = newOrganisation().withId(123L).build();
        Application application = newApplication().build();
        ProcessRole processRole = newProcessRole().withApplication(application).withOrganisationId(organisation.getId()).build();
        UserResource user = newUserResource().build();

        when(processRoleRepository.findByUserId(user.getId())).thenReturn(singletonList(processRole));
        when(processRoleRepository.findByApplicationId(application.getId())).thenReturn(singletonList(processRole));

        OrganisationResource organisationResource =
                newOrganisationResource().withId(organisation.getId()).withProcessRoles(singletonList(processRole.getId())).build();

        assertTrue(rules.usersCanViewOrganisationsOnTheirOwnApplications(organisationResource, user));
    }

    @Test
    public void usersCanViewOrganisationsOnTheirOwnApplicationsButUserIsNotOnAnyApplicationsWithThisOrganisation() {

        UserResource user = newUserResource().build();

        Organisation anotherOrganisation = newOrganisation().withId(456L).build();
        User anotherUser = newUser().build();
        Application anotherApplication = newApplication().build();
        ProcessRole anotherProcessRole = newProcessRole().withUser(anotherUser).withApplication(anotherApplication).withOrganisationId(anotherOrganisation.getId()).build();

        OrganisationResource anotherOrganisationResource =
                newOrganisationResource().withId(anotherOrganisation.getId()).withProcessRoles(singletonList(anotherProcessRole.getId())).build();

        assertFalse(rules.usersCanViewOrganisationsOnTheirOwnApplications(anotherOrganisationResource, user));
    }

    @Test
    public void systemRegistrationUserCanCreateOrganisations() {
        allGlobalRoleUsers.forEach(user -> {
            if (user.equals(systemRegistrationUser())) {
                assertTrue(rules.systemRegistrationUserCanCreateOrganisations(newOrganisationResource().build(), user));
            } else {
                assertFalse(rules.systemRegistrationUserCanCreateOrganisations(newOrganisationResource().build(), user));
            }
        });
    }

    @Test
    public void systemRegistrationUserCanUpdateOrganisationsNotYetConnectedToApplicationsOrUsers() {
        allGlobalRoleUsers.forEach(user -> {
            if (user.equals(systemRegistrationUser())) {
                assertTrue(rules.systemRegistrationUserCanUpdateOrganisationsNotYetConnectedToApplicationsOrUsers(newOrganisationResource().build(), user));
            } else {
                assertFalse(rules.systemRegistrationUserCanUpdateOrganisationsNotYetConnectedToApplicationsOrUsers(newOrganisationResource().build(), user));
            }
        });
    }

    @Test
    public void systemRegistrationUserCanUpdateOrganisationsNotYetConnectedToApplicationsOrUsersButOrganisationAttachedToApplication() {
        OrganisationResource organisation = newOrganisationResource().withProcessRoles(singletonList(123L)).build();
        allGlobalRoleUsers.forEach(user ->
                assertFalse(rules.systemRegistrationUserCanUpdateOrganisationsNotYetConnectedToApplicationsOrUsers(organisation, user)));
    }

    @Test
    public void systemRegistrationUserCanUpdateOrganisationsNotYetConnectedToApplicationsOrUsersButOrganisationAttachedToUsers() {
        OrganisationResource organisation = newOrganisationResource().withUsers(singletonList(123L)).build();
        allGlobalRoleUsers.forEach(user ->
                assertFalse(rules.systemRegistrationUserCanUpdateOrganisationsNotYetConnectedToApplicationsOrUsers(organisation, user)));
    }

    @Test
    public void systemRegistrationUserCanSeeOrganisationSearchResults() {
        allGlobalRoleUsers.forEach(user -> {
            if (user.equals(systemRegistrationUser())) {
                assertTrue(rules.systemRegistrationUserCanSeeOrganisationSearchResults(new OrganisationSearchResult(), user));
            } else {
                assertFalse(rules.systemRegistrationUserCanSeeOrganisationSearchResults(new OrganisationSearchResult(), user));
            }
        });
    }

    @Test
    public void projectPartnerUserCanSeePartnerOrganisationsWithinTheirProjects() {

        UserResource user = newUserResource().build();

        Organisation differentOrganisation = newOrganisation().build();
        Organisation organisationBeingChecked = newOrganisation().build();
        Organisation anotherDifferentOrganisation = newOrganisation().build();

        Project projectWithoutLinkedOrganisation = newProject().
                withPartnerOrganisations(newPartnerOrganisation().withOrganisation(differentOrganisation).build(1)).
                build();

        Project projectWithLinkedOrganisation = newProject().
                withPartnerOrganisations(newPartnerOrganisation().
                        withOrganisation(organisationBeingChecked, anotherDifferentOrganisation).
                        build(2)).
                build();

        // a project that doesn't include the Organisation being checked
        List<ProjectUser> thisUsersProjectUserEntriesWithoutLinkedOrganisation = newProjectUser().
                withProject(projectWithoutLinkedOrganisation).
                withOrganisation(differentOrganisation).
                build(1);

        // a project that DOES include the Organisation being checked
        List<ProjectUser> thisUsersProjectUserEntriesIncludingLinkedOrganisation = newProjectUser().
                withProject(projectWithLinkedOrganisation).
                withOrganisation(organisationBeingChecked, anotherDifferentOrganisation).
                build(2);

        List<ProjectUser> allProjectUserEntries =
                combineLists(thisUsersProjectUserEntriesWithoutLinkedOrganisation, thisUsersProjectUserEntriesIncludingLinkedOrganisation);

        when(projectUserRepository.findByUserIdAndRole(user.getId(), PROJECT_PARTNER)).thenReturn(allProjectUserEntries);

        OrganisationResource linkedOrganisationToCheck = newOrganisationResource().
                withId(organisationBeingChecked.getId()).
                build();

        assertTrue(rules.projectPartnerUserCanSeePartnerOrganisationsWithinTheirProjects(linkedOrganisationToCheck, user));

        verify(projectUserRepository).findByUserIdAndRole(user.getId(), PROJECT_PARTNER);
    }

    @Test
    public void projectPartnerUserCanSeePartnerOrganisationsWithinTheirProjectsButNoLinkWithOrganisationViaProjects() {

        UserResource user = newUserResource().build();

        Organisation differentOrganisation = newOrganisation().build();
        Organisation organisationBeingChecked = newOrganisation().build();
        Organisation anotherDifferentOrganisation = newOrganisation().build();

        Project projectWithoutLinkedOrganisation = newProject().
                withPartnerOrganisations(newPartnerOrganisation().withOrganisation(differentOrganisation).build(1)).
                build();

        Project anotherProjectWithoutLinkedOrganisation = newProject().
                withPartnerOrganisations(newPartnerOrganisation().
                        withOrganisation(anotherDifferentOrganisation).
                        build(2)).
                build();

        // a project that doesn't include the Organisation being checked
        List<ProjectUser> thisUsersProjectUserEntriesWithoutLinkedOrganisation = newProjectUser().
                withProject(projectWithoutLinkedOrganisation).
                withOrganisation(differentOrganisation).
                build(1);

        // another project that also doesn't include the Organisation being checked
        List<ProjectUser> thisUsersProjectUserEntriesIncludingLinkedOrganisation = newProjectUser().
                withProject(anotherProjectWithoutLinkedOrganisation).
                withOrganisation(anotherDifferentOrganisation).
                build(1);

        List<ProjectUser> allProjectUserEntries =
                combineLists(thisUsersProjectUserEntriesWithoutLinkedOrganisation, thisUsersProjectUserEntriesIncludingLinkedOrganisation);

        when(projectUserRepository.findByUserIdAndRole(user.getId(), PROJECT_PARTNER)).thenReturn(allProjectUserEntries);

        OrganisationResource linkedOrganisationToCheck = newOrganisationResource().
                withId(organisationBeingChecked.getId()).
                build();

        assertFalse(rules.projectPartnerUserCanSeePartnerOrganisationsWithinTheirProjects(linkedOrganisationToCheck, user));

        verify(projectUserRepository).findByUserIdAndRole(user.getId(), PROJECT_PARTNER);
    }

    @Test
    public void usersCanViewOrganisationsTheyAreInvitedTo() {
        UserResource invitedUser = newUserResource().build();
        UserResource notInvitedUser = newUserResource().build();
        OrganisationResource organisation = newOrganisationResource().build();

        when(inviteOrganisationRepository.findFirstByOrganisationIdAndInvitesUserId(organisation.getId(), invitedUser.getId()))
                .thenReturn(Optional.of(newInviteOrganisation().build()));
        when(inviteOrganisationRepository.findFirstByOrganisationIdAndInvitesUserId(organisation.getId(), notInvitedUser.getId()))
                .thenReturn(Optional.empty());

        assertTrue(rules.usersCanViewOrganisationsTheyAreInvitedTo(organisation, invitedUser));
        assertFalse(rules.usersCanViewOrganisationsTheyAreInvitedTo(organisation, notInvitedUser));
    }

    @Override
    protected OrganisationPermissionRules supplyPermissionRulesUnderTest() {
        return new OrganisationPermissionRules();
    }
}