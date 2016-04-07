package com.worth.ifs.application.repository;

import com.worth.ifs.application.domain.Application;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface ApplicationRepository extends PagingAndSortingRepository<Application, Long> {
    List<Application> findByName(@Param("name") String name);
    
    @Override
    List<Application> findAll();
    
    Page<Application> findByCompetitionId(Long competitionId, Pageable pageable);

    List<Application> findByCompetitionId(Long competitionId);

	Page<Application> findByCompetitionIdAndApplicationStatusId(Long competitionId, Long applicationStatusId, Pageable pageable);

	List<Application> findByCompetitionIdAndApplicationStatusId(Long competitionId, Long applicationStatusId);

    Long countByCompetitionId(Long competitionId);

    Long countByCompetitionIdAndApplicationStatusId(Long competitionId, Long applicationStatusId);

}
