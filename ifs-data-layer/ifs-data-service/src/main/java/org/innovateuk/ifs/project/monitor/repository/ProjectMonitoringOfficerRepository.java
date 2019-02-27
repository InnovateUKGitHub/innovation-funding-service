package org.innovateuk.ifs.project.monitor.repository;

import org.innovateuk.ifs.project.core.domain.ProjectParticipantRole;
import org.innovateuk.ifs.project.monitor.domain.ProjectMonitoringOfficer;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ProjectMonitoringOfficerRepository extends PagingAndSortingRepository<ProjectMonitoringOfficer, Long> {

    List<ProjectMonitoringOfficer> findByUserId(long userId);

    boolean existsByUserEmailAndRole(String email, ProjectParticipantRole role);
}