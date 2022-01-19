package org.innovateuk.ifs.procurement.milestone.resource;

public class ApplicationProcurementMilestoneId extends ProcurementMilestoneId {

    public static ApplicationProcurementMilestoneId of(long id) {
        ApplicationProcurementMilestoneId applicationProcurementMilestoneId = new ApplicationProcurementMilestoneId();
        applicationProcurementMilestoneId.setId(id);
        return applicationProcurementMilestoneId;
    }
}