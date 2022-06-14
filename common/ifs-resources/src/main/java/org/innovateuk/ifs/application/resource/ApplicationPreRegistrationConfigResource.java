package org.innovateuk.ifs.application.resource;

public class ApplicationPreRegistrationConfigResource {

    private Long id;

    private Long applicationId;

    private boolean enableForEOI;

    public ApplicationPreRegistrationConfigResource() {
    }

    public ApplicationPreRegistrationConfigResource(Long id, Long applicationId, boolean enableForEOI) {
        this.id = id;
        this.applicationId = applicationId;
        this.enableForEOI = enableForEOI;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public boolean isEnableForEOI() {
        return enableForEOI;
    }

    public void setEnableForEOI(boolean enableForEOI) {
        this.enableForEOI = enableForEOI;
    }
}
