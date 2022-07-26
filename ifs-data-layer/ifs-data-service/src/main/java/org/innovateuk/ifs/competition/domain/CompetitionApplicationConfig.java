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

    @Column(columnDefinition = "double")
    private BigDecimal maximumFundingSought;

    private Boolean maximumFundingSoughtEnabled;

    @Column(name="im_survey_required")
    private boolean imSurveyRequired = false;

    public CompetitionApplicationConfig() {
    }

    public CompetitionApplicationConfig(Competition competition,
                                        BigDecimal maximumFundingSought,
                                        Boolean maximumFundingSoughtEnabled,
                                        boolean imSurveyRequired) {
        this.competition = competition;
        this.maximumFundingSought = maximumFundingSought;
        this.maximumFundingSoughtEnabled = maximumFundingSoughtEnabled;
        this.imSurveyRequired = imSurveyRequired;
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

    public Boolean isMaximumFundingSoughtEnabled() {
        return maximumFundingSoughtEnabled;
    }

    public void setMaximumFundingSoughtEnabled(Boolean maximumFundingSoughtEnabled) {
        this.maximumFundingSoughtEnabled = maximumFundingSoughtEnabled;
    }

    public boolean isImSurveyRequired() {
        return imSurveyRequired;
    }

    public void setImSurveyRequired(boolean imSurveyRequired) {
        this.imSurveyRequired = imSurveyRequired;
    }
}