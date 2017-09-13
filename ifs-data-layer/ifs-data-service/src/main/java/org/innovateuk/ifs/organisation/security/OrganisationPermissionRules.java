package org.innovateuk.ifs.organisation.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.organisation.resource.OrganisationSearchResult;
import org.innovateuk.ifs.project.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.domain.ProjectUser;
import org.innovateuk.ifs.project.repository.ProjectUserRepository;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.innovateuk.ifs.invite.domain.ProjectParticipantRole.PROJECT_PARTNER;
import static org.innovateuk.ifs.util.CollectionFunctions.flattenLists;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.SecurityRuleUtil.*;

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

    @PermissionRule(value = "READ", description = "Internal Users can see all Organisations")
    public boolean internalUsersCanSeeAllOrganisations(OrganisationResource organisation, UserResource user) {
        return isInternal(user);
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
        List<ProcessRole> processRoles = processRoleRepository.findByUserId(user.getId());
        List<Long> applicationsThatThisUserIsLinkedTo = simpleMap(processRoles, ProcessRole::getApplicationId);
        List<ProcessRole> processRolesForAllApplications = flattenLists(simpleMap(applicationsThatThisUserIsLinkedTo, applicationId -> {
            return processRoleRepository.findByApplicationId(applicationId);
        }));

        return simpleMap(processRolesForAllApplications, ProcessRole::getOrganisationId).contains(organisation.getId());
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
