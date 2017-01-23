package org.innovateuk.ifs.management.viewmodel;

import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.invite.resource.ParticipantStatusResource;
import org.innovateuk.ifs.user.resource.BusinessType;

import java.util.List;

/**
 * Holder of model attributes for the assessors shown in the 'Overview' tab of the Invite Assessors view.
 */
public class OverviewAssessorRowViewModel extends InviteAssessorsRowViewModel {

    private BusinessType businessType;
    private ParticipantStatusResource status;
    private String details;

    public OverviewAssessorRowViewModel(String name, List<InnovationAreaResource> innovationAreas, boolean compliant, BusinessType businessType, ParticipantStatusResource status, String details) {
        super(name, innovationAreas, compliant);
        this.businessType = businessType;
        this.status = status;
        this.details = details;
    }

    public BusinessType getBusinessType() {
        return businessType;
    }

    public ParticipantStatusResource getStatus() {
        return status;
    }

    public String getDetails() {
        return details;
    }
}