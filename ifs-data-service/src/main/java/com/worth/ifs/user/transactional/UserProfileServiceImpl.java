package com.worth.ifs.user.transactional;

import com.worth.ifs.commons.service.ServiceFailure;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.transactional.BaseTransactionalService;
import com.worth.ifs.user.domain.Profile;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.mapper.UserMapper;
import com.worth.ifs.user.resource.ProfileResource;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.worth.ifs.commons.error.CommonErrors.badRequestError;
import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.util.EntityLookupCallbacks.find;
/**
 * A Service for operations regarding Users' profiles.  This implementation delegates some of this work to an Identity Provider Service
 */
@Service
public class UserProfileServiceImpl extends BaseTransactionalService implements UserProfileService {

    @Autowired
    UserService userService;

    @Autowired
    UserMapper userMapper;

    public enum ServiceFailures {
        UNABLE_TO_UPDATE_USER
    }

    @Override
    public ServiceResult<Void> updateProfile(Long userId, ProfileResource profileResource) {
        return find(userRepository.findOne(userId), notFoundError(User.class, userId))
                .andOnSuccess(user -> updateUserProfile(user, profileResource));
    }

    private ServiceResult<Void> updateUserProfile(User user, ProfileResource profileResource) {
        if (user.getProfile() == null) {
            user.setProfile(new Profile(user));
        }

        final Profile profile = user.getProfile();

        profile.setBusinessType(profileResource.getBusinessType());
        profile.setSkillsAreas(profileResource.getSkillsAreas());

        userRepository.save(user);

        return serviceSuccess();
    }

    @Override
    public ServiceResult<Void> updateDetails(UserResource userResource) {
        if(userResource!=null) {
            return userService.findByEmail(userResource.getEmail())
                    .andOnSuccess(existingUser ->
                            updateUser(existingUser, userResource));
        } else {
            return serviceFailure(badRequestError("User resource may not be null"));
        }
    }

    private ServiceResult<Void> updateUser(UserResource existingUserResource, UserResource updatedUserResource){
        existingUserResource.setPhoneNumber(updatedUserResource.getPhoneNumber());
        existingUserResource.setTitle(updatedUserResource.getTitle());
        existingUserResource.setLastName(updatedUserResource.getLastName());
        existingUserResource.setFirstName(updatedUserResource.getFirstName());
        existingUserResource.setProfile(updatedUserResource.getProfile());
        User existingUser = userMapper.mapToDomain(existingUserResource);
        return serviceSuccess(userRepository.save(existingUser)).andOnSuccessReturnVoid();
    }
}
