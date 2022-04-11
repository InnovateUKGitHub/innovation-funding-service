package org.innovateuk.ifs.horizon.resource;

public class ApplicationHorizonWorkProgrammeResource {

    private Long applicationId;
    private String workProgramme;

    public ApplicationHorizonWorkProgrammeResource() {
    }

    public ApplicationHorizonWorkProgrammeResource(Long applicationId, String workProgramme) {
        this.applicationId = applicationId;
        this.workProgramme = workProgramme;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public String getWorkProgramme() {
        return workProgramme;
    }

    public void setWorkProgramme(String workProgramme) {
        this.workProgramme = workProgramme;
    }
}
