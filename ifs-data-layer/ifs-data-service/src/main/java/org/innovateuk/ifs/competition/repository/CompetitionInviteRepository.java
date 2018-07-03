package org.innovateuk.ifs.competition.repository;

import org.innovateuk.ifs.competition.domain.CompetitionInvite;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.repository.InviteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Set;

/**
 * Base repository for querying {@link CompetitionInvite} parent class.
 *
 * We assume that the target of the {@link org.innovateuk.ifs.invite.domain.Invite}
 * is a {@link org.innovateuk.ifs.competition.domain.Competition}.
 */
@NoRepositoryBean
public interface CompetitionInviteRepository<T extends CompetitionInvite> extends InviteRepository<T> {

    T getByEmailAndCompetitionId(String email, long competitionId);

    List<T> getByCompetitionId(long competitionId);

    List<T> getByCompetitionIdAndStatus(long competitionId, InviteStatus status);

    Page<T> getByCompetitionIdAndStatus(long competitionId, InviteStatus status, Pageable pageable);

    int countByCompetitionIdAndStatusIn(long competitionId, Set<InviteStatus> statuses);

    void deleteByCompetitionIdAndStatus(long competitionId, InviteStatus status);
}
