package org.innovateuk.ifs.project.repository;

import org.innovateuk.ifs.project.domain.MonitoringOfficer;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface MonitoringOfficerRepository extends PagingAndSortingRepository<MonitoringOfficer, Long> {

    MonitoringOfficer findOneByProjectId(Long projectId);
}
