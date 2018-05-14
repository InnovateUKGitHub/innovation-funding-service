package org.innovateuk.ifs.application.forms.viewmodel;

public class InterviewFeedbackViewModel {

    private final String responseFilename;
    private final boolean leadApplicant;


    public InterviewFeedbackViewModel(String applicantFeedbackFilename, boolean leadApplicant) {
        this.responseFilename = applicantFeedbackFilename;
        this.leadApplicant = leadApplicant;
    }

    public String getResponseFilename() {
        return responseFilename;
    }

    public boolean isLeadApplicant() {
        return leadApplicant;
    }

    /* View logic methods. */
    public boolean hasAttachment() {
        return responseFilename != null;
    }
}
