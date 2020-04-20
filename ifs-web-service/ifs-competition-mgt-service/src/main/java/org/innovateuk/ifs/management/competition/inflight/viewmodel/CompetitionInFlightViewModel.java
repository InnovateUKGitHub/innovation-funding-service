package org.innovateuk.ifs.management.competition.inflight.viewmodel;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.competition.resource.AssessorFinanceView;
import org.innovateuk.ifs.competition.resource.CompetitionFunderResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;

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
    private boolean averageAssessorScoreEnabled;
    private boolean competitionHasAssessmentStage;
    private AssessorFinanceView assessorFinanceView;

    public CompetitionInFlightViewModel(CompetitionResource competitionResource,
                                        List<MilestonesRowViewModel> milestones,
                                        long changesSinceLastNotify,
                                        CompetitionInFlightStatsViewModel keyStatistics,
                                        boolean readOnly) {
        this.competitionId = competitionResource.getId();
        this.competitionName = competitionResource.getName();
        this.competitionStatus = competitionResource.getCompetitionStatus();
        this.competitionType = competitionResource.getCompetitionTypeName();
        this.fundingDecisionAllowedBeforeAssessment = !competitionResource.hasAssessmentStage();
        this.innovationSector = competitionResource.getInnovationSectorName();
        this.innovationArea = StringUtils.join(competitionResource.getInnovationAreaNames(), ", ");
        this.executive = competitionResource.getExecutiveName();
        this.lead = competitionResource.getLeadTechnologistName();
        this.funding = competitionResource.getFunders().stream().map(CompetitionFunderResource::getFunderBudget).reduce(BigInteger.ZERO, BigInteger::add);
        this.keyStatistics = keyStatistics;
        this.milestones = milestones;
        this.changesSinceLastNotify = changesSinceLastNotify;
        this.readOnly = readOnly;
        this.assessmentPanelEnabled = competitionResource.getCompetitionAssessmentConfig().getHasAssessmentPanel() != null ? competitionResource.getCompetitionAssessmentConfig().getHasAssessmentPanel() : false;
        this.interviewPanelEnabled = competitionResource.getCompetitionAssessmentConfig().getHasInterviewStage() != null ? competitionResource.getCompetitionAssessmentConfig().getHasInterviewStage() : false;
        this.averageAssessorScoreEnabled = competitionResource.getCompetitionAssessmentConfig().getAverageAssessorScore() != null ? competitionResource.getCompetitionAssessmentConfig().getAverageAssessorScore() : false;
        this.assessorFinanceView = competitionResource.getCompetitionAssessmentConfig().getAssessorFinanceView();
        this.competitionHasAssessmentStage = competitionResource.getCompetitionAssessmentConfig().getHasAssessmentPanel();
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

    public boolean isAssessmentPanelEnabled() {
        return assessmentPanelEnabled && competitionStatus != READY_TO_OPEN &&
                competitionStatus != OPEN && competitionStatus != ASSESSOR_FEEDBACK;
    }

    public boolean isInterviewPanelEnabled() {
        return interviewPanelEnabled && competitionStatus != READY_TO_OPEN &&
                competitionStatus != OPEN && competitionStatus != ASSESSOR_FEEDBACK;
    }

    public boolean isAverageAssessorScoreEnabled() {
        return averageAssessorScoreEnabled;
    }

    public AssessorFinanceView getAssessorFinanceView() {
        return assessorFinanceView;
    }

    public boolean isFundingDecisionEnabled() {
        return fundingDecisionAllowedBeforeAssessment
                || !asList(READY_TO_OPEN, OPEN, CLOSED, IN_ASSESSMENT).contains(competitionStatus);
    }

    public boolean isFundingNotificationDisplayed() {
        return fundingDecisionAllowedBeforeAssessment
                || asList(FUNDERS_PANEL, ASSESSOR_FEEDBACK).contains(competitionStatus);
    }

    public boolean isInviteAssessorsLinkEnabled() {
        return competitionHasAssessmentStage &&
                !asList(FUNDERS_PANEL, ASSESSOR_FEEDBACK, PROJECT_SETUP).contains(competitionStatus);
    }
}
