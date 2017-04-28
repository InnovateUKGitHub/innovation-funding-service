package org.innovateuk.ifs.applicant.resource;

import org.innovateuk.ifs.form.resource.FormInputResource;

import java.util.List;

/**
 * Created by luke.harper on 25/04/2017.
 */
public class ApplicantFormInputResource {

    private FormInputResource formInput;

    private List<ApplicantFormInputResponseResource> applicantResponses;

    public FormInputResource getFormInput() {
        return formInput;
    }

    public void setFormInput(FormInputResource formInput) {
        this.formInput = formInput;
    }

    public List<ApplicantFormInputResponseResource> getApplicantResponses() {
        return applicantResponses;
    }

    public void setApplicantResponses(List<ApplicantFormInputResponseResource> applicantResponses) {
        this.applicantResponses = applicantResponses;
    }
}
