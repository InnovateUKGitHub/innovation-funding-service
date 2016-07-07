package com.worth.ifs.project.repository;

import com.worth.ifs.project.domain.MonitoringOfficer;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface MonitoringOfficerRepository extends PagingAndSortingRepository<MonitoringOfficer, Long> {

    MonitoringOfficer findOneByProjectId(Long projectId);
}
