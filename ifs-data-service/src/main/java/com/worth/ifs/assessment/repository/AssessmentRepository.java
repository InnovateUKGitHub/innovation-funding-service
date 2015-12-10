package com.worth.ifs.assessment.repository;

import com.worth.ifs.assessment.domain.Assessment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;


/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface AssessmentRepository extends PagingAndSortingRepository<Assessment, Long> {
    Assessment findById(@Param("id") Long id);
    Set<Assessment> findAll();
    Assessment findOneByProcessRoleId(Long processRoleId);
    Integer countByProcessRoleUserIdAndProcessRoleApplicationCompetitionIdAndStatus(Long userId, Long competitionId,  String status);
    Integer countByProcessRoleUserIdAndProcessRoleApplicationCompetitionIdAndStatusNot(Long userId, Long competitionId, String status);
    List<Assessment> findByProcessRoleUserIdAndProcessRoleApplicationCompetitionIdAndStatusIn(Long userId, Long competitionId, Set<String> status);
}
