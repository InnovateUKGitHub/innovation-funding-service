package org.innovateuk.ifs.management.viewmodel;

import org.innovateuk.ifs.user.resource.BusinessType;

/**
 * Holder of model attributes for the assessors shown in the 'Overview' tab of the Invite Assessors view.
 */
public class OverviewAssessorRowViewModel extends InviteAssessorsRowViewModel {

    private BusinessType businessType;
    private String status;
    private String details;

    public OverviewAssessorRowViewModel(String name, String innovationArea, boolean compliant, BusinessType businessType, String status, String details) {
        super(name, innovationArea, compliant);
        this.businessType = businessType;
        this.status = status;
        this.details = details;
    }

    public BusinessType getBusinessType() {
        return businessType;
    }

    public String getStatus() {
        return status;
    }

    public String getDetails() {
        return details;
    }
}