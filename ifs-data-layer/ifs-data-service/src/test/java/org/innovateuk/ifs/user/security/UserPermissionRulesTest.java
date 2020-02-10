package org.innovateuk.ifs.user.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.Stakeholder;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectParticipantRole;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.monitoring.domain.MonitoringOfficer;
import org.innovateuk.ifs.user.builder.UserOrganisationResourceBuilder;
import org.innovateuk.ifs.user.builder.UserResourceBuilder;
import org.innovateuk.ifs.user.command.GrantRoleCommand;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.*;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.StakeholderBuilder.newStakeholder;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.core.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.project.monitoring.builder.MonitoringOfficerBuilder.newMonitoringOfficer;
import static org.innovateuk.ifs.registration.builder.UserRegistrationResourceBuilder.newUserRegistrationResource;
import static org.innovateuk.ifs.user.builder.AffiliationResourceBuilder.newAffiliationResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.builder.ProfileAgreementResourceBuilder.newProfileAgreementResource;
import static org.innovateuk.ifs.user.builder.ProfileSkillsResourceBuilder.newProfileSkillsResource;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserProfileResourceBuilder.newUserProfileResource;
import static org.innovateuk.ifs.user.builder.UserProfileStatusResourceBuilder.newUserProfileStatusResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.innovateuk.ifs.util.CollectionFunctions.combineLists;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.SecurityRuleUtil.isInternal;
import static org.innovateuk.ifs.util.SecurityRuleUtil.isMonitoringOfficer;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Tests around the permissions for UserService and related services
 */
public class UserPermissionRulesTest extends BasePermissionRulesTest<UserPermissionRules> {

    @Mock
    private ApplicationRepository applicationRepository;

    @Test
    public void anyoneCanViewThemselves() {
        allGlobalRoleUsers.forEach(user ->
                allGlobalRoleUsers.forEach(otherUser -> {
                    if (user.equals(otherUser)) {
                        assertTrue(rules.anyUserCanViewThemselves(otherUser, user));
                    } else {
                        assertFalse(rules.anyUserCanViewThemselves(otherUser, user));
                    }
                }));
    }

    @Test
    public void internalUsersCanViewEveryone() {
        allGlobalRoleUsers.forEach(user ->
                allGlobalRoleUsers.forEach(otherUser -> {
                    if (allInternalUsers.contains(user)) {
                        assertTrue(rules.internalUsersCanViewEveryone(otherUser, user));
                    } else {
                        assertFalse(rules.internalUsersCanViewEveryone(otherUser, user));
                    }
                }));
    }

    @Test
    public void stakeholdersCanViewUsersInCompetitionsTheyAreAssignedTo() {
        Competition competition = newCompetition().build();
        Application application = newApplication().withCompetition(competition).build();
        Project project = newProject().withApplication(application).build();
        Stakeholder stakeholder = newStakeholder().withCompetition(competition).build();
        UserResource stakeholderResource = newUserResource().withRoleGlobal(STAKEHOLDER).build();
        UserResource userResource = newUserResource().withRoleGlobal(LEADAPPLICANT).build();
        User user = newUser().withId(userResource.getId()).build();
        List<ProcessRole> processRoles = newProcessRole()
                .withUser(user)
                .build(2);
        List<ProjectUser> projectUsers = newProjectUser()
                .withProject(project)
                .withRole(ProjectParticipantRole.PROJECT_MANAGER)
                .build(2);

        when(processRoleRepository.findByUserId(userResource.getId())).thenReturn(processRoles);
        when(projectUserRepository.findByUserId(userResource.getId())).thenReturn(projectUsers);
        when(stakeholderRepository.findByStakeholderId(stakeholderResource.getId())).thenReturn(singletonList(stakeholder));

        assertTrue(rules.stakeholdersCanViewUsersInCompetitionsTheyAreAssignedTo(userResource, stakeholderResource));

        allInternalUsers.forEach(internalUser -> assertFalse(rules.stakeholdersCanViewUsersInCompetitionsTheyAreAssignedTo(userResource, internalUser)));
    }

    @Test
    public void monitoringOfficersCanViewUsersInProjectsTheyAreAssignedTo() {
        Project project = newProject().build();
        UserResource userResource = newUserResource().withRoleGlobal(LEADAPPLICANT).build();
        List<ProjectUser> projectUsers = newProjectUser()
                .withProject(project)
                .withRole(ProjectParticipantRole.PROJECT_MANAGER)
                .build(2);

        List<MonitoringOfficer> projectMonitoringOfficers = newMonitoringOfficer()
                .withProject(project)
                .build(1);

        when(projectUserRepository.findByUserId(userResource.getId())).thenReturn(projectUsers);
        when(projectMonitoringOfficerRepository.findByUserId(monitoringOfficerUser().getId())).thenReturn(projectMonitoringOfficers);

        allGlobalRoleUsers.forEach(user -> {
            if (isMonitoringOfficer(user)) {
                assertTrue(rules.monitoringOfficersCanViewUsersInCompetitionsTheyAreAssignedTo(userResource, monitoringOfficerUser()));
            } else {
                assertFalse(rules.monitoringOfficersCanViewUsersInCompetitionsTheyAreAssignedTo(userResource, user));
            }
        });
    }

    @Test
    public void internalUsersCanViewEveryoneUserPageResource() {

        ManageUserPageResource manageUserPageResource = new ManageUserPageResource();

        allGlobalRoleUsers.forEach(user -> {
            if (user.equals(ifsAdminUser())) {
                assertTrue(rules.internalUsersCanViewEveryone(manageUserPageResource, user));
            } else {
                assertFalse(rules.internalUsersCanViewEveryone(manageUserPageResource, user));
            }
        });
    }

    @Test
    public void systemRegistrationUserCanViewEveryone() {
        allGlobalRoleUsers.forEach(user ->
                allGlobalRoleUsers.forEach(otherUser -> {
                    if (user.equals(systemRegistrationUser())) {
                        assertTrue(rules.systemRegistrationUserCanViewEveryone(otherUser, user));
                    } else {
                        assertFalse(rules.systemRegistrationUserCanViewEveryone(otherUser, user));
                    }
                }));
    }

    @Test
    public void systemRegistrationUserCanCreateUsers() {
        allGlobalRoleUsers.forEach(user -> {
            if (user.equals(systemRegistrationUser())) {
                assertTrue(rules.systemRegistrationUserCanCreateUsers(newUserResource().build(), user));
            } else {
                assertFalse(rules.systemRegistrationUserCanCreateUsers(newUserResource().build(), user));
            }
        });
    }

    @Test
    public void systemRegistrationUserCanCreateUsers_UserRegistrationResource() {
        allGlobalRoleUsers.forEach(user -> {
            if (user.equals(systemRegistrationUser())) {
                assertTrue(rules.systemRegistrationUserCanCreateUsers(newUserRegistrationResource().build(), user));
            } else {
                assertFalse(rules.systemRegistrationUserCanCreateUsers(newUserRegistrationResource().build(), user));
            }
        });
    }

    @Test
    public void systemRegistrationUserCanActivateUsers() {
        allGlobalRoleUsers.forEach(user -> {
            if (user.equals(systemRegistrationUser())) {
                assertTrue(rules.systemRegistrationUserCanActivateUsers(newUserResource().build(), user));
            } else {
                assertFalse(rules.systemRegistrationUserCanActivateUsers(newUserResource().build(), user));
            }
        });
    }

    @Test
    public void systemRegistrationUserChangeUsersPasswords() {
        allGlobalRoleUsers.forEach(user ->
                allGlobalRoleUsers.forEach(otherUser -> {
                    if (user.equals(systemRegistrationUser())) {
                        assertTrue(rules.systemRegistrationUserCanChangePasswordsForUsers(otherUser, user));
                    } else {
                        assertFalse(rules.systemRegistrationUserCanChangePasswordsForUsers(otherUser, user));
                    }
                }));
    }

    @Test
    public void anyoneCanChangeTheirOwnPassword() {
        allGlobalRoleUsers.forEach(user ->
                allGlobalRoleUsers.forEach(otherUser -> {
                    if (user.equals(otherUser)) {
                        assertTrue(rules.usersCanChangeTheirOwnPassword(otherUser, user));
                    } else {
                        assertFalse(rules.usersCanChangeTheirOwnPassword(otherUser, user));
                    }
                }));
    }

    @Test
    public void consortiumMembersCanViewOtherConsortiumMembers() {

        Application application1 = newApplication().build();
        when(applicationRepository.findById(application1.getId())).thenReturn(Optional.of(application1));

        User application1Lead1 = newUser().build();
        User application1Lead2 = newUser().build();
        User application1Lead3AndApplication2Collaborator2 = newUser().build();
        User application1Collaborator1 = newUser().build();
        User application1Collaborator2 = newUser().build();

        List<ProcessRole> application1ConsortiumRoles = newProcessRole().withApplication(application1).
                withRole(Role.LEADAPPLICANT, Role.LEADAPPLICANT, Role.LEADAPPLICANT, Role.COLLABORATOR, Role.COLLABORATOR).
                withUser(application1Lead1, application1Lead2, application1Lead3AndApplication2Collaborator2,
                        application1Collaborator1, application1Collaborator2).
                build(5);

        List<User> application1Consortium = simpleMap(application1ConsortiumRoles, ProcessRole::getUser);
        List<UserResource> application1ConsortiumResources = simpleMap(application1Consortium, userResourceForUser());

        when(processRoleRepository.findByUserId(application1Lead1.getId())).
                thenReturn(singletonList(application1ConsortiumRoles.get(0)));
        when(processRoleRepository.findByUserId(application1Lead2.getId())).
                thenReturn(singletonList(application1ConsortiumRoles.get(1)));
        when(processRoleRepository.findByUserId(application1Collaborator1.getId())).
                thenReturn(singletonList(application1ConsortiumRoles.get(3)));
        when(processRoleRepository.findByUserId(application1Collaborator2.getId())).
                thenReturn(singletonList(application1ConsortiumRoles.get(4)));

        Application application2 = newApplication().build();
        when(applicationRepository.findById(application2.getId())).thenReturn(Optional.of(application2));

        User application2Lead = newUser().build();
        User application2Collaborator1 = newUser().build();

        List<ProcessRole> application2ConsortiumRoles = newProcessRole().withApplication(application2).
                withRole(Role.LEADAPPLICANT, Role.COLLABORATOR, Role.COLLABORATOR).
                withUser(application2Lead, application2Collaborator1, application1Lead3AndApplication2Collaborator2).
                build(3);

        List<User> application2Consortium = simpleMap(application2ConsortiumRoles, ProcessRole::getUser);
        List<UserResource> application2ConsortiumResources = simpleMap(application2Consortium, userResourceForUser());

        when(processRoleRepository.findByUserId(application2Lead.getId())).
                thenReturn(singletonList(application2ConsortiumRoles.get(0)));
        when(processRoleRepository.findByUserId(application2Collaborator1.getId())).
                thenReturn(singletonList(application2ConsortiumRoles.get(1)));

        // user common to both applications
        when(processRoleRepository.findByUserId(application1Lead3AndApplication2Collaborator2.getId())).
                thenReturn(asList(application1ConsortiumRoles.get(2), application2ConsortiumRoles.get(2)));

        // assert that all members of the application 1 consortium can see all other consortium members
        application1ConsortiumResources.forEach(user ->
                application1ConsortiumResources.forEach(otherUser ->
                        assertTrue(rules.consortiumMembersCanViewOtherConsortiumMembers(otherUser, user))));

        // assert that all members of the application 2 consortium can see all other consortium members
        application2ConsortiumResources.forEach(user ->
                application2ConsortiumResources.forEach(otherUser ->
                        assertTrue(rules.consortiumMembersCanViewOtherConsortiumMembers(otherUser, user))));

        // assert that only the user with crossover between the 2 applications is able to see members of the other
        // applications
        application1ConsortiumResources.forEach(consortium1User ->
                application2ConsortiumResources.forEach(consortium2User -> {
                    if (asList(consortium1User.getId(), consortium2User.getId()).contains(application1Lead3AndApplication2Collaborator2.getId())) {
                        assertTrue(rules.consortiumMembersCanViewOtherConsortiumMembers(consortium2User, consortium1User));
                        assertTrue(rules.consortiumMembersCanViewOtherConsortiumMembers(consortium1User, consortium2User));
                    } else {
                        assertFalse(rules.consortiumMembersCanViewOtherConsortiumMembers(consortium2User, consortium1User));
                        assertFalse(rules.consortiumMembersCanViewOtherConsortiumMembers(consortium1User, consortium2User));
                    }
                }));
    }

    @Test
    public void consortiumMembersCanViewOtherConsortiumMembersButNotAssessors() {

        Application application1 = newApplication().build();

        User applicationLead = newUser().build();
        User applicationCollaborator = newUser().build();
        User applicationAssessor = newUser().build();

        List<ProcessRole> applicationConsortiumRoles = newProcessRole().withApplication(application1).
                withRole(Role.LEADAPPLICANT, Role.COLLABORATOR).
                withUser(applicationLead, applicationCollaborator).
                build(2);

        ProcessRole assessorProcessRole = newProcessRole().withApplication(application1).withRole(Role.ASSESSOR).
                withUser(applicationAssessor).build();

        List<User> applicationConsortium = simpleMap(applicationConsortiumRoles, ProcessRole::getUser);
        List<UserResource> applicationConsortiumResources = simpleMap(applicationConsortium, userResourceForUser());
        UserResource applicationAssessorResource = userResourceForUser().apply(applicationAssessor);

        combineLists(applicationConsortiumRoles, assessorProcessRole).forEach(role ->
                when(processRoleRepository.findById(role.getId())).thenReturn(Optional.of(role)));

        // assert that consortium members can't see the assessor using this rule
        applicationConsortiumResources.forEach(consortiumUser ->
                assertFalse(rules.consortiumMembersCanViewOtherConsortiumMembers(applicationAssessorResource, consortiumUser)));
    }

    @Test
    public void assessorsCanViewConsortiumMembersForApplicationsTheyAreAssessing() {

        Application application1 = newApplication().build();
        Application application2 = newApplication().build();
        Application application3 = newApplication().build();

        when(applicationRepository.findById(application1.getId())).thenReturn(Optional.of(application1));
        when(applicationRepository.findById(application2.getId())).thenReturn(Optional.of(application2));
        when(applicationRepository.findById(application3.getId())).thenReturn(Optional.of(application3));

        User application1Lead = newUser().build();
        User application2Collaborator = newUser().build();
        User application3Lead = newUser().build();
        User assessorForApplications1And2 = newUser().build();
        User panelAssessorForApplication1 = newUser().build();

        ProcessRole application1LeadProcessRole = newProcessRole().
                withApplication(application1).
                withRole(Role.LEADAPPLICANT).
                withUser(application1Lead).
                build();
        ProcessRole application2CollaboratorProcessRole = newProcessRole().
                withApplication(application2).
                withRole(Role.COLLABORATOR).
                withUser(application2Collaborator).
                build();
        ProcessRole application3LeadProcessRole = newProcessRole().
                withApplication(application3).
                withRole(Role.LEADAPPLICANT).
                withUser(application3Lead).
                build();

        List<ProcessRole> assessorProcessRoles = newProcessRole().
                withApplication(application1, application2).
                withRole(Role.ASSESSOR, Role.ASSESSOR).
                withUser(assessorForApplications1And2, assessorForApplications1And2).
                build(2);

        List<ProcessRole> panelAssessorProcessRole = newProcessRole()
                .withApplication(application1)
                .withRole(Role.PANEL_ASSESSOR)
                .withUser(panelAssessorForApplication1)
                .build(1);

        when(processRoleRepository.findByUserId(application1Lead.getId())).
                thenReturn(singletonList(application1LeadProcessRole));
        when(processRoleRepository.findByUserId(application2Collaborator.getId())).
                thenReturn(singletonList(application2CollaboratorProcessRole));
        when(processRoleRepository.findByUserId(application3Lead.getId())).
                thenReturn(singletonList(application3LeadProcessRole));
        when(processRoleRepository.findByUserId(assessorForApplications1And2.getId())).
                thenReturn(assessorProcessRoles);
        when(processRoleRepository.findByUserId(panelAssessorForApplication1.getId())).
                thenReturn(panelAssessorProcessRole);

        UserResource application1LeadResource = userResourceForUser().apply(application1Lead);
        UserResource application2CollaboratorResource = userResourceForUser().apply(application2Collaborator);
        UserResource application3LeadResource = userResourceForUser().apply(application3Lead);
        UserResource assessorForApplications1And2Resource = userResourceForUser().apply(assessorForApplications1And2);
        UserResource panelAssessorForApplications1Resource = userResourceForUser().apply(panelAssessorForApplication1);

        // assert that the assessor can see users from application1 and application2
        assertTrue(rules.assessorsCanViewConsortiumUsersOnApplicationsTheyAreAssessing(application1LeadResource, assessorForApplications1And2Resource));
        assertTrue(rules.assessorsCanViewConsortiumUsersOnApplicationsTheyAreAssessing(application2CollaboratorResource, assessorForApplications1And2Resource));

        assertTrue(rules.assessorsCanViewConsortiumUsersOnApplicationsTheyAreAssessing(application1LeadResource, panelAssessorForApplications1Resource));

        // assert that they can't see users from application 3 because they are not assessing it
        assertFalse(rules.assessorsCanViewConsortiumUsersOnApplicationsTheyAreAssessing(application3LeadResource, assessorForApplications1And2Resource));
    }

    @Test
    public void consortiumMembersCanViewTheProcessRolesOtherConsortiumMembers() {

        Application application1 = newApplication().build();
        when(applicationRepository.findById(application1.getId())).thenReturn(Optional.of(application1));

        User application1Lead1 = newUser().build();
        User application1Lead2 = newUser().build();
        User application1Lead3AndApplication2Collaborator2 = newUser().build();
        User application1Collaborator1 = newUser().build();
        User application1Collaborator2 = newUser().build();

        List<ProcessRole> application1ConsortiumRoles = newProcessRole().withApplication(application1).
                withRole(Role.LEADAPPLICANT, Role.LEADAPPLICANT, Role.LEADAPPLICANT, Role.COLLABORATOR, Role.COLLABORATOR).
                withUser(application1Lead1, application1Lead2, application1Lead3AndApplication2Collaborator2,
                        application1Collaborator1, application1Collaborator2).build(2);

        List<User> application1Consortium = simpleMap(application1ConsortiumRoles, ProcessRole::getUser);
        List<UserResource> application1ConsortiumResources = simpleMap(application1Consortium, userResourceForUser());

        when(processRoleRepository.findByUserId(application1Lead1.getId())).
                thenReturn(singletonList(application1ConsortiumRoles.get(0)));
        when(processRoleRepository.findByUserId(application1Lead2.getId())).
                thenReturn(singletonList(application1ConsortiumRoles.get(1)));

        ProcessRoleResource validResource = newProcessRoleResource().withApplication(application1.getId()).build();
        ProcessRoleResource invalidResource = newProcessRoleResource().withApplication(10L).build();

        assertTrue(rules.consortiumMembersCanViewTheProcessRolesOfOtherConsortiumMembers(validResource, application1ConsortiumResources.get(0)));
        assertFalse(rules.consortiumMembersCanViewTheProcessRolesOfOtherConsortiumMembers(invalidResource, application1ConsortiumResources.get(0)));
    }

    @Test
    public void assessorsCanViewTheProcessRolesOfConsortiumUsersOnApplicationsTheyAreAssessing() {

        Application application1 = newApplication().build();
        when(applicationRepository.findById(application1.getId())).thenReturn(Optional.of(application1));

        User application1Assessor1 = newUser().build();
        User application1Assessor2 = newUser().build();

        List<ProcessRole> application1ConsortiumRoles = newProcessRole().withApplication(application1).
                withRole(Role.ASSESSOR, Role.ASSESSOR, Role.ASSESSOR).
                withUser(application1Assessor1, application1Assessor2).build(2);

        List<User> application1Consortium = simpleMap(application1ConsortiumRoles, ProcessRole::getUser);
        List<UserResource> application1ConsortiumResources = simpleMap(application1Consortium, userResourceForUser());

        when(processRoleRepository.findByUserId(application1Assessor1.getId())).
                thenReturn(singletonList(application1ConsortiumRoles.get(0)));
        when(processRoleRepository.findByUserId(application1Assessor2.getId())).
                thenReturn(singletonList(application1ConsortiumRoles.get(1)));

        ProcessRoleResource validResource = newProcessRoleResource().withApplication(application1.getId()).build();
        ProcessRoleResource invalidResource = newProcessRoleResource().withApplication(10L).build();

        assertTrue(rules.assessorsCanViewTheProcessRolesOfConsortiumUsersOnApplicationsTheyAreAssessing(validResource, application1ConsortiumResources.get(0)));
        assertFalse(rules.assessorsCanViewTheProcessRolesOfConsortiumUsersOnApplicationsTheyAreAssessing(invalidResource, application1ConsortiumResources.get(0)));
    }

    @Test
    public void usersCanUpdateTheirOwnProfiles() {
        UserResource user = newUserResource().build();
        assertTrue(rules.usersCanUpdateTheirOwnProfiles(user, user));
    }

    @Test
    public void usersCanUpdateTheirOwnProfilesButAttemptingToUpdateAnotherUsersProfile() {
        UserResource user = newUserResource().build();
        UserResource anotherUser = newUserResource().build();
        assertFalse(rules.usersCanUpdateTheirOwnProfiles(user, anotherUser));
    }

    @Test
    public void allowedRolesCanUpdateUsersToMonitoringOfficers() {
        UserResource userToUpdate = newUserResource().build();

        assertTrue(rules.adminsCanUpdateUserDetails(userToUpdate, compAdminUser()));
        assertTrue(rules.adminsCanUpdateUserDetails(userToUpdate, projectFinanceUser()));
        assertTrue(rules.adminsCanUpdateUserDetails(userToUpdate, ifsAdminUser()));
        assertFalse(rules.adminsCanUpdateUserDetails(userToUpdate, assessorUser()));
    }

    @Test
    public void usersCanChangeTheirOwnPasswords() {
        UserResource user = newUserResource().build();
        assertTrue(rules.usersCanChangeTheirOwnPassword(user, user));
    }

    @Test
    public void usersCanViewTheirOwnProfileSkills() {
        UserResource user = newUserResource().build();
        ProfileSkillsResource profileSkillsResource = newProfileSkillsResource()
                .withUser(user.getId())
                .build();
        assertTrue(rules.usersCanViewTheirOwnProfileSkills(profileSkillsResource, user));
    }

    @Test
    public void usersCanViewTheirOwnProfileSkillsButAttemptingToViewAnotherUsersProfileSkills() {
        UserResource user = newUserResource().build();
        UserResource anotherUser = newUserResource().build();
        ProfileSkillsResource profileSkillsResource = newProfileSkillsResource()
                .withUser(user.getId())
                .build();
        assertFalse(rules.usersCanViewTheirOwnProfileSkills(profileSkillsResource, anotherUser));
    }

    @Test
    public void usersCanViewTheirOwnProfileAgreement() {
        UserResource user = newUserResource().build();
        ProfileAgreementResource profileAgreementResource = newProfileAgreementResource()
                .withUser(user.getId())
                .build();
        assertTrue(rules.usersCanViewTheirOwnProfileAgreement(profileAgreementResource, user));
    }

    @Test
    public void usersCanViewTheirOwnProfileAgreementButAttemptingToViewAnotherUsersProfileAgreement() {
        UserResource user = newUserResource().build();
        UserResource anotherUser = newUserResource().build();
        ProfileAgreementResource profileAgreementResource = newProfileAgreementResource()
                .withUser(user.getId())
                .build();
        assertFalse(rules.usersCanViewTheirOwnProfileAgreement(profileAgreementResource, anotherUser));
    }

    @Test
    public void usersCanViewTheirOwnAffiliations() {
        UserResource user = newUserResource().build();
        AffiliationResource affiliation = newAffiliationResource()
                .withUser(user.getId())
                .build();
        assertTrue(rules.usersCanViewTheirOwnAffiliations(affiliation, user));
    }

    @Test
    public void usersCanViewTheirOwnAffiliationsButAttemptingToViewAnotherUsersAffiliation() {
        UserResource user = newUserResource().build();
        UserResource anotherUser = newUserResource().build();
        AffiliationResource affiliation = newAffiliationResource()
                .withUser(user.getId())
                .build();
        assertFalse(rules.usersCanViewTheirOwnAffiliations(affiliation, anotherUser));
    }

    @Test
    public void usersCanViewTheirOwnDetails() {
        UserResource user = newUserResource().build();
        UserProfileResource userDetails = newUserProfileResource().withUser(user.getId()).build();
        assertTrue(rules.usersCanViewTheirOwnProfile(userDetails, user));
    }

    @Test
    public void usersCanViewTheirOwnDetailsButNotAnotherUsersDetails() {
        UserResource anotherUser = newUserResource().withId(1L).build();
        UserProfileResource userDetails = newUserProfileResource().withUser(2L).build();
        assertFalse(rules.usersCanViewTheirOwnProfile(userDetails, anotherUser));
    }

    @Test
    public void usersCanChangeTheirOwnPasswordsButAttemptingToUpdateAnotherUsersPassword() {
        UserResource user = newUserResource().build();
        UserResource anotherUser = newUserResource().build();
        assertFalse(rules.usersCanChangeTheirOwnPassword(user, anotherUser));
    }

    @Test
    public void usersCanViewTheirOwnProfileStatus() {
        UserResource user = newUserResource().build();
        UserProfileStatusResource userProfileStatus = newUserProfileStatusResource().withUser(user.getId()).build();
        assertTrue(rules.usersAndCompAdminCanViewProfileStatus(userProfileStatus, user));
    }

    @Test
    public void usersCanViewTheirOwnProfileStatusButNotAnotherUsersProfileStatus() {
        UserResource user = newUserResource().withId(1L).build();
        UserProfileStatusResource anotherUsersProfileStatus = newUserProfileStatusResource().withUser(2L).build();
        assertFalse(rules.usersAndCompAdminCanViewProfileStatus(anotherUsersProfileStatus, user));
    }

    @Test
    public void compAdminCanViewUserProfileStatus() {
        UserResource user = newUserResource().build();
        UserProfileStatusResource userProfileStatus = newUserProfileStatusResource().withUser(user.getId()).build();
        assertTrue(rules.usersAndCompAdminCanViewProfileStatus(userProfileStatus, compAdminUser()));
    }

    @Test
    public void usersCanViewTheirOwnProcessRole() {
        UserResource user = newUserResource().build();
        ProcessRoleResource processRoleResource = newProcessRoleResource().withUser(user).build();
        assertTrue(rules.usersCanViewTheirOwnProcessRole(processRoleResource, user));
    }

    @Test
    public void usersCanViewTheirOwnProcessRoleButNotAnotherUsersProcessRole() {
        UserResource user1 = newUserResource().withId(1L).build();
        UserResource user2 = newUserResource().withId(2L).build();

        ProcessRoleResource anotherUsersprocessRoleResource = newProcessRoleResource().withUser(user2).build();
        assertFalse(rules.usersAndInternalUsersCanViewProcessRole(anotherUsersprocessRoleResource, user1));
    }

    @Test
    public void compAdminCanViewUserProcessRole() {
        UserResource user = newUserResource().build();
        ProcessRoleResource processRoleResource = newProcessRoleResource().withUser(user).build();
        assertTrue(rules.usersAndInternalUsersCanViewProcessRole(processRoleResource, compAdminUser()));
    }

    @Test
    public void projectFinanceCanViewUserProcessRole() {
        UserResource user = newUserResource().build();
        ProcessRoleResource processRoleResource = newProcessRoleResource().withUser(user).build();
        assertTrue(rules.usersAndInternalUsersCanViewProcessRole(processRoleResource, projectFinanceUser()));
    }

    @Test
    public void allUsersWithProjectUserRolesCanAccessProcessRolesWithinConsortium() {
        final long userId = 11L;
        final long applicationId = 1L;

        ProjectParticipantRole.PROJECT_USER_ROLES
                .forEach(roleType -> {

                    UserResource userResource = newUserResource().withId(userId).build();

                    ProjectUser projectUser = newProjectUser().withUser(newUser().withId(userId).build()).withRole(roleType).withProject(newProject().withApplication(newApplication().withId(applicationId).build()).build()).build();

                    when(projectUserRepository.findByUserId(userId)).thenReturn(singletonList(projectUser));

                    ProcessRoleResource processRoleResource = newProcessRoleResource().withUser(userResource).withApplication(applicationId).build();

                    assertTrue(rules.projectPartnersCanViewTheProcessRolesWithinSameApplication(processRoleResource, userResource));
                });
    }

    @Test
    public void allUsersWithMonitoringOfficersCannotAccessProcessRolesWithinConsortium() {
        final long userId = 11L;
        final long applicationId = 1L;

        UserResource userResource = newUserResource().withId(userId).build();

        List<MonitoringOfficer> projectMonitoringOfficers = newMonitoringOfficer().withUser(newUser().withId(userId).build()).withProject(newProject().withApplication(newApplication().withId(applicationId).build()).build()).build(1);

        when(projectMonitoringOfficerRepository.findByUserId(userId)).thenReturn(projectMonitoringOfficers);

        ProcessRoleResource processRoleResource = newProcessRoleResource().withUser(userResource).withApplication(applicationId).build();

        assertFalse(rules.projectPartnersCanViewTheProcessRolesWithinSameApplication(processRoleResource, userResource));
    }

    @Test
    public void allUsersWithProjectRolesCanNotAccessProcessRolesWhenNotInConsortium() {
        final long userId = 11L;
        final long applicationId = 1L;

        ProjectParticipantRole.PROJECT_USER_ROLES
                .forEach(roleType -> {

                    UserResource userResource = newUserResource().withId(userId).build();

                    ProjectUser projectUser = newProjectUser().withUser(newUser().withId(userId).build()).withRole(roleType).withProject(newProject().withApplication(newApplication().withId(applicationId).build()).build()).build();

                    when(projectUserRepository.findByUserId(userId)).thenReturn(singletonList(projectUser));

                    ProcessRoleResource processRoleResource = newProcessRoleResource().withUser(userResource).withApplication(123L).build();

                    assertFalse(rules.projectPartnersCanViewTheProcessRolesWithinSameApplication(processRoleResource, userResource));

                });
    }

    @Test
    public void userCanCheckTheyHaveApplicationForCompetition() {
        UserResource user = newUserResource().build();
        assertTrue(rules.userCanCheckTheyHaveApplicationForCompetition(user, user));
    }

    @Test
    public void userCanCheckTheyHaveApplicationForCompetitionButAttemptingToCheckAnotherUser() {
        UserResource user = newUserResource().build();
        UserResource anotherUser = newUserResource().build();
        assertFalse(rules.userCanCheckTheyHaveApplicationForCompetition(user, anotherUser));
    }

    @Test
    public void isViewingOwnDashboard() {
        UserResource user = newUserResource().build();
        assertTrue(rules.isViewingOwnDashboard(user, user));
    }

    @Test
    public void isNotViewingOwnDashboard() {
        UserResource user = newUserResource().build();
        UserResource anotherUser = newUserResource().build();
        assertFalse(rules.isViewingOwnDashboard(user, anotherUser));
    }

    @Test
    public void ifsAdminCanViewAnyUsersProfile() {
        allGlobalRoleUsers.forEach(user -> {
            if (user.equals(ifsAdminUser())) {
                assertTrue(rules.ifsAdminCanViewAnyUsersProfile(newUserProfileResource().build(), user));
            } else {
                assertFalse(rules.ifsAdminCanViewAnyUsersProfile(newUserProfileResource().build(), user));
            }
        });
    }

    @Test
    public void ifsAdminCanEditInternalUser() {

        UserResource userToEdit = UserResourceBuilder.newUserResource().build();

        allGlobalRoleUsers.forEach(user -> {
            if (user.equals(ifsAdminUser())) {
                assertTrue(rules.ifsAdminCanEditInternalUser(userToEdit, user));
            } else {
                assertFalse(rules.ifsAdminCanEditInternalUser(userToEdit, user));
            }
        });
    }

    @Test
    public void ifsAdminCanDeactivateUser() {

        UserResource userToDeactivate = UserResourceBuilder.newUserResource().build();

        allGlobalRoleUsers.forEach(user -> {
            if (user.equals(ifsAdminUser())) {
                assertTrue(rules.ifsAdminCanDeactivateUsers(userToDeactivate, user));
            } else {
                assertFalse(rules.ifsAdminCanDeactivateUsers(userToDeactivate, user));
            }
        });
    }

    @Test
    public void systemMaintenanceUserCanDeactivateUser() {

        UserResource userToDeactivate = UserResourceBuilder.newUserResource().build();

        allGlobalRoleUsers.forEach(user -> {
            if (user.equals(systemMaintenanceUser())) {
                assertTrue(rules.systemMaintenanceUserCanDeactivateUsers(userToDeactivate, user));
            } else {
                assertFalse(rules.systemMaintenanceUserCanDeactivateUsers(userToDeactivate, user));
            }
        });
    }

    @Test
    public void ifsAdminCanReactivateUser() {

        UserResource userToReactivate = UserResourceBuilder.newUserResource().build();

        allGlobalRoleUsers.forEach(user -> {
            if (user.equals(ifsAdminUser())) {
                assertTrue(rules.ifsAdminCanDeactivateUsers(userToReactivate, user));
            } else {
                assertFalse(rules.ifsAdminCanDeactivateUsers(userToReactivate, user));
            }
        });
    }

    @Test
    public void internalUsersCanAccessAllUserOrganisations() {

        UserOrganisationResource userOrganisationResource = UserOrganisationResourceBuilder.newUserOrganisationResource().build();

        allGlobalRoleUsers.forEach(user -> {
            if (isInternal(user)) {
                assertTrue(rules.internalUsersCanViewUserOrganisation(userOrganisationResource, user));
            } else {
                assertFalse(rules.internalUsersCanViewUserOrganisation(userOrganisationResource, user));
            }
        });
    }

    @Test
    public void systemMaintenanceUserCanUpdateUsersEmailAddresses() {

        UserResource userResource = newUserResource().withRoleGlobal(APPLICANT).build();

        allGlobalRoleUsers.forEach(user -> {
            if (user.equals(systemMaintenanceUser())) {
                assertTrue(rules.systemMaintenanceUserCanUpdateUsersEmailAddresses(userResource, user));
            } else {
                assertFalse(rules.systemMaintenanceUserCanUpdateUsersEmailAddresses(userResource, user));
            }
        });
    }

    @Test
    public void supportCanUpdateExternalUsersEmailAddresses() {

        UserResource externalUser = newUserResource().withRoleGlobal(APPLICANT).build();
        UserResource internalUser = newUserResource().withRoleGlobal(IFS_ADMINISTRATOR).build();

        allGlobalRoleUsers.forEach(user -> {
            if (user.hasRole(SUPPORT)) {
                assertTrue(rules.supportCanUpdateExternalUsersEmailAddresses(externalUser, user));
                assertFalse(rules.supportCanUpdateExternalUsersEmailAddresses(internalUser, user));
            } else {
                assertFalse(rules.supportCanUpdateExternalUsersEmailAddresses(externalUser, user));
                assertFalse(rules.supportCanUpdateExternalUsersEmailAddresses(internalUser, user));
            }
        });
    }

    @Test
    public void ifsAdminCanUpdateAllEmailAddresses() {

        UserResource userResource = newUserResource().withRoleGlobal(APPLICANT).build();

        allGlobalRoleUsers.forEach(user -> {
            if (user.hasRole(IFS_ADMINISTRATOR)) {
                assertTrue(rules.ifsAdminCanUpdateAllEmailAddresses(userResource, user));
            } else {
                assertFalse(rules.ifsAdminCanUpdateAllEmailAddresses(userResource, user));
            }
        });
    }

    @Test
    public void supportUserCanDeactivateExternalUsers() {

        UserResource externalUser = newUserResource().withRoleGlobal(APPLICANT).build();
        UserResource internalUser = newUserResource().withRoleGlobal(IFS_ADMINISTRATOR).build();

        allGlobalRoleUsers.forEach(user -> {
            if (user.hasRole(SUPPORT)) {
                assertTrue(rules.supportUserCanDeactivateExternalUsers(externalUser, user));
                assertFalse(rules.supportUserCanDeactivateExternalUsers(internalUser, user));
            } else {
                assertFalse(rules.supportUserCanDeactivateExternalUsers(externalUser, user));
                assertFalse(rules.supportUserCanDeactivateExternalUsers(internalUser, user));
            }
        });
    }

    @Test
    public void supportUserCanReactivateExternalUsers() {

        UserResource externalUser = newUserResource().withRoleGlobal(APPLICANT).build();
        UserResource internalUser = newUserResource().withRoleGlobal(IFS_ADMINISTRATOR).build();

        allGlobalRoleUsers.forEach(user -> {
            if (user.hasRole(SUPPORT)) {
                assertTrue(rules.supportUserCanReactivateExternalUsers(externalUser, user));
                assertFalse(rules.supportUserCanReactivateExternalUsers(internalUser, user));
            } else {
                assertFalse(rules.supportUserCanReactivateExternalUsers(externalUser, user));
                assertFalse(rules.supportUserCanReactivateExternalUsers(internalUser, user));
            }
        });
    }

    @Test
    public void usersCanAgreeSiteTermsAndConditionsForThemselves() {
        allGlobalRoleUsers.forEach(user ->
                allGlobalRoleUsers.forEach(otherUser -> {
                    if (user.equals(otherUser)) {
                        assertTrue(rules.usersCanAgreeSiteTermsAndConditions(otherUser, user));
                    } else {
                        assertFalse(rules.usersCanAgreeSiteTermsAndConditions(otherUser, user));
                    }
                }));
    }

    @Test
    public void assessorCanRequestApplicantRole() {
        UserResource otherAssessor = newUserResource().withRolesGlobal(singletonList(ASSESSOR)).build();

        assertFalse(rules.assessorCanRequestApplicantRole(new GrantRoleCommand(assessorUser().getId(), APPLICANT), compAdminUser()));
        assertFalse(rules.assessorCanRequestApplicantRole(new GrantRoleCommand(otherAssessor.getId(), APPLICANT), assessorUser()));
        assertFalse(rules.assessorCanRequestApplicantRole(new GrantRoleCommand(assessorUser().getId(), IFS_ADMINISTRATOR), assessorUser()));

        assertTrue(rules.assessorCanRequestApplicantRole(new GrantRoleCommand(assessorUser().getId(), APPLICANT), assessorUser()));

    }

    @Test
    public void correctRolesCanGrantMonitoringOfficerRole() {
        GrantRoleCommand grantMonitoringOfficerRole = new GrantRoleCommand(assessorUser().getId(), MONITORING_OFFICER);

        assertTrue(rules.isGrantingMonitoringOfficerRoleAndHasPermission(grantMonitoringOfficerRole, compAdminUser()));
        assertTrue(rules.isGrantingMonitoringOfficerRoleAndHasPermission(grantMonitoringOfficerRole, projectFinanceUser()));
        assertTrue(rules.isGrantingMonitoringOfficerRoleAndHasPermission(grantMonitoringOfficerRole, ifsAdminUser()));
        assertFalse(rules.isGrantingMonitoringOfficerRoleAndHasPermission(grantMonitoringOfficerRole, assessorUser()));

    }

    @Test
    public void usersAllowedToGrantMonitoringOfficerRoleCannotGrantOtherRoles() {
        GrantRoleCommand grantInnovationLeadRole = new GrantRoleCommand(assessorUser().getId(), INNOVATION_LEAD);

        assertFalse(rules.isGrantingMonitoringOfficerRoleAndHasPermission(grantInnovationLeadRole, compAdminUser()));
        assertFalse(rules.isGrantingMonitoringOfficerRoleAndHasPermission(grantInnovationLeadRole, projectFinanceUser()));
        assertFalse(rules.isGrantingMonitoringOfficerRoleAndHasPermission(grantInnovationLeadRole, ifsAdminUser()));
        assertFalse(rules.isGrantingMonitoringOfficerRoleAndHasPermission(grantInnovationLeadRole, assessorUser()));

    }

    @Test
    public void compAdminAndProjectFinanceCanViewAssessors() {
        UserPageResource userPageResourceWithOnlyAssessors = new UserPageResource();
        userPageResourceWithOnlyAssessors.setContent(newUserResource().withRoleGlobal(ASSESSOR).build(1));

        UserPageResource userPageResourceWithNonAssessors = new UserPageResource();
        userPageResourceWithNonAssessors.setContent(newUserResource().withRolesGlobal(singletonList(ASSESSOR), singletonList(APPLICANT)).build(2));

        allGlobalRoleUsers.forEach(u -> {
                if (u.hasAnyRoles(COMP_ADMIN, PROJECT_FINANCE)) {
                    assertTrue(rules.compAdminAndProjectFinanceCanViewAssessors(userPageResourceWithOnlyAssessors, u));
                    assertFalse(rules.compAdminAndProjectFinanceCanViewAssessors(userPageResourceWithNonAssessors, u));
                }
                else {
                    assertFalse(rules.compAdminAndProjectFinanceCanViewAssessors(userPageResourceWithOnlyAssessors, u));
                    assertFalse(rules.compAdminAndProjectFinanceCanViewAssessors(userPageResourceWithNonAssessors, u));
                }
            }
        );
    }

    @Override
    protected UserPermissionRules supplyPermissionRulesUnderTest() {
        return new UserPermissionRules();
    }

    private Function<User, UserResource> userResourceForUser() {
        return user -> newUserResource().withId(user.getId()).build();
    }
}
