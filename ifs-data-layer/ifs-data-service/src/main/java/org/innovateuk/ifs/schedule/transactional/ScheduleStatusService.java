package org.innovateuk.ifs.schedule.transactional;

public interface ScheduleStatusService {

    void startScheduledJob(String jobName);
    void endScheduledJob(String jobName);
}
