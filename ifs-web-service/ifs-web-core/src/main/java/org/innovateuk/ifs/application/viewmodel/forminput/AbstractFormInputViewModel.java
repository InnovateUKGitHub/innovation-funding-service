package org.innovateuk.ifs.application.viewmodel.forminput;


import org.innovateuk.ifs.applicant.resource.*;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputResponseResource;
import org.innovateuk.ifs.form.resource.FormInputType;

/**
 * Abstract class for all form input view models.
 */
public abstract class AbstractFormInputViewModel {
    protected boolean summary;
    protected boolean closed;
    protected boolean complete;
    protected boolean readonly;
    protected ApplicantSectionResource applicantSection;
    protected ApplicantQuestionResource applicantQuestion;
    protected ApplicantFormInputResource applicantFormInput;
    protected ApplicantFormInputResponseResource applicantResponse;
    protected ApplicantResource currentApplicant;

    protected abstract FormInputType formInputType();
    public SectionResource getSection() {
        return applicantSection.getSection();
    }
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

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public boolean isSummary() {
        return summary;
    }

    public void setSummary(boolean summary) {
        this.summary = summary;
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

    public ApplicantSectionResource getApplicantSection() {
        return applicantSection;
    }

    public void setApplicantSection(ApplicantSectionResource applicantSection) {
        this.applicantSection = applicantSection;
    }

    public boolean getHasResponse() {
        return applicantResponse != null;
    }


}
