package com.worth.ifs.competitiontemplate.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.worth.ifs.competitiontemplate.domain.CompetitionTemplate;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface CompetitionTemplateRepository extends PagingAndSortingRepository<CompetitionTemplate, Long> {

    CompetitionTemplate findByCompetitionTypeId(Long competitionTypeId);

}
