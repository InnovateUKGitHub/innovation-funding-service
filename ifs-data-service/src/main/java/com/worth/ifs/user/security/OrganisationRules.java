package com.worth.ifs.user.security;

import com.worth.ifs.security.PermissionRule;
import com.worth.ifs.security.PermissionRules;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.OrganisationResource;

import org.springframework.stereotype.Component;

@Component
@PermissionRules
public class OrganisationRules {

    @PermissionRule(value = "READ", description = "user can read Organisation Data")
    public boolean userCanReadOrganisation(OrganisationResource organisation, User user){
        return userIsConnectedToOrganisation(organisation, user);
    }

    private boolean userIsConnectedToOrganisation(OrganisationResource organisation, User user){
        return user.getProcessRoles().stream()
                .map(ProcessRole::getOrganisation)
                .map(Organisation::getId)
                .anyMatch(id -> id.equals(organisation.getId()));
    }
}
