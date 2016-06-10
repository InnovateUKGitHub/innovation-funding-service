package com.worth.ifs.competition.repository;

import com.worth.ifs.competition.domain.Competition;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface CompetitionRepository extends PagingAndSortingRepository<Competition, Long> {

    List<Competition> findByName(String name);
    Competition findById(Long id);
    @Override
    List<Competition> findAll();
    List<Competition> findByCodeLike(String code);

}
