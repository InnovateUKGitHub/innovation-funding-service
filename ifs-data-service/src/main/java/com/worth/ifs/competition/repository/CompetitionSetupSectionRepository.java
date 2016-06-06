package com.worth.ifs.competition.repository;

import com.worth.ifs.competition.domain.CompetitionSetupSection;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface CompetitionSetupSectionRepository extends CrudRepository<CompetitionSetupSection, Long> {
    List<CompetitionSetupSection> findAllByOrderByPriorityAsc();
}
