package org.innovateuk.ifs.application.readonly.viewmodel;

import org.innovateuk.ifs.application.readonly.ApplicationReadOnlySettings;
import org.innovateuk.ifs.application.summary.viewmodel.InterviewFeedbackViewModel;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public class ApplicationReadOnlyViewModel {

    private final ApplicationReadOnlySettings settings;
    private final Set<ApplicationSectionReadOnlyViewModel> sections;
    private final BigDecimal applicationScore;
    private List<String> overallFeedbacks;
    private InterviewFeedbackViewModel interviewFeedbackViewModel;
    private String downloadBaseURL;

    public ApplicationReadOnlyViewModel(ApplicationReadOnlySettings settings, Set<ApplicationSectionReadOnlyViewModel> sections, BigDecimal applicationScore, List<String> overallFeedbacks) {
        this.settings = settings;
        this.sections = sections;
        this.applicationScore = applicationScore;
        this.overallFeedbacks = overallFeedbacks;
    }

    public void setInterviewFeedbackViewModel(InterviewFeedbackViewModel interviewFeedbackViewModel) {
        this.interviewFeedbackViewModel = interviewFeedbackViewModel;
    }

    public void setDownloadBaseURL(String downloadBaseURL) {
        this.downloadBaseURL = downloadBaseURL;
    }

    public String getDownloadBaseURL() { return downloadBaseURL; }

    public InterviewFeedbackViewModel getInterviewFeedbackViewModel() {
        return interviewFeedbackViewModel;
    }

    public List<String> getOverallFeedbacks() {
        return overallFeedbacks;
    }

    public ApplicationReadOnlySettings getSettings() {
        return settings;
    }

    public Set<ApplicationSectionReadOnlyViewModel> getSections() {
        return sections;
    }

    public BigDecimal getApplicationScore() { return applicationScore; }
}
