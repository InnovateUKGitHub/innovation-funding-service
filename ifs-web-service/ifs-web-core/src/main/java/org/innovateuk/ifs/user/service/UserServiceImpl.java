package org.innovateuk.ifs.user.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.String.format;


/**
 * This class contains methods to retrieve and store {@link UserResource} related data,
 * through the RestService {@link UserRestService}.
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Log LOG = LogFactory.getLog(UserServiceImpl.class);
    @Autowired
    private UserRestService userRestService;

    @Override
    public Boolean isLeadApplicant(Long userId, ApplicationResource application) {
        List<ProcessRoleResource> userApplicationRoles = userRestService.findProcessRole(application.getId()).getSuccess();
        return userApplicationRoles.stream().anyMatch(uar -> uar.getRoleName()
                .equals(Role.LEADAPPLICANT.getName()) && uar.getUser().equals(userId));

    }

    @Override
    public ProcessRoleResource getLeadApplicantProcessRoleOrNull(Long applicationId) {
        List<ProcessRoleResource> userApplicationRoles = userRestService.findProcessRole(applicationId).getSuccess();
        for (final ProcessRoleResource processRole : userApplicationRoles) {
            if (processRole.getRoleName().equals(Role.LEADAPPLICANT.getName())) {
                return processRole;
            }
        }
        return null;
    }

    @Override
    public List<ProcessRoleResource> getOrganisationProcessRoles(ApplicationResource application, Long organisation) {
        List<ProcessRoleResource> userApplicationRoles = userRestService.findProcessRole(application.getId()).getSuccess();
        return userApplicationRoles.stream()
                .filter(prr -> organisation.equals(prr.getOrganisationId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProcessRoleResource> getLeadPartnerOrganisationProcessRoles(ApplicationResource application) {
        ProcessRoleResource leadProcessRole = getLeadApplicantProcessRoleOrNull(application.getId());
        if (leadProcessRole == null) {
            return new ArrayList<>();
        }
        return userRestService.findProcessRole(application.getId()).getSuccess().stream()
                .filter(pr -> leadProcessRole.getOrganisationId().equals(pr.getOrganisationId()))
                .collect(Collectors.toList());
    }

    @Override
    public ServiceResult<UserResource> createUserForOrganisation(String firstName, String lastName, String password, String email, String title, String phoneNumber, Long organisationId, Boolean allowMarketingEmails) {
        return userRestService.createLeadApplicantForOrganisation(firstName, lastName, password, email, title, phoneNumber, null, null, null, organisationId, allowMarketingEmails).toServiceResult();
    }

    @Override
    public ServiceResult<UserResource> createLeadApplicantForOrganisationWithCompetitionId(String firstName, String lastName, String password, String email,
                                                                                           String title, String phoneNumber,
                                                                                           String gender, Long ethnicity, String disability,
                                                                                           Long organisationId, Long competitionId, Boolean allowMarketingEmails) {
        return userRestService.createLeadApplicantForOrganisationWithCompetitionId(firstName, lastName, password, email, title, phoneNumber, gender, ethnicity, disability, organisationId, competitionId, allowMarketingEmails).toServiceResult();
    }

    @Override
    public ServiceResult<UserResource> createOrganisationUser(String firstName, String lastName, String password, String email, String title, String phoneNumber, Long organisationId, Boolean allowMarketingEmails) {
        return createUserForOrganisation(firstName, lastName, password, email, title, phoneNumber, organisationId, allowMarketingEmails);
    }

    @Override
    public ServiceResult<UserResource> updateDetails(Long id, String email, String firstName, String lastName, String title, String phoneNumber, String gender, Long ethnicity, String disability, boolean allowMarketingEmails) {
        return userRestService.updateDetails(id, email, firstName, lastName, title, phoneNumber, gender, ethnicity, disability, allowMarketingEmails).toServiceResult();
    }

    @Override
    public Long getUserOrganisationId(Long userId, Long applicationId) {
        ProcessRoleResource userApplicationRole = userRestService.findProcessRole(userId, applicationId).getSuccess();
        return userApplicationRole.getOrganisationId();
    }

    @Override
    public void resendEmailVerificationNotification(String email) {
        try {
            userRestService.resendEmailVerificationNotification(email).getSuccess();
        } catch (ObjectNotFoundException e) {
            // Do nothing. We don't want to reveal that the address was not recognised
            LOG.debug(format("Purposely ignoring ObjectNotFoundException for email address: [%s] when resending email verification notification.", email), e);
        }
    }

    @Override
    public Boolean userHasApplicationForCompetition(Long userId, Long competitionId) {
        return userRestService.userHasApplicationForCompetition(userId, competitionId).getSuccess();
    }

    @Override
    public void sendPasswordResetNotification(String email) {
        userRestService.sendPasswordResetNotification(email);
    }

    @Override
    public Optional<UserResource> findUserByEmail(String email) {
        return userRestService.findUserByEmail(email).getOptionalSuccessObject();
    }

    @Override
    public boolean existsAndHasRole(Long userId, Role role) {
        RestResult<UserResource> result = userRestService.retrieveUserById(userId);

        if (result.isFailure()) {
            return false;
        }

        UserResource execUser = result.getSuccess();

        return execUser != null && execUser.hasRole(role);
    }
}
