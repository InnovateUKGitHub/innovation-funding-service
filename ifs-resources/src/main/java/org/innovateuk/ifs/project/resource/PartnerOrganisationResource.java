package org.innovateuk.ifs.project.resource;

public class PartnerOrganisationResource {
    private Long id;

    private Long project;

    private Long organisation;

    private String organisationName;

    private boolean leadOrganisation;

    private String postcode;

    private String internationalLocation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Long organisation) {
        this.organisation = organisation;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    public boolean isLeadOrganisation() {
        return leadOrganisation;
    }

    public void setLeadOrganisation(boolean leadOrganisation) {
        this.leadOrganisation = leadOrganisation;
    }

    public Long getProject() {
        return project;
    }

    public void setProject(Long project) {
        this.project = project;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getInternationalLocation() {
        return internationalLocation;
    }

    public void setInternationalLocation(String internationalLocation) {
        this.internationalLocation = internationalLocation;
    }
}
