package org.innovateuk.ifs.procurement.milestone.domain;


import org.innovateuk.ifs.finance.domain.ProjectFinance;

import javax.persistence.*;

@Entity
public class ProjectProcurementMilestone extends ProcurementMilestone {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="projectFinanceId", referencedColumnName="id")
    private ProjectFinance projectFinance;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="applicationProcurementMilestoneId", referencedColumnName="id")
    private ApplicationProcurementMilestone applicationProcurementMilestone;

    public ProjectProcurementMilestone() {
        super();
    }

    public ProjectProcurementMilestone(ApplicationProcurementMilestone milestone, ProjectFinance projectFinance) {
        super();
        this.setMonth(milestone.getMonth());
        this.setDescription(milestone.getDescription());
        this.setTaskOrActivity(milestone.getTaskOrActivity());
        this.setDeliverable(milestone.getDeliverable());
        this.setSuccessCriteria(milestone.getSuccessCriteria());
        this.setPayment(milestone.getPayment());
        this.projectFinance = projectFinance;
        this.applicationProcurementMilestone = milestone;
    }

    public ProjectFinance getProjectFinance() {
        return projectFinance;
    }

    public void setProjectFinance(ProjectFinance projectFinance) {
        this.projectFinance = projectFinance;
    }

    public ApplicationProcurementMilestone getApplicationProcurementMilestone() {
        return applicationProcurementMilestone;
    }

    public void setApplicationProcurementMilestone(ApplicationProcurementMilestone applicationProcurementMilestone) {
        this.applicationProcurementMilestone = applicationProcurementMilestone;
    }
}
