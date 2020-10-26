package org.innovateuk.ifs.cofunder.resource;

import java.util.List;

public class AssignCofundersResource {
    private long applicationId;
    private List<Long> cofunderIds;

    public long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(long applicationId) {
        this.applicationId = applicationId;
    }

    public List<Long> getCofunderIds() {
        return cofunderIds;
    }

    public void setCofunderIds(List<Long> cofunderIds) {
        this.cofunderIds = cofunderIds;
    }
}
