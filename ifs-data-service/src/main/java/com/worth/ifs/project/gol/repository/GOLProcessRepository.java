package com.worth.ifs.project.gol.repository;

import com.worth.ifs.project.gol.domain.GOLProcess;
import com.worth.ifs.workflow.repository.ProcessRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface GOLProcessRepository extends ProcessRepository<GOLProcess>, PagingAndSortingRepository<GOLProcess, Long> {

}
