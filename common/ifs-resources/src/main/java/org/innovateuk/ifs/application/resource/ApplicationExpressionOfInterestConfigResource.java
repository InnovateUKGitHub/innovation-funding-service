package org.innovateuk.ifs.application.resource;

import lombok.Builder;

@Builder
public class ApplicationExpressionOfInterestConfigResource {

    private Long id;

    private Long applicationId;

    private boolean enabledForExpressionOfInterest;

    private Long eoiApplicationId;

    public ApplicationExpressionOfInterestConfigResource() {
    }

    public ApplicationExpressionOfInterestConfigResource(Long id, Long applicationId, boolean enabledForExpressionOfInterest, Long eoiApplicationId) {
        this.id = id;
        this.applicationId = applicationId;
        this.enabledForExpressionOfInterest = enabledForExpressionOfInterest;
        this.eoiApplicationId = eoiApplicationId;
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

    public Long getEoiApplicationId() {
        return eoiApplicationId;
    }

    public void setEoiApplicationId(Long eoiApplicationId) {
        this.eoiApplicationId = eoiApplicationId;
    }
}
