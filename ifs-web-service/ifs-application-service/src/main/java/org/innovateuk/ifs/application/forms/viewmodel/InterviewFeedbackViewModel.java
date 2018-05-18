package org.innovateuk.ifs.application.forms.viewmodel;

public class InterviewFeedbackViewModel {
    static String LEAD_WITH_RESPONSE_BANNER =  "Your response has been uploaded." +
            " This response will be noted by the interview panel.";
    static String LEAD_WITHOUT_RESPONSE_BANNER =  "As the lead applicant you can respond to feedback." +
            " This response will be noted by the interview panel.";
    static String COLLAB_WITH_RESPONSE_BANNER =  "The lead applicant has responded to feedback." +
            " This response will be noted by the interview panel.";
    static String COLLAB_WITHOUT_RESPONSE_BANNER =  "The lead applicant can respond to feedback." +
            " This response will be noted by the interview panel.";

    private final String responseFilename;
    private final String feedbackFilename;
    private final boolean leadApplicant;

    public InterviewFeedbackViewModel(String responseFilename, String feedbackFilename, boolean leadApplicant) {
        this.feedbackFilename = feedbackFilename;
        this.responseFilename = responseFilename;
        this.leadApplicant = leadApplicant;
    }

    public String getResponseFilename() {
        return responseFilename;
    }

    public String getFeedbackFilename() {
        return feedbackFilename;
    }

    public boolean isLeadApplicant() {
        return leadApplicant;
    }

    /* View logic methods. */
    public boolean hasResponse() {
        return responseFilename != null;
    }

    public boolean hasFeedback() {
        return feedbackFilename != null;
    }

    public String getBannerText() {
        if (isLeadApplicant()) {
            if (hasResponse()) {
                return LEAD_WITH_RESPONSE_BANNER;
            } else {
                return LEAD_WITHOUT_RESPONSE_BANNER;
            }
        } else {
            if (hasResponse()) {
                return COLLAB_WITH_RESPONSE_BANNER;
            } else {
                return COLLAB_WITHOUT_RESPONSE_BANNER;
            }
        }
    }
}
