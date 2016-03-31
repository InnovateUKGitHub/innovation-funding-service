package com.worth.ifs.organisation.security;

import com.worth.ifs.security.PermissionRule;
import com.worth.ifs.security.PermissionRules;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.OrganisationResource;
import org.springframework.stereotype.Component;

import static com.worth.ifs.util.CollectionFunctions.simpleMap;

/**
 * Permission Rules determining who can perform which actions upon an Organisation
 */
@Component
@PermissionRules
public class OrganisationRules {

    @PermissionRule(value = "READ", description = "A member of an Organisation can view their own Organisation")
    public boolean memberOfOrganisationCanViewOwnOrganisation(OrganisationResource organisation, User user) {
        return simpleMap(organisation.getUsers(), User::getId).contains(user.getId());
    }
}
