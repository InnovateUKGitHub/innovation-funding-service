package com.worth.ifs.project.repository;

import com.worth.ifs.project.domain.ProjectDetailsProcess;
import com.worth.ifs.workflow.repository.ProcessRepository;


/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface ProjectDetailsProcessRepository extends ProcessRepository<ProjectDetailsProcess> {

    @Override
    ProjectDetailsProcess findOneByParticipantId(Long participantId);
}