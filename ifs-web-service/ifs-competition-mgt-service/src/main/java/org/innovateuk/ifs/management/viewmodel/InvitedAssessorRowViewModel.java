package org.innovateuk.ifs.management.viewmodel;

import org.innovateuk.ifs.category.resource.InnovationAreaResource;

import java.util.List;

/**
 * Holder of model attributes for the invited assessors shown in the 'Invite' tab of the Invite Assessors view.
 */
public class InvitedAssessorRowViewModel extends InviteAssessorsRowViewModel {

    private String email;
    private long inviteId;

    public InvitedAssessorRowViewModel(String name, List<InnovationAreaResource> innovationArea, boolean compliant, String email, long inviteId) {
        super(name, innovationArea, compliant);
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