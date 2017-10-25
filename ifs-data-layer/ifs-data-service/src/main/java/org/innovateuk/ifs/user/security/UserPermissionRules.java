package org.innovateuk.ifs.user.security;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.invite.domain.ProjectParticipantRole;
import org.innovateuk.ifs.project.domain.ProjectUser;
import org.innovateuk.ifs.project.repository.ProjectUserRepository;
import org.innovateuk.ifs.registration.resource.UserRegistrationResource;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.resource.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.user.resource.UserRoleType.*;
import static org.innovateuk.ifs.util.CollectionFunctions.*;
import static org.innovateuk.ifs.util.SecurityRuleUtil.*;

/**
 * Permission rules that determines who can perform CRUD operations based around Users.
 */
@Component
@PermissionRules
public class UserPermissionRules {

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ProjectUserRepository projectUserRepository;

    private static List<String> CONSORTIUM_ROLES = asList(LEADAPPLICANT.getName(), COLLABORATOR.getName());

    private static Predicate<ProcessRole> consortiumProcessRoleFilter = role -> CONSORTIUM_ROLES.contains(role.getRole().getName());

    private static Predicate<ProcessRole> assessorProcessRoleFilter = role -> role.getRole().getName().equals(ASSESSOR.getName());

    private static List<String> PROJECT_ROLES = asList(ProjectParticipantRole.PROJECT_MANAGER.getName(), ProjectParticipantRole.PROJECT_FINANCE_CONTACT.getName(), ProjectParticipantRole.PROJECT_PARTNER.getName());

    private static Predicate<ProjectUser> projectUserFilter = projectUser -> PROJECT_ROLES.contains(projectUser.getRole().getName());


    @PermissionRule(value = "CREATE", description = "A System Registration User can create new Users on behalf of non-logged in users")
    public boolean systemRegistrationUserCanCreateUsers(UserResource userToCreate, UserResource user) {
        return isSystemRegistrationUser(user);
    }

    @PermissionRule(value = "CREATE", description = "A System Registration User can create new Users on behalf of non-logged in users")
    public boolean systemRegistrationUserCanCreateUsers(UserRegistrationResource userToCreate, UserResource user) {
        return isSystemRegistrationUser(user);
    }

    @PermissionRule(value = "VERIFY", description = "A System Registration User can send a new User a verification link by e-mail")
    public boolean systemRegistrationUserCanSendUserVerificationEmail(UserResource userToSendVerificationEmail, UserResource user) {
        return isSystemRegistrationUser(user);
    }

    @PermissionRule(value = "READ", description = "Any user can view themselves")
    public boolean anyUserCanViewThemselves(UserResource userToView, UserResource user) {
        return userToView.getId().equals(user.getId());
    }

    @PermissionRule(value = "READ", description = "Internal users can view everyone")
    public boolean internalUsersCanViewEveryone(UserResource userToView, UserResource user) {
        return isInternal(user);
    }

    @PermissionRule(value = "READ_USER_ORGANISATION", description = "Internal support users can view all users and associated organisations")
    public boolean internalUsersCanViewUserOrganisation(UserOrganisationResource userToView, UserResource user) {
        return isInternal(user);
    }

    @PermissionRule(value = "READ", description = "Internal users can view everyone")
    public boolean internalUsersCanViewEveryone(UserPageResource userToView, UserResource user) {
        return user.hasRole(UserRoleType.IFS_ADMINISTRATOR);
    }

    @PermissionRule(value = "READ", description = "The System Registration user can view everyone")
    public boolean systemRegistrationUserCanViewEveryone(UserResource userToView, UserResource user) {
        return isSystemRegistrationUser(user);
    }

    @PermissionRule(value = "READ", description = "Consortium members (Lead Applicants and Collaborators) can view the others in their Consortium Teams on their various Applications")
    public boolean consortiumMembersCanViewOtherConsortiumMembers(UserResource userToView, UserResource user) {
        List<Application> applicationsWhereThisUserIsInConsortium = getApplicationsRelatedToUserByProcessRoles(user, consortiumProcessRoleFilter);
        List<ProcessRole> otherProcessRolesForThoseApplications = getAllProcessRolesForApplications(applicationsWhereThisUserIsInConsortium);
        List<ProcessRole> allConsortiumProcessRoles = simpleFilter(otherProcessRolesForThoseApplications, consortiumProcessRoleFilter);
        List<User> allConsortiumUsers = simpleMap(allConsortiumProcessRoles, ProcessRole::getUser);
        return simpleMap(allConsortiumUsers, User::getId).contains(userToView.getId());
    }

    @PermissionRule(value = "READ", description = "Assessors can view the members of individual Consortiums on the various Applications that they are assessing")
    public boolean assessorsCanViewConsortiumUsersOnApplicationsTheyAreAssessing(UserResource userToView, UserResource user) {
        List<Application> applicationsThatThisUserIsAssessing = getApplicationsRelatedToUserByProcessRoles(user, assessorProcessRoleFilter);
        List<ProcessRole> processRolesForAllApplications = getAllProcessRolesForApplications(applicationsThatThisUserIsAssessing);
        List<ProcessRole> allConsortiumProcessRoles = simpleFilter(processRolesForAllApplications, consortiumProcessRoleFilter);
        List<User> allConsortiumUsers = simpleMap(allConsortiumProcessRoles, ProcessRole::getUser);
        return simpleMap(allConsortiumUsers, User::getId).contains(userToView.getId());
    }

    @PermissionRule(value = "CHANGE_PASSWORD", description = "A User should be able to change their own password")
    public boolean usersCanChangeTheirOwnPassword(UserResource userToUpdate, UserResource user) {
        return userToUpdate.getId().equals(user.getId());
    }

    @PermissionRule(value = "CHANGE_PASSWORD", description = "The System Registration user should be able to change passwords on behalf of other Users")
    public boolean systemRegistrationUserCanChangePasswordsForUsers(UserResource userToUpdate, UserResource user) {
        return isSystemRegistrationUser(user);
    }

    @PermissionRule(value = "ACTIVATE", description = "A System Registration User can activate Users")
    public boolean systemRegistrationUserCanActivateUsers(UserResource userToCreate, UserResource user) {
        return isSystemRegistrationUser(user);
    }

    @PermissionRule(value = "UPDATE", description = "A User can update their own profile")
    public boolean usersCanUpdateTheirOwnProfiles(UserResource userToUpdate, UserResource user) {
        return userToUpdate.getId().equals(user.getId());
    }

    @PermissionRule(value = "READ", description = "A user can read their own profile skills")
    public boolean usersCanViewTheirOwnProfileSkills(ProfileSkillsResource profileSkills, UserResource user) {
        return user.getId().equals(profileSkills.getUser());
    }

    @PermissionRule(value = "READ", description = "A user can read their own profile agreement")
    public boolean usersCanViewTheirOwnProfileAgreement(ProfileAgreementResource profileAgreementResource, UserResource user) {
        return user.getId().equals(profileAgreementResource.getUser());
    }

    @PermissionRule(value = "READ", description = "A user can read their own affiliations")
    public boolean usersCanViewTheirOwnAffiliations(AffiliationResource affiliation, UserResource user) {
        return user.getId().equals(affiliation.getUser());
    }

    @PermissionRule(value = "READ_USER_PROFILE", description = "A user can read their own profile")
    public boolean usersCanViewTheirOwnProfile(UserProfileResource profileDetails, UserResource user) {
        return profileDetails.getUser().equals(user.getId());
    }

    @PermissionRule(value = "READ_USER_PROFILE", description = "A ifs admin user can read any user's profile")
    public boolean ifsAdminCanViewAnyUsersProfile(UserProfileResource profileDetails, UserResource user) {
        return user.hasRole(UserRoleType.IFS_ADMINISTRATOR);
    }

    @PermissionRule(value = "READ", description = "The user, as well as Comp Admin and Exec can read the user's profile status")
    public boolean usersAndCompAdminCanViewProfileStatus(UserProfileStatusResource profileStatus, UserResource user) {
        return profileStatus.getUser().equals(user.getId()) || isCompAdmin(user);
    }

    @PermissionRule(value = "READ", description = "A user can read their own role")
    public boolean usersCanViewTheirOwnProcessRole(ProcessRoleResource processRole, UserResource user) {
        return user.getId().equals(processRole.getUser());
    }

    @PermissionRule(value = "READ", description = "Consortium members (Lead Applicants and Collaborators) can view the process role of others in their Consortium Teams on their various Applications")
    public boolean consortiumMembersCanViewTheProcessRolesOfOtherConsortiumMembers(ProcessRoleResource processRole, UserResource user) {
        List<Application> applicationsWhereThisUserIsInConsortium = getApplicationsRelatedToUserByProcessRoles(user, consortiumProcessRoleFilter);

        return simpleMap(applicationsWhereThisUserIsInConsortium, Application::getId).contains(processRole.getApplicationId());
    }

    @PermissionRule(value = "READ", description = "Project managers and partners can view the process role for the same organisation")
    public boolean projectPartnersCanViewTheProcessRolesWithinSameApplication(ProcessRoleResource processRole, UserResource user) {
        return getFilteredProjectUsers(user, projectUserFilter).stream().anyMatch(projectUser -> projectUser.getProject().getApplication().getId().equals(processRole.getApplicationId()));
    }

    @PermissionRule(value = "READ", description = "The user, as well as internal users can read the user's process role")
    public boolean usersAndInternalUsersCanViewProcessRole(ProcessRoleResource processRole, UserResource user) {
        return processRole.getUser().equals(user.getId()) || isInternal(user);
    }

    @PermissionRule(value = "READ", description = "Assessors can view the process roles of members of individual Consortiums on the various Applications that they are assessing")
    public boolean assessorsCanViewTheProcessRolesOfConsortiumUsersOnApplicationsTheyAreAssessing(ProcessRoleResource processRole, UserResource user) {
        List<Application> applicationsThatThisUserIsAssessing = getApplicationsRelatedToUserByProcessRoles(user, assessorProcessRoleFilter);
        return simpleMap(applicationsThatThisUserIsAssessing, Application::getId).contains(processRole.getApplicationId());
    }

    @PermissionRule(value = "CHECK_USER_APPLICATION", description = "The user can check if they have an application for the competition")
    public boolean userCanCheckTheyHaveApplicationForCompetition(UserResource userToCheck, UserResource user) {
        return userToCheck.getId().equals(user.getId());
    }

    @PermissionRule(value = "EDIT_INTERNAL_USER", description = "Only an IFS Administrator can edit an internal user")
    public boolean ifsAdminCanEditInternalUser(final UserResource userToEdit, UserResource user) {
        return user.hasRole(UserRoleType.IFS_ADMINISTRATOR);
    }

    @PermissionRule(value = "DEACTIVATE", description = "IFS Administrator can deactivate Users")
    public boolean ifsAdminCanDeactivateUsers(UserResource userToCreate, UserResource user) {
        return user.hasRole(UserRoleType.IFS_ADMINISTRATOR);
    }

    @PermissionRule(value = "ACTIVATE", description = "IFS Administrator can reactivate Users")
    public boolean ifsAdminCanReactivateUsers(UserResource userToCreate, UserResource user) {
        return user.hasRole(UserRoleType.IFS_ADMINISTRATOR);
    }

    private List<Application> getApplicationsRelatedToUserByProcessRoles(UserResource user, Predicate<ProcessRole> processRoleFilter) {
        List<ProcessRole> applicableProcessRoles = getFilteredProcessRoles(user, processRoleFilter);
        return simpleMap(applicableProcessRoles, processRole -> applicationRepository.findOne(processRole.getApplicationId()));
    }

    private List<ProcessRole> getFilteredProcessRoles(UserResource user, Predicate<ProcessRole> filter) {
        List<ProcessRole> processRoles = processRoleRepository.findByUserId(user.getId());
        return simpleFilter(processRoles, filter);
    }

    private List<ProjectUser> getFilteredProjectUsers(UserResource user, Predicate<ProjectUser> filter) {
        List<ProjectUser> projectUsers = projectUserRepository.findByUserId(user.getId());
        return simpleFilter(projectUsers, filter);
    }

    private List<ProcessRole> getAllProcessRolesForApplications(List<Application> applicationsWhereThisUserIsInConsortium) {
        return flattenLists(simpleMap(applicationsWhereThisUserIsInConsortium, Application::getProcessRoles));
    }

}
