package org.innovateuk.ifs.schedule.transactional;

import org.innovateuk.ifs.commons.security.NotSecured;

public interface ScheduleStatusService {

    @NotSecured(value = "Used by @Scheduled methods", mustBeSecuredByOtherServices = false)
    void startJob(String jobName);

    @NotSecured(value = "Used by @Scheduled methods", mustBeSecuredByOtherServices = false)
    void endJob(String jobName);
}
