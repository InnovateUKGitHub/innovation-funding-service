package org.innovateuk.ifs.competition.resource;

public class CompetitionOrganisationConfigResource {

    private Long id;
    private Boolean internationalOrganisationsAllowed;

    public CompetitionOrganisationConfigResource(Boolean internationalOrganisationsAllowed) {
        this.internationalOrganisationsAllowed = internationalOrganisationsAllowed;
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
}
