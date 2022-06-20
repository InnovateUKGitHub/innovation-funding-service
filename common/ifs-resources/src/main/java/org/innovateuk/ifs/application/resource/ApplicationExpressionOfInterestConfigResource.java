package org.innovateuk.ifs.application.resource;

public class ApplicationExpressionOfInterestConfigResource {

    private Long id;

    private Long applicationId;

    private boolean enabledForExpressionOfInterest;

    public ApplicationExpressionOfInterestConfigResource() {
    }

    public ApplicationExpressionOfInterestConfigResource(Long id, Long applicationId, boolean enabledForExpressionOfInterest) {
        this.id = id;
        this.applicationId = applicationId;
        this.enabledForExpressionOfInterest = enabledForExpressionOfInterest;
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

    public boolean isEnabledForExpressionOfInterest() {
        return enabledForExpressionOfInterest;
    }

    public void setEnabledForExpressionOfInterest(boolean enabledForExpressionOfInterest) {
        this.enabledForExpressionOfInterest = enabledForExpressionOfInterest;
    }
}
