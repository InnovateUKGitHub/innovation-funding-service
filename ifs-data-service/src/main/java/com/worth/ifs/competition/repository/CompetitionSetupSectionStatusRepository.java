package com.worth.ifs.competition.repository;

import com.worth.ifs.competition.domain.CompetitionSetupSectionStatus;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface CompetitionSetupSectionStatusRepository extends CrudRepository<CompetitionSetupSectionStatus, Long> {
    List<CompetitionSetupSectionStatus> findByCompetitionId(Long id);
}
