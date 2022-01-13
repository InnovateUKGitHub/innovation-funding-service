package org.innovateuk.ifs.competition.resource;

public class CompetitionExternalConfigResource {

    private Long id;

    private String externalCompetitionId;

    public CompetitionExternalConfigResource() {

    }

    public CompetitionExternalConfigResource(String externalCompetitionId) {
        this.externalCompetitionId = externalCompetitionId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExternalCompetitionId() {
        return externalCompetitionId;
    }

    public void setExternalCompetitionId(String externalCompetitionId) {
        this.externalCompetitionId = externalCompetitionId;
    }
}
