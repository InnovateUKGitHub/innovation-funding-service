package org.innovateuk.ifs.review.viewmodel;

import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.management.assessor.viewmodel.InviteAssessorsRowViewModel;
import org.innovateuk.ifs.user.resource.BusinessType;

import java.util.List;

/**
 * Holder of model attributes for the available assessors shown in the 'Find' tab of the Assessment Panel Invite Assessors view.
 */
public class ReviewAvailableAssessorRowViewModel extends InviteAssessorsRowViewModel {

    private BusinessType businessType;

    public ReviewAvailableAssessorRowViewModel(long id,
                                               String name,
                                               List<InnovationAreaResource> innovationAreas,
                                               boolean compliant,
                                               BusinessType businessType) {
        super(id, name, innovationAreas, compliant);
        this.businessType = businessType;
    }

    public BusinessType getBusinessType() {
        return businessType;
    }
}