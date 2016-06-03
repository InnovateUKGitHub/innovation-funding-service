package com.worth.ifs.competition.repository;

import com.worth.ifs.competition.domain.CompetitionType;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface CompetitionTypeRepository extends PagingAndSortingRepository<CompetitionType, Long> {

    List<CompetitionType> findByName(@Param("name") String name);
    CompetitionType findById(@Param("id") Long id);

    @Override
    List<CompetitionType> findAll();

}
