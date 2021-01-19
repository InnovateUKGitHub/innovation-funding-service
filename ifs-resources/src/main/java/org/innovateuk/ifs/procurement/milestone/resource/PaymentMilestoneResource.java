package org.innovateuk.ifs.procurement.milestone.resource;

public class PaymentMilestoneResource extends ProcurementMilestoneResource {

    private long projectId;

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }
}
