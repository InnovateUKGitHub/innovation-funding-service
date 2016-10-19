package com.worth.ifs.organisation.security;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.commons.security.PermissionRule;
import com.worth.ifs.commons.security.PermissionRules;
import com.worth.ifs.organisation.resource.OrganisationSearchResult;
import com.worth.ifs.project.domain.PartnerOrganisation;
import com.worth.ifs.project.domain.ProjectUser;
import com.worth.ifs.project.repository.ProjectUserRepository;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.worth.ifs.invite.domain.ProjectParticipantRole.PROJECT_PARTNER;
import static com.worth.ifs.security.SecurityRuleUtil.*;
import static com.worth.ifs.util.CollectionFunctions.flattenLists;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;

/**
 * Permission Rules determining who can perform which actions upon an Organisation
 */
@Component
@PermissionRules
public class OrganisationPermissionRules {

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Autowired
    private ProjectUserRepository projectUserRepository;

    @PermissionRule(value = "READ", description = "Comp Admins can see all Organisations")
    public boolean compAdminsCanSeeAllOrganisations(OrganisationResource organisation, UserResource user) {
        return isCompAdmin(user);
    }

    @PermissionRule(value = "READ", description = "Project Finance Users can see all Organisations")
    public boolean projectFinanceUserCanSeeAllOrganisations(OrganisationResource organisation, UserResource user) {
        return isProjectFinanceUser(user);
    }


    @PermissionRule(value = "READ", description = "System Registration User can see all Organisations, in order to view particular Organisations during registration and invite")
    public boolean systemRegistrationUserCanSeeAllOrganisations(OrganisationResource organisation, UserResource user) {
        return isSystemRegistrationUser(user);
    }

    @PermissionRule(value = "READ", description = "The System Registration User can see Organisations on behalf of non-logged in users " +
            "whilst the Organisation is not yet linked to an Application")
    public boolean systemRegistrationUserCanSeeOrganisationsNotYetConnectedToApplications(OrganisationResource organisation, UserResource user) {
        return isSystemRegistrationUser(user) && organisationNotYetLinkedToAnApplication(organisation);
    }

    @PermissionRule(value = "READ", description = "A member of an Organisation can view their own Organisation")
    public boolean memberOfOrganisationCanViewOwnOrganisation(OrganisationResource organisation, UserResource user) {
        return isMemberOfOrganisation(organisation, user);
    }

    @PermissionRule(value = "READ", description = "Users linked to Applications can view the basic details of the other Organisations on their own Applications")
    public boolean usersCanViewOrganisationsOnTheirOwnApplications(OrganisationResource organisation, UserResource user) {

        // TODO DW - INFUND-1556 - this code feels pretty heavy given that all we need to do is find a link between a User and an Organisation via an Application
        List<Long> applicationRoles = user.getProcessRoles();
        List<ProcessRole> processRoles = simpleMap(applicationRoles, processRoleRepository::findOne);
        List<Application> applicationsThatThisUserIsLinkedTo = simpleMap(processRoles, ProcessRole::getApplication);
        List<ProcessRole> processRolesForAllApplications = flattenLists(simpleMap(applicationsThatThisUserIsLinkedTo, Application::getProcessRoles));
        List<Organisation> allOrganisationsLinkedToAnyOfUsersApplications = simpleMap(processRolesForAllApplications, ProcessRole::getOrganisation);

        return simpleMap(allOrganisationsLinkedToAnyOfUsersApplications, Organisation::getId).contains(organisation.getId());
    }

    @PermissionRule(value = "CREATE", description = "The System Registration User can create Organisations on behalf of non-logged in Users " +
            "during the regsitration process")
    public boolean systemRegistrationUserCanCreateOrganisations(OrganisationResource organisation, UserResource user) {
        return isSystemRegistrationUser(user);
    }

    @PermissionRule(value = "UPDATE", description = "The System Registration User can update Organisations that are not yet linked to Applications on behalf of non-logged in Users " +
            "during the regsitration process")
    public boolean systemRegistrationUserCanUpdateOrganisationsNotYetConnectedToApplicationsOrUsers(OrganisationResource organisation, UserResource user) {
        return isSystemRegistrationUser(user) && organisationNotYetLinkedToAnApplication(organisation) && organisationNotYetLinkedToAnyUsers(organisation);
    }

    @PermissionRule(value = "UPDATE", description = "A member of an Organisation can update their own Organisation")
    public boolean memberOfOrganisationCanUpdateOwnOrganisation(OrganisationResource organisation, UserResource user) {
        return isMemberOfOrganisation(organisation, user);
    }

    @PermissionRule(value = "READ", description = "The System Registration User can search for Organisations on behalf of non-logged in " +
            "users during the registration process")
    public boolean systemRegistrationUserCanSeeOrganisationSearchResults(OrganisationSearchResult organisation, UserResource user) {
        return isSystemRegistrationUser(user);
    }

    @PermissionRule(value = "UPDATE", description = "A project finance user can update any Organisation")
    public boolean projectFinanceUserCanUpdateAnyOrganisation(OrganisationResource organisation, UserResource user) {
        return isProjectFinanceUser(user);
    }

    @PermissionRule(value = "READ", description = "Project Partners can see the Partner Organisations within their Projects")
    public boolean projectPartnerUserCanSeePartnerOrganisationsWithinTheirProjects(OrganisationResource organisation, UserResource user) {

        List<ProjectUser> projectRoles = projectUserRepository.findByUserIdAndRole(user.getId(), PROJECT_PARTNER);

        return projectRoles.stream().anyMatch(projectUser -> {
            List<PartnerOrganisation> partnerOrganisations = projectUser.getProject().getPartnerOrganisations();
            return partnerOrganisations.stream().anyMatch(org -> org.getOrganisation().getId().equals(organisation.getId()));
        });
    }

    private boolean isMemberOfOrganisation(OrganisationResource organisation, UserResource user) {
        return organisation.getUsers() != null && organisation.getUsers().contains(user.getId());
    }

    private boolean organisationNotYetLinkedToAnApplication(OrganisationResource organisation) {
        return organisation.getProcessRoles().isEmpty();
    }

    private boolean organisationNotYetLinkedToAnyUsers(OrganisationResource organisation) {
        return organisation.getUsers().isEmpty();
    }
}
