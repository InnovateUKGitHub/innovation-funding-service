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
import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
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

    private ServiceResult<Void> updateUser(UserResource userResource, ProfileResource profileResource) {
        if (userResource.getProfile() == null) {
            userResource.setProfile(profileResource);
        }
        else {
            final ProfileResource existingProfileResource = userResource.getProfile();
            existingProfileResource.setBusinessType(profileResource.getBusinessType());
            existingProfileResource.setSkillsAreas(profileResource.getSkillsAreas());
            existingProfileResource.setContract(profileResource.getContract());
        }
        return serviceSuccess(
                userRepository.save(
                        userMapper.mapToDomain(userResource)
                )
        ).andOnSuccessReturnVoid();
    }
}
