package com.worth.ifs.user.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.transactional.BaseTransactionalService;
import com.worth.ifs.user.domain.Affiliation;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.mapper.AffiliationMapper;
import com.worth.ifs.user.mapper.UserMapper;
import com.worth.ifs.user.resource.AffiliationResource;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.worth.ifs.commons.error.CommonErrors.badRequestError;
import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.util.EntityLookupCallbacks.find;
import static java.util.stream.Collectors.toList;

/**
 * A Service for operations regarding Users' profiles.  This implementation delegates some of this work to an Identity Provider Service
 */
@Service
public class UserProfileServiceImpl extends BaseTransactionalService implements UserProfileService {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AffiliationMapper affiliationMapper;

    public enum ServiceFailures {
        UNABLE_TO_UPDATE_USER
    }

    @Override
    public ServiceResult<Void> updateProfile(UserResource userResource) {
        if (userResource != null) {
            return userService.findByEmail(userResource.getEmail())
                    .andOnSuccess(existingUser ->
                            updateUser(existingUser, userResource));
        } else {
            return serviceFailure(badRequestError("User resource may not be null"));
        }
    }

    @Override
    public ServiceResult<List<AffiliationResource>> getAffiliationsByUserId(Long userId) {
        return find(userRepository.findOne(userId), notFoundError(User.class, userId)).andOnSuccessReturn(user -> user.getAffiliations().stream().map(affiliation -> affiliationMapper.mapToResource(affiliation)).collect(toList()));
    }

    @Override
    public ServiceResult<Void> updateUserAffilliations(long userId, List<AffiliationResource> affiliations) {
        return find(userRepository.findOne(userId), notFoundError(User.class, userId)).andOnSuccess(user -> {
            List<Affiliation> targetAffiliations = user.getAffiliations();
            targetAffiliations.clear();
            affiliationMapper.mapToDomain(affiliations).forEach(targetAffiliations::add);
            userRepository.save(user);
            return serviceSuccess();
        });
    }

    private ServiceResult<Void> updateUser(UserResource existingUserResource, UserResource updatedUserResource) {
        existingUserResource.setPhoneNumber(updatedUserResource.getPhoneNumber());
        existingUserResource.setTitle(updatedUserResource.getTitle());
        existingUserResource.setLastName(updatedUserResource.getLastName());
        existingUserResource.setFirstName(updatedUserResource.getFirstName());
        User existingUser = userMapper.mapToDomain(existingUserResource);
        return serviceSuccess(userRepository.save(existingUser)).andOnSuccessReturnVoid();
    }
}
