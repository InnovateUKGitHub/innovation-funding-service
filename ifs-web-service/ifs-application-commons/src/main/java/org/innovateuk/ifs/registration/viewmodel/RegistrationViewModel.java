package org.innovateuk.ifs.registration.viewmodel;

public class RegistrationViewModel {
    private static final String DEFAULT_PHONE_GUIDANCE = "We may use this to contact you about the application.";
    private static final String DEFAULT_BUTTON_TEXT = "Create account";
    private static final String DEFAULT_PAGE_TITLE = "Your details";
    private static final String DEFAULT_SUB_TITLE = "Create your account";
    private String pageTitle;
    private String subTitle;
    private final boolean invitee;
    private final String role;
    private final String project;
    private final String phoneGuidance;
    private final String buttonText;
    private final boolean phoneRequired;
    private final boolean termsRequired;
    private final boolean addressRequired;

    public RegistrationViewModel(String pageTitle, String subTitle, boolean invitee, String role, String project, String phoneGuidance, String buttonText, boolean phoneRequired, boolean termsRequired, boolean addressRequired) {
        this.pageTitle = pageTitle == null ? DEFAULT_PAGE_TITLE : pageTitle;
        this.subTitle = subTitle == null ? DEFAULT_SUB_TITLE : subTitle;
        this.invitee = invitee;
        this.role = role;
        this.project = project;
        this.phoneGuidance = phoneGuidance == null ? DEFAULT_PHONE_GUIDANCE : phoneGuidance;
        this.buttonText = buttonText == null ? DEFAULT_BUTTON_TEXT : buttonText;
        this.phoneRequired = phoneRequired;
        this.termsRequired = termsRequired;
        this.addressRequired = addressRequired;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public String getSubTitle() {
        return subTitle;
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

    public String getButtonText() {
        return buttonText;
    }

    public boolean isPhoneRequired() {
        return phoneRequired;
    }

    public boolean isTermsRequired() {
        return termsRequired;
    }

    public boolean isAddressRequired() {
        return addressRequired;
    }

    public static RegistrationViewModel anInvitedUserViewModel() {
        return RegistrationViewModelBuilder.aRegistrationViewModel().withInvitee(true).withPhoneRequired(true).withTermsRequired(true).build();
    }

    public static final class RegistrationViewModelBuilder {
        private String pageTitle;
        private String subTitle;
        private boolean invitee;
        private String role;
        private String project;
        private String phoneGuidance;
        private String buttonText;
        private boolean phoneRequired;
        private boolean termsRequired;
        private boolean addressRequired;

        private RegistrationViewModelBuilder() {
        }

        public static RegistrationViewModelBuilder aRegistrationViewModel() {
            return new RegistrationViewModelBuilder();
        }

        public RegistrationViewModelBuilder withPageTitle(String pageTitle) {
            this.pageTitle = pageTitle;
            return this;
        }

        public RegistrationViewModelBuilder withSubTitle(String subTitle) {
            this.subTitle = subTitle;
            return this;
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

        public RegistrationViewModelBuilder withButtonText(String buttonText) {
            this.buttonText = buttonText;
            return this;
        }

        public RegistrationViewModelBuilder withPhoneRequired(boolean phoneRequired) {
            this.phoneRequired = phoneRequired;
            return this;
        }

        public RegistrationViewModelBuilder withTermsRequired(boolean termsRequired) {
            this.termsRequired = termsRequired;
            return this;
        }

        public RegistrationViewModelBuilder withAddressRequired(boolean addressRequired) {
            this.addressRequired = addressRequired;
            return this;
        }

        public RegistrationViewModel build() {
            return new RegistrationViewModel(pageTitle, subTitle, invitee, role, project, phoneGuidance, buttonText, phoneRequired, termsRequired, addressRequired);
        }
    }
}
