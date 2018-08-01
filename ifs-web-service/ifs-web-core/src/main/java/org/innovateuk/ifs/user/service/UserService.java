package org.innovateuk.ifs.user.service;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Interface for CRUD operations on {@link UserResource} related data.
 */
public interface UserService {

    Boolean isLeadApplicant(Long userId, ApplicationResource application);

    boolean existsAndHasRole(Long userId, Role role);

    ProcessRoleResource getLeadApplicantProcessRoleOrNull(Long applicationId);

    List<ProcessRoleResource> getLeadPartnerOrganisationProcessRoles(ApplicationResource applicationResource);

    void resendEmailVerificationNotification(String email);

    Boolean userHasApplicationForCompetition(Long userId, Long competitionId);

    void sendPasswordResetNotification(String email);

    Optional<UserResource> findUserByEmail(String email);

    ServiceResult<UserResource> createUserForOrganisation(String firstName, String lastName, String password, String email, String title, String phoneNumber, Long organisationId, Boolean allowMarketingEmails);

    ServiceResult<UserResource> createLeadApplicantForOrganisationWithCompetitionId(String firstName, String lastName, String password, String email, String title,
                                                                                    String phoneNumber, String gender, Long ethnicity, String disability, Long organisationId,
                                                                                    Long competitionId, Boolean allowMarketingEmails);

    ServiceResult<UserResource> createOrganisationUser(String firstName, String lastName, String password, String email, String title, String phoneNumber, Long organisationId, Boolean allowMarketingEmails);

    ServiceResult<UserResource> updateDetails(Long id, String email, String firstName, String lastName, String title, String phoneNumber, String gender, Long ethnicity, String disability, boolean allowMarketingEmails);

    List<ProcessRoleResource> getOrganisationProcessRoles(ApplicationResource application, Long organisation);

    Long getUserOrganisationId(Long userId, Long applicationId);
}
