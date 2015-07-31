package com.worth.ifs.repository;

import com.worth.ifs.domain.ProcessStatus;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "processstatus", path = "processtatus")
public interface ProcessStatusRepository extends PagingAndSortingRepository<ProcessStatus, Long> {
    List<ProcessStatus> findByName(@Param("name") String name);
    List<ProcessStatus> findById(@Param("id") Long id);
    List<ProcessStatus> findAll();
}
