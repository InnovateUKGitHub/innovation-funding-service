package org.innovateuk.ifs.competition.repository;

import org.innovateuk.ifs.competition.domain.CompetitionParticipant;
import org.innovateuk.ifs.competition.domain.CompetitionParticipantRole;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

@NoRepositoryBean
public interface CompetitionParticipantRepository<ParticipantType extends CompetitionParticipant> extends
        PagingAndSortingRepository<ParticipantType, Long> {

    List<ParticipantType> getByCompetitionIdAndRole(long competitionId, CompetitionParticipantRole role);

    ParticipantType getByCompetitionIdAndUserIdAndRole(long competitionId, long userId, CompetitionParticipantRole
            role);

    void deleteByCompetitionIdAndUserIdAndRole(long competitionId, long userId, CompetitionParticipantRole role);

    void deleteByCompetitionIdAndRole(long competitionId, CompetitionParticipantRole role);

    boolean existsByCompetitionIdAndUserIdAndRole(long competitionId, long userId, CompetitionParticipantRole role);

}
