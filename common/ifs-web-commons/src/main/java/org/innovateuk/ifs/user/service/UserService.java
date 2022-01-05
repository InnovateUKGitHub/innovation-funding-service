package org.innovateuk.ifs.user.service;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;

import java.util.List;
import java.util.Optional;

/**
 * Interface for CRUD operations on {@link UserResource} related data.
 */
public interface UserService {

    Boolean isLeadApplicant(Long userId, ApplicationResource application);

    boolean existsAndHasRole(Long userId, Role role);

    ProcessRoleResource getLeadApplicantProcessRole(Long applicationId);

    List<ProcessRoleResource> getLeadPartnerOrganisationProcessRoles(ApplicationResource applicationResource);

    void resendEmailVerificationNotification(String email);

    Boolean userHasApplicationForCompetition(Long userId, Long competitionId);

    void sendPasswordResetNotification(String email);

    Optional<UserResource> findUserByEmail(String email);

    ServiceResult<UserResource> updateDetails(Long id, String email, String firstName, String lastName, String title, String phoneNumber, boolean allowMarketingEmails);

    List<ProcessRoleResource> getOrganisationProcessRoles(ApplicationResource application, Long organisation);

    Long getUserOrganisationId(Long userId, Long applicationId);
}
