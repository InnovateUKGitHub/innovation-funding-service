package org.innovateuk.ifs.competition.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

    /* two methods to handle null cases in applicant journey. */
    @JsonIgnore
    public boolean areInternationalApplicantsAllowed() {
        return Boolean.TRUE.equals(getInternationalOrganisationsAllowed();
    }

    @JsonIgnore
    public boolean cantInternationalApplicantsLead() {
        return Boolean.TRUE.equals(getInternationalLeadOrganisationAllowed();
    }


}