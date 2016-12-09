package com.worth.ifs.assessment.transactional;

import com.worth.ifs.assessment.mapper.CompetitionInviteMapper;
import com.worth.ifs.category.domain.Category;
import com.worth.ifs.category.repository.CategoryRepository;
import com.worth.ifs.category.resource.CategoryResource;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.competition.repository.CompetitionRepository;
import com.worth.ifs.invite.domain.CompetitionInvite;
import com.worth.ifs.invite.domain.CompetitionParticipant;
import com.worth.ifs.invite.domain.RejectionReason;
import com.worth.ifs.invite.repository.CompetitionInviteRepository;
import com.worth.ifs.invite.repository.CompetitionParticipantRepository;
import com.worth.ifs.invite.repository.RejectionReasonRepository;
import com.worth.ifs.invite.resource.*;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.repository.UserRepository;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.transactional.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.worth.ifs.category.resource.CategoryType.INNOVATION_AREA;
import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.error.CommonFailureKeys.*;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.invite.constant.InviteStatus.CREATED;
import static com.worth.ifs.invite.constant.InviteStatus.OPENED;
import static com.worth.ifs.invite.domain.ParticipantStatus.ACCEPTED;
import static com.worth.ifs.invite.domain.ParticipantStatus.REJECTED;
import static com.worth.ifs.user.resource.BusinessType.BUSINESS;
import static com.worth.ifs.util.EntityLookupCallbacks.find;
import static java.lang.Boolean.TRUE;
import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;

/**
 * Service for managing {@link com.worth.ifs.invite.domain.CompetitionInvite}s.
 */
@Service
public class CompetitionInviteServiceImpl implements CompetitionInviteService {

    @Autowired
    private CompetitionInviteRepository competitionInviteRepository;

    @Autowired
    private CompetitionParticipantRepository competitionParticipantRepository;

    @Autowired
    private RejectionReasonRepository rejectionReasonRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CompetitionInviteMapper mapper;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Override
    public ServiceResult<CompetitionInviteResource> getInvite(String inviteHash) {
        return getByHashIfOpen(inviteHash)
                .andOnSuccessReturn(mapper::mapToResource);
    }

    @Override
    public ServiceResult<CompetitionInviteResource> openInvite(String inviteHash) {
        return getByHashIfOpen(inviteHash)
                .andOnSuccessReturn(this::openInvite)
                .andOnSuccessReturn(mapper::mapToResource);
    }

    @Override
    public ServiceResult<Void> acceptInvite(String inviteHash, UserResource currentUser) {
        final User user = userRepository.findOne(currentUser.getId());
        return getParticipantByInviteHash(inviteHash)
                .andOnSuccess(p -> accept(p, user))
                .andOnSuccessReturnVoid();
    }

    @Override
    public ServiceResult<Void> rejectInvite(String inviteHash, RejectionReasonResource rejectionReason, Optional<String> rejectionComment) {
        return getRejectionReason(rejectionReason)
                .andOnSuccess(reason -> getParticipantByInviteHash(inviteHash)
                        .andOnSuccess(invite -> reject(invite, reason, rejectionComment)))
                .andOnSuccessReturnVoid();
    }

    @Override
    public ServiceResult<Boolean> checkExistingUser(@P("inviteHash") String inviteHash) {
        return getByHash(inviteHash).andOnSuccessReturn(invite -> {
            if (invite.getUser() != null) {
                return TRUE;
            }
            return userService.findByEmail(invite.getEmail()).getOptionalSuccessObject().isPresent();
        });
    }

    @Override
    public ServiceResult<List<AvailableAssessorResource>> getAvailableAssessors(long competitionId) {
        // TODO INFUND-6775
        return serviceSuccess(
                asList(new AvailableAssessorResource(77L, "Jeremy", "Alufson", "worth.email.test+assessor1@gmail.com", BUSINESS,
                        new CategoryResource(null, "Earth Observation", null, null, null), true, false)));
    }

    @Override
    public ServiceResult<CompetitionInviteResource> inviteUser(NewUserStagedInviteResource stagedInvite) {
        return getInnovationArea(stagedInvite.getInnovationCategoryId())
                .andOnSuccess(innovationArea -> getCompetition(stagedInvite.getCompetitionId())
                        .andOnSuccess(competition -> inviteUserToCompetition(stagedInvite.getName(), stagedInvite.getEmail(), competition, innovationArea))
                )
                .andOnSuccessReturn(mapper::mapToResource);
    }

    private ServiceResult<Category> getInnovationArea(long innovationCategoryId) {
        return find(categoryRepository.findByIdAndType(innovationCategoryId, INNOVATION_AREA), notFoundError(Category.class, innovationCategoryId, INNOVATION_AREA));
    }

    private ServiceResult<CompetitionInvite> inviteUserToCompetition(String name, String email, Competition competition, Category innovationArea) {
        return serviceSuccess(
                competitionInviteRepository.save(new CompetitionInvite(name, email, generateHash(), competition, innovationArea))
        );
    }

    @Override
    public ServiceResult<CompetitionInviteResource> inviteUser(ExistingUserStagedInviteResource stagedInvite) {
        return getUserByEmail(stagedInvite.getEmail()) // I'm not particularly tied to finding by email, vs id
                .andOnSuccess(user -> inviteUserToCompetition(user, stagedInvite.getCompetitionId()))
                .andOnSuccessReturn(mapper::mapToResource);
    }

    private ServiceResult<CompetitionInvite> inviteUserToCompetition(User user, long competitionId) {
        return getCompetition(competitionId)
                .andOnSuccessReturn(
                        competition -> competitionInviteRepository.save(new CompetitionInvite(user, generateHash(), competition))
                );
    }

    private ServiceResult<Competition> getCompetition(long competitionId) {
        return find(competitionRepository.findOne(competitionId), notFoundError(Competition.class, competitionId));
    }

    // TODO INFUND-6725 this needs to be replaced with an alternate token generator
    private static String generateHash() {
        return randomUUID().toString();
    }

    private ServiceResult<User> getUserByEmail(String email) {
        return find(userRepository.findByEmail(email), notFoundError(User.class, email));
    }

    @Override
    public ServiceResult<Void> sendInvite(long inviteId) {
        return getById(inviteId).andOnSuccess(this::sendInvite);
    }

    private ServiceResult<Void> sendInvite(CompetitionInvite invite) {
        competitionParticipantRepository.save(new CompetitionParticipant(invite.send()));
        return serviceSuccess();
    }

    @Override
    public ServiceResult<Void> deleteInvite(String email, long competitionId) {
        return getByEmailAndCompetition(email, competitionId).andOnSuccess(this::deleteInvite);
    }

    private ServiceResult<CompetitionInvite> getByHash(String inviteHash) {
        return find(competitionInviteRepository.getByHash(inviteHash), notFoundError(CompetitionInvite.class, inviteHash));
    }

    private ServiceResult<CompetitionInvite> getById(long id) {
        return find(competitionInviteRepository.findOne(id), notFoundError(CompetitionInvite.class, id));
    }

    private ServiceResult<CompetitionInvite> getByEmailAndCompetition(String email, long competitionId) {
        return find(competitionInviteRepository.getByEmailAndCompetitionId(email, competitionId), notFoundError(CompetitionInvite.class, email, competitionId));
    }

    private ServiceResult<Void> deleteInvite(CompetitionInvite invite) {
        if (invite.getStatus() != CREATED) {
            return ServiceResult.serviceFailure(new Error(COMPETITION_INVITE_CANNOT_DELETE_ONCE_SENT, invite.getEmail()));
        }

        competitionInviteRepository.delete(invite);
        return serviceSuccess();
    }

    private ServiceResult<CompetitionInvite> getByHashIfOpen(String inviteHash) {
        return getByHash(inviteHash).andOnSuccess(invite -> {
            CompetitionParticipant participant = competitionParticipantRepository.getByInviteHash(inviteHash);

            if (participant == null) {
                return serviceSuccess(invite);
            }

            if (participant.getStatus() == ACCEPTED || participant.getStatus() == REJECTED) {
                return ServiceResult.serviceFailure(new Error(COMPETITION_INVITE_CLOSED, invite.getTarget().getName()));
            }

            return serviceSuccess(invite);
        });
    }

    private CompetitionInvite openInvite(CompetitionInvite invite) {
        return competitionInviteRepository.save(invite.open());
    }

    private ServiceResult<CompetitionParticipant> getParticipantByInviteHash(String inviteHash) {
        return find(competitionParticipantRepository.getByInviteHash(inviteHash), notFoundError(CompetitionParticipant.class, inviteHash));
    }

    private ServiceResult<CompetitionParticipant> accept(CompetitionParticipant participant, User user) {
        if (participant.getInvite().getStatus() != OPENED) {
            return ServiceResult.serviceFailure(new Error(COMPETITION_PARTICIPANT_CANNOT_ACCEPT_UNOPENED_INVITE, getInviteCompetitionName(participant)));
        } else if (participant.getStatus() == ACCEPTED) {
            return ServiceResult.serviceFailure(new Error(COMPETITION_PARTICIPANT_CANNOT_ACCEPT_ALREADY_ACCEPTED_INVITE, getInviteCompetitionName(participant)));
        } else if (participant.getStatus() == REJECTED) {
            return ServiceResult.serviceFailure(new Error(COMPETITION_PARTICIPANT_CANNOT_ACCEPT_ALREADY_REJECTED_INVITE, getInviteCompetitionName(participant)));
        } else {
            return serviceSuccess(competitionParticipantRepository.save(participant.acceptAndAssignUser(user)));
        }
    }

    private ServiceResult<CompetitionParticipant> reject(CompetitionParticipant participant, RejectionReason rejectionReason, Optional<String> rejectionComment) {
        if (participant.getInvite().getStatus() != OPENED) {
            return ServiceResult.serviceFailure(new Error(COMPETITION_PARTICIPANT_CANNOT_REJECT_UNOPENED_INVITE, getInviteCompetitionName(participant)));
        } else if (participant.getStatus() == ACCEPTED) {
            return ServiceResult.serviceFailure(new Error(COMPETITION_PARTICIPANT_CANNOT_REJECT_ALREADY_ACCEPTED_INVITE, getInviteCompetitionName(participant)));
        } else if (participant.getStatus() == REJECTED) {
            return ServiceResult.serviceFailure(new Error(COMPETITION_PARTICIPANT_CANNOT_REJECT_ALREADY_REJECTED_INVITE, getInviteCompetitionName(participant)));
        } else {
            return serviceSuccess(competitionParticipantRepository.save(participant.reject(rejectionReason, rejectionComment)));
        }
    }

    private ServiceResult<RejectionReason> getRejectionReason(final RejectionReasonResource rejectionReason) {
        return find(rejectionReasonRepository.findOne(rejectionReason.getId()), notFoundError(RejectionReason.class, rejectionReason.getId()));
    }

    private String getInviteCompetitionName(CompetitionParticipant participant) {
        return participant.getInvite().getTarget().getName();
    }
}
