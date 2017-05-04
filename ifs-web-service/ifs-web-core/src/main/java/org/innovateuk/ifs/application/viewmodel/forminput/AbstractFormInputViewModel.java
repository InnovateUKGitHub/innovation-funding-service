package org.innovateuk.ifs.application.viewmodel.forminput;


import org.innovateuk.ifs.applicant.resource.ApplicantFormInputResource;
import org.innovateuk.ifs.applicant.resource.ApplicantFormInputResponseResource;
import org.innovateuk.ifs.applicant.resource.ApplicantQuestionResource;
import org.innovateuk.ifs.applicant.resource.ApplicantResource;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputResponseResource;
import org.innovateuk.ifs.form.resource.FormInputType;

public abstract class AbstractFormInputViewModel {
    protected boolean complete;
    protected boolean readonly;
    protected ApplicantQuestionResource applicantQuestion;
    protected ApplicantFormInputResource applicantFormInput;
    protected ApplicantFormInputResponseResource applicantResponse;
    protected ApplicantResource currentApplicant;

    protected abstract FormInputType formInputType();
    public QuestionResource getQuestion() {
        return applicantQuestion.getQuestion();
    }
    public FormInputResource getFormInput() {
        return applicantFormInput.getFormInput();
    }
    public FormInputResponseResource getResponse() {
        return applicantResponse.getResponse();
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public boolean isReadonly() {
        return readonly;
    }

    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }

    public ApplicantQuestionResource getApplicantQuestion() {
        return applicantQuestion;
    }

    public void setApplicantQuestion(ApplicantQuestionResource applicantQuestion) {
        this.applicantQuestion = applicantQuestion;
    }

    public ApplicantFormInputResource getApplicantFormInput() {
        return applicantFormInput;
    }

    public void setApplicantFormInput(ApplicantFormInputResource applicantFormInput) {
        this.applicantFormInput = applicantFormInput;
    }

    public ApplicantFormInputResponseResource getApplicantResponse() {
        return applicantResponse;
    }

    public void setApplicantResponse(ApplicantFormInputResponseResource applicantResponse) {
        this.applicantResponse = applicantResponse;
    }

    public ApplicantResource getCurrentApplicant() {
        return currentApplicant;
    }

    public void setCurrentApplicant(ApplicantResource currentApplicant) {
        this.currentApplicant = currentApplicant;
    }

    public boolean getHasResponse() {
        return applicantResponse != null;
    }


}
