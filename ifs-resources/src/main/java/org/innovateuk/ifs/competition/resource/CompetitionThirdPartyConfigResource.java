package org.innovateuk.ifs.competition.resource;

public class CompetitionThirdPartyConfigResource {
    private Long id;
    private String termsAndConditionsLabel;
    private String termsAndConditionsGuidance;
    private String projectCostGuidanceUrl;
    private Long competitionId;

    public CompetitionThirdPartyConfigResource() {
    }

    public CompetitionThirdPartyConfigResource(String termsAndConditionsLabel, String termsAndConditionsGuidance, String projectCostGuidanceUrl) {
        this.termsAndConditionsLabel = termsAndConditionsLabel;
        this.termsAndConditionsGuidance = termsAndConditionsGuidance;
        this.projectCostGuidanceUrl = projectCostGuidanceUrl;
    }

    public CompetitionThirdPartyConfigResource(Long competitionId, String termsAndConditionsLabel, String termsAndConditionsGuidance, String projectCostGuidanceUrl) {
        this.competitionId = competitionId;
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

    public Long getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(Long competitionId) {
        this.competitionId = competitionId;
    }
}
