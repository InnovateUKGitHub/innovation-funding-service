package org.innovateuk.ifs.application.readonly.viewmodel;

import org.innovateuk.ifs.application.readonly.ApplicationReadOnlySettings;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ApplicationReadOnlyViewModel {

    private final ApplicationReadOnlySettings settings;
    private final Set<ApplicationSectionReadOnlyViewModel> sections;
    private final BigDecimal applicationScore;
    private List<String> overallFeedbacks;
    private Map<String, List<SupporterAssignmentReadOnlyViewModel>> assignments;
    private boolean shouldDisplayKtpApplicationFeedback;
    private final boolean ktpCompetition;

    public ApplicationReadOnlyViewModel(ApplicationReadOnlySettings settings,
                                        Set<ApplicationSectionReadOnlyViewModel> sections,
                                        BigDecimal applicationScore,
                                        List<String> overallFeedbacks,
                                        Map<String, List<SupporterAssignmentReadOnlyViewModel>> assignments,
                                        boolean shouldDisplayKtpApplicationFeedback,
                                        boolean ktpCompetition) {
        this.settings = settings;
        this.sections = sections;
        this.applicationScore = applicationScore;
        this.overallFeedbacks = overallFeedbacks;
        this.assignments = assignments;
        this.shouldDisplayKtpApplicationFeedback = shouldDisplayKtpApplicationFeedback;
        this.ktpCompetition = ktpCompetition;
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

    public Map<String, List<SupporterAssignmentReadOnlyViewModel>> getAssignments() {
        return assignments;
    }

    public boolean isShouldDisplayKtpApplicationFeedback() {
        return shouldDisplayKtpApplicationFeedback;
    }

    public boolean isAccepted() {
        return this.assignments.containsKey("accepted");
    }

    public boolean isPending() {
        return this.assignments.containsKey("created");
    }

    public boolean isDeclined() {
        return this.assignments.containsKey("rejected");
    }

    public int getAcceptedCount() {
        return isAccepted() ? this.assignments.get("accepted").size() : 0;
    }

    public int getPendingCount() {
        return isPending() ? this.assignments.get("created").size() : 0;
    }

    public int getDeclinedCount() {
        return isDeclined() ? this.assignments.get("rejected").size() : 0;
    }

    public boolean isKtpCompetition() {
        return ktpCompetition;
    }
}
