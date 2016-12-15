package org.innovateuk.ifs.management.viewmodel;

/**
 * Holder of model attributes for the invited assessors shown in the 'Invite' tab of the Invite Assessors view.
 */
public class InvitedAssessorRowViewModel extends InviteAssessorsRowViewModel {

    private String email;

    public InvitedAssessorRowViewModel(String name, String innovationArea, boolean compliant, String email) {
        super(name, innovationArea, compliant);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}