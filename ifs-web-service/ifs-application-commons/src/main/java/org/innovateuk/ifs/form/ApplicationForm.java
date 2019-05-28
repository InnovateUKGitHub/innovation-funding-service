package org.innovateuk.ifs.form;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.springframework.validation.FieldError;
import org.springframework.web.multipart.MultipartFile;

/**
 * This class is used to setup and submit the form input values. On submit the values are converted into an Form object.
 * http://stackoverflow.com/a/4511716
 */
public class ApplicationForm extends Form {

    private ApplicationResource application;

    private boolean adminMode = false;

    private boolean termsAgreed;

    private boolean stateAidAgreed;

    private Long impersonateOrganisationId;

    private MultipartFile overheadfile;
    private MultipartFile jesFileUpload;

    public ApplicationForm() {
        super();
    }

    public ApplicationResource getApplication() {
        return application;
    }

    public void setApplication(ApplicationResource application) {
        this.application = application;
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

    public MultipartFile getOverheadfile() {
        return overheadfile;
    }

    public void setOverheadfile(MultipartFile overheadfile) {
        this.overheadfile = overheadfile;
    }

    public MultipartFile getJesFileUpload() {
        return jesFileUpload;
    }

    public void setJesFileUpload(MultipartFile jesFileUpload) {
        this.jesFileUpload = jesFileUpload;
    }
}