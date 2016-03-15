package com.worth.ifs.competition.repository;

import com.worth.ifs.competition.domain.Competition;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface CompetitionRepository extends PagingAndSortingRepository<Competition, Long> {

    List<Competition> findByName(@Param("name") String name);
    Competition findById(@Param("id") Long id);
    List<Competition> findAll();

}
