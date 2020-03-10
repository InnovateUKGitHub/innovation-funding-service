package org.innovateuk.ifs.project.monitoring.repository;

import org.innovateuk.ifs.project.monitoring.domain.BaseMonitoringOfficer;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BaseMonitoringOfficerRepository extends CrudRepository<BaseMonitoringOfficer, Long> {

    List<BaseMonitoringOfficer> findByUserId(long userId);

    List<BaseMonitoringOfficer> findByProjectId(long projectId);

    boolean existsByProjectIdAndUserId(long projectId, long userId);

    boolean existsByProjectApplicationCompetitionIdAndUserId(long competitionId, long userId);

}

