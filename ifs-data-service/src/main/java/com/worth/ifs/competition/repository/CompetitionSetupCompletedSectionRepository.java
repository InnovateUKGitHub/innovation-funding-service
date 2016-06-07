package com.worth.ifs.competition.repository;

import com.worth.ifs.competition.domain.CompetitionSetupCompletedSection;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface CompetitionSetupCompletedSectionRepository extends CrudRepository<CompetitionSetupCompletedSection, Long> {
    List<CompetitionSetupCompletedSection> findByCompetitionId(Long id);
    Optional<CompetitionSetupCompletedSection> findByCompetitionIdAndCompetitionSetupSectionId(Long competitionId, Long competitionSetupSectionId);

}
