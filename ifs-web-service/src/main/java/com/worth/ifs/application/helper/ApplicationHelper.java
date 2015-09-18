package com.worth.ifs.application.helper;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ApplicationHelper {
    private final Log log = LogFactory.getLog(ApplicationHelper.class);

    public TreeSet<Organisation> getApplicationOrganisations(Application application){
        List<ProcessRole> userApplicationRoles = application.getProcessRoles();
        Comparator<Organisation> compareById =
                Comparator.comparingLong(Organisation::getId);
        Supplier<TreeSet<Organisation>> supplier = () -> new TreeSet<Organisation>(compareById);
        TreeSet<Organisation> organisations = userApplicationRoles.stream()
                .map(uar -> uar.getOrganisation())
                .collect(Collectors.toCollection(supplier));

        return organisations;
    }

    public Optional<Organisation> getApplicationLeadOrganisation(Application application){
        List<ProcessRole> userApplicationRoles = application.getProcessRoles();

        Optional<Organisation> leadOrganisation = userApplicationRoles.stream()
                .filter(uar -> uar.getRole().getName().equals(UserApplicationRole.LEAD_APPLICANT.getRoleName()))
                .map(uar -> uar.getOrganisation())
                .findFirst();

        return leadOrganisation;
    }

    public Optional<Organisation> getUserOrganisation(Application application, Long userId){
        List<ProcessRole> userApplicationRoles = application.getProcessRoles();

        Optional<Organisation> userOrganisation = userApplicationRoles.stream()
                .filter(uar -> uar.getUser().getId().equals(userId))
                .map(uar -> uar.getOrganisation())
                .findFirst();

        return userOrganisation;
    }

    public Boolean isLeadApplicant(Long userId, Application application) {
        List<ProcessRole> userApplicationRoles = application.getProcessRoles();
        return userApplicationRoles.stream().anyMatch(uar -> uar.getRole().getName()
                .equals(UserApplicationRole.LEAD_APPLICANT.getRoleName()) && uar.getUser().getId().equals(userId));

    }


    public Set<User> getAssignableUsers(Application application){
        List<ProcessRole> userApplicationRoles = application.getProcessRoles();
        Set<User> users = userApplicationRoles.stream()
                .filter(uar -> uar.getRole().getName().equals(UserApplicationRole.LEAD_APPLICANT.getRoleName()) || uar.getRole().getName().equals(UserApplicationRole.COLLABORATOR.getRoleName()))
                .map(uar -> uar.getUser())
                .collect(Collectors.toSet());
        log.info("Assignable users: "+ users.size());
        return users;
    }
    public Set<User> getApplicationUsers(Application application){
        List<ProcessRole> userApplicationRoles = application.getProcessRoles();
        Set<User> users = userApplicationRoles.stream()
                .map(uar -> uar.getUser())
                .collect(Collectors.toSet());
        log.info("Application users: "+ users.size());
        return users;
    }
}
