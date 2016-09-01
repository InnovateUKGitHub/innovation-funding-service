package com.worth.ifs.invite.repository;

import com.worth.ifs.invite.domain.CompetitionParticipant;
import com.worth.ifs.invite.domain.CompetitionParticipantRole;
import com.worth.ifs.invite.domain.ParticipantStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface CompetitionParticipantRepository extends CrudRepository<CompetitionParticipant, Long> {

    CompetitionParticipant getByInviteHash( String hash);

    List<CompetitionParticipant> getByUserIdAndRoleAndStatus(Long userId, @Param("role") CompetitionParticipantRole role, @Param("status") ParticipantStatus status);
}