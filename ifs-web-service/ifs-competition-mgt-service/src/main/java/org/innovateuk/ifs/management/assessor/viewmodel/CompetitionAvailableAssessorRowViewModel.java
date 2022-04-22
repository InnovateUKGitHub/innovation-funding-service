package org.innovateuk.ifs.management.assessor.viewmodel;

import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.user.resource.BusinessType;

import java.util.List;

/**
 * Holder of model attributes for the available assessors shown in the 'Find' tab of the Invite Assessors view.
 */
public class CompetitionAvailableAssessorRowViewModel extends InviteAssessorsRowViewModel {

    private String email;
    private BusinessType businessType;

    public CompetitionAvailableAssessorRowViewModel(long id,
                                                    String name,
                                                    List<InnovationAreaResource> innovationAreas,
                                                    boolean compliant,
                                                    boolean validAgreement,
                                                    boolean validDoi,
                                                    String email,
                                                    BusinessType businessType) {
        super(id, name, innovationAreas, compliant, validAgreement, validDoi);
        this.email = email;
        this.businessType = businessType;
    }

    public String getEmail() {
        return email;
    }

    public BusinessType getBusinessType() {
        return businessType;
    }
}