package org.innovateuk.ifs.competition.resource;

public class CompetitionOrganisationConfigResource {

    private Long id;
    private Long competitionId;
    private Boolean internationalOrganisationsAllowed;

    public CompetitionOrganisationConfigResource(Long competitionId, Boolean internationalOrganisationsAllowed) {
        this.competitionId = competitionId;
        this.internationalOrganisationsAllowed = internationalOrganisationsAllowed;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(Long competitionId) {
        this.competitionId = competitionId;
    }

    public Boolean getInternationalOrganisationsAllowed() {
        return internationalOrganisationsAllowed;
    }

    public void setInternationalOrganisationsAllowed(Boolean internationalOrganisationsAllowed) {
        this.internationalOrganisationsAllowed = internationalOrganisationsAllowed;
    }
}
