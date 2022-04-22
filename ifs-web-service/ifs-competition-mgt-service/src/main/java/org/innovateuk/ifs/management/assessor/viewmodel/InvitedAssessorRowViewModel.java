package org.innovateuk.ifs.management.assessor.viewmodel;

import org.innovateuk.ifs.category.resource.InnovationAreaResource;

import java.util.List;

/**
 * Holder of model attributes for the invited assessors shown in the 'Invite' tab of the Invite Assessors view.
 */
public class InvitedAssessorRowViewModel extends InviteAssessorsRowViewModel {

    private String email;
    private Long inviteId;

    public InvitedAssessorRowViewModel(Long id,
                                       String name,
                                       List<InnovationAreaResource> innovationAreas,
                                       boolean compliant,
                                       boolean validAgreement,
                                       boolean validDoi,
                                       String email,
                                       Long inviteId) {
        super(id, name, innovationAreas, compliant, validAgreement, validDoi);
        this.email = email;
        this.inviteId = inviteId;
    }

    public String getEmail() {
        return email;
    }

    public Long getInviteId() {
        return inviteId;
    }
}