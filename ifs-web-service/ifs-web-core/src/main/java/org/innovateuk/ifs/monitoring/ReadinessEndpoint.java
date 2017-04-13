package org.innovateuk.ifs.monitoring;

import org.springframework.stereotype.Component;

@Component
public class ReadinessEndpoint extends AbstractMonitoringEndpoint {

    protected String id = "ready";

    @Override
    protected boolean isReady() {
        // TODO: return false if this container should not receive requests at this time
        return true;
    }
}
