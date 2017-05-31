package org.innovateuk.ifs.applicant.resource;

import org.innovateuk.ifs.form.resource.FormInputResponseResource;

/**
 * Rich resource for a form input response.
 */
public class ApplicantFormInputResponseResource {

    private FormInputResponseResource response;

    private ApplicantResource applicant;

    public FormInputResponseResource getResponse() {
        return response;
    }

    public void setResponse(FormInputResponseResource response) {
        this.response = response;
    }

    public ApplicantResource getApplicant() {
        return applicant;
    }

    public void setApplicant(ApplicantResource applicant) {
        this.applicant = applicant;
    }
}
