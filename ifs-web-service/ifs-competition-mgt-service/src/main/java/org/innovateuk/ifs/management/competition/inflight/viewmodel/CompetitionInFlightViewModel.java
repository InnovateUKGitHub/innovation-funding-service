package org.innovateuk.ifs.management.competition.inflight.viewmodel;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.*;

import java.math.BigInteger;
import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.*;

/**
 * Holder of model attributes for the Competition Management 'in flight' Dashboard
 */
public class CompetitionInFlightViewModel {

    private Long competitionId;
    private String competitionName;
    private CompetitionStatus competitionStatus;
    private boolean fundingDecisionAllowedBeforeAssessment;
    private String competitionType;
    private FundingType competitionFundingType;
    private String innovationSector;
    private String innovationArea;
    private String executive;
    private String lead;
    private BigInteger funding;
    private List<MilestonesRowViewModel> milestones;
    private long changesSinceLastNotify;
    private CompetitionInFlightStatsViewModel keyStatistics;
    private boolean readOnly;
    private boolean assessmentPanelEnabled;
    private boolean interviewPanelEnabled;
    private boolean competitionHasAssessmentStage;
    private AssessorFinanceView assessorFinanceView;
    private CompetitionCompletionStage competitionCompletionStage;
    private boolean supporterEnabled;
    private boolean alwaysOpen;
    private boolean isSuperAdminUser;

    public CompetitionInFlightViewModel(CompetitionResource competitionResource,
                                        CompetitionAssessmentConfigResource competitionAssessmentConfigResource,
                                        List<MilestonesRowViewModel> milestones,
                                        long changesSinceLastNotify,
                                        CompetitionInFlightStatsViewModel keyStatistics,
                                        boolean readOnly,
                                        boolean isSuperAdminUser) {
        this.competitionId = competitionResource.getId();
        this.competitionName = competitionResource.getName();
        this.competitionCompletionStage = competitionResource.getCompletionStage();
        this.competitionStatus = competitionResource.getCompetitionStatus();
        this.competitionType = competitionResource.getCompetitionTypeName();
        this.competitionFundingType = competitionResource.getFundingType();
        this.fundingDecisionAllowedBeforeAssessment = !competitionResource.isHasAssessmentStage();
        this.innovationSector = competitionResource.getInnovationSectorName();
        this.innovationArea = StringUtils.join(competitionResource.getInnovationAreaNames(), ", ");
        this.executive = competitionResource.getExecutiveName();
        this.lead = competitionResource.getLeadTechnologistName();
        this.funding = competitionResource.getFunders().stream().map(CompetitionFunderResource::getFunderBudget).reduce(BigInteger.ZERO, BigInteger::add);
        this.keyStatistics = keyStatistics;
        this.milestones = milestones;
        this.changesSinceLastNotify = changesSinceLastNotify;
        this.readOnly = readOnly;
        this.isSuperAdminUser = isSuperAdminUser;
        this.assessmentPanelEnabled = competitionAssessmentConfigResource.getHasAssessmentPanel() != null ? competitionAssessmentConfigResource.getHasAssessmentPanel() : false;
        this.interviewPanelEnabled = competitionAssessmentConfigResource.getHasInterviewStage() != null ? competitionAssessmentConfigResource.getHasInterviewStage() : false;
        this.assessorFinanceView = competitionAssessmentConfigResource.getAssessorFinanceView();
        this.competitionHasAssessmentStage = competitionResource.isHasAssessmentStage();
        this.supporterEnabled = competitionResource.isKtp();
        this.alwaysOpen = competitionResource.isAlwaysOpen();
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public CompetitionStatus getCompetitionStatus() {
        return competitionStatus;
    }

    public String getCompetitionType() {
        return competitionType;
    }

    public FundingType getCompetitionFundingType() {
        return competitionFundingType;
    }

    public String getInnovationSector() {
        return innovationSector;
    }

    public String getInnovationArea() {
        return innovationArea;
    }

    public String getExecutive() {
        return executive;
    }

    public String getLead() {
        return lead;
    }

    public BigInteger getFunding() {
        return funding;
    }

    public List<MilestonesRowViewModel> getMilestones() {
        return milestones;
    }

    public long getChangesSinceLastNotify() {
        return changesSinceLastNotify;
    }

    public CompetitionInFlightStatsViewModel getKeyStatistics() {
        return keyStatistics;
    }

    public boolean isFundingDecisionAllowedBeforeAssessment() {
        return fundingDecisionAllowedBeforeAssessment;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public boolean isSuperAdminUser() {
        return isSuperAdminUser;
    }

    public CompetitionCompletionStage getCompetitionCompletionStage() {
        return competitionCompletionStage;
    }

    public boolean isAssessmentPanelEnabled() {
        return assessmentPanelEnabled && competitionStatus != READY_TO_OPEN &&
                competitionStatus != OPEN && competitionStatus != ASSESSOR_FEEDBACK;
    }

    public boolean isInterviewPanelEnabled() {
        return interviewPanelEnabled && competitionStatus != READY_TO_OPEN &&
                competitionStatus != OPEN && competitionStatus != ASSESSOR_FEEDBACK;
    }

    public AssessorFinanceView getAssessorFinanceView() {
        return assessorFinanceView;
    }

    public boolean isFundingDecisionEnabled() {
        return fundingDecisionAllowedBeforeAssessment
                || !asList(READY_TO_OPEN, OPEN, CLOSED, IN_ASSESSMENT).contains(competitionStatus)
                || (alwaysOpen && hasAClosedAssessmentPeriod());
    }

    public boolean isFundingNotificationDisplayed() {
        return fundingDecisionAllowedBeforeAssessment
                || asList(FUNDERS_PANEL, ASSESSOR_FEEDBACK).contains(competitionStatus);
    }

    public boolean isInviteAssessorsLinkEnabled() {
        return competitionHasAssessmentStage &&
                !asList(FUNDERS_PANEL, ASSESSOR_FEEDBACK, PROJECT_SETUP).contains(competitionStatus);
    }

    public boolean isManageSupportersLinkEnabled() {
        return supporterEnabled;
    }

    public boolean isAlwaysOpen() {
        return alwaysOpen;
    }

    public MilestonesRowViewModel findMilestoneByType(MilestoneType milestoneType) {
        return milestones
                .stream()
                .filter(m -> m.getMilestoneType() == milestoneType)
                .findAny()
                .orElseThrow(ObjectNotFoundException::new);
    }

    public boolean hasAClosedAssessmentPeriod() {
        return milestones
                .stream()
                .anyMatch(m -> m.getMilestoneType() == MilestoneType.ASSESSMENT_CLOSED && m.isPassed());
    }

    public boolean isManageAssessmentLinkEnabled() {
        return competitionStatus != READY_TO_OPEN
                && (competitionStatus != OPEN || alwaysOpen);
    }
}
