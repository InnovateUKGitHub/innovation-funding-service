package org.innovateuk.ifs.application.feedback.viewmodel;

public class InterviewFeedbackViewModel {
    static final String LEAD_WITH_RESPONSE_BANNER =  "Your response has been uploaded." +
            " This response will be noted by the interview panel.";
    static final String LEAD_WITHOUT_RESPONSE_BANNER =  "As the lead applicant you can respond to feedback." +
            " This response will be noted by the interview panel.";
    static final String COLLAB_WITH_RESPONSE_BANNER =  "The lead applicant has responded to feedback." +
            " This response will be noted by the interview panel.";
    static final String COLLAB_WITHOUT_RESPONSE_BANNER =  "The lead applicant can respond to feedback." +
            " This response will be noted by the interview panel.";
    static final String ASSESSOR_WITH_RESPONSE_BANNER =  "The lead applicant has responded to feedback." +
            " Download and review all attachments before the interview panel.";
    static final String ASSESSOR_WITHOUT_RESPONSE_BANNER =  "The lead applicant has not responded to feedback.";

    private final String responseFilename;
    private final String feedbackFilename;
    private final boolean leadApplicant;
    private final boolean feedbackReleased;
    private final boolean assessorOrAdmin;

    public InterviewFeedbackViewModel(String responseFilename, String feedbackFilename, boolean leadApplicant,  boolean feedbackReleased, boolean assessorOrAdmin) {
        this.feedbackFilename = feedbackFilename;
        this.responseFilename = responseFilename;
        this.leadApplicant = leadApplicant;
        this.feedbackReleased = feedbackReleased;
        this.assessorOrAdmin = assessorOrAdmin;
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

    public boolean isFeedbackReleased() {
        return feedbackReleased;
    }

    public boolean isAssessorOrAdmin() {
        return assessorOrAdmin;
    }

    /* View logic methods. */
    public boolean hasResponse() {
        return responseFilename != null;
    }

    public boolean hasFeedback() {
        return feedbackFilename != null;
    }

    public boolean isResponseSectionEnabled() {
        return !feedbackReleased || hasResponse();
    }

    public String getBannerText() {
        if (isAssessorOrAdmin()) {
            return getAssessorBannerText();
        } else {
            return getApplicantBannerText();
        }
    }

    public String getAssessorBannerText() {
        if (hasResponse()) {
            return ASSESSOR_WITH_RESPONSE_BANNER;
        } else {
            return ASSESSOR_WITHOUT_RESPONSE_BANNER;
        }
    }

    public String getApplicantBannerText() {
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
