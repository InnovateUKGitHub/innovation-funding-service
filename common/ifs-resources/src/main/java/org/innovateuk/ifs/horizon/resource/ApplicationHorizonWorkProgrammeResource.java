package org.innovateuk.ifs.horizon.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ApplicationHorizonWorkProgrammeResource {

    private Long applicationId;
    private HorizonWorkProgrammeResource workProgramme;

    public ApplicationHorizonWorkProgrammeResource() {
    }

    public ApplicationHorizonWorkProgrammeResource(Long applicationId, HorizonWorkProgrammeResource workProgramme) {
        this.applicationId = applicationId;
        this.workProgramme = workProgramme;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
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
