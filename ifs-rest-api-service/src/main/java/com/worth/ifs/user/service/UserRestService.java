package com.worth.ifs.user.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.*;

import java.util.List;
import java.util.concurrent.Future;

/**
 * Interface for CRUD operations on {@link User} related data.
 */
public interface UserRestService {
    RestResult<UserResource> retrieveUserResourceByUid(String uid);

    RestResult<UserResource> findUserByEmailForAnonymousUserFlow(String email);

    RestResult<UserResource> retrieveUserById(Long id);
    RestResult<List<UserResource>> findAll();

    RestResult<List<UserResource>> findByUserRoleType(UserRoleType userRoleType);

    RestResult<ProcessRoleResource> findProcessRole(Long userId, Long applicationId);
    RestResult<List<ProcessRoleResource>> findProcessRole(Long applicationId);
    RestResult<List<UserResource>> findAssignableUsers(Long applicationId);
    RestResult<UserResource> findUserByEmail(String email);
    Future<RestResult<ProcessRoleResource[]>> findAssignableProcessRoles(Long applicationId);
    RestResult<List<UserResource>> findRelatedUsers(Long applicationId);

    Future<RestResult<ProcessRoleResource>> findProcessRoleById(Long processRoleId);
    RestResult<Void> verifyEmail(String hash);
    RestResult<Void> resendEmailVerificationNotification(String email);
    RestResult<Void> sendPasswordResetNotification(String email);
    RestResult<Void> checkPasswordResetHash(String hash);
    RestResult<Void> resetPassword(String hash, String password);

    RestResult<UserResource> createLeadApplicantForOrganisationWithCompetitionId(String firstName, String lastName, String password, String email, String title, String phoneNumber, Long organisationId, Long competitionId);
    RestResult<UserResource> createLeadApplicantForOrganisation(String firstName, String lastName, String password, String email, String title, String phoneNumber, Long organisationId);
    RestResult<UserResource> updateDetails(Long id, String email, String firstName, String lastName, String title, String phoneNumber);
    RestResult<ProfileSkillsResource> getProfileSkills(Long userId);
    RestResult<Void> updateProfileSkills(Long userId, ProfileSkillsResource profileSkills);
    RestResult<ProfileContractResource> getProfileContract(Long userId);
    RestResult<Void> updateProfileContract(Long userId);
    RestResult<List<AffiliationResource>> getUserAffiliations(Long userId);
    RestResult<Void> updateUserAffiliations(Long userId, List<AffiliationResource> affiliations);

}
