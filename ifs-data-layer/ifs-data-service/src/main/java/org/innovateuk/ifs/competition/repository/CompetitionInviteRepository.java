package org.innovateuk.ifs.competition.repository;

import org.innovateuk.ifs.competition.domain.CompetitionInvite;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.repository.InviteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
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
    String GET_BY_COMPETITION_ID_AND_STATUS_WITHOUT_INACTIVE_ASSESSORS = "SELECT invite FROM #{#entityName} invite " +
            "LEFT JOIN invite.user.roleProfileStatuses roleStatuses " +
            "WHERE invite.competition.id = :competitionId AND " +
            "      invite.status = :status AND " +
            " (roleStatuses IS NULL OR " +
            "(" +
            "    roleStatuses.profileRole = org.innovateuk.ifs.user.resource.ProfileRole.ASSESSOR " +
            "AND roleStatuses.roleProfileState = org.innovateuk.ifs.user.resource.RoleProfileState.ACTIVE " +
            "))";

    String COUNT_BY_COMPETITION_ID_AND_STATUSES_IN_WITHOUT_INACTIVE_ASSESSORS = "SELECT COUNT(invite) FROM #{#entityName} invite " +
            "LEFT JOIN invite.user.roleProfileStatuses roleStatuses " +
            "WHERE invite.competition.id = :competitionId AND " +
            "      invite.status IN :statuses AND " +
            " (roleStatuses IS NULL OR " +
            "(" +
            "    roleStatuses.profileRole = org.innovateuk.ifs.user.resource.ProfileRole.ASSESSOR " +
            "AND roleStatuses.roleProfileState = org.innovateuk.ifs.user.resource.RoleProfileState.ACTIVE " +
            "))";

    T getByEmailAndCompetitionId(String email, long competitionId);

    List<T> getByCompetitionId(long competitionId);

    @Query(GET_BY_COMPETITION_ID_AND_STATUS_WITHOUT_INACTIVE_ASSESSORS)
    List<T> getByCompetitionIdAndStatus(long competitionId, InviteStatus status);

    @Query(GET_BY_COMPETITION_ID_AND_STATUS_WITHOUT_INACTIVE_ASSESSORS)
    Page<T> getByCompetitionIdAndStatus(long competitionId, InviteStatus status, Pageable pageable);

    @Query(COUNT_BY_COMPETITION_ID_AND_STATUSES_IN_WITHOUT_INACTIVE_ASSESSORS)
    int countByCompetitionIdAndStatusIn(long competitionId, Set<InviteStatus> statuses);

    void deleteByCompetitionIdAndStatus(long competitionId, InviteStatus status);
}
