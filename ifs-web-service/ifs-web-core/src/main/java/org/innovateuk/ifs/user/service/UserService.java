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
    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    UserResource findById(Long userId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    List<UserResource> getAssignable(Long applicationId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    Boolean isLeadApplicant(Long userId, ApplicationResource application);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    boolean existsAndHasRole(Long userId, UserRoleType role);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ProcessRoleResource getLeadApplicantProcessRoleOrNull(ApplicationResource application);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    List<ProcessRoleResource> getLeadPartnerOrganisationProcessRoles(ApplicationResource applicationResource);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    Void verifyEmail(String hash);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    void resendEmailVerificationNotification(String email);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    Boolean userHasApplicationForCompetition(Long userId, Long competitionId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    UserResource retrieveUserById(Long id);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    void sendPasswordResetNotification(String email);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    Void checkPasswordResetHash(String hash);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> resetPassword(String hash, String password);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    Optional<UserResource> findUserByEmail(String email);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    Set<UserResource> getAssignableUsers(ApplicationResource application);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ServiceResult<UserResource> createUserForOrganisation(String firstName, String lastName, String password, String email, String title, String phoneNumber, Long organisationId, Boolean allowMarketingEmails);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ServiceResult<UserResource> createLeadApplicantForOrganisationWithCompetitionId(String firstName, String lastName, String password, String email, String title,
                                                                                    String phoneNumber, String gender, Long ethnicity, String disability, Long organisationId,
                                                                                    Long competitionId, Boolean allowMarketingEmails);
    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ServiceResult<UserResource> createOrganisationUser(String firstName, String lastName, String password, String email, String title, String phoneNumber, Long organisationId, Boolean allowMarketingEmails);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ServiceResult<UserResource> updateDetails(Long id, String email, String firstName, String lastName, String title, String phoneNumber, String gender, Long ethnicity, String disability, boolean allowMarketingEmails);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    List<UserResource> findUserByType(UserRoleType type);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    List<ProcessRoleResource> getOrganisationProcessRoles(ApplicationResource application, Long organisation);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    Long getUserOrganisationId(Long userId, Long applicationId);
}
