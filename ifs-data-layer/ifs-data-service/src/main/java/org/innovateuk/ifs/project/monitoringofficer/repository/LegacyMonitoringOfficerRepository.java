package org.innovateuk.ifs.project.monitoringofficer.repository;

import org.innovateuk.ifs.project.monitoringofficer.domain.LegacyMonitoringOfficer;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface LegacyMonitoringOfficerRepository extends PagingAndSortingRepository<LegacyMonitoringOfficer, Long> {

    LegacyMonitoringOfficer findOneByProjectId(Long projectId);
}
