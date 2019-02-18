package org.innovateuk.ifs.project.monitor.repository;

import org.innovateuk.ifs.project.core.domain.ProjectParticipantRole;
import org.innovateuk.ifs.project.monitor.domain.ProjectMonitoringOfficer;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ProjectMonitoringOfficerRepository extends PagingAndSortingRepository<ProjectMonitoringOfficer, Long> {

    boolean existsByUserEmailAndRole(String email, ProjectParticipantRole role);

    ProjectMonitoringOfficer findByUserId(long userId);
}