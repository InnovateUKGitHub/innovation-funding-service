package org.innovateuk.ifs.application.readonly.viewmodel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.innovateuk.ifs.application.readonly.ApplicationReadOnlySettings;
import org.innovateuk.ifs.application.resource.ApplicationEoiEvidenceResponseResource;
import org.innovateuk.ifs.application.resource.EoiEvidenceReadOnlyViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionThirdPartyConfigResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;

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
    private final boolean thirdPartyProcurement;
    private final CompetitionThirdPartyConfigResource thirdPartyConfig;
    private final boolean isLoanPartBEnabled;
    private final boolean isExpressionOfInterestApplication;
    private boolean eoiFullApplication;
    private EoiEvidenceReadOnlyViewModel eoiEvidenceReadOnlyViewModel;

    public ApplicationReadOnlyViewModel(ApplicationReadOnlySettings settings,
                                        Set<ApplicationSectionReadOnlyViewModel> sections,
                                        BigDecimal applicationScore,
                                        List<String> overallFeedbacks,
                                        Map<String, List<SupporterAssignmentReadOnlyViewModel>> assignments,
                                        boolean shouldDisplayKtpApplicationFeedback,
                                        boolean ktpCompetition,
                                        boolean thirdPartyProcurement,
                                        CompetitionThirdPartyConfigResource thirdPartyConfig,
                                        boolean isLoanPartBEnabled,
                                        boolean isExpressionOfInterestApplication,
                                        boolean eoiFullApplication,
                                        EoiEvidenceReadOnlyViewModel eoiEvidenceReadOnlyViewModel) {
        this.settings = settings;
        this.sections = sections;
        this.applicationScore = applicationScore;
        this.overallFeedbacks = overallFeedbacks;
        this.assignments = assignments;
        this.shouldDisplayKtpApplicationFeedback = shouldDisplayKtpApplicationFeedback;
        this.ktpCompetition = ktpCompetition;
        this.thirdPartyProcurement = thirdPartyProcurement;
        this.thirdPartyConfig = thirdPartyConfig;
        this.isLoanPartBEnabled = isLoanPartBEnabled;
        this.isExpressionOfInterestApplication = isExpressionOfInterestApplication;
        this.eoiFullApplication = eoiFullApplication;
        this.eoiEvidenceReadOnlyViewModel = eoiEvidenceReadOnlyViewModel;
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

    public boolean isThirdPartyProcurement() { return thirdPartyProcurement; }

    public CompetitionThirdPartyConfigResource getThirdPartyConfig() { return thirdPartyConfig; }

    public boolean isLoanPartBEnabled() {
        return isLoanPartBEnabled;
    }

    public boolean isExpressionOfInterestApplication() {
        return isExpressionOfInterestApplication;
    }

    public boolean isEoiFullApplication() {
        return eoiFullApplication;
    }

    public EoiEvidenceReadOnlyViewModel getEoiEvidenceReadOnlyViewModel() {
        return eoiEvidenceReadOnlyViewModel;
    }

    public void setEoiEvidenceReadOnlyViewModel(EoiEvidenceReadOnlyViewModel eoiEvidenceReadOnlyViewModel) {
        this.eoiEvidenceReadOnlyViewModel = eoiEvidenceReadOnlyViewModel;
    }
}
