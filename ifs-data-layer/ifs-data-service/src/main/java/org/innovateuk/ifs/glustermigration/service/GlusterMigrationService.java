package org.innovateuk.ifs.glustermigration.service;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.schedule.transactional.ScheduleResponse;

import java.io.IOException;

public interface GlusterMigrationService {

    @NotSecured(value = "Gluster migration scheduler", mustBeSecuredByOtherServices = false)
    ServiceResult<ScheduleResponse> processGlusterFiles() throws IOException;

}
