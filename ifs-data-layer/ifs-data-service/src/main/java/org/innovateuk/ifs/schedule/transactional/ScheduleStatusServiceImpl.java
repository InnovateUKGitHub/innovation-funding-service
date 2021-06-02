package org.innovateuk.ifs.schedule.transactional;

import org.innovateuk.ifs.commons.exception.IFSRuntimeException;
import org.innovateuk.ifs.schedule.domain.ScheduleStatus;
import org.innovateuk.ifs.schedule.repository.ScheduleStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
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
    public void startJob(String jobName) {
        clearTimedOutJobs();
        Optional<ScheduleStatus> scheduleStatus = scheduleStatusRepository.findByJobName(jobName);
        if (scheduleStatus.isPresent()) {
            throw new IFSRuntimeException(String.format("Error starting job %s another service may be running.", jobName));
        }
        scheduleStatusRepository.save(new ScheduleStatus(jobName));
    }

    private void clearTimedOutJobs() {
        int timeoutMins = 5;
        scheduleStatusRepository.deleteAll(
                scheduleStatusRepository.findByCreatedOnBefore(ZonedDateTime.now().minusMinutes(timeoutMins))
        );
    }

    @Override
    @Transactional
    public void endJob(String jobName) {
        Optional<ScheduleStatus> scheduleStatus = scheduleStatusRepository.findByJobName(jobName);
        if (!scheduleStatus.isPresent()) {
            throw new IFSRuntimeException(String.format("Error ending job %s ", jobName));
        }
        scheduleStatusRepository.delete(scheduleStatus.get());
    }
}
