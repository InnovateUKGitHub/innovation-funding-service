package org.innovateuk.ifs.user.service;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.resource.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Interface for CRUD operations on {@link UserResource} related data.
 */
public interface UserService {
    UserResource findById(Long userId);
    List<UserResource> getAssignable(Long applicationId);
    Boolean isLeadApplicant(Long userId, ApplicationResource application);
    ProcessRoleResource getLeadApplicantProcessRoleOrNull(ApplicationResource application);
    List<ProcessRoleResource> getLeadPartnerOrganisationProcessRoles(ApplicationResource applicationResource);
    Void verifyEmail(String hash);
    void resendEmailVerificationNotification(String email);
    Boolean userHasApplicationForCompetition(Long userId, Long competitionId);
    UserResource retrieveUserById(Long id);
    void sendPasswordResetNotification(String email);
    Void checkPasswordResetHash(String hash);
    ServiceResult<Void> resetPassword(String hash, String password);
    Optional<UserResource> findUserByEmail(String email);
    Set<UserResource> getAssignableUsers(ApplicationResource application);
    ServiceResult<UserResource> createUserForOrganisation(String firstName, String lastName, String password, String email, String title, String phoneNumber, Long organisationId);
    ServiceResult<UserResource> createLeadApplicantForOrganisationWithCompetitionId(String firstName, String lastName, String password, String email, String title,
                                                                                    String phoneNumber, String gender, Long ethnicity, String disability, Long organisationId, Long competitionId);
    ServiceResult<UserResource> createOrganisationUser(String firstName, String lastName, String password, String email, String title, String phoneNumber, Long organisationId);
    ServiceResult<UserResource> updateDetails(Long id, String email, String firstName, String lastName, String title, String phoneNumber, String gender, Long ethnicity, String disability);
    ProfileSkillsResource getProfileSkills(Long userId);
    ServiceResult<Void> updateProfileSkills(Long userId, BusinessType businessType, String skillsAreas);
    ProfileAgreementResource getProfileAgreement(Long userId);
    ServiceResult<Void> updateProfileAgreement(Long userId);
    List<AffiliationResource> getUserAffiliations(Long userId);
    ServiceResult<Void> updateUserAffiliations(Long userId, List<AffiliationResource> affiliations);
    List<UserResource> findUserByType(UserRoleType type);
	List<ProcessRoleResource> getOrganisationProcessRoles(ApplicationResource application, Long organisation);
    UserProfileResource getUserProfile(Long userId);
    ServiceResult<Void> updateUserProfile(Long userId, UserProfileResource userProfile);
    Long getUserOrganisationId(Long userId, Long applicationId);
}
