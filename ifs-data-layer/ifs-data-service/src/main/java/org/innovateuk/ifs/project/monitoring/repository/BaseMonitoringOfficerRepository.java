package org.innovateuk.ifs.project.monitoring.repository;

import org.innovateuk.ifs.finance.domain.GrowthTable;
import org.innovateuk.ifs.project.monitoring.domain.BaseMonitoringOfficer;
import org.innovateuk.ifs.project.monitoring.domain.MonitoringOfficer;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BaseMonitoringOfficerRepository extends CrudRepository<BaseMonitoringOfficer, Long> {

    List<BaseMonitoringOfficer> findByUserId(long userId);

}

