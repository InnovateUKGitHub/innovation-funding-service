package com.worth.ifs.user.transactional;

import com.worth.ifs.address.mapper.AddressMapper;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.transactional.BaseTransactionalService;
import com.worth.ifs.user.domain.Affiliation;
import com.worth.ifs.user.domain.Profile;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.mapper.AffiliationMapper;
import com.worth.ifs.user.mapper.UserMapper;
import com.worth.ifs.user.resource.AffiliationResource;
import com.worth.ifs.user.resource.ProfileAddressResource;
import com.worth.ifs.user.resource.ProfileSkillsResource;
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

    @Autowired
    private AddressMapper addressMapper;

    public enum ServiceFailures {
        UNABLE_TO_UPDATE_USER
    }

    @Override
    public ServiceResult<ProfileSkillsResource> getProfileSkills(Long userId) {
        return find(userRepository.findOne(userId), notFoundError(User.class, userId))
                .andOnSuccess(user -> {
                    ProfileSkillsResource profileSkills = new ProfileSkillsResource();
                    profileSkills.setUser(user.getId());
                    if (user.getProfile() != null) {
                        profileSkills.setBusinessType(user.getProfile().getBusinessType());
                        profileSkills.setSkillsAreas(user.getProfile().getSkillsAreas());
                    }
                    return serviceSuccess(profileSkills);
                });
    }

    @Override
    public ServiceResult<Void> updateProfileSkills(Long userId, ProfileSkillsResource profileSkills) {
        return find(userRepository.findOne(userId), notFoundError(User.class, userId))
                .andOnSuccess(user -> updateUserProfileSkills(user, profileSkills));
    }

    private ServiceResult<Void> updateUserProfileSkills(User user, ProfileSkillsResource profileSkills) {
        if (user.getProfile() == null) {
            user.setProfile(new Profile(user));
        }

        Profile profile = user.getProfile();

        profile.setBusinessType(profileSkills.getBusinessType());
        profile.setSkillsAreas(profileSkills.getSkillsAreas());

        userRepository.save(user);

        return serviceSuccess();
    }

    @Override
    public ServiceResult<Void> updateDetails(UserResource userResource) {
        if (userResource != null) {
            return userService.findByEmail(userResource.getEmail())
                    .andOnSuccess(existingUser ->
                            updateUser(existingUser, userResource));
        } else {
            return serviceFailure(badRequestError("User resource may not be null"));
        }
    }

    @Override
    public ServiceResult<List<AffiliationResource>> getUserAffiliations(Long userId) {
        return find(userRepository.findOne(userId), notFoundError(User.class, userId)).andOnSuccessReturn(user -> user.getAffiliations().stream().map(affiliation -> affiliationMapper.mapToResource(affiliation)).collect(toList()));
    }

    @Override
    public ServiceResult<Void> updateUserAffiliations(Long userId, List<AffiliationResource> affiliations) {
        return find(userRepository.findOne(userId), notFoundError(User.class, userId)).andOnSuccess(user -> {
            List<Affiliation> targetAffiliations = user.getAffiliations();
            targetAffiliations.clear();
            affiliationMapper.mapToDomain(affiliations)
                    .forEach(affiliation -> {
                        affiliation.setUser(user);
                        targetAffiliations.add(affiliation);
                    });
            userRepository.save(user);
            return serviceSuccess();
        });
    }

    @Override
    public ServiceResult<ProfileAddressResource> getProfileAddress(Long userId) {
        return find(userRepository.findOne(userId), notFoundError(User.class, userId))
                .andOnSuccess(user -> {
                    ProfileAddressResource profileAddress = new ProfileAddressResource();
                    profileAddress.setUser(user.getId());
                    if (user.getProfile() != null) {
                        profileAddress.setAddress(addressMapper.mapToResource(user.getProfile().getAddress()));
                    }
                    return serviceSuccess(profileAddress);
                });
    }

    @Override
    public ServiceResult<Void> updateProfileAddress(Long userId, ProfileAddressResource profileAddress) {
        return find(userRepository.findOne(userId), notFoundError(User.class, userId))
                .andOnSuccess(user -> updateUserProfileAddress(user, profileAddress));

    }

    private ServiceResult<Void> updateUserProfileAddress(User user, ProfileAddressResource profileAddress) {
        if (user.getProfile() == null) {
            user.setProfile(new Profile(user));
        }

        Profile profile = user.getProfile();
        profile.setAddress(addressMapper.mapToDomain(profileAddress.getAddress()));
        userRepository.save(user);

        return serviceSuccess();
    }

    private ServiceResult<Void> updateUser(UserResource existingUserResource, UserResource updatedUserResource) {
        existingUserResource.setPhoneNumber(updatedUserResource.getPhoneNumber());
        existingUserResource.setTitle(updatedUserResource.getTitle());
        existingUserResource.setLastName(updatedUserResource.getLastName());
        existingUserResource.setFirstName(updatedUserResource.getFirstName());
        existingUserResource.setGender(updatedUserResource.getGender());
        existingUserResource.setDisability(updatedUserResource.getDisability());
        existingUserResource.setEthnicity(updatedUserResource.getEthnicity());
        User existingUser = userMapper.mapToDomain(existingUserResource);
        return serviceSuccess(userRepository.save(existingUser)).andOnSuccessReturnVoid();
    }
}
