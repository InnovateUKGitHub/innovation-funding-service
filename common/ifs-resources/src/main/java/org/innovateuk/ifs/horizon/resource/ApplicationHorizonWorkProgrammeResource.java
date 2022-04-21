package org.innovateuk.ifs.horizon.resource;

public class ApplicationHorizonWorkProgrammeResource {

    private Long applicationId;
    private HorizonWorkProgramme workProgramme;

    public ApplicationHorizonWorkProgrammeResource() {
    }

    public ApplicationHorizonWorkProgrammeResource(Long applicationId, HorizonWorkProgramme workProgramme) {
        this.applicationId = applicationId;
        this.workProgramme = workProgramme;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public HorizonWorkProgramme getWorkProgramme() {
        return workProgramme;
    }

    public void setWorkProgramme(HorizonWorkProgramme workProgramme) {
        this.workProgramme = workProgramme;
    }
}
