package org.innovateuk.ifs.competition.domain;

import javax.persistence.*;

@Entity
@Table(name = "competition_organisation_config")
public class CompetitionOrganisationConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "competitionOrganisationConfig" ,fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Competition competition;

    @Column(name = "international_organisations_allowed")
    private Boolean internationalOrganisationsAllowed;

    @Column(name = "international_lead_organisation_allowed")
    private Boolean internationalLeadOrganisationAllowed;

    private CompetitionOrganisationConfig(){
    }

    public CompetitionOrganisationConfig(Competition competition,
                                         Boolean internationalOrganisationsAllowed,
                                         Boolean internationalLeadOrganisationAllowed) {
        this.competition = competition;
        this.internationalOrganisationsAllowed = internationalOrganisationsAllowed;
        this.internationalLeadOrganisationAllowed = internationalLeadOrganisationAllowed;
    }

    public Long getId() {
        return id;
    }

    public Competition getCompetition() {
        return competition;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
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