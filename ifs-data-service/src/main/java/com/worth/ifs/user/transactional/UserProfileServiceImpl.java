package com.worth.ifs.user.transactional;

import com.worth.ifs.authentication.service.IdentityProviderService;
import com.worth.ifs.transactional.BaseTransactionalService;
import com.worth.ifs.transactional.ServiceResult;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.repository.UserRepository;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.worth.ifs.transactional.BaseTransactionalService.Failures.USER_NOT_FOUND;
import static com.worth.ifs.transactional.ServiceResult.success;
import static com.worth.ifs.util.EntityLookupCallbacks.getOrFail;
import static com.worth.ifs.util.EntityLookupCallbacks.onlyElement;

/**
 * A Service for operations regarding Users' profiles.  This implementation delegates some of this work to an Identity Provider Service
 */
@Service
public class UserProfileServiceImpl extends BaseTransactionalService implements UserProfileService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IdentityProviderService identityProviderService;

    @Override
    public ServiceResult<User> updateProfile(UserResource userResource) {
        return getUserByEmailAddress(userResource).map(existingUser -> updateUser(existingUser, userResource));
    }

    private ServiceResult<User> updateUser(User existingUser, UserResource updatedUserResource){

        ServiceResult<String> updateIdpResult = identityProviderService.updateUserDetails(existingUser.getUid(), updatedUserResource.getTitle(), updatedUserResource.getFirstName(), updatedUserResource.getLastName(), updatedUserResource.getEmail(), updatedUserResource.getPhoneNumber());

        return updateIdpResult.map(successfulUpdate -> {

            // TODO DW - INFUND-1267 - these fields probably to be removed from IFS
            existingUser.setPhoneNumber(updatedUserResource.getPhoneNumber());
            existingUser.setTitle(updatedUserResource.getTitle());
            existingUser.setLastName(updatedUserResource.getLastName());
            existingUser.setFirstName(updatedUserResource.getFirstName());

            return success(userRepository.save(existingUser));
        });

    }


    private ServiceResult<User> getUserByEmailAddress(UserResource userResource) {
        return getOrFail(() -> userRepository.findByEmail(userResource.getEmail()), USER_NOT_FOUND).map(users -> onlyElement(users, USER_NOT_FOUND));
    }
}
