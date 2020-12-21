package org.innovateuk.ifs.competition.domain;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "competition_application_config")
public class CompetitionApplicationConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "competitionApplicationConfig",fetch = FetchType.LAZY)
    private Competition competition;

    @Column
    private BigDecimal maximumFundingSought;

    private Boolean alwaysOpen;

    public CompetitionApplicationConfig() {
    }

    public CompetitionApplicationConfig(Competition competition,
                                        BigDecimal maximumFundingSought,
                                        Boolean alwaysOpen) {
        this.competition = competition;
        this.maximumFundingSought = maximumFundingSought;
        this.alwaysOpen = alwaysOpen;
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

    public Boolean getAlwaysOpen() {
        return alwaysOpen;
    }

    public void setAlwaysOpen(Boolean alwaysOpen) {
        this.alwaysOpen = alwaysOpen;
    }
}