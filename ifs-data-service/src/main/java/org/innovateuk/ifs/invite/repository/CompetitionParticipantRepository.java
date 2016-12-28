package org.innovateuk.ifs.invite.repository;

import org.innovateuk.ifs.invite.domain.CompetitionParticipant;
import org.innovateuk.ifs.invite.domain.CompetitionParticipantRole;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface CompetitionParticipantRepository extends CrudRepository<CompetitionParticipant, Long> {

    CompetitionParticipant getByInviteHash( String hash);

    List<CompetitionParticipant> getByUserIdAndRole(Long userId, CompetitionParticipantRole role);
}
