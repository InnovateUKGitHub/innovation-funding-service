package org.innovateuk.ifs.application.ProcurementMilestones;

import org.innovateuk.ifs.application.forms.sections.procurement.milestones.viewmodel.AbstractProcurementMilestoneViewModel;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.project.finance.resource.PaymentMilestoneState;
import org.innovateuk.ifs.project.finance.resource.ProjectProcurementMilestoneResource;
import org.innovateuk.ifs.project.resource.ProjectResource;

public class ProjectProcurementMilestoneViewModel extends AbstractProcurementMilestoneViewModel {

    private final long applicationId;
    private final long organisationId;
    private final long projectId;
    private final String applicationName;
    private final String financesUrl;
    private final boolean readOnly;
    private final ProjectProcurementMilestoneResource projectProcurementMilestoneResource;
    private final boolean eligibilityAndViabilityApproved;
    private final boolean externalUser;

    public ProjectProcurementMilestoneViewModel(ProjectResource project, long organisationId, ProjectFinanceResource finance, String financesUrl, boolean readOnly, ProjectProcurementMilestoneResource projectProcurementMilestoneResource, boolean eligibilityAndViabilityApproved, boolean externalUser) {
        super(project.getDurationInMonths(), finance);
        this.applicationId = project.getApplication();
        this.organisationId = organisationId;
        this.projectId = project.getId();
        this.applicationName = project.getName();
        this.financesUrl = financesUrl;
        this.readOnly = readOnly;
        this.eligibilityAndViabilityApproved = eligibilityAndViabilityApproved;
        this.projectProcurementMilestoneResource = projectProcurementMilestoneResource;
        this.externalUser = externalUser;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public String getFinancesUrl() {
        return financesUrl;
    }

    public long getProjectId() {
        return projectId;
    }

    public long getOrganisationId() {
        return organisationId;
    }


    public ProjectProcurementMilestoneResource getProjectProcurementMilestoneResource() {
        return projectProcurementMilestoneResource;
    }

    public boolean isEligibilityAndViabilityApproved() {
        return eligibilityAndViabilityApproved;
    }

    public boolean isExternalUser() {
        return externalUser;
    }

    @Override
    public boolean isReadOnly() {
        return readOnly;
    }

    public boolean isCanApprove() {
        return this.eligibilityAndViabilityApproved && !this.projectProcurementMilestoneResource.isMilestonePaymentApproved()
                && this.isReadOnly();
    }

    public boolean isCanReset() {
        return this.projectProcurementMilestoneResource.isMilestonePaymentApproved();
    }

    public boolean isApproved() {
        return this.projectProcurementMilestoneResource.isMilestonePaymentApproved();
    }

    public boolean isShowApprovalMessage() {
        return this.isApproved();
    }

    public boolean isShowResetMessage() {
        return PaymentMilestoneState.REVIEW == this.projectProcurementMilestoneResource.getPaymentMilestoneState() && projectProcurementMilestoneResource.getPaymentMilestoneInternalUserLastName() != null;
    }
}