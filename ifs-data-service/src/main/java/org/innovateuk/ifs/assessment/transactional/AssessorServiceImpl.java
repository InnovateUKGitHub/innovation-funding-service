package org.innovateuk.ifs.assessment.transactional;

import org.innovateuk.ifs.assessment.mapper.AssessorProfileMapper;
import org.innovateuk.ifs.assessment.resource.AssessorProfileResource;
import org.innovateuk.ifs.assessment.resource.ProfileResource;
import org.innovateuk.ifs.category.mapper.InnovationAreaMapper;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.domain.CompetitionParticipant;
import org.innovateuk.ifs.invite.repository.CompetitionParticipantRepository;
import org.innovateuk.ifs.invite.resource.CompetitionInviteResource;
import org.innovateuk.ifs.registration.resource.UserRegistrationResource;
import org.innovateuk.ifs.profile.domain.Profile;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.mapper.AffiliationMapper;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.profile.repository.ProfileRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.RoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.transactional.RegistrationService;
import org.innovateuk.ifs.user.transactional.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.user.resource.UserRoleType.ASSESSOR;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class AssessorServiceImpl implements AssessorService {

    @Autowired
    private CompetitionInviteService competitionInviteService;

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private CompetitionParticipantRepository competitionParticipantRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AssessorProfileMapper assessorProfileMapper;

    @Autowired
    private InnovationAreaMapper innovationAreaMapper;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AffiliationMapper affiliationMapper;

    @Override
    public ServiceResult<Void> registerAssessorByHash(String inviteHash, UserRegistrationResource userRegistrationResource) {

        // TODO: Handle failures gracefully and hand them back to the webservice
        return retrieveInvite(inviteHash).andOnSuccess(inviteResource -> {
            userRegistrationResource.setEmail(inviteResource.getEmail());
            return getAssessorRoleResource().andOnSuccess(assessorRole -> {
                userRegistrationResource.setRoles(singletonList(assessorRole));
                return createUser(userRegistrationResource).andOnSuccessReturnVoid(created -> {
                    assignCompetitionParticipantsToUser(created);
                    Profile profile = profileRepository.findOne(created.getProfileId());
                    // profile is guaranteed to have been created by createUser(...)
                    profile.addInnovationArea(innovationAreaMapper.mapToDomain(inviteResource.getInnovationArea()));
                    profileRepository.save(profile);
                });
            });
        });
    }

    @Override
    public ServiceResult<AssessorProfileResource> getAssessorProfile(Long assessorId) {
        return getAssessor(assessorId)
                .andOnSuccess(user -> getProfile(user.getProfileId())
                        .andOnSuccessReturn(
                                profile -> {
                                    // TODO INFUND-7750 - tidy up assessor profile DTOs
                                    UserResource userResource = userMapper.mapToResource(user);
                                    ProfileResource profileResource = assessorProfileMapper.mapToResource(profile);
                                    profileResource.setAffiliations(affiliationMapper.mapToResource(user.getAffiliations()));
                                    return new AssessorProfileResource(
                                            userResource,
                                            profileResource
                                    );
                                }
                        )
                );
    }

    private ServiceResult<Profile> getProfile(Long profileId) {
        return find(profileRepository.findOne(profileId), notFoundError(Profile.class, profileId));
    }

    private ServiceResult<User> getAssessor(long assessorId) {
        return find(userRepository.findByIdAndRolesName(assessorId, ASSESSOR.getName()), notFoundError(User.class, assessorId));
    }

    private ServiceResult<CompetitionInviteResource> retrieveInvite(String inviteHash) {
        return competitionInviteService.getInvite(inviteHash);
    }

    private void assignCompetitionParticipantsToUser(User user) {
        List<CompetitionParticipant> competitionParticipants = competitionParticipantRepository.getByInviteEmail(user.getEmail());
        competitionParticipants.forEach(competitionParticipant -> competitionParticipant.setUser(user));
        competitionParticipantRepository.save(competitionParticipants);
    }

    private ServiceResult<RoleResource> getAssessorRoleResource() {
        return roleService.findByUserRoleType(ASSESSOR);
    }

    private ServiceResult<User> createUser(UserRegistrationResource userRegistrationResource) {
        return registrationService.createUser(userRegistrationResource).andOnSuccess(
                created -> registrationService.activateUser(created.getId()).andOnSuccessReturn(result -> userRepository.findOne(created.getId())));
    }
}
