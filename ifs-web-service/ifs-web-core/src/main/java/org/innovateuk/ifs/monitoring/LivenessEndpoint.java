package org.innovateuk.ifs.monitoring;

import org.springframework.stereotype.Component;

@Component
public class LivenessEndpoint extends AbstractMonitoringEndpoint {

    @Override
    public String getId() {
        return "live";
    }

    @Override
    protected boolean isReady() {
        // TODO: return false if this container should be replaced
        return true;
    }
}
