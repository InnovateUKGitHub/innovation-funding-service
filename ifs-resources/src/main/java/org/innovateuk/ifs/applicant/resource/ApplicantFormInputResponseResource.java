package org.innovateuk.ifs.applicant.resource;

import org.innovateuk.ifs.form.resource.FormInputResponseResource;

/**
 * Created by luke.harper on 25/04/2017.
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
