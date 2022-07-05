package org.innovateuk.ifs.horizon.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class CompetitionHorizonWorkProgrammeResource {

    private Long competitionId;
    private HorizonWorkProgrammeResource workProgramme;

    public CompetitionHorizonWorkProgrammeResource() {
    }

    public CompetitionHorizonWorkProgrammeResource(Long competitionId, HorizonWorkProgrammeResource workProgramme) {
        this.competitionId = competitionId;
        this.workProgramme = workProgramme;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(Long competitionId) {
        this.competitionId = competitionId;
    }

    public HorizonWorkProgrammeResource getWorkProgramme() {
        return workProgramme;
    }

    public void setWorkProgramme(HorizonWorkProgrammeResource workProgramme) {
        this.workProgramme = workProgramme;
    }

    @JsonIgnore
    public boolean isCallerId() {
        return getWorkProgramme() != null && getWorkProgramme().isCallerId();
    }
}
