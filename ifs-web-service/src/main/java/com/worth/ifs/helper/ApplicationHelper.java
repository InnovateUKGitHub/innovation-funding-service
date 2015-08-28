package com.worth.ifs.helper;

import com.worth.ifs.domain.Application;
import com.worth.ifs.domain.Organisation;
import com.worth.ifs.domain.User;
import com.worth.ifs.domain.UserApplicationRole;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ApplicationHelper {
    private static final Log log = LogFactory.getLog(ApplicationHelper.class);

    public static Set<Organisation> getApplicationOrganisations(Application application){
        List<UserApplicationRole> userApplicationRoles = application.getUserApplicationRoles();
        Set<Organisation> organisations = userApplicationRoles.stream()
                .map(uar -> uar.getOrganisation())
                .collect(Collectors.toSet());

        return organisations;
    }
    public static Optional<Organisation> getApplicationLeadOrganisation(Application application){
        List<UserApplicationRole> userApplicationRoles = application.getUserApplicationRoles();

        Optional<Organisation> leadOrganisation = userApplicationRoles.stream()
                .filter(uar -> uar.getRole().getName().equals("leadapplicant"))
                .map(uar -> uar.getOrganisation())
                .findFirst();

        return leadOrganisation;
    }
    public static Set<User> getAssignableUsers(Application application){
        List<UserApplicationRole> userApplicationRoles = application.getUserApplicationRoles();
        Set<User> users = userApplicationRoles.stream()
                .filter(uar -> uar.getRole().getName().equals("leadapplicant") || uar.getRole().getName().equals("collaborator"))
                .map(uar -> uar.getUser())
                .collect(Collectors.toSet());
        log.info("Assignable users: "+ users.size());
        return users;
    }
}
