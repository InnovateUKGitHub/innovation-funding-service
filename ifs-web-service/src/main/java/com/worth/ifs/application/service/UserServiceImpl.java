package com.worth.ifs.application.service;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.model.UserApplicationRole;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class contains methods to retrieve and store {@link User} related data,
 * through the RestService {@link UserRestService}.
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRestService userRestService;

    @Override
    public List<User> getAssignable(Long applicationId) {
        return userRestService.findAssignableUsers(applicationId);
    }

    public Boolean isLeadApplicant(Long userId, Application application) {
        List<ProcessRole> userApplicationRoles = application.getProcessRoles();
        return userApplicationRoles.stream().anyMatch(uar -> uar.getRole().getName()
                .equals(UserApplicationRole.LEAD_APPLICANT.getRoleName()) && uar.getUser().getId().equals(userId));

    }

    public Set<User> getAssignableUsers(Application application) {
        List<ProcessRole> userApplicationRoles = application.getProcessRoles();
        Set<User> users = userApplicationRoles.stream()
                .filter(uar -> uar.getRole().getName().equals(UserApplicationRole.LEAD_APPLICANT.getRoleName()) || uar.getRole().getName().equals(UserApplicationRole.COLLABORATOR.getRoleName()))
                .map(uar -> uar.getUser())
                .collect(Collectors.toSet());
        return users;
    }

    public Set<User> getApplicationUsers(Application application) {
        List<ProcessRole> userApplicationRoles = application.getProcessRoles();
        Set<User> users = userApplicationRoles.stream()
                .map(uar -> uar.getUser())
                .collect(Collectors.toSet());
        return users;
    }
}
