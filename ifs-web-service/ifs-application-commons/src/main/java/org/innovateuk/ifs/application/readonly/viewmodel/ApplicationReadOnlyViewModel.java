package org.innovateuk.ifs.application.readonly.viewmodel;

import org.innovateuk.ifs.application.readonly.ApplicationReadOnlySettings;
import org.innovateuk.ifs.supporter.resource.SupporterAssignmentResource;
import org.innovateuk.ifs.supporter.resource.SupporterState;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ApplicationReadOnlyViewModel {

    private final ApplicationReadOnlySettings settings;
    private final Set<ApplicationSectionReadOnlyViewModel> sections;
    private final BigDecimal applicationScore;
    private List<String> overallFeedbacks;
    private Map<SupporterState, List<SupporterAssignmentResource>> assignments;
    private boolean shouldDisplayKtpApplicationFeedback;

    public ApplicationReadOnlyViewModel(ApplicationReadOnlySettings settings,
                                        Set<ApplicationSectionReadOnlyViewModel> sections,
                                        BigDecimal applicationScore,
                                        List<String> overallFeedbacks,
                                        Map<SupporterState, List<SupporterAssignmentResource>> assignments,
                                        boolean shouldDisplayKtpApplicationFeedback) {
        this.settings = settings;
        this.sections = sections;
        this.applicationScore = applicationScore;
        this.overallFeedbacks = overallFeedbacks;
        this.assignments = assignments;
        this.shouldDisplayKtpApplicationFeedback = shouldDisplayKtpApplicationFeedback;
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

    public Map<SupporterState, List<SupporterAssignmentResource>> getAssignments() {
        return assignments;
    }

    public boolean isShouldDisplayKtpApplicationFeedback() {
        return shouldDisplayKtpApplicationFeedback;
    }

    public boolean isAccepted() {
        return this.assignments.containsKey(SupporterState.ACCEPTED);
    }

    public boolean isPending() {
        return this.assignments.containsKey(SupporterState.CREATED);
    }

    public boolean isDeclined() {
        return this.assignments.containsKey(SupporterState.REJECTED);
    }

    public int getAcceptedCount() {
        return isAccepted() ? this.assignments.get(SupporterState.ACCEPTED).size() : 0;
    }

    public int getPendingCount() {
        return isPending() ? this.assignments.get(SupporterState.CREATED).size() : 0;
    }

    public int getDeclinedCount() {
        return isDeclined() ? this.assignments.get(SupporterState.REJECTED).size() : 0;
    }
}
