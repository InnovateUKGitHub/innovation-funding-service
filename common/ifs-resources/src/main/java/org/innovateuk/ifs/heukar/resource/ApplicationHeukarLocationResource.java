package org.innovateuk.ifs.heukar.resource;

public class ApplicationHeukarLocationResource {
    private Long applicationId;
    private String location;

    public ApplicationHeukarLocationResource() {
    }

    public ApplicationHeukarLocationResource(Long applicationId, String location) {
        this.applicationId = applicationId;
        this.location = location;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
