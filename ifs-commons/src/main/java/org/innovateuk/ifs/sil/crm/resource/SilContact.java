package org.innovateuk.ifs.sil.crm.resource;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SilContact {

    private String email;
    private String lastName;
    private String firstName;
    private String title;
    private String jobTitle;
    @JsonProperty("Address")
    private SilAddress address;
    private SilOrganisation organisation;
    private final String sourceSystem = "IFS";
    private String srcSysContactId;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public SilAddress getAddress() {
        return address;
    }

    public void setAddress(SilAddress address) {
        this.address = address;
    }

    public SilOrganisation getOrganisation() {
        return organisation;
    }

    public void setOrganisation(SilOrganisation organisation) {
        this.organisation = organisation;
    }

    public String getSourceSystem() {
        return sourceSystem;
    }

    public String getSrcSysContactId() {
        return srcSysContactId;
    }

    public void setSrcSysContactId(String srcSysContactId) {
        this.srcSysContactId = srcSysContactId;
    }
}
