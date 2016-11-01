package com.worth.ifs.assessment.transactional;

import com.worth.ifs.assessment.mapper.CompetitionInviteMapper;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.invite.domain.CompetitionInvite;
import com.worth.ifs.invite.domain.CompetitionParticipant;
import com.worth.ifs.invite.domain.RejectionReason;
import com.worth.ifs.invite.repository.CompetitionInviteRepository;
import com.worth.ifs.invite.repository.CompetitionParticipantRepository;
import com.worth.ifs.invite.repository.RejectionReasonRepository;
import com.worth.ifs.invite.resource.CompetitionInviteResource;
import com.worth.ifs.invite.resource.RejectionReasonResource;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.repository.UserRepository;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.transactional.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;

import java.util.Optional;


import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.error.CommonFailureKeys.*;
import static com.worth.ifs.invite.constant.InviteStatus.OPENED;
import static com.worth.ifs.invite.domain.ParticipantStatus.ACCEPTED;
import static com.worth.ifs.invite.domain.ParticipantStatus.REJECTED;
import static com.worth.ifs.util.EntityLookupCallbacks.find;
import static java.lang.Boolean.TRUE;

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
    private CompetitionInviteMapper mapper;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Override
    public ServiceResult<CompetitionInviteResource> getInvite(String inviteHash) {
        return getByHashIfOpen(inviteHash).andOnSuccessReturn(mapper::mapToResource);
    }

    @Override
    public ServiceResult<CompetitionInviteResource> openInvite(String inviteHash) {
        return getByHashIfOpen(inviteHash).andOnSuccessReturn(invite -> mapper.mapToResource(openInvite(invite)));
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

    private ServiceResult<CompetitionInvite> getByHash(String inviteHash) {
        return find(competitionInviteRepository.getByHash(inviteHash), notFoundError(CompetitionInvite.class, inviteHash));
    }

    private ServiceResult<CompetitionInvite> getByHashIfOpen(String inviteHash) {
        return getByHash(inviteHash).andOnSuccess(invite -> {
            CompetitionParticipant participant = competitionParticipantRepository.getByInviteHash(inviteHash);

            if (participant == null) {
                return ServiceResult.serviceSuccess(invite);
            }

            if (participant.getStatus() == ACCEPTED || participant.getStatus() == REJECTED) {
                return ServiceResult.serviceFailure(new Error(COMPETITION_INVITE_CLOSED, invite.getTarget().getName()));
            }

            return ServiceResult.serviceSuccess(invite);
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
            return ServiceResult.serviceSuccess(competitionParticipantRepository.save(participant.acceptAndAssignUser(user)));
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
            return ServiceResult.serviceSuccess(competitionParticipantRepository.save(participant.reject(rejectionReason, rejectionComment)));
        }
    }

    private ServiceResult<RejectionReason> getRejectionReason(final RejectionReasonResource rejectionReason) {
        return find(rejectionReasonRepository.findOne(rejectionReason.getId()), notFoundError(RejectionReason.class, rejectionReason.getId()));
    }

    private String getInviteCompetitionName(CompetitionParticipant participant) {
        return participant.getInvite().getTarget().getName();
    }
}
