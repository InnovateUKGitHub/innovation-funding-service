package org.innovateuk.ifs.registration.viewmodel;

import static org.innovateuk.ifs.registration.viewmodel.RegistrationViewModel.RegistrationViewModelBuilder.aRegistrationViewModel;

public class RegistrationViewModel {
    private static final String DEFAULT_PHONE_GUIDANCE = "We may use this to contact you about the application.";
    private final boolean invitee;
    private final String role;
    private final String project;
    private final String phoneGuidance;
    private final boolean externalUser;
    private final boolean addressRequired;

    public RegistrationViewModel(boolean invitee, String role, String project, String phoneGuidance, boolean externalUser, boolean addressRequired) {
        this.invitee = invitee;
        this.role = role;
        this.project = project;
        this.phoneGuidance = phoneGuidance == null ? DEFAULT_PHONE_GUIDANCE : phoneGuidance;
        this.externalUser = externalUser;
        this.addressRequired = addressRequired;
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

    public boolean isExternalUser() {
        return externalUser;
    }

    public boolean isAddressRequired() {
        return addressRequired;
    }

    public static RegistrationViewModel anInvitedUserViewModel() {
        return aRegistrationViewModel().withInvitee(true).withExternalUser(true).build();
    }
    public static final class RegistrationViewModelBuilder {
        private boolean invitee;
        private String role;
        private String project;
        private String phoneGuidance;
        private boolean externalUser;
        private boolean addressRequired;

        private RegistrationViewModelBuilder() {
        }

        public static RegistrationViewModelBuilder aRegistrationViewModel() {
            return new RegistrationViewModelBuilder();
        }

        public RegistrationViewModelBuilder withInvitee(boolean invitee) {
            this.invitee = invitee;
            return this;
        }

        public RegistrationViewModelBuilder withRole(String role) {
            this.role = role;
            return this;
        }

        public RegistrationViewModelBuilder withProject(String project) {
            this.project = project;
            return this;
        }

        public RegistrationViewModelBuilder withPhoneGuidance(String phoneGuidance) {
            this.phoneGuidance = phoneGuidance;
            return this;
        }

        public RegistrationViewModelBuilder withExternalUser(boolean externalUser) {
            this.externalUser = externalUser;
            return this;
        }

        public RegistrationViewModelBuilder withAddressRequired(boolean addressRequired) {
            this.addressRequired = addressRequired;
            return this;
        }

        public RegistrationViewModel build() {
            return new RegistrationViewModel(invitee, role, project, phoneGuidance, externalUser, addressRequired);
        }
    }
}
