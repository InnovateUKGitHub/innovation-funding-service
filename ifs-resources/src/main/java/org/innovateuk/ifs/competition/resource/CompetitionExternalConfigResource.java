package org.innovateuk.ifs.competition.resource;

public class CompetitionExternalConfigResource {

    private String externalCompetitionId;

    public CompetitionExternalConfigResource() {

    }

    public CompetitionExternalConfigResource(String externalCompetitionId) {
        this.externalCompetitionId = externalCompetitionId;
    }

    public String getExternalCompetitionId() {
        return externalCompetitionId;
    }

    public void setExternalCompetitionId(String externalCompetitionId) {
        this.externalCompetitionId = externalCompetitionId;
    }
}
