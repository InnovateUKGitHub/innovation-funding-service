package com.worth.ifs.project.repository;

import com.worth.ifs.project.domain.ProjectDetailsProcess;
import com.worth.ifs.project.finance.domain.FinanceCheckProcess;
import com.worth.ifs.workflow.repository.ProcessRepository;
import org.springframework.data.repository.PagingAndSortingRepository;


/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface ProjectDetailsProcessRepository extends ProcessRepository<ProjectDetailsProcess>, PagingAndSortingRepository<ProjectDetailsProcess, Long> {

}