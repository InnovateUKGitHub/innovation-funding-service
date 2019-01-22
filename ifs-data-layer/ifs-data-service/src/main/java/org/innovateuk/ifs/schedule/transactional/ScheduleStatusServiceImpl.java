package org.innovateuk.ifs.schedule.transactional;

import org.innovateuk.ifs.schedule.domain.ScheduleStatus;
import org.innovateuk.ifs.schedule.repository.ScheduleStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ScheduleStatusServiceImpl implements ScheduleStatusService {

    @Autowired
    private ScheduleStatusRepository scheduleStatusRepository;

    /**
     * Requires new transaction so that the update to the schedule status table is immediate.
     * In a multi data-service scenario this will cause a failure in all but one of the services.
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void startScheduledJob(String jobName) {
        Optional<ScheduleStatus> scheduleStatus = scheduleStatusRepository.findByJobName(jobName);
        scheduleStatus
                .filter(status -> !status.isActive())
                .orElseThrow(() -> new RuntimeException(String.format("Inactive job not found for name %s", jobName)))
                .setActive(true);
    }

    @Override
    @Transactional
    public void endScheduledJob(String jobName) {
        Optional<ScheduleStatus> scheduleStatus = scheduleStatusRepository.findByJobName(jobName);
        scheduleStatus
                .orElseThrow(() -> new RuntimeException(String.format("Job not found for name %s", jobName)))
                .setActive(false);
    }
}
