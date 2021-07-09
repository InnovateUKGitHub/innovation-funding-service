package org.innovateuk.ifs.application.resource;

public class ApplicationExternalConfigResource {

    private Long id;

    private String externalApplicationId;

    private String externalApplicantName;

    public ApplicationExternalConfigResource() {

    }

    public ApplicationExternalConfigResource(String externalApplicationId, String externalApplicantName) {
        this.externalApplicationId = externalApplicationId;
        this.externalApplicantName = externalApplicantName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExternalApplicationId() {
        return externalApplicationId;
    }

    public void setExternalApplicationId(String externalApplicationId) {
        this.externalApplicationId = externalApplicationId;
    }

    public String getExternalApplicantName() {
        return externalApplicantName;
    }

    public void setExternalApplicantName(String externalApplicantName) {
        this.externalApplicantName = externalApplicantName;
    }
}
