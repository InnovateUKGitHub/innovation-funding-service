package com.worth.ifs.competition.repository;

import com.worth.ifs.competition.domain.CompetitionFunder;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface CompetitionFunderRepository extends CrudRepository<CompetitionFunder, Long> {

    List<CompetitionFunder> findByCompetitionId(Long competitionId);

    void deleteByCompetitionId(@Param("competitionId") Long competitionId);

}
