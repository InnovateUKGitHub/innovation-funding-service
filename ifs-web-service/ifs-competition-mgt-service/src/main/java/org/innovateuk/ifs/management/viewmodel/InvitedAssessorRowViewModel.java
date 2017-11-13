package org.innovateuk.ifs.management.viewmodel;

import org.innovateuk.ifs.category.resource.InnovationAreaResource;

import java.util.List;

/**
 * Holder of model attributes for the invited assessors shown in the 'Invite' tab of the Invite Assessors view.
 */
public class InvitedAssessorRowViewModel extends InviteAssessorsRowViewModel {

    private String email;
    private long inviteId;

    public InvitedAssessorRowViewModel(long id, String name, List<InnovationAreaResource> innovationAreas, boolean compliant, String email, long inviteId) {
        super(id, name, innovationAreas, compliant);
        this.email = email;
        this.inviteId = inviteId;
    }

    public String getEmail() {
        return email;
    }

    public long getInviteId() {
        return inviteId;
    }
}