package com.worth.ifs.assessment.repository;

import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.user.domain.ProcessRole;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Set;


/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface AssessmentRepository extends PagingAndSortingRepository<Assessment, Long> {

    @Override
    Set<Assessment> findAll();

    Assessment findOneByParticipantId(Long processRoleId);

    List<Assessment> findByParticipantIn(List<ProcessRole> processRoles);
}