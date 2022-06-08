package org.innovateuk.ifs.glustermigration.scheduler;

import org.innovateuk.ifs.IfsProfileConstants;
import org.innovateuk.ifs.glustermigration.service.GlusterMigrationService;
import org.innovateuk.ifs.schedule.transactional.ScheduleStatusWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Profile(IfsProfileConstants.NOT_INTEGRATION_TEST)
public class GlusterFilesProcessor {

    private static final String JOB_NAME = "MOVE_FILE_FROM_GLUSTER_TO_S3";

    @Autowired
    private GlusterMigrationService glusterMigrationService;

    @Autowired
    private ScheduleStatusWrapper scheduleStatusWrapper;

    @Scheduled(fixedDelayString = "${ifs.data.service.gluster.file.migration.millis:20000}")
    public void send() {
        scheduleStatusWrapper.doScheduledJob(JOB_NAME, () -> {
            try {
                return glusterMigrationService.processGlusterFiles();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
