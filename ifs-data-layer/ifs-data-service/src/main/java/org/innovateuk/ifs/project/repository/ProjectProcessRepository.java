package org.innovateuk.ifs.project.repository;

import org.innovateuk.ifs.project.domain.ProjectProcess;
import org.innovateuk.ifs.workflow.repository.ProcessRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface ProjectProcessRepository extends ProcessRepository<ProjectProcess>, PagingAndSortingRepository<ProjectProcess, Long> {

}
