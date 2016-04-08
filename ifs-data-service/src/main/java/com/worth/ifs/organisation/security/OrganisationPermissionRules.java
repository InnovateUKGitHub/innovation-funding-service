package com.worth.ifs.organisation.security;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.organisation.resource.OrganisationSearchResult;
import com.worth.ifs.security.PermissionRule;
import com.worth.ifs.security.PermissionRules;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.worth.ifs.user.domain.UserRoleType.COMP_ADMIN;
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

    @PermissionRule(value = "READ", description = "Comp Admins can see all Competitions")
    public boolean compAdminsCanSeeAllOrganisations(OrganisationResource organisation, UserResource user) {
        return user.hasRole(COMP_ADMIN);
    }

    @PermissionRule(value = "READ", description = "Organisations that are not yet a part of any Applications are visible to anyone, " +
            "because this needs to be possible to create them during registration where there is not yet a logged-in user")
    public boolean anyoneCanSeeOrganisationsNotYetConnectedToApplications(OrganisationResource organisation, UserResource user) {
        return organisationNotYetLinkedToAnApplication(organisation);
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

    @PermissionRule(value = "CREATE", description = "Anyone should be able to create Organisations, " +
            "because this needs to be possible to create them during registration where there is not yet a logged-in user")
    public boolean anyoneCanCreateOrganisations(OrganisationResource organisation, UserResource user) {
        return true;
    }

    @PermissionRule(value = "UPDATE", description = "Organisations that are not yet a part of any Applications are " +
            "updatable by anyone, because this needs to be possible to update them during registration where there is " +
            "not yet a logged-in user")
    public boolean anyoneCanUpdateOrganisationsNotYetConnectedToApplicationsOrUsers(OrganisationResource organisation, UserResource user) {
        return organisationNotYetLinkedToAnApplication(organisation) && organisationNotYetLinkedToAnyUsers(organisation);
    }

    @PermissionRule(value = "UPDATE", description = "A member of an Organisation can update their own Organisation")
    public boolean memberOfOrganisationCanUpdateOwnOrganisation(OrganisationResource organisation, UserResource user) {
        return isMemberOfOrganisation(organisation, user);
    }

    @PermissionRule(value = "READ", description = "Anyone can search for and see all search results for Organisations")
    public boolean anyoneCanSeeOrganisationSearchResults(OrganisationSearchResult organisation, UserResource user) {
        return true;
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
