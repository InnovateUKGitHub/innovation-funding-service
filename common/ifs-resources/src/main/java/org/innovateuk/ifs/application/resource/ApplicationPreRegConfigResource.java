package org.innovateuk.ifs.application.resource;

public class ApplicationPreRegConfigResource {

    private Long id;

    private boolean enableForEOI;

    private Long applicationId;

    public ApplicationPreRegConfigResource() {
    }

    public ApplicationPreRegConfigResource(Long id, boolean enableForEOI, Long applicationId) {
        this.id = id;
        this.enableForEOI = enableForEOI;
        this.applicationId = applicationId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isEnableForEOI() {
        return enableForEOI;
    }

    public void setEnableForEOI(boolean enableForEOI) {
        this.enableForEOI = enableForEOI;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }
}
