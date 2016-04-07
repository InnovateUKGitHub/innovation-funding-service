package com.worth.ifs.user.service;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.service.Futures;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.application.UserApplicationRole;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.worth.ifs.application.service.Futures.call;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;

/**
 * This class contains methods to retrieve and store {@link User} related data,
 * through the RestService {@link UserRestService}.
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRestService userRestService;

    @Autowired
    private ProcessRoleService processRoleService;

    @Override
    // TODO DW - INFUND-1555 - get service to return RestResult
    public List<UserResource> getAssignable(Long applicationId) {
        return userRestService.findAssignableUsers(applicationId).getSuccessObjectOrThrowException();
    }

    @Override
    public Boolean isLeadApplicant(Long userId, ApplicationResource application) {
        List<ProcessRole> userApplicationRoles = call(simpleMap(application.getProcessRoles(), id -> processRoleService.getById(id)));
        return userApplicationRoles.stream().anyMatch(uar -> uar.getRole().getName()
                .equals(UserApplicationRole.LEAD_APPLICANT.getRoleName()) && uar.getUser().getId().equals(userId));

    }

    @Override
    public ProcessRole getLeadApplicantProcessRoleOrNull(ApplicationResource application) {
        List<ProcessRole> userApplicationRoles = call(simpleMap(application.getProcessRoles(), id -> processRoleService.getById(id)));
        for(final ProcessRole processRole : userApplicationRoles){
            if(processRole.getRole().getName().equals(UserApplicationRole.LEAD_APPLICANT.getRoleName())){
                return processRole;
            }
        }
        return null;
    }

    @Override
    public Set<User> getAssignableUsers(ApplicationResource application) {
        List<ProcessRole> userApplicationRoles = Futures.call(application.getProcessRoles().stream()
                .map(id -> processRoleService.getById(id))
                .collect(Collectors.toList()));
        return userApplicationRoles.stream()
                .filter(uar -> uar.getRole().getName().equals(UserApplicationRole.LEAD_APPLICANT.getRoleName()) || uar.getRole().getName().equals(UserApplicationRole.COLLABORATOR.getRoleName()))
                .map(ProcessRole::getUser)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<User> getApplicationUsers(ApplicationResource application) {
        List<ProcessRole> userApplicationRoles = Futures.call(application.getProcessRoles().stream()
            .map(id -> processRoleService.getById(id))
            .collect(Collectors.toList())); 
        return userApplicationRoles.stream()
                .map(ProcessRole::getUser)
                .collect(Collectors.toSet());
    }

    @Override
    public RestResult<UserResource> createLeadApplicantForOrganisation(String firstName, String lastName, String password, String email, String title, String phoneNumber, Long organisationId) {
        return userRestService.createLeadApplicantForOrganisation(firstName, lastName, password, email, title, phoneNumber, organisationId);
    }
    @Override
    public RestResult<UserResource> createLeadApplicantForOrganisationWithCompetitionId(String firstName, String lastName, String password, String email, String title, String phoneNumber, Long organisationId, Long competitionId) {
        return userRestService.createLeadApplicantForOrganisationWithCompetitionId(firstName, lastName, password, email, title, phoneNumber, organisationId, competitionId);
    }

    @Override
    public RestResult<UserResource> updateDetails(String email, String firstName, String lastName, String title, String phoneNumber) {
        return userRestService.updateDetails(email, firstName, lastName, title, phoneNumber);
    }

    @Override
    public RestResult<Void> verifyEmail(String hash) {
        return userRestService.verifyEmail(hash);
    }

    @Override
    public RestResult<Void> sendPasswordResetNotification(String email) {
        return userRestService.sendPasswordResetNotification(email);
    }

    @Override
    public RestResult<Void> checkPasswordResetHash(String hash) {
        return userRestService.checkPasswordResetHash(hash);
    }

    @Override
    public RestResult<Void> resetPassword(String hash, String password) {
        return userRestService.resetPassword(hash,password);
    }

    @Override
    public RestResult<UserResource> findUserByEmail(String email) {
        return userRestService.findUserByEmail(email);
    }
}
