package org.innovateuk.ifs.docusign.transactional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.schedule.transactional.ScheduleStatusWrapper;
import org.innovateuk.ifs.util.AuthenticationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ImportSignedDocusignScheduled {
    private static final Log LOG = LogFactory.getLog(ImportSignedDocusignScheduled.class);

    private static final String JOB_NAME = "DOCUSIGN_IMPORT";

    @Autowired
    private DocusignService docusignService;

    @Autowired
    private AuthenticationHelper authenticationHelper;

    @Autowired
    private ScheduleStatusWrapper scheduleStatusWrapper;

    //Every hour
    @Scheduled(cron = "0 0 * ? * *")
    public void send() {
        scheduleStatusWrapper.doScheduledJob(JOB_NAME, () -> docusignService.downloadFileIfSigned());
    }
}
