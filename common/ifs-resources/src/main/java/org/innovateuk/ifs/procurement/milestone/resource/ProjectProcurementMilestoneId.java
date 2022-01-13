package org.innovateuk.ifs.procurement.milestone.resource;

public class ProjectProcurementMilestoneId extends ProcurementMilestoneId {

    public static ProjectProcurementMilestoneId of(long id) {
        ProjectProcurementMilestoneId projectProcurementMilestoneId = new ProjectProcurementMilestoneId();
        projectProcurementMilestoneId.setId(id);
        return projectProcurementMilestoneId;
    }
}