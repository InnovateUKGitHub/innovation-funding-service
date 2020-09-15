package org.innovateuk.ifs.docusign.transactional;

import com.docusign.esign.client.ApiException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.schedule.transactional.ScheduleStatusService;
import org.innovateuk.ifs.util.AuthenticationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ImportSignedDocusignScheduled {
    private static final Log LOG = LogFactory.getLog(ImportSignedDocusignScheduled.class);

    private static final String JOB_NAME = "DOCUSIGN_IMPORT";

    @Autowired
    private DocusignService docusignService;

    @Autowired
    private AuthenticationHelper authenticationHelper;

    @Autowired
    private ScheduleStatusService scheduleStatusService;

    //Every hour
    @Scheduled(cron = "0 0 * ? * *")
    public void send() {
        try {
            scheduleStatusService.startJob(JOB_NAME);
        } catch (Exception e) {
            return;
        }
        try {
            authenticationHelper.loginSystemUser();
            docusignService.downloadFileIfSigned();
        } catch (ApiException | IOException e) {
            LOG.error(e);
        }
        scheduleStatusService.endJob(JOB_NAME);
    }
}
