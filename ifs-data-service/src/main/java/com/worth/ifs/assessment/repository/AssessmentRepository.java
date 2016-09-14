package com.worth.ifs.assessment.repository;

import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.workflow.repository.ProcessRepository;

import java.util.Set;


/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface AssessmentRepository extends ProcessRepository<Assessment> {

    @Override
    Set<Assessment> findAll();

    @Override
    Assessment findOneByParticipantId(Long processRoleId);
}