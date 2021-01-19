package org.innovateuk.ifs.project.procurement.milestones.viewmodel;

import org.innovateuk.ifs.application.forms.sections.procurement.milestones.viewmodel.AbstractProcurementMilestoneViewModel;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.project.finance.resource.PaymentMilestoneResource;
import org.innovateuk.ifs.project.finance.resource.PaymentMilestoneState;
import org.innovateuk.ifs.project.resource.ProjectResource;

public class ProjectProcurementMilestoneViewModel extends AbstractProcurementMilestoneViewModel {

    private final long applicationId;
    private final long organisationId;
    private final String organisationName;
    private final long projectId;
    private final String applicationName;
    private final String financesUrl;
    private final boolean readOnly;
    private final PaymentMilestoneResource paymentMilestoneResource;
    private final boolean eligibilityAndViabilityApproved;
    private final boolean externalUser;

    public ProjectProcurementMilestoneViewModel(ProjectResource project, ProjectFinanceResource finance, String financesUrl, boolean readOnly, PaymentMilestoneResource paymentMilestoneResource, boolean eligibilityAndViabilityApproved, boolean externalUser) {
        super(project.getDurationInMonths(), finance);
        this.applicationId = project.getApplication();
        this.organisationId = finance.getOrganisation();
        this.organisationName = finance.getOrganisationName();
        this.projectId = project.getId();
        this.applicationName = project.getName();
        this.financesUrl = financesUrl;
        this.readOnly = readOnly;
        this.eligibilityAndViabilityApproved = eligibilityAndViabilityApproved;
        this.paymentMilestoneResource = paymentMilestoneResource;
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

    public String getOrganisationName() {
        return organisationName;
    }

    public PaymentMilestoneResource getPaymentMilestoneResource() {
        return paymentMilestoneResource;
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
        return this.eligibilityAndViabilityApproved && !this.paymentMilestoneResource.isMilestonePaymentApproved()
                && this.isReadOnly();
    }

    public boolean isCanReset() {
        return this.paymentMilestoneResource.isMilestonePaymentApproved();
    }

    public boolean isApproved() {
        return this.paymentMilestoneResource.isMilestonePaymentApproved();
    }

    public boolean isShowApprovalMessage() {
        return this.isApproved();
    }

    public boolean isShowResetMessage() {
        return PaymentMilestoneState.REVIEW == this.paymentMilestoneResource.getPaymentMilestoneState() && paymentMilestoneResource.getPaymentMilestoneInternalUserLastName() != null;
    }
}