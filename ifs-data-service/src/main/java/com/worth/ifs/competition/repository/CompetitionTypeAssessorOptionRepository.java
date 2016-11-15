package com.worth.ifs.competition.repository;

import com.worth.ifs.competition.domain.CompetitionTypeAssessorOption;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface CompetitionTypeAssessorOptionRepository extends CrudRepository<CompetitionTypeAssessorOption, Long> {

    List<CompetitionTypeAssessorOption> findByCompetitionTypeId(@Param("competitionTypeId") Long competitionTypeId);

    Optional<CompetitionTypeAssessorOption> findByCompetitionTypeIdAndDefaultOptionTrue(@Param("competitionTypeId") Long competitionTypeId);

}
