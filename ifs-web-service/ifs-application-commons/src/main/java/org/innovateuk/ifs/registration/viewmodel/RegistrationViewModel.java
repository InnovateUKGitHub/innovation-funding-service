package org.innovateuk.ifs.registration.viewmodel;

public class RegistrationViewModel {

    private static final String DEFAULT_PHONE_GUIDANCE = "We may use this number to contact you about the application.";
    private final boolean invitee;
    private final String role;
    private final String project;
    private final String phoneGuidance;

    public RegistrationViewModel(boolean invitee, String role, String project, String phoneGuidance) {
        this.invitee = invitee;
        this.role = role;
        this.project = project;
        this.phoneGuidance = phoneGuidance != null ? phoneGuidance : DEFAULT_PHONE_GUIDANCE;
    }

    public RegistrationViewModel(boolean invitee) {
        this(invitee, null, null, null);
    }

    public static RegistrationViewModel anInvitedUserViewModel() {
        return new RegistrationViewModel(true);
    }

    public boolean isInvitee() {
        return invitee;
    }

    public String getRole() {
        return role;
    }

    public String getProject() {
        return project;
    }

    public String getPhoneGuidance() {
        return phoneGuidance;
    }
}
