package org.innovateuk.ifs.schedule.repository;

import org.innovateuk.ifs.schedule.domain.ScheduleStatus;
import org.springframework.data.repository.CrudRepository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface ScheduleStatusRepository extends CrudRepository<ScheduleStatus, Long> {

    Optional<ScheduleStatus> findByJobName(String jobName);

    List<ScheduleStatus> findByCreatedOnBefore(ZonedDateTime minusMinutes);
}
