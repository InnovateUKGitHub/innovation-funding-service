package org.innovateuk.ifs.competition.resource;

public class CompetitionOrganisationConfigResource {

    private Long id;
    private Boolean internationalOrganisationsAllowed;
    private Boolean internationalLeadOrganisationAllowed;

    public CompetitionOrganisationConfigResource() {
    }

    public CompetitionOrganisationConfigResource(Boolean internationalOrganisationsAllowed, Boolean internationalLeadOrganisationAllowed) {
        this.internationalOrganisationsAllowed = internationalOrganisationsAllowed;
        this.internationalLeadOrganisationAllowed = internationalLeadOrganisationAllowed;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getInternationalOrganisationsAllowed() {
        return internationalOrganisationsAllowed;
    }

    public void setInternationalOrganisationsAllowed(Boolean internationalOrganisationsAllowed) {
        this.internationalOrganisationsAllowed = internationalOrganisationsAllowed;
    }

    public Boolean getInternationalLeadOrganisationAllowed() {
        return internationalLeadOrganisationAllowed;
    }

    public void setInternationalLeadOrganisationAllowed(Boolean internationalLeadOrganisationAllowed) {
        this.internationalLeadOrganisationAllowed = internationalLeadOrganisationAllowed;
    }
}