package com.worth.ifs.authentication.service;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.security.NotSecured;

/**
 * Represents an endpoint attached to the (external) Authentication service
 */
public interface IdentityProviderService {

    /**
     * Creates a user record in the Identity Provider's database and returns a unique id for that user record
     *
     * @param emailAddress
     * @param password
     * @return
     */
    @NotSecured(value = "TODO - implement when permissions matrix defined", mustBeSecuredByOtherServices = false)
    ServiceResult<String> createUserRecordWithUid(String emailAddress, String password);

    /**
     * Update a user record in the Identity Provider's database and returns the User's unique id
     *
     * @param uid
     * @param password
     * @return
     */
    @NotSecured(value = "TODO - implement when permissions matrix defined", mustBeSecuredByOtherServices = false)
    ServiceResult<String> updateUserPassword(String uid, String password);

    /**
     * Activate a user in the Identity Provider
     *
     * @param uid
     * @return
     */
    @NotSecured(value = "TODO - implement when permissions matrix defined", mustBeSecuredByOtherServices = false)
    ServiceResult<String> activateUser(String uid);
}
