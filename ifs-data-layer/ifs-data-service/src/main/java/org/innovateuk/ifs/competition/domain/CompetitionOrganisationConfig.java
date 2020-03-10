package org.innovateuk.ifs.competition.domain;

import javax.persistence.*;

@Entity
@Table(name = "competition_organisation_config")
public class CompetitionOrganisationConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name="competition_id")
    private Competition competition;

    @Column(name = "international_application_allowed")
    private Boolean internationalApplicationAllowed;

    public Competition getCompetition() {
        return competition;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }

    public Boolean getInternationalApplicationAllowed() {
        return internationalApplicationAllowed;
    }

    public void setInternationalApplicationAllowed(Boolean internationalApplicationAllowed) {
        this.internationalApplicationAllowed = internationalApplicationAllowed;
    }
}
