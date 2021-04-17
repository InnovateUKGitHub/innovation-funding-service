package org.innovateuk.ifs.user.security;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.security.ApplicationSecurityHelper;
import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.ExternalFinance;
import org.innovateuk.ifs.competition.domain.Stakeholder;
import org.innovateuk.ifs.competition.mapper.ExternalFinanceRepository;
import org.innovateuk.ifs.competition.repository.StakeholderRepository;
import org.innovateuk.ifs.project.core.ProjectParticipantRole;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.core.repository.ProjectUserRepository;
import org.innovateuk.ifs.project.monitoring.domain.MonitoringOfficer;
import org.innovateuk.ifs.project.monitoring.repository.MonitoringOfficerRepository;
import org.innovateuk.ifs.registration.resource.UserRegistrationResource;
import org.innovateuk.ifs.user.command.GrantRoleCommand;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.resource.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Collections.disjoint;
import static org.innovateuk.ifs.project.core.ProjectParticipantRole.PROJECT_FINANCE_CONTACT;
import static org.innovateuk.ifs.project.core.ProjectParticipantRole.PROJECT_PARTNER;
import static org.innovateuk.ifs.user.resource.Role.*;
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

    @Autowired
    private StakeholderRepository stakeholderRepository;

    @Autowired
    private ExternalFinanceRepository externalFinanceRepository;

    @Autowired
    private MonitoringOfficerRepository projectMonitoringOfficerRepository;

    @Autowired
    private ApplicationSecurityHelper applicationSecurityHelper;

    private static List<ProcessRoleType> CONSORTIUM_ROLES = asList(ProcessRoleType.LEADAPPLICANT, ProcessRoleType.COLLABORATOR);

    private static Predicate<ProcessRole> consortiumProcessRoleFilter = role -> CONSORTIUM_ROLES.contains(role.getRole());

    private static List<ProcessRoleType> ASSESSOR_ROLES = asList(ProcessRoleType.ASSESSOR, ProcessRoleType.PANEL_ASSESSOR, ProcessRoleType.INTERVIEW_ASSESSOR);

    private static Predicate<ProcessRole> assessorProcessRoleFilter = role -> ASSESSOR_ROLES.contains(role.getRole());

    private static List<String> PROJECT_ROLES = asList(ProjectParticipantRole.PROJECT_MANAGER.getName(), PROJECT_FINANCE_CONTACT.getName(), PROJECT_PARTNER.getName());

    private static Predicate<ProjectUser> projectUserFilter = projectUser -> PROJECT_ROLES.contains(projectUser.getRole().getName());

    @PermissionRule(value = "CREATE", description = "A System Registration User can create new Users on behalf of non-logged in users")
    public boolean systemRegistrationUserCanCreateUsers(UserCreationResource userToCreate, UserResource user) {
        return isSystemRegistrationUser(user);
    }

    @PermissionRule(value = "CREATE", description = "An internal user can invite a monitoring officer and create the pending user associated.")
    public boolean compAdminProjectFinanceCanCreateMonitoringOfficer(UserCreationResource userToCreate, UserResource user) {
        return userToCreate.getRole() == MONITORING_OFFICER &&
                (isCompAdmin(user) || hasProjectFinanceAuthority(user));
    }

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

    @PermissionRule(value = "READ_USER_ORGANISATION", description = "Internal support users can view all users and associated organisations")
    public boolean internalUsersCanViewUserOrganisation(UserOrganisationResource userToView, UserResource user) {
        return isInternal(user);
    }

    @PermissionRule(value = "UPDATE_USER_EMAIL", description = "IFS admins can update all users email addresses")
    public boolean ifsAdminCanUpdateAllEmailAddresses(UserResource userToUpdate, UserResource user) {
        return user.hasAuthority(Authority.IFS_ADMINISTRATOR);
    }

    @PermissionRule(value = "UPDATE_USER_EMAIL", description = "Support users can update external users email addresses ")
    public boolean supportCanUpdateExternalUsersEmailAddresses(UserResource userToUpdate, UserResource user) {
        return userToUpdate.isExternalUser() && user.hasRole(SUPPORT);
    }

    @PermissionRule(value = "UPDATE_USER_EMAIL", description = "System Maintenance update all users email addresses")
    public boolean systemMaintenanceUserCanUpdateUsersEmailAddresses(UserResource userToUpdate, UserResource user) {
        return isSystemMaintenanceUser(user);
    }

    @PermissionRule(value = "READ_INTERNAL", description = "Administrators can view internal users")
    public boolean internalUsersCanViewEveryone(ManageUserPageResource userToView, UserResource user) {
        return user.hasAuthority(Authority.IFS_ADMINISTRATOR);
    }

    @PermissionRule(value = "READ", description = "The System Registration user can view everyone")
    public boolean systemRegistrationUserCanViewEveryone(UserResource userToView, UserResource user) {
        return isSystemRegistrationUser(user);
    }

    @PermissionRule(value = "READ", description = "Comp admins and project finance can view assessors")
    public boolean compAdminAndProjectFinanceCanViewAssessors(UserPageResource usersToView, UserResource user) {
        return usersToView.getContent().stream().allMatch(u -> u.hasRole(ASSESSOR)) &&
                user.hasAuthority(Authority.COMP_ADMIN);
    }

    @PermissionRule(value = "READ", description = "Consortium members (Lead Applicants and Collaborators) can view the others in their Consortium Teams on their various Applications")
    public boolean consortiumMembersCanViewOtherConsortiumMembers(UserResource userToView, UserResource user) {
        List<Application> applicationsWhereThisUserIsInConsortium = getApplicationsRelatedToUserByProcessRoles(user.getId(), consortiumProcessRoleFilter);
        List<ProcessRole> otherProcessRolesForThoseApplications = getAllProcessRolesForApplications(applicationsWhereThisUserIsInConsortium);
        List<ProcessRole> allConsortiumProcessRoles = simpleFilter(otherProcessRolesForThoseApplications, consortiumProcessRoleFilter);
        List<User> allConsortiumUsers = simpleMap(allConsortiumProcessRoles, ProcessRole::getUser);
        return simpleMap(allConsortiumUsers, User::getId).contains(userToView.getId());
    }

    @PermissionRule(value = "READ", description = "Assessors can view the members of individual Consortiums on the various Applications that they are assessing")
    public boolean assessorsCanViewConsortiumUsersOnApplicationsTheyAreAssessing(UserResource userToView, UserResource user) {
        List<Application> applicationsThatThisUserIsAssessing = getApplicationsRelatedToUserByProcessRoles(user.getId(), assessorProcessRoleFilter);
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

    @PermissionRule(value = "UPDATE", description = "An admin user can update user details to assign monitoring officers")
    public boolean adminsCanUpdateUserDetails(UserResource userToUpdate, UserResource user) {
        return hasPermissionToGrantRole(user);
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

    @PermissionRule(value = "READ_USER_PROFILE", description = "An internal user can read any user's profile")
    public boolean internalUserCanViewAnyUsersProfile(UserProfileResource profileDetails, UserResource user) {
        return user.isInternalUser();
    }

    @PermissionRule(value = "READ", description = "The user, as well as Comp Admin and Exec can read the user's profile status")
    public boolean usersAndCompAdminCanViewProfileStatus(UserProfileStatusResource profileStatus, UserResource user) {
        return profileStatus.getUser() == user.getId() || isCompAdmin(user);
    }

    @PermissionRule(value = "READ", description = "A user can read their own role")
    public boolean usersCanViewTheirOwnProcessRole(ProcessRoleResource processRole, UserResource user) {
        return user.getId().equals(processRole.getUser());
    }

    @PermissionRule(value = "READ", description = "Consortium members (Lead Applicants and Collaborators) can view the process role of others in their Consortium Teams on their various Applications")
    public boolean consortiumMembersCanViewTheProcessRolesOfOtherConsortiumMembers(ProcessRoleResource processRole, UserResource user) {
        List<Application> applicationsWhereThisUserIsInConsortium = getApplicationsRelatedToUserByProcessRoles(user.getId(), consortiumProcessRoleFilter);

        return simpleMap(applicationsWhereThisUserIsInConsortium, Application::getId).contains(processRole.getApplicationId());
    }

    @PermissionRule(value = "READ", description = "Project managers and partners can view the process role for the same organisation")
    public boolean projectPartnersCanViewTheProcessRolesWithinSameApplication(ProcessRoleResource processRole, UserResource user) {
        return getFilteredProjectUsers(user.getId(), projectUserFilter).stream().anyMatch(projectUser -> projectUser.getProject().getApplication().getId().equals(processRole.getApplicationId()));
    }

    @PermissionRule(value = "READ", description = "The user, as well as internal users can read the user's process role")
    public boolean usersAndInternalUsersCanViewProcessRole(ProcessRoleResource processRole, UserResource user) {
        return isInternal(user);
    }

    @PermissionRule(value = "READ", description = "Support users and administrators can view external users")
    public boolean supportUsersCanViewExternalUsers(ManageUserPageResource userToView, UserResource user) {
        return user.hasAnyAuthority(asList(Authority.IFS_ADMINISTRATOR, Authority.SUPPORT));
    }

    @PermissionRule(value = "READ", description = "Internal users can view everyone")
    public boolean internalUsersCanViewEveryone(UserResource userToView, UserResource user) {
        return isInternal(user);
    }

    @PermissionRule(value = "READ", description = "Stakeholders can view users in competitions they are assigned to")
    public boolean stakeholdersCanViewUsersInCompetitionsTheyAreAssignedTo(UserResource userToView, UserResource user) {
        return userIsInCompetitionAssignedToStakeholder(userToView.getId(), user);
    }

    @PermissionRule(value = "READ", description = "Competition finance users can view users in competitions they are assigned to")
    public boolean competitionFinanceUsersCanViewUsersInCompetitionsTheyAreAssignedTo(UserResource userToView, UserResource user) {
        return userIsInCompetitionAssignedToCompetitionFinance(userToView, user);
    }

    @PermissionRule(value = "READ", description = "Monitoring officers can view users in projects they are assigned to")
    public boolean monitoringOfficersCanViewUsersInCompetitionsTheyAreAssignedTo(UserResource userToView, UserResource user) {
        return userIsInProjectAssignedToMonitoringOfficer(userToView, user);
    }

    @PermissionRule(value = "READ", description = "Assessors can view the process roles of members of individual Consortiums on the various Applications that they are assessing")
    public boolean assessorsCanViewTheProcessRolesOfConsortiumUsersOnApplicationsTheyAreAssessing(ProcessRoleResource processRole, UserResource user) {
        List<Application> applicationsThatThisUserIsAssessing = getApplicationsRelatedToUserByProcessRoles(user.getId(), assessorProcessRoleFilter);
        return simpleMap(applicationsThatThisUserIsAssessing, Application::getId).contains(processRole.getApplicationId());
    }

    @PermissionRule(value = "READ", description = "user has permission to view the application")
    public boolean canViewTheApplication(ProcessRoleResource processRole, UserResource user) {
        return applicationSecurityHelper.canViewApplication(processRole.getApplicationId(), user);
    }

    @PermissionRule(value = "READ", description = "External finance users can read.")
    public boolean readExternalFinance(ProcessRoleResource processRole, UserResource user) {
        return isExternalFinanceUser(user);
    }

    @PermissionRule(value = "CHECK_USER_APPLICATION", description = "The user can check if they have an application for the competition")
    public boolean userCanCheckTheyHaveApplicationForCompetition(UserResource userToCheck, UserResource user) {
        return userToCheck.getId().equals(user.getId());
    }

    @PermissionRule(value = "EDIT_INTERNAL_USER", description = "Only an IFS Administrator can edit an internal user")
    public boolean ifsAdminCanEditInternalUser(final UserResource userToEdit, UserResource user) {
        return user.hasAuthority(Authority.IFS_ADMINISTRATOR);
    }

    @PermissionRule(value = "DEACTIVATE", description = "IFS Administrator can deactivate Users")
    public boolean ifsAdminCanDeactivateUsers(UserResource userToDeactivate, UserResource user) {
        return user.hasAuthority(Authority.IFS_ADMINISTRATOR);
    }

    @PermissionRule(value = "DEACTIVATE", description = "A Support user can deactivate external Users")
    public boolean supportUserCanDeactivateExternalUsers(UserResource userToDeactivate, UserResource user) {
        return userToDeactivate.isExternalUser() && user.hasRole(SUPPORT);
    }

    @PermissionRule(value = "DEACTIVATE", description = "System Maintenance can deactivate Users")
    public boolean systemMaintenanceUserCanDeactivateUsers(UserResource userToDeactivate, UserResource user) {
        return isSystemMaintenanceUser(user);
    }

    @PermissionRule(value = "ACTIVATE", description = "IFS Administrator can reactivate Users")
    public boolean ifsAdminCanReactivateUsers(UserResource userToReactivate, UserResource user) {
        return user.hasAuthority(Authority.IFS_ADMINISTRATOR);
    }

    @PermissionRule(value = "ACTIVATE", description = "A Support user can reactivate external Users")
    public boolean supportUserCanReactivateExternalUsers(UserResource userToActivate, UserResource user) {
        return userToActivate.isExternalUser() && user.hasRole(SUPPORT);
    }

    @PermissionRule(value = "AGREE_TERMS", description = "A user can accept the site terms and conditions")
    public boolean usersCanAgreeSiteTermsAndConditions(UserResource userToUpdate, UserResource user) {
        return userToUpdate.getId().equals(user.getId());
    }

    @PermissionRule(value = "GRANT_ROLE", description = "An admin user can grant monitoring officer role")
    public boolean isGrantingMonitoringOfficerRoleAndHasPermission(GrantRoleCommand roleCommand, UserResource user) {
        return hasPermissionToGrantRole(user) && roleCommand.getTargetRole().equals(MONITORING_OFFICER);
    }

    @PermissionRule(value = "GRANT_ROLE", description = "An admin user can grant a KTA role")
    public boolean isGrantingKTARoleAndHasPermission(GrantRoleCommand roleCommand, UserResource user) {
        return hasPermissionToGrantRole(user) && roleCommand.getTargetRole().equals(KNOWLEDGE_TRANSFER_ADVISER);
    }

    @PermissionRule(value = "GRANT_ROLE", description = "An admin user can grant a supporter role")
    public boolean isGrantingCoFunderRoleAndHasPermission(GrantRoleCommand roleCommand, UserResource user) {
        return hasPermissionToGrantRole(user) && roleCommand.getTargetRole().equals(SUPPORTER);
    }

    @PermissionRule(value = "GRANT_ROLE", description = "Users such as assessor, stakeholder, monitoring_officer, live_projects_user and " +
            "supporter can request applicant role")
    public boolean isMultipleRoleDashboardUsersCanRequestApplicantRole(GrantRoleCommand roleCommand, UserResource user) {
        return user.hasAnyRoles(Role.multiDashboardRoles());
    }

    @PermissionRule(value = "CAN_VIEW_OWN_DASHBOARD", description = "User is requesting own dashboard")
    public boolean isViewingOwnDashboard(UserResource userToView, UserResource user) {
        return userToView.getId().equals(user.getId());
    }

    private boolean hasPermissionToGrantRole(UserResource user) {
        return user.hasAuthority(Authority.COMP_ADMIN);
    }

    private boolean userIsInCompetitionAssignedToStakeholder(long userToViewId, UserResource stakeholder) {
        List<Application> applicationsWhereThisUserIsInConsortium = getApplicationsRelatedToUserByProcessRoles(userToViewId, consortiumProcessRoleFilter);
        List<Project> projectsThisUserIsAMemberOf =
                simpleMap(getFilteredProjectUsers(userToViewId, projectUserFilter), ProjectUser::getProject);

        List<Competition> stakeholderCompetitions =
                simpleMap(stakeholderRepository.findByStakeholderId(stakeholder.getId()), Stakeholder::getProcess);

        List<Competition> userCompetitions = getUserCompetitions(applicationsWhereThisUserIsInConsortium, projectsThisUserIsAMemberOf);

        return !disjoint(stakeholderCompetitions, userCompetitions);
    }

    private boolean userIsInCompetitionAssignedToCompetitionFinance(UserResource userToView, UserResource compFinance) {
        List<Application> applicationsWhereThisUserIsInConsortium = getApplicationsRelatedToUserByProcessRoles(userToView.getId(), consortiumProcessRoleFilter);
        List<Project> projectsThisUserIsAMemberOf =
                simpleMap(getFilteredProjectUsers(userToView.getId(), projectUserFilter), ProjectUser::getProject);

        List<Competition> competitions =
                simpleMap(externalFinanceRepository.findByCompetitionFinanceId(compFinance.getId()), ExternalFinance::getProcess);

        List<Competition> userCompetitions = getUserCompetitions(applicationsWhereThisUserIsInConsortium, projectsThisUserIsAMemberOf);

        return !disjoint(competitions, userCompetitions);
    }

    private boolean userIsInProjectAssignedToMonitoringOfficer(UserResource userToView, UserResource monitoringOfficer) {
        List<Project> projectsThisUserIsAMemberOf =
                simpleMap(getFilteredProjectUsers(userToView.getId(), projectUserFilter), ProjectUser::getProject);

        List<MonitoringOfficer> projectMonitoringOfficers = projectMonitoringOfficerRepository.findByUserId(monitoringOfficer.getId());

        List<Project> monitoringOfficerProjects = simpleMap(projectMonitoringOfficers, MonitoringOfficer::getProject);

        return !disjoint(monitoringOfficerProjects, projectsThisUserIsAMemberOf);
    }

    private List<Competition> getUserCompetitions(List<Application> userApplications, List<Project> userProjects) {
        List<Competition> competitions = new ArrayList<>();
        competitions.addAll(simpleMap(userApplications, Application::getCompetition));
        competitions.addAll(
                userProjects.stream()
                        .map(project -> project.getApplication().getCompetition())
                        .collect(Collectors.toList())
        );
        return competitions;
    }

    private List<Application> getApplicationsRelatedToUserByProcessRoles(long userToViewId, Predicate<ProcessRole> processRoleFilter) {
        List<ProcessRole> applicableProcessRoles = getFilteredProcessRoles(userToViewId, processRoleFilter);
        return simpleMap(applicableProcessRoles, processRole -> applicationRepository.findById(processRole.getApplicationId()).orElse(null));
    }

    private List<ProcessRole> getFilteredProcessRoles(long userToViewId, Predicate<ProcessRole> filter) {
        List<ProcessRole> processRoles = processRoleRepository.findByUserId(userToViewId);
        return simpleFilter(processRoles, filter);
    }

    private List<ProjectUser> getFilteredProjectUsers(long userId, Predicate<ProjectUser> filter) {
        List<ProjectUser> projectUsers = projectUserRepository.findByUserId(userId);
        return simpleFilter(projectUsers, filter);
    }

    private List<ProcessRole> getAllProcessRolesForApplications(List<Application> applicationsWhereThisUserIsInConsortium) {
        return flattenLists(simpleMap(applicationsWhereThisUserIsInConsortium, Application::getProcessRoles));
    }
}