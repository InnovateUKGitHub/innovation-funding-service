package org.innovateuk.ifs.schedule.transactional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.util.AuthenticationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ScheduleStatusWrapper {
    private static final Log LOG = LogFactory.getLog(ScheduleStatusWrapper.class);

    @Autowired
    private ScheduleStatusService scheduleStatusService;

    @Autowired
    private AuthenticationHelper authenticationHelper;

    @Autowired
    private SlackErrorReporter errorReporter;

    public void doScheduledJob(String jobName, Runnable runnable, Runnable failedToGetLock) {
        try {
            scheduleStatusService.startJob(jobName);
        } catch (Exception e) {
            failedToGetLock.run();
            return;
        }
        try {
            authenticationHelper.loginSystemUser();
            runnable.run();
            errorReporter.reportProblem("Schedule job ran fine " + jobName);
        } catch (Exception e) {
            LOG.error("Error running scheduled job " + jobName, e);
            errorReporter.reportProblem("Error running scheduled job " + jobName + " error: " + e.getMessage());
        } finally {
            scheduleStatusService.endJob(jobName);
        }
    }

    public void doScheduledJob(String jobName, Runnable runnable) {
        doScheduledJob(jobName, runnable, () -> {});
    }
}
