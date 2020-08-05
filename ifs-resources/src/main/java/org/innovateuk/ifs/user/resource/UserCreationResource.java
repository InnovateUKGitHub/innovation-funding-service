package org.innovateuk.ifs.user.resource;

import org.innovateuk.ifs.address.resource.AddressResource;

import static com.google.common.collect.Lists.newArrayList;
import static org.innovateuk.ifs.user.resource.Role.IFS_ADMINISTRATOR;
import static org.innovateuk.ifs.user.resource.Role.PROJECT_FINANCE;

public class UserCreationResource {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String password;
    private boolean allowMarketingEmails;
    private Role role;

    private AddressResource address;

    private String inviteHash;

    private Long organisationId;
    private Long competitionId;

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

    public UserResource toUserResource() {
        UserResource user = new UserResource();

        if (role == IFS_ADMINISTRATOR) {
            user.setRoles(newArrayList(IFS_ADMINISTRATOR, PROJECT_FINANCE));
        } else {
            user.setRoles(newArrayList(role));
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
        private Role role;
        private AddressResource address;
        private String inviteHash;
        private Long organisationId;
        private Long competitionId;

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

        public UserCreationResource build() {
            UserCreationResource userCreationResource = new UserCreationResource();
            userCreationResource.setFirstName(firstName);
            userCreationResource.setLastName(lastName);
            userCreationResource.setEmail(email);
            userCreationResource.setPhoneNumber(phoneNumber);
            userCreationResource.setPassword(password);
            userCreationResource.setAllowMarketingEmails(allowMarketingEmails);
            userCreationResource.setRole(role);
            userCreationResource.setAddress(address);
            userCreationResource.setInviteHash(inviteHash);
            userCreationResource.setOrganisationId(organisationId);
            userCreationResource.setCompetitionId(competitionId);
            return userCreationResource;
        }
    }
}