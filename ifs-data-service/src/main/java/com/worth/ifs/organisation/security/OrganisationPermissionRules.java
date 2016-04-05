package com.worth.ifs.organisation.security;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.organisation.resource.OrganisationSearchResult;
import com.worth.ifs.security.PermissionRule;
import com.worth.ifs.security.PermissionRules;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.OrganisationResource;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.worth.ifs.util.CollectionFunctions.flattenLists;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;

/**
 * Permission Rules determining who can perform which actions upon an Organisation
 */
@Component
@PermissionRules
public class OrganisationPermissionRules {

    @PermissionRule(value = "READ", description = "Organisations that are not yet a part of any Applications are visible to anyone, " +
            "because this needs to be possible to create them during registration where there is not yet a logged-in user")
    public boolean anyoneCanSeeOrganisationsNotYetConnectedToApplications(OrganisationResource organisation, User user) {
        return organisationNotYetLinkedToAnApplication(organisation);
    }

    @PermissionRule(value = "READ", description = "A member of an Organisation can view their own Organisation")
    public boolean memberOfOrganisationCanViewOwnOrganisation(OrganisationResource organisation, User user) {
        return isMemberOfOrganisation(organisation, user);
    }

    @PermissionRule(value = "READ", description = "Users linked to Applications can view the basic details of the other Organisations on their own Applications")
    public boolean usersCanViewOrganisationsOnTheirOwnApplications(OrganisationResource organisation, User user) {

        // TODO DW - INFUND-1556 - this code feels pretty heavy given that all we need to do is find a link between a User and an Organisation via an Application
        List<ProcessRole> applicationRoles = user.getProcessRoles();
        List<Application> applicationsThatThisUserIsLinkedTo = simpleMap(applicationRoles, ProcessRole::getApplication);
        List<ProcessRole> processRolesForAllApplications = flattenLists(simpleMap(applicationsThatThisUserIsLinkedTo, Application::getProcessRoles));
        List<Organisation> allOrganisationsLinkedToAnyOfUsersApplications = simpleMap(processRolesForAllApplications, ProcessRole::getOrganisation);

        return simpleMap(allOrganisationsLinkedToAnyOfUsersApplications, Organisation::getId).contains(organisation.getId());
    }

    @PermissionRule(value = "CREATE", description = "Anyone should be able to create Organisations, " +
            "because this needs to be possible to create them during registration where there is not yet a logged-in user")
    public boolean anyoneCanCreateOrganisations(OrganisationResource organisation, User user) {
        return true;
    }

    @PermissionRule(value = "UPDATE", description = "Organisations that are not yet a part of any Applications are " +
            "updatable by anyone, because this needs to be possible to update them during registration where there is " +
            "not yet a logged-in user")
    public boolean anyoneCanUpdateOrganisationsNotYetConnectedToApplications(OrganisationResource organisation, User user) {
        return organisationNotYetLinkedToAnApplication(organisation);
    }

    @PermissionRule(value = "UPDATE", description = "A member of an Organisation can update their own Organisation")
    public boolean memberOfOrganisationCanUpdateOwnOrganisation(OrganisationResource organisation, User user) {
        return isMemberOfOrganisation(organisation, user);
    }

    @PermissionRule(value = "READ", description = "Anyone can search for and see all search results for Organisations")
    public boolean anyoneCanSeeOrganisationSearchResults(OrganisationSearchResult organisation, User user) {
        return true;
    }

    private boolean isMemberOfOrganisation(OrganisationResource organisation, User user) {
        return simpleMap(organisation.getUsers(), User::getId).contains(user.getId());
    }

    private boolean organisationNotYetLinkedToAnApplication(OrganisationResource organisation) {
        return organisation.getProcessRoles().isEmpty();
    }
}
