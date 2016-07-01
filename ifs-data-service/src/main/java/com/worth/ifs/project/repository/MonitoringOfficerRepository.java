package com.worth.ifs.project.repository;

import com.worth.ifs.project.domain.MonitoringOfficer;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by bronnyl on 6/27/16.
 */
public interface MonitoringOfficerRepository extends PagingAndSortingRepository<MonitoringOfficer, Long> {

    MonitoringOfficer findOneByProjectId(Long projectId);
}
