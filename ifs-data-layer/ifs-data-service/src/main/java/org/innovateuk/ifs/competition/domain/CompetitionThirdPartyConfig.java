package org.innovateuk.ifs.competition.domain;

import javax.persistence.*;

@Entity
@Table(name = "competition_third_party_config")
public class CompetitionThirdPartyConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "competitionThirdPartyConfig", fetch = FetchType.LAZY)
    private Competition competition;

    private String termsAndConditionsLabel;

    @Column(length = 5000, columnDefinition = "LONGTEXT")
    private String termsAndConditionsGuidance;

    private String projectCostGuidanceUrl;

    public CompetitionThirdPartyConfig() {
    }

    public CompetitionThirdPartyConfig(Competition competition,
                                       String termsAndConditionsLabel,
                                       String termsAndConditionsGuidance,
                                       String projectCostGuidanceUrl) {
        this.competition = competition;
        this.termsAndConditionsLabel = termsAndConditionsLabel;
        this.termsAndConditionsGuidance = termsAndConditionsGuidance;
        this.projectCostGuidanceUrl = projectCostGuidanceUrl;
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

    public String getTermsAndConditionsLabel() {
        return termsAndConditionsLabel;
    }

    public void setTermsAndConditionsLabel(String termsAndConditionsLabel) {
        this.termsAndConditionsLabel = termsAndConditionsLabel;
    }

    public String getTermsAndConditionsGuidance() {
        return termsAndConditionsGuidance;
    }

    public void setTermsAndConditionsGuidance(String termsAndConditionsGuidance) {
        this.termsAndConditionsGuidance = termsAndConditionsGuidance;
    }

    public String getProjectCostGuidanceUrl() {
        return projectCostGuidanceUrl;
    }

    public void setProjectCostGuidanceUrl(String projectCostGuidanceUrl) {
        this.projectCostGuidanceUrl = projectCostGuidanceUrl;
    }
}
