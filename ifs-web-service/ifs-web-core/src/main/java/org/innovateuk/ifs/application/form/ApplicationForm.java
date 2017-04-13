package org.innovateuk.ifs.application.form;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.springframework.validation.FieldError;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

/**
 * This class is used to setup and submit the form input values. On submit the values are converted into an Form object.
 * http://stackoverflow.com/a/4511716
 */
public class ApplicationForm extends Form {

    @Valid
    private ApplicationResource application;

    private MultipartFile assessorFeedback;

    private boolean adminMode = false;

    private boolean termsAgreed;

    private boolean stateAidAgreed;

    private Long impersonateOrganisationId;

    public ApplicationForm() {
        super();
    }

    public ApplicationResource getApplication() {
        return application;
    }

    public void setApplication(ApplicationResource application) {
        this.application = application;
    }

    /** Placeholder function for mapping errors to field in Thymeleaf */
    public String getOrganisationSize() {
        return "";
    }

    /** Placeholder function for mapping errors to field in Thymeleaf */
    public void setOrganisationSize(String organisationSize) {
    }

    public boolean isAdminMode() {
        return adminMode;
    }

    public void setAdminMode(boolean adminMode) {
        this.adminMode = adminMode;
    }

    public Long getImpersonateOrganisationId() {
        return impersonateOrganisationId;
    }

    public void setImpersonateOrganisationId(Long impersonateOrganisationId) {
        this.impersonateOrganisationId = impersonateOrganisationId;
    }

    public MultipartFile getAssessorFeedback() {
        return assessorFeedback;
    }

    public void setAssessorFeedback(MultipartFile assessorFeedback) {
        this.assessorFeedback = assessorFeedback;
    }

    public boolean isTermsAgreed() {
        return termsAgreed;
    }

    public void setTermsAgreed(boolean termsAgreed) {
        this.termsAgreed = termsAgreed;
    }

    public boolean isStateAidAgreed() {
        return stateAidAgreed;
    }

    public void setStateAidAgreed(boolean stateAidAgreed) {
        this.stateAidAgreed = stateAidAgreed;
    }

    public String getRejectedValue(String fieldId){
        FieldError fieldError = getBindingResult().getFieldError("formInput[" + fieldId + "]");
        Object rejectedValue = fieldError != null ? fieldError.getRejectedValue() : null;
        return rejectedValue != null ? rejectedValue.toString() : null;
    }
}
