package com.worth.ifs.application.repository;

import com.worth.ifs.application.domain.ApplicationStatus;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ApplicationStatusRepository extends PagingAndSortingRepository<ApplicationStatus, Long> {
    List<ApplicationStatus> findByName(@Param("name") String name);
    List<ApplicationStatus> findById(@Param("id") Long id);
    List<ApplicationStatus> findAll();
}
