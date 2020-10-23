package org.innovateuk.ifs.supporter.resource;

import java.util.List;

public class AssignSupportersResource {
    private long applicationId;
    private List<Long> supporterIds;

    public long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(long applicationId) {
        this.applicationId = applicationId;
    }

    public List<Long> getSupporterIds() {
        return supporterIds;
    }

    public void setSupporterIds(List<Long> supporterIds) {
        this.supporterIds = supporterIds;
    }
}
