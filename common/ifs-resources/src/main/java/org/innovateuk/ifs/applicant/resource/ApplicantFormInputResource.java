package org.innovateuk.ifs.applicant.resource;

import org.innovateuk.ifs.form.resource.FormInputResource;

import java.util.List;

/**
 * Rich resource for a form input.
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

    public ApplicantFormInputResponseResource responseForApplicant(ApplicantResource applicantResource, ApplicantQuestionResource questionResource) {
        return applicantResponses.stream().filter(response ->
                !questionResource.getQuestion().hasMultipleStatuses() ||
                response.getApplicant().hasSameOrganisation(applicantResource)).findAny().orElse(null);
    }
}
