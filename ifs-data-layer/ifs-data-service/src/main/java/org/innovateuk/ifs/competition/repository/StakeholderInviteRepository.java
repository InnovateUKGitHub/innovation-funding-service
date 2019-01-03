package org.innovateuk.ifs.competition.repository;

import org.innovateuk.ifs.competition.domain.StakeholderInvite;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.repository.InviteRepository;

import java.util.List;

public interface StakeholderInviteRepository extends InviteRepository<StakeholderInvite> {

    List<StakeholderInvite> findByCompetitionIdAndStatus(long competitionId, InviteStatus status);

    boolean existsByCompetitionIdAndStatusAndEmail(long competitionId, InviteStatus sent, String email);
}


