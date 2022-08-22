package org.innovateuk.ifs.starters.messaging.cfg;

/**
 * Common queue names - shared as bean name and internally as the queue name.
 *
 * Required in annotations so needs to be constant
 */
public class CommonQueues {

    private CommonQueues() {
        // Static only class
    }

    public static final String FILE_UPLOAD_SERVICE_UPLOAD = "FILE_UPLOAD_SERVICE_UPLOAD";
}
