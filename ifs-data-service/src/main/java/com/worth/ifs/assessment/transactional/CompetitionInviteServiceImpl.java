package com.worth.ifs.assessment.transactional;

import com.worth.ifs.assessment.mapper.CompetitionInviteMapper;
import com.worth.ifs.assessment.resource.CompetitionRejectionReasonResource;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.invite.domain.CompetitionInvite;
import com.worth.ifs.invite.domain.CompetitionParticipant;
import com.worth.ifs.invite.repository.CompetitionInviteRepository;
import com.worth.ifs.invite.repository.CompetitionParticipantRepository;
import com.worth.ifs.invite.resource.CompetitionInviteResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.util.EntityLookupCallbacks.find;

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
    private CompetitionInviteMapper mapper;


    @Override
    public ServiceResult<CompetitionInviteResource> openInvite(String inviteHash) {
        return getByHash(inviteHash).andOnSuccessReturn(invite -> mapper.mapToResource(openInvite(invite)));
    }

    @Override
    public ServiceResult<Void> acceptInvite(String inviteHash) {
        return getParticipantByInviteHash(inviteHash).andOnSuccessReturnVoid(i -> accept(i));
    }

    @Override
    public ServiceResult<Void> rejectInvite(String inviteHash, CompetitionRejectionReasonResource rejectionReason) {
        return ServiceResult.serviceSuccess();
    }

    private ServiceResult<CompetitionInvite> getByHash(String inviteHash) {
        return find(competitionInviteRepository.getByHash(inviteHash), notFoundError(CompetitionInvite.class, inviteHash));
    }

    private CompetitionInvite openInvite(CompetitionInvite invite) {
        return competitionInviteRepository.save(invite.open());
    }


    private ServiceResult<CompetitionParticipant> getParticipantByInviteHash(String inviteHash) {
        return find(competitionParticipantRepository.getByInviteHash(inviteHash), notFoundError(CompetitionParticipant.class, inviteHash));
    }

    private CompetitionParticipant accept(CompetitionParticipant participant) {
        return competitionParticipantRepository.save(participant.accept());
    }
}
