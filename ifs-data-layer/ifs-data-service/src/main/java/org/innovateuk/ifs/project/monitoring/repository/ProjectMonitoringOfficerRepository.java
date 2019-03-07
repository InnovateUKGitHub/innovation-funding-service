package org.innovateuk.ifs.project.monitoring.repository;

import org.innovateuk.ifs.project.monitoring.domain.ProjectMonitoringOfficer;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ProjectMonitoringOfficerRepository extends PagingAndSortingRepository<ProjectMonitoringOfficer, Long> {

    List<ProjectMonitoringOfficer> findByUserId(long userId);

    boolean existsByProjectIdAndUserId(long projectId, long userId);

    boolean existsByProjectApplicationIdAndUserId(long applicationId, long userId);

    boolean existsByProjectApplicationCompetitionIdAndUserId(long competitionId, long userId);

    void deleteByUserIdAndProjectId(long userId, long projectId);
}