package org.innovateuk.ifs.project.monitoringofficer.repository;

import org.innovateuk.ifs.project.monitoringofficer.domain.MonitoringOfficer;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface MonitoringOfficerRepository extends PagingAndSortingRepository<MonitoringOfficer, Long> {

    MonitoringOfficer findOneByProjectId(Long projectId);
}
