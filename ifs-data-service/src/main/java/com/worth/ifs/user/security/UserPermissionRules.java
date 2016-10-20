package com.worth.ifs.user.security;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.commons.security.PermissionRule;
import com.worth.ifs.commons.security.PermissionRules;
import com.worth.ifs.registration.resource.UserRegistrationResource;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.user.resource.AffiliationResource;
import com.worth.ifs.user.resource.ProfileAddressResource;
import com.worth.ifs.user.resource.ProfileContractResource;
import com.worth.ifs.user.resource.ProfileSkillsResource;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

import static com.worth.ifs.security.SecurityRuleUtil.*;
import static com.worth.ifs.user.resource.UserRoleType.*;
import static com.worth.ifs.util.CollectionFunctions.*;
import static java.util.Arrays.asList;

/**
 * Permission rules that determines who can perform CRUD operations based around Users.
 */
@Component
@PermissionRules
public class UserPermissionRules {

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    private static List<String> CONSORTIUM_ROLES = asList(LEADAPPLICANT.getName(), COLLABORATOR.getName());

    private static Predicate<ProcessRole> consortiumProcessRoleFilter = role -> CONSORTIUM_ROLES.contains(role.getRole().getName());

    private static Predicate<ProcessRole> assessorProcessRoleFilter = role -> role.getRole().getName().equals(ASSESSOR.getName());

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

    @PermissionRule(value = "READ", description = "Comp Admins can view everyone")
    public boolean compAdminsCanViewEveryone(UserResource userToView, UserResource user) {
        return isCompAdmin(user);
    }

    @PermissionRule(value = "READ", description = "Project finance users can view everyone")
    public boolean projectFinanceUsersCanViewEveryone(UserResource userToView, UserResource user) {
        return isProjectFinanceUser(user);
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

    @PermissionRule(value = "READ", description = "A user can read their own profile contract")
    public boolean usersCanViewTheirOwnProfileContract(ProfileContractResource profileContract, UserResource user) {
        return user.getId().equals(profileContract.getUser());
    }

    @PermissionRule(value = "READ", description = "A user can read their own affiliations")
    public boolean usersCanViewTheirOwnAffiliations(AffiliationResource affiliation, UserResource user) {
        return user.getId().equals(affiliation.getUser());
    }

    @PermissionRule(value = "UPDATE_AFFILIATIONS", description = "A User can update their own affiliations")
    public boolean usersCanUpdateTheirOwnAffiliations(UserResource userToUpdate, UserResource user) {
        return user.getId().equals(userToUpdate.getId());
    }

    @PermissionRule(value = "READ", description = "A user can read their own address")
    public boolean usersCanViewTheirOwnAddress(ProfileAddressResource address, UserResource user) {
        return user.getId().equals(address.getUser());
    }

    @PermissionRule(value = "UPDATE_PROFILE_ADDRESS", description = "A User can update their own address")
    public boolean usersCanUpdateTheirOwnAddress(UserResource userToUpdate, UserResource user) {
        return user.getId().equals(userToUpdate.getId());
    }

    private List<Application> getApplicationsRelatedToUserByProcessRoles(UserResource user, Predicate<ProcessRole> processRoleFilter) {
        List<ProcessRole> applicableProcessRoles = getFilteredProcessRoles(user, processRoleFilter);
        return simpleMap(applicableProcessRoles, ProcessRole::getApplication);
    }

    private List<ProcessRole> getFilteredProcessRoles(UserResource user, Predicate<ProcessRole> filter) {
        List<Long> applicationRoles = user.getProcessRoles();
        List<ProcessRole> processRoles = simpleMap(applicationRoles, processRoleRepository::findOne);
        return simpleFilter(processRoles, filter);
    }

    private List<ProcessRole> getAllProcessRolesForApplications(List<Application> applicationsWhereThisUserIsInConsortium) {
        return flattenLists(simpleMap(applicationsWhereThisUserIsInConsortium, Application::getProcessRoles));
    }
}
