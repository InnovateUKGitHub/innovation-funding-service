package org.innovateuk.ifs.competition.repository;

import org.innovateuk.ifs.competition.domain.Milestone;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface MilestoneRepository extends JpaRepository<Milestone, Long> {

    List<Milestone> findAllByCompetitionId(Long competitionId);

    List<Milestone> findByCompetitionIdAndTypeIn(Long competitionId, List<MilestoneType> types);

    Optional<Milestone> findByTypeAndCompetitionId(MilestoneType type, Long competitionId);

    void deleteByCompetitionId(long competitionId);
}
