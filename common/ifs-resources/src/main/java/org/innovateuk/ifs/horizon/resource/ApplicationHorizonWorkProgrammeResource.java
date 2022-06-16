package org.innovateuk.ifs.horizon.resource;

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
}
