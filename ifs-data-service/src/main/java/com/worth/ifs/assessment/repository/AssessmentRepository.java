package com.worth.ifs.assessment.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.worth.ifs.assessment.domain.Assessment;


/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface AssessmentRepository extends PagingAndSortingRepository<Assessment, Long> {

    @Override
    Set<Assessment> findAll();

    Assessment findOneByProcessRoleId(Long processRoleId);
}