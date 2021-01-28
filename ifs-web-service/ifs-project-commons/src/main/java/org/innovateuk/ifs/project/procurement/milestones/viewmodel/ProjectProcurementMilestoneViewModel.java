package org.innovateuk.ifs.project.procurement.milestones.viewmodel;

import org.innovateuk.ifs.application.forms.sections.procurement.milestones.viewmodel.AbstractProcurementMilestoneViewModel;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.project.finance.resource.PaymentMilestoneResource;
import org.innovateuk.ifs.project.resource.ProjectResource;

public class ProjectProcurementMilestoneViewModel extends AbstractProcurementMilestoneViewModel {

    private final long applicationId;
    private final long organisationId;
    private final long projectId;
    private final String applicationName;
    private final String financesUrl;
    private final boolean readOnly;
    private final PaymentMilestoneResource paymentMilestoneResource;
    private final boolean eligibilityAndViabilityApproved;
    private final boolean externalUser;
    private final boolean showChangesLink;

    public ProjectProcurementMilestoneViewModel(ProjectResource project, long organisationId, ProjectFinanceResource finance, String financesUrl, boolean readOnly, PaymentMilestoneResource paymentMilestoneResource, boolean eligibilityAndViabilityApproved, boolean externalUser) {
        super(project.getDurationInMonths(), finance);
        this.applicationId = project.getApplication();
        this.organisationId = organisationId;
        this.projectId = project.getId();
        this.applicationName = project.getName();
        this.financesUrl = financesUrl;
        this.readOnly = readOnly;
        this.eligibilityAndViabilityApproved = eligibilityAndViabilityApproved;
        this.paymentMilestoneResource = paymentMilestoneResource;
        this.externalUser = externalUser;
        this.showChangesLink = showChangesLink;
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

    public boolean isApproved() {
        return this.paymentMilestoneResource.isMilestonePaymentApproved();
    }

    public boolean isShowBanner() {
        return this.isApproved();
    }

    public boolean isShowChangesLink() {
        return showChangesLink;
    }

    public boolean canEdit() {
        return this.isReadOnly() && !isApproved();
    }
}