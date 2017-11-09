package org.innovateuk.ifs.user.service;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Interface for CRUD operations on {@link UserResource} related data.
 */
public interface UserService {
    @NotSecured("Not currently secured")
    UserResource findById(Long userId);

    @NotSecured("Not currently secured")
    List<UserResource> getAssignable(Long applicationId);

    @NotSecured("Not currently secured")
    Boolean isLeadApplicant(Long userId, ApplicationResource application);

    @NotSecured("Not currently secured")
    boolean existsAndHasRole(Long userId, UserRoleType role);

    @NotSecured("Not currently secured")
    ProcessRoleResource getLeadApplicantProcessRoleOrNull(ApplicationResource application);

    @NotSecured("Not currently secured")
    List<ProcessRoleResource> getLeadPartnerOrganisationProcessRoles(ApplicationResource applicationResource);

    @NotSecured("Not currently secured")
    Void verifyEmail(String hash);

    @NotSecured("Not currently secured")
    void resendEmailVerificationNotification(String email);

    @NotSecured("Not currently secured")
    Boolean userHasApplicationForCompetition(Long userId, Long competitionId);

    @NotSecured("Not currently secured")
    UserResource retrieveUserById(Long id);

    @NotSecured("Not currently secured")
    void sendPasswordResetNotification(String email);

    @NotSecured("Not currently secured")
    Void checkPasswordResetHash(String hash);

    @NotSecured("Not currently secured")
    ServiceResult<Void> resetPassword(String hash, String password);

    @NotSecured("Not currently secured")
    Optional<UserResource> findUserByEmail(String email);

    @NotSecured("Not currently secured")
    Set<UserResource> getAssignableUsers(ApplicationResource application);

    @NotSecured("Not currently secured")
    ServiceResult<UserResource> createUserForOrganisation(String firstName, String lastName, String password, String email, String title, String phoneNumber, Long organisationId, Boolean allowMarketingEmails);

    @NotSecured("Not currently secured")
    ServiceResult<UserResource> createLeadApplicantForOrganisationWithCompetitionId(String firstName, String lastName, String password, String email, String title,
                                                                                    String phoneNumber, String gender, Long ethnicity, String disability, Long organisationId,
                                                                                    Long competitionId, Boolean allowMarketingEmails);
    @NotSecured("Not currently secured")
    ServiceResult<UserResource> createOrganisationUser(String firstName, String lastName, String password, String email, String title, String phoneNumber, Long organisationId, Boolean allowMarketingEmails);

    @NotSecured("Not currently secured")
    ServiceResult<UserResource> updateDetails(Long id, String email, String firstName, String lastName, String title, String phoneNumber, String gender, Long ethnicity, String disability, boolean allowMarketingEmails);

    @NotSecured("Not currently secured")
    List<UserResource> findUserByType(UserRoleType type);

    @NotSecured("Not currently secured")
    List<ProcessRoleResource> getOrganisationProcessRoles(ApplicationResource application, Long organisation);

    @NotSecured("Not currently secured")
    Long getUserOrganisationId(Long userId, Long applicationId);
}
