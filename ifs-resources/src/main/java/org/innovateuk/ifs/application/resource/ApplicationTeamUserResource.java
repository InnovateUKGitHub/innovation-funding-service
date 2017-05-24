package org.innovateuk.ifs.application.resource;

/**
 /**
 * Application Team User data transfer object
 */
public class ApplicationTeamUserResource {
    private String name;
    private String email;
    private String phoneNumber;
    private Boolean lead;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Boolean getLead() {
        return lead;
    }

    public void setLead(Boolean lead) {
        this.lead = lead;
    }
}
