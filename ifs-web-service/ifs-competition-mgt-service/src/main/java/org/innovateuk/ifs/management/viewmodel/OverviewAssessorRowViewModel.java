package org.innovateuk.ifs.management.viewmodel;

/**
 * Holder of model attributes for the assessors shown in the 'Overview' tab of the Invite Assessors view.
 */
public class OverviewAssessorRowViewModel extends InviteAssessorsRowViewModel {

    private String status;
    private String details;

    public OverviewAssessorRowViewModel(String name, String innovationArea, boolean compliant, String status, String details) {
        super(name, innovationArea, compliant);
        this.status = status;
        this.details = details;
    }

    public String getStatus() {
        return status;
    }

    public String getDetails() {
        return details;
    }
}