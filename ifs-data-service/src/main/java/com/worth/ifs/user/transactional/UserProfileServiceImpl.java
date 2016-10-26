package com.worth.ifs.user.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.transactional.BaseTransactionalService;
import com.worth.ifs.user.domain.Affiliation;
import com.worth.ifs.user.domain.Contract;
import com.worth.ifs.user.domain.Profile;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.mapper.AffiliationMapper;
import com.worth.ifs.user.mapper.ContractMapper;
import com.worth.ifs.user.mapper.UserMapper;
import com.worth.ifs.user.repository.ContractRepository;
import com.worth.ifs.user.resource.AffiliationResource;
import com.worth.ifs.user.resource.ProfileContractResource;
import com.worth.ifs.user.resource.ProfileSkillsResource;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
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
    private ContractRepository contractRepository;

    @Autowired
    private ContractMapper contractMapper;

    @Autowired
    private AffiliationMapper affiliationMapper;

    private Clock clock = Clock.systemDefaultZone();


    public enum ServiceFailures {
        UNABLE_TO_UPDATE_USER;
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
        setUserProfileIfNoneExists(user);
        Profile profile = user.getProfile();

        profile.setBusinessType(profileSkills.getBusinessType());
        profile.setSkillsAreas(profileSkills.getSkillsAreas());

        userRepository.save(user);

        return serviceSuccess();
    }

    @Override
    public ServiceResult<ProfileContractResource> getProfileContract(Long userId) {
        return find(userRepository.findOne(userId), notFoundError(User.class, userId))
                .andOnSuccess(user ->
                        getCurrentContract().andOnSuccess(currentContract -> {
                            Profile profile = user.getProfile();
                            boolean hasAgreement = profile.getContract() != null;
                            boolean hasCurrentAgreement = hasAgreement && currentContract.getId().equals(profile.getContract().getId());
                            ProfileContractResource profileContract = new ProfileContractResource();
                            profileContract.setUser(user.getId());
                            profileContract.setContract(contractMapper.mapToResource(currentContract));
                            profileContract.setCurrentAgreement(hasCurrentAgreement);
                            if (hasCurrentAgreement) {
                                profileContract.setContractSignedDate(profile.getContractSignedDate());
                            }
                            return serviceSuccess(profileContract);
                        })
                );
    }

    @Override
    public ServiceResult<Void> updateProfileContract(Long userId) {
        return find(userRepository.findOne(userId), notFoundError(User.class, userId))
                .andOnSuccess(user -> {
                    setUserProfileIfNoneExists(user);
                    return getCurrentContract().andOnSuccess(currentContract ->
                            validateContract(currentContract, user).andOnSuccess(() -> {
                                user.getProfile().setContractSignedDate(LocalDateTime.now(clock));
                                user.getProfile().setContract(currentContract);
                                userRepository.save(user);
                                return serviceSuccess();
                            })
                    );
                });
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

    private ServiceResult<Void> updateUser(UserResource existingUserResource, UserResource updatedUserResource) {
        existingUserResource.setPhoneNumber(updatedUserResource.getPhoneNumber());
        existingUserResource.setTitle(updatedUserResource.getTitle());
        existingUserResource.setLastName(updatedUserResource.getLastName());
        existingUserResource.setFirstName(updatedUserResource.getFirstName());
        User existingUser = userMapper.mapToDomain(existingUserResource);
        return serviceSuccess(userRepository.save(existingUser)).andOnSuccessReturnVoid();
    }

    private ServiceResult<Void> validateContract(Contract contract, User user) {
        if (user.getProfile().getContract() != null && contract.getId().equals(user.getProfile().getContract().getId())) {
            return serviceFailure(badRequestError("validation.assessorprofiletermsform.terms.alreadysigned"));
        }
        return serviceSuccess();
    }


    private void setUserProfileIfNoneExists(User user) {
        if (user.getProfile() == null) {
            user.setProfile(new Profile());
        }
    }

    private ServiceResult<Contract> getCurrentContract() {
        return find(contractRepository.findByCurrentTrue(), notFoundError(Contract.class));
    }
}
