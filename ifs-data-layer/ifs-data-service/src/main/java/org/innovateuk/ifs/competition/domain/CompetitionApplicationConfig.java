package org.innovateuk.ifs.competition.domain;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "competition_application_config")
public class CompetitionApplicationConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "competitionOrganisationConfig",fetch = FetchType.LAZY)
    private Competition competition;

    @Column
    private BigDecimal maximumFundingSought;

    public CompetitionApplicationConfig() {
    }

    public CompetitionApplicationConfig(Competition competition,
                                        BigDecimal maximumFundingSought) {
        this.competition = competition;
        this.maximumFundingSought = maximumFundingSought;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Competition getCompetition() {
        return competition;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }

    public BigDecimal getMaximumFundingSought() {
        return maximumFundingSought;
    }

    public void setMaximumFundingSought(BigDecimal maximumFundingSought) {
        this.maximumFundingSought = maximumFundingSought;
    }
}