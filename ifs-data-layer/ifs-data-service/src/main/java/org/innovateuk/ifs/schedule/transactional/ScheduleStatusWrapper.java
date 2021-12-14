package org.innovateuk.ifs.schedule.transactional;

import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.util.AuthenticationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Slf4j
@Component
public class ScheduleStatusWrapper {

    @Autowired
    private ScheduleStatusService scheduleStatusService;

    @Autowired
    private AuthenticationHelper authenticationHelper;

    @Autowired
    private SlackReporter errorReporter;

    public void doScheduledJob(String jobName, Supplier<ServiceResult<ScheduleResponse>> runnable, Runnable failedToGetLock) {
        authenticationHelper.loginSystemUser();
        try {
            scheduleStatusService.startJob(jobName);
        } catch (Exception e) {
            failedToGetLock.run();
            return;
        }
        try {
            ServiceResult<ScheduleResponse> response = runnable.get();
            if (response.isSuccess()) {
                if (response.getSuccess().getResponse() != null) {
                    errorReporter.report("Schedule SUCCESS " + jobName + " response: " + response.getSuccess().getResponse());
                }
            } else {
                errorReporter.report("Schedule FAILURE " + jobName + " response: " + response.getFailure().toDisplayString());
            }
        } catch (Exception e) {
            log.error("Error running scheduled job " + jobName, e);
            errorReporter.report("Schedule FAILURE " + jobName + " response: " + e.getMessage());
        } finally {
            scheduleStatusService.endJob(jobName);
        }
    }

    public void doScheduledJob(String jobName,  Supplier<ServiceResult<ScheduleResponse>> runnable) {
        doScheduledJob(jobName, runnable, () -> {});
    }
}
