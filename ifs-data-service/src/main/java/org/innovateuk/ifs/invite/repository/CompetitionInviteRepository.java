package org.innovateuk.ifs.invite.repository;

import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.CompetitionInvite;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface CompetitionInviteRepository extends CrudRepository<CompetitionInvite, Long> {

    CompetitionInvite getByEmailAndCompetitionId(String email, long competitionId);

    List<CompetitionInvite> getByCompetitionIdAndStatus(long competitionId, InviteStatus status);

    CompetitionInvite getByHash(String hash);
}
