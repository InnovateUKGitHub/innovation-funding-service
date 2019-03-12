package org.innovateuk.ifs.schedule.repository;

import org.innovateuk.ifs.schedule.domain.ScheduleStatus;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ScheduleStatusRepository extends CrudRepository<ScheduleStatus, Long> {

    Optional<ScheduleStatus> findByJobName(String jobName);
}
