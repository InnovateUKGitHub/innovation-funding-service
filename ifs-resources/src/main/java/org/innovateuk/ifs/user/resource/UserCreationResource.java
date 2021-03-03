package org.innovateuk.ifs.user.resource;

import org.innovateuk.ifs.address.resource.AddressResource;

import static com.google.common.collect.Lists.newArrayList;
import static org.innovateuk.ifs.user.resource.Role.*;

public class UserCreationResource {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String password;
    private boolean allowMarketingEmails;
    private boolean agreedTerms;
    private Role role;

    private AddressResource address;

    private String inviteHash;

    private Long organisationId;
    private Long competitionId;

    private boolean addLiveProjectUserRole;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAllowMarketingEmails() {
        return allowMarketingEmails;
    }

    public void setAllowMarketingEmails(boolean allowMarketingEmails) {
        this.allowMarketingEmails = allowMarketingEmails;
    }

    public boolean isAgreedTerms() {
        return agreedTerms;
    }

    public void setAgreedTerms(boolean agreedTerms) {
        this.agreedTerms = agreedTerms;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public AddressResource getAddress() {
        return address;
    }

    public void setAddress(AddressResource address) {
        this.address = address;
    }

    public String getInviteHash() {
        return inviteHash;
    }

    public void setInviteHash(String inviteHash) {
        this.inviteHash = inviteHash;
    }

    public Long getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(Long organisationId) {
        this.organisationId = organisationId;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(Long competitionId) {
        this.competitionId = competitionId;
    }

    public boolean isAddLiveProjectUserRole() {
        return addLiveProjectUserRole;
    }

    public void setAddLiveProjectUserRole(boolean addLiveProjectUserRole) {
        this.addLiveProjectUserRole = addLiveProjectUserRole;
    }

    public UserResource toUserResource() {
        UserResource user = new UserResource();

        user.setRoles(newArrayList(role));

        if (addLiveProjectUserRole) {
            user.getRoles().add(LIVE_PROJECTS_USER);
        }

        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhoneNumber(phoneNumber);
        user.setAllowMarketingEmails(allowMarketingEmails);

        return user;
    }

    public static final class UserCreationResourceBuilder {
        private String firstName;
        private String lastName;
        private String email;
        private String phoneNumber;
        private String password;
        private boolean allowMarketingEmails;
        private boolean agreedTerms;
        private Role role;
        private AddressResource address;
        private String inviteHash;
        private Long organisationId;
        private Long competitionId;
        private boolean addLiveProjectUserRole;

        private UserCreationResourceBuilder() {
        }

        public static UserCreationResourceBuilder anUserCreationResource() {
            return new UserCreationResourceBuilder();
        }

        public UserCreationResourceBuilder withFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public UserCreationResourceBuilder withLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public UserCreationResourceBuilder withEmail(String email) {
            this.email = email;
            return this;
        }

        public UserCreationResourceBuilder withPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public UserCreationResourceBuilder withPassword(String password) {
            this.password = password;
            return this;
        }

        public UserCreationResourceBuilder withAllowMarketingEmails(boolean allowMarketingEmails) {
            this.allowMarketingEmails = allowMarketingEmails;
            return this;
        }

        public UserCreationResourceBuilder withAgreedTerms(boolean agreedTerms) {
            this.agreedTerms = agreedTerms;
            return this;
        }

        public UserCreationResourceBuilder withRole(Role role) {
            this.role = role;
            return this;
        }

        public UserCreationResourceBuilder withAddress(AddressResource address) {
            this.address = address;
            return this;
        }

        public UserCreationResourceBuilder withInviteHash(String inviteHash) {
            this.inviteHash = inviteHash;
            return this;
        }

        public UserCreationResourceBuilder withOrganisationId(Long organisationId) {
            this.organisationId = organisationId;
            return this;
        }

        public UserCreationResourceBuilder withCompetitionId(Long competitionId) {
            this.competitionId = competitionId;
            return this;
        }

        public UserCreationResourceBuilder withAddLiveProjectUserRole(boolean addLiveProjectUserRole) {
            this.addLiveProjectUserRole = addLiveProjectUserRole;
            return this;
        }

        public UserCreationResource build() {
            UserCreationResource userCreationResource = new UserCreationResource();
            userCreationResource.setFirstName(firstName);
            userCreationResource.setLastName(lastName);
            userCreationResource.setEmail(email);
            userCreationResource.setPhoneNumber(phoneNumber);
            userCreationResource.setPassword(password);
            userCreationResource.setAllowMarketingEmails(allowMarketingEmails);
            userCreationResource.setAgreedTerms(agreedTerms);
            userCreationResource.setRole(role);
            userCreationResource.setAddress(address);
            userCreationResource.setInviteHash(inviteHash);
            userCreationResource.setOrganisationId(organisationId);
            userCreationResource.setCompetitionId(competitionId);
            userCreationResource.setAddLiveProjectUserRole(addLiveProjectUserRole);
            return userCreationResource;
        }
    }
}