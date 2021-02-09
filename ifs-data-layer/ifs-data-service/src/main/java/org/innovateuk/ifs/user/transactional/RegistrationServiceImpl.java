package org.innovateuk.ifs.user.transactional;

import org.innovateuk.ifs.address.mapper.AddressMapper;
import org.innovateuk.ifs.authentication.service.IdentityProviderService;
import org.innovateuk.ifs.authentication.validator.PasswordPolicyValidator;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.*;
import org.innovateuk.ifs.competition.mapper.ExternalFinanceRepository;
import org.innovateuk.ifs.competition.repository.StakeholderRepository;
import org.innovateuk.ifs.competition.transactional.TermsAndConditionsService;
import org.innovateuk.ifs.invite.domain.Invite;
import org.innovateuk.ifs.invite.domain.RoleInvite;
import org.innovateuk.ifs.invite.repository.AllInviteRepository;
import org.innovateuk.ifs.profile.domain.Profile;
import org.innovateuk.ifs.profile.repository.ProfileRepository;
import org.innovateuk.ifs.project.monitoring.repository.MonitoringOfficerInviteRepository;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.cache.UserUpdate;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserCreationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GENERAL_NOT_FOUND;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.NOT_AN_INTERNAL_USER_ROLE;
import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.user.resource.Role.ASSESSOR;
import static org.innovateuk.ifs.user.resource.Role.MONITORING_OFFICER;
import static org.innovateuk.ifs.user.resource.UserStatus.PENDING;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * A service around Registration and general user-creation operations
 */
@Service
public class RegistrationServiceImpl extends BaseTransactionalService implements RegistrationService {

    @Autowired
    private IdentityProviderService idpService;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private PasswordPolicyValidator passwordPolicyValidator;

    @Autowired
    private UserSurveyService userSurveyService;

    @Autowired
    private TermsAndConditionsService termsAndConditionsService;

    @Autowired
    private RegistrationNotificationService registrationEmailService;

    @Autowired
    private MonitoringOfficerInviteRepository monitoringOfficerInviteRepository;

    @Autowired
    private StakeholderRepository stakeholderRepository;

    @Autowired
    private ExternalFinanceRepository externalFinanceRepository;

    @Autowired
    private AllInviteRepository allInviteRepository;

    @Override
    @Transactional
    public ServiceResult<UserResource> createUser(UserCreationResource user) {
        ServiceResult<UserResource> userResult = serviceSuccess(user.toUserResource());
        if (!shouldBePending(user)) {
            //Pending users don't provide passwords until they accept their invite.
            userResult = userResult.andOnSuccess(u -> validateUser(user.getPassword(), u));
        }
        ServiceResult<User> result = userResult
                .andOnSuccess(savedUser -> createUserWithUid(user));

        if (shouldSendVerificationEmail(user)) {
            result = result
                    .andOnSuccess(savedUser -> sendUserVerificationEmail(ofNullable(user.getCompetitionId()), ofNullable(user.getOrganisationId()), savedUser));
        }
        if (shouldBePending(user)) {
            result = result.andOnSuccess(this::saveUserAsPending);
        } else {
            result = result.andOnSuccess(savedUser -> markLatestSiteTermsAndConditions(savedUser, user))
                    .andOnSuccess(savedUser -> handleInvite(savedUser, user));
        }
        if (shouldImmediatelyActivate(user)) {
            result = result
                    .andOnSuccess(this::activateUser);
        }
        return result
                .andOnSuccessReturn(userMapper::mapToResource);
    }

    private boolean shouldImmediatelyActivate(UserCreationResource user) {
        return user.getInviteHash() != null; // Already answered an email notification.
    }

    private boolean shouldSendVerificationEmail(UserCreationResource user) {
        return user.getRole() != ASSESSOR &&
                !shouldImmediatelyActivate(user) &&
                !shouldBePending(user);
    }

    private ServiceResult<User> handleInvite(User created, UserCreationResource user) {
        if (user.getInviteHash() != null) {
            Invite invite =  allInviteRepository.getByHash(user.getInviteHash());
            if (invite != null) {
                allInviteRepository.save(invite.open());

                if (invite instanceof StakeholderInvite) {
                    associateUserWithCompetition(((StakeholderInvite) invite).getTarget(), created);
                }
                if (invite instanceof ExternalFinanceInvite) {
                    associateCompetitionFinanceUserWithCompetition(((ExternalFinanceInvite) invite).getTarget(), created);
                }
            }
        }
        return serviceSuccess(created);
    }

    private String getPasswordOrPlaceholder(UserCreationResource user) {
         if (shouldBePending(user)) {
             return randomAlphabetic(6) + randomAlphabetic(6).toUpperCase() + randomNumeric(6);
         } else {
             return user.getPassword();
         }
    }

    private boolean shouldBePending(UserCreationResource user) {
        return user.getRole() == MONITORING_OFFICER && !user.isAddLiveProjectUserRole();
    }

    private ServiceResult<User> saveUserAsPending(User user) {
        user.setStatus(PENDING);
        return serviceSuccess(userRepository.save(user));
    }

    private ServiceResult<UserResource> validateUser(String password, UserResource userResource) {
        return passwordPolicyValidator.validatePassword(password, userResource)
                .handleSuccessOrFailure(
                        failure -> serviceFailure(
                                simpleMap(
                                        failure.getErrors(),
                                        error -> fieldError("password", error.getFieldRejectedValue(), error.getErrorKey())
                                )
                        ),
                        success -> serviceSuccess(userResource)
                );
    }

    private ServiceResult<User> markLatestSiteTermsAndConditions(User userWithRole, UserCreationResource userCreationResource) {
        return userCreationResource.isAgreedTerms() ?
                agreeLatestSiteTermsAndConditionsForUser(userWithRole) : serviceSuccess(userWithRole);
    }

    private ServiceResult<User> createUserWithUid(UserCreationResource userCreationResource) {
        UserResource userResource = userCreationResource.toUserResource();
        User user = new User();
        Profile profile = new Profile();
        user.setFirstName(userResource.getFirstName());
        user.setLastName(userResource.getLastName());
        user.setEmail(userResource.getEmail());
        user.setPhoneNumber(userResource.getPhoneNumber());
        user.setAllowMarketingEmails(userResource.getAllowMarketingEmails());
        user.setRoles(newHashSet(userResource.getRoles()));
        if (userCreationResource.getInviteHash() != null) {
            Invite invite = allInviteRepository.getByHash(userCreationResource.getInviteHash());
            user.setEmail(invite.getEmail());

            if (invite instanceof RoleInvite) {
                user.setRoles(newHashSet(((RoleInvite) invite).getTarget()));
                if (((RoleInvite) invite).getSimpleOrganisation() != null) {
                    profile.setSimpleOrganisation(((RoleInvite) invite).getSimpleOrganisation());
                }
                userCreationResource.setRole(((RoleInvite) invite).getTarget());
            }
        }

        String password = getPasswordOrPlaceholder(userCreationResource);
        ServiceResult<String> uidFromIdpResult = idpService.createUserRecordWithUid(user.getEmail(), password);
    
        return uidFromIdpResult.andOnSuccessReturn(uidFromIdp -> {
            user.setUid(uidFromIdp);
            user.setStatus(UserStatus.INACTIVE);
            if (userCreationResource.getAddress() != null) profile.setAddress(addressMapper.mapToDomain(userCreationResource.getAddress()));
            Profile savedProfile = profileRepository.save(profile);
            user.setProfileId(savedProfile.getId());
            return userRepository.save(user);
        });
    }

    private ServiceResult<User> sendUserVerificationEmail(Optional<Long> competitionId, Optional<Long> organisationId, User user) {
        return registrationEmailService.sendUserVerificationEmail(userMapper.mapToResource(user), competitionId, organisationId).
                andOnSuccessReturn(() -> user);
    }

    private ServiceResult<User> agreeLatestSiteTermsAndConditionsForUser(User user) {
        return termsAndConditionsService.getLatestSiteTermsAndConditions().andOnSuccessReturn(termsAndConditions -> {
            if (user.getTermsAndConditionsIds() == null) {
                user.setTermsAndConditionsIds(new LinkedHashSet<>());
            }
            user.getTermsAndConditionsIds().add(termsAndConditions.getId());
            return user;
        });
    }

    @Override
    @Transactional
    public ServiceResult<Void> resendUserVerificationEmail(final UserResource user) {
        return registrationEmailService.resendUserVerificationEmail(user);
    }

    private ServiceResult<Void> associateUserWithCompetition(Competition competition, User user) {
        stakeholderRepository.save(new Stakeholder(competition, user));
        return serviceSuccess();
    }

    private ServiceResult<Void> associateCompetitionFinanceUserWithCompetition(Competition competition, User user) {
        externalFinanceRepository.save(new ExternalFinance(competition, user));
        return serviceSuccess();
    }

    @Override
    @Transactional
    @UserUpdate
    public ServiceResult<UserResource> activateUser(long userId) {
        return getUser(userId).andOnSuccess(this::activateUser)
                .andOnSuccessReturn(userMapper::mapToResource);
    }

    private ServiceResult<User> activateUser(User user) {
        return idpService
                .activateUser(user.getUid())
                .andOnSuccessReturn(() -> {
                    user.setStatus(UserStatus.ACTIVE);
                    return userRepository.save(user);
                });
    }

    @Override
    @Transactional
    @UserUpdate
    public ServiceResult<UserResource> deactivateUser(long userId) {
        return getUser(userId).andOnSuccess(this::deactivateUser)
                .andOnSuccessReturn(userMapper::mapToResource);
    }

    private ServiceResult<User> deactivateUser(User user) {
        return idpService
                .deactivateUser(user.getUid())
                .andOnSuccessReturn(() -> {
                    user.setStatus(UserStatus.INACTIVE);
                    return userRepository.save(user);
                });
    }

    @Override
    @Transactional
    public ServiceResult<User> activatePendingUser(User user,
                                                   String password,
                                                   String hash) {
        if(monitoringOfficerInviteRepository.existsByHash(hash)) {
            return activateUser(user)
                    .andOnSuccess(activatedUser -> idpService.updateUserPassword(activatedUser.getUid(), password))
                    .andOnSuccessReturn(() -> user);
        }

        return serviceFailure(GENERAL_NOT_FOUND);
    }

    @Override
    @Transactional
    public ServiceResult<Void> activateApplicantAndSendDiversitySurvey(long userId) {
        return getUser(userId)
                .andOnSuccess(this::activateUser)
                .andOnSuccessReturnVoid(this::sendApplicantDiversitySurvey);
    }

    @Override
    @Transactional
    public ServiceResult<Void> activateAssessorAndSendDiversitySurvey(long userId) {
        return getUser(userId)
                .andOnSuccess(this::activateUser)
                .andOnSuccessReturnVoid(this::sendAssessorDiversitySurvey);
    }

    private ServiceResult<Void> sendApplicantDiversitySurvey(User user) {
        return userSurveyService.sendApplicantDiversitySurvey(user);
    }

    private ServiceResult<Void> sendAssessorDiversitySurvey(User user) {
        return userSurveyService.sendAssessorDiversitySurvey(user);
    }

    @Override
    @Transactional
    @UserUpdate
    public ServiceResult<UserResource> editInternalUser(UserResource userToEdit, Role userRoleType) {
        return validateInternalUserRole(userRoleType)
                .andOnSuccess(() -> ServiceResult.getNonNullValue(userRepository.findById(userToEdit.getId()).orElse(null), notFoundError(User.class)))
                .andOnSuccessReturn(user -> {
                        Set<Role> roleList = newHashSet(userRoleType);
                        user.setFirstName(userToEdit.getFirstName());
                        user.setLastName(userToEdit.getLastName());
                        user.setRoles(roleList);
                        return userRepository.save(user);
                })
                .andOnSuccessReturn(userMapper::mapToResource);
    }

    private ServiceResult<Void> validateInternalUserRole(Role userRoleType) {

        return Role.internalRoles().contains(userRoleType) ?
                serviceSuccess() : serviceFailure(NOT_AN_INTERNAL_USER_ROLE);
    }
}