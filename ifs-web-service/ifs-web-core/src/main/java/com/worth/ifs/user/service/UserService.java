package com.worth.ifs.user.service;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.user.resource.*;

import java.util.List;
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
    RestResult<Void> verifyEmail(String hash);
    void resendEmailVerificationNotification(String email);

    RestResult<UserResource> retrieveUserById(Long id);

    RestResult<Void> sendPasswordResetNotification(String email);
    RestResult<Void> checkPasswordResetHash(String hash);
    RestResult<Void> resetPassword(String hash, String password);
    RestResult<UserResource> findUserByEmail(String email);
    RestResult<UserResource> findUserByEmailForAnonymousUserFlow(String email);
    Set<UserResource> getAssignableUsers(ApplicationResource application);
    RestResult<UserResource> createLeadApplicantForOrganisation(String firstName, String lastName, String password, String email, String title, String phoneNumber, Long organisationId);
    RestResult<UserResource> createLeadApplicantForOrganisationWithCompetitionId(String firstName, String lastName, String password, String email, String title, String phoneNumber, Long organisationId, Long competitionId);
    RestResult<UserResource> createOrganisationUser(String firstName, String lastName, String password, String email, String title, String phoneNumber, Long organisationId);
    RestResult<UserResource> updateDetails(Long id, String email, String firstName, String lastName, String title, String phoneNumber);
    ProfileSkillsResource getProfileSkills(Long userId);
    ServiceResult<Void> updateProfileSkills(Long userId, BusinessType businessType, String skillsAreas);
    List<AffiliationResource> getUserAffiliations(Long userId);
    ServiceResult<Void> updateUserContract(Long userId, ProfileResource profileResource);
    ServiceResult<Void> updateUserAffiliations(Long userId, List<AffiliationResource> affiliations);
    List<UserResource> findUserByType(UserRoleType type);
	List<ProcessRoleResource> getOrganisationProcessRoles(ApplicationResource application, Long organisation);
}
