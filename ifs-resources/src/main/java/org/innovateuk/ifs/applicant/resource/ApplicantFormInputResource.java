package org.innovateuk.ifs.applicant.resource;

import org.innovateuk.ifs.form.resource.FormInputResource;

/**
 * Created by luke.harper on 25/04/2017.
 */
public class ApplicantFormInputResource {

    private FormInputResource formInput;

    private ApplicantFormInputResponseResource response;

    public FormInputResource getFormInput() {
        return formInput;
    }

    public void setFormInput(FormInputResource formInput) {
        this.formInput = formInput;
    }

    public ApplicantFormInputResponseResource getResponse() {
        return response;
    }

    public void setResponse(ApplicantFormInputResponseResource response) {
        this.response = response;
    }
}
