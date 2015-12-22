package com.worth.ifs.application.service;

import com.worth.ifs.application.model.UserApplicationRole;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.commons.resource.ResourceEnvelope;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.worth.ifs.util.CollectionFunctions.simpleMap;

/**
 * This class contains methods to retrieve and store {@link User} related data,
 * through the RestService {@link UserRestService}.
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRestService userRestService;

    @Autowired
    ProcessRoleService processRoleService;

    @Override
    public List<User> getAssignable(Long applicationId) {
        return userRestService.findAssignableUsers(applicationId);
    }

    public Boolean isLeadApplicant(Long userId, ApplicationResource application) {
        List<ProcessRole> userApplicationRoles = simpleMap(application.getProcessRoleIds(),id -> processRoleService.getById(id));
        return userApplicationRoles.stream().anyMatch(uar -> uar.getRole().getName()
                .equals(UserApplicationRole.LEAD_APPLICANT.getRoleName()) && uar.getUser().getId().equals(userId));

    }

    public ProcessRole getLeadApplicantProcessRoleOrNull(ApplicationResource application) {
        List<ProcessRole> userApplicationRoles = simpleMap(application.getProcessRoleIds(),id -> processRoleService.getById(id));
        for(final ProcessRole processRole : userApplicationRoles){
            if(processRole.getRole().getName().equals(UserApplicationRole.LEAD_APPLICANT.getRoleName())){
                return processRole;
            }
        }
        return null;
    }

    public Set<User> getAssignableUsers(ApplicationResource application) {
        List<ProcessRole> userApplicationRoles = application.getProcessRoleIds().stream()
            .map(id -> processRoleService.getById(id))
            .collect(Collectors.toList());
        return userApplicationRoles.stream()
                .filter(uar -> uar.getRole().getName().equals(UserApplicationRole.LEAD_APPLICANT.getRoleName()) || uar.getRole().getName().equals(UserApplicationRole.COLLABORATOR.getRoleName()))
                .map(ProcessRole::getUser)
                .collect(Collectors.toSet());
    }

    public Set<User> getApplicationUsers(ApplicationResource application) {
        List<ProcessRole> userApplicationRoles = application.getProcessRoleIds().stream()
            .map(id -> processRoleService.getById(id))
            .collect(Collectors.toList());
        return userApplicationRoles.stream()
                .map(ProcessRole::getUser)
                .collect(Collectors.toSet());
    }

    public ResourceEnvelope<UserResource> createLeadApplicantForOrganisation(String firstName, String lastName, String password, String email, String title, String phoneNumber, Long organisationId) {
        ResourceEnvelope<UserResource> userResourceResourceStatusEnvelope = userRestService.createLeadApplicantForOrganisation(firstName, lastName, password, email, title, phoneNumber, organisationId);
        return userResourceResourceStatusEnvelope;
    }

    public List<UserResource> findUserByEmail(String email) {
        List<UserResource> users = userRestService.findUserByEmail(email);

        return users;
    }
}
