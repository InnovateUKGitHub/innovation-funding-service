package org.innovateuk.ifs.competition.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static java.time.temporal.ChronoUnit.DAYS;

public class CompetitionResource {

    public static final ChronoUnit CLOSING_SOON_CHRONOUNIT = ChronoUnit.HOURS;
    public static final int CLOSING_SOON_AMOUNT = 3;
    public static final DateTimeFormatter START_DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/YYYY");

    private static final DateTimeFormatter ASSESSMENT_DATE_FORMAT = DateTimeFormatter.ofPattern("MMMM YYYY");
    public static final ImmutableSet<String> NON_FINANCE_TYPES = ImmutableSet.of(
            "Expression of interest",
            "The Prince's Trust"
    );

    private Long id;
    private List<Long> milestones = new ArrayList<>();
    private List<CompetitionFunderResource> funders = new ArrayList<>();
    @Size(max = 255, message = "{validation.field.too.many.characters}")
    private String name;
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;
    private ZonedDateTime registrationDate;
    private ZonedDateTime assessorAcceptsDate;
    private ZonedDateTime assessorDeadlineDate;
    private ZonedDateTime releaseFeedbackDate;
    private ZonedDateTime feedbackReleasedDate;
    private ZonedDateTime fundersPanelDate;
    private ZonedDateTime fundersPanelEndDate;
    private ZonedDateTime assessorFeedbackDate;
    private ZonedDateTime assessorBriefingDate;
    private CompetitionStatus competitionStatus;
    @Min(0)
    @Max(100)
    private Integer maxResearchRatio;
    @Min(0)
    @Max(100)
    private Integer academicGrantPercentage;
    private Long competitionType;
    private String competitionTypeName;
    private Long executive;
    private String executiveName;
    private Long leadTechnologist;
    private String leadTechnologistName;
    private Long innovationSector;
    private String innovationSectorName;
    private Set<Long> innovationAreas;
    private Set<String> innovationAreaNames;

    private String pafCode;
    private String budgetCode;
    private String code;

    private Boolean resubmission;
    private Boolean multiStream;
    private String streamName;
    private CollaborationLevel collaborationLevel;
    private List<Long> leadApplicantTypes;
    private Set<Long> researchCategories;

    private Integer minProjectDuration;
    private Integer maxProjectDuration;

    private Integer assessorCount;
    private BigDecimal assessorPay;

    private String activityCode;

    private Boolean fullApplicationFinance = true;
    private boolean setupComplete = false;

    private Boolean useResubmissionQuestion;
    private Boolean hasAssessmentPanel;
    private Boolean hasInterviewStage;
    private AssessorFinanceView assessorFinanceView = AssessorFinanceView.OVERVIEW;

    private boolean nonIfs = false;
    private String nonIfsUrl;

    private GrantTermsAndConditionsResource termsAndConditions;

    private boolean locationPerPartner = true;
    private Boolean stateAid;

    // IFS-3088 & IFS-2123 & IFS-3753: This is temporary until all competitions with the old menu view are complete
    private boolean useNewApplicantMenu;

    public CompetitionResource() {
        // no-arg constructor
    }

    public CompetitionResource(long id,
            String name,
            ZonedDateTime startDate,
            ZonedDateTime endDate,
            ZonedDateTime registrationDate) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.registrationDate = registrationDate;
    }

    @JsonIgnore
    public boolean isOpen() {
        return CompetitionStatus.OPEN.equals(competitionStatus);
    }

    @JsonIgnore
    public boolean isAssessmentClosed() {
        return competitionStatus != null && (competitionStatus.isLaterThan(CompetitionStatus.IN_ASSESSMENT));
    }

    @JsonIgnore
    public boolean isSetupAndLive() {
        return Boolean.TRUE.equals(setupComplete) && startDate.isBefore(ZonedDateTime.now());
    }

    @JsonIgnore
    public boolean isSetupAndAfterNotifications() {
        return Boolean.TRUE.equals(setupComplete) && (fundersPanelDate != null && fundersPanelDate.isBefore(
                ZonedDateTime.now()));
    }

    public CompetitionStatus getCompetitionStatus() {
        return competitionStatus;
    }

    public void setCompetitionStatus(CompetitionStatus competitionStatus) {
        this.competitionStatus = competitionStatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ZonedDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(ZonedDateTime endDate) {
        this.endDate = endDate;
    }

    public ZonedDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(ZonedDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }

    public String assessmentEndDateDisplay() {
        return displayDate(getFundersPanelDate(), ASSESSMENT_DATE_FORMAT);
    }

    public String startDateDisplay() {
        return displayDate(getStartDate(), START_DATE_FORMAT);
    }

    private String displayDate(ZonedDateTime date, DateTimeFormatter formatter) {
        if (date != null) {
            return date.format(formatter);
        }
        return "";
    }

    public ZonedDateTime getAssessorAcceptsDate() {
        return assessorAcceptsDate;
    }

    public void setAssessorAcceptsDate(ZonedDateTime assessorAcceptsDate) {
        this.assessorAcceptsDate = assessorAcceptsDate;
    }

    public ZonedDateTime getAssessorDeadlineDate() {
        return assessorDeadlineDate;
    }

    public void setAssessorDeadlineDate(ZonedDateTime assessorDeadlineDate) {
        this.assessorDeadlineDate = assessorDeadlineDate;
    }

    public ZonedDateTime getReleaseFeedbackDate() {
        return releaseFeedbackDate;
    }

    public void setReleaseFeedbackDate(ZonedDateTime releaseFeedbackDate) {
        this.releaseFeedbackDate = releaseFeedbackDate;
    }

    public ZonedDateTime getFeedbackReleasedDate() {
        return feedbackReleasedDate;
    }

    public void setFeedbackReleasedDate(ZonedDateTime feedbackReleasedDate) {
        this.feedbackReleasedDate = feedbackReleasedDate;
    }

    public ZonedDateTime getFundersPanelDate() {
        return fundersPanelDate;
    }

    public void setFundersPanelDate(ZonedDateTime fundersPanelDate) {
        this.fundersPanelDate = fundersPanelDate;
    }

    public ZonedDateTime getAssessorFeedbackDate() {
        return assessorFeedbackDate;
    }

    public void setAssessorFeedbackDate(ZonedDateTime assessorFeedbackDate) {
        this.assessorFeedbackDate = assessorFeedbackDate;
    }

    public ZonedDateTime getAssessorBriefingDate() {
        return assessorBriefingDate;
    }

    public void setAssessorBriefingDate(ZonedDateTime assessorBriefingDate) {
        this.assessorBriefingDate = assessorBriefingDate;
    }

    @JsonIgnore
    public long getDaysLeft() {
        return DAYS.between(ZonedDateTime.now(), this.endDate);
    }

    @JsonIgnore
    public long getAssessmentDaysLeft() {
        return DAYS.between(ZonedDateTime.now(), this.assessorDeadlineDate);
    }

    @JsonIgnore
    public long getTotalDays() {
        return DAYS.between(this.startDate, this.endDate);
    }

    @JsonIgnore
    public boolean isClosingSoon() {
        long hoursToGo = CLOSING_SOON_CHRONOUNIT.between(ZonedDateTime.now(), this.endDate);
        return isOpen() && hoursToGo < CLOSING_SOON_AMOUNT;
    }

    @JsonIgnore
    public long getAssessmentTotalDays() {
        return DAYS.between(this.assessorAcceptsDate, this.assessorDeadlineDate);
    }

    @JsonIgnore
    public long getStartDateToEndDatePercentage() {
        return getDaysLeftPercentage(getDaysLeft(), getTotalDays());
    }

    @JsonIgnore
    public long getAssessmentDaysLeftPercentage() {
        return getDaysLeftPercentage(getAssessmentDaysLeft(), getAssessmentTotalDays());
    }

    private static long getDaysLeftPercentage(long daysLeft, long totalDays) {
        if (daysLeft <= 0) {
            return 100;
        }
        double deadlineProgress = 100 - (((double) daysLeft / (double) totalDays) * 100);
        long startDateToEndDatePercentage = (long) deadlineProgress;
        return startDateToEndDatePercentage;
    }

    public Integer getMaxResearchRatio() {
        return maxResearchRatio;
    }

    public void setMaxResearchRatio(Integer maxResearchRatio) {
        this.maxResearchRatio = maxResearchRatio;
    }

    public Integer getAcademicGrantPercentage() {
        return academicGrantPercentage;
    }

    public void setAcademicGrantPercentage(Integer academicGrantPercentage) {
        this.academicGrantPercentage = academicGrantPercentage;
    }

    public ZonedDateTime getFundersPanelEndDate() {
        return fundersPanelEndDate;
    }

    public void setFundersPanelEndDate(ZonedDateTime fundersPanelEndDate) {
        this.fundersPanelEndDate = fundersPanelEndDate;
    }

    public Long getExecutive() {
        return executive;
    }

    public void setExecutive(Long executive) {
        this.executive = executive;
    }

    public String getExecutiveName() {
        return executiveName;
    }

    public void setExecutiveName(String executiveName) {
        this.executiveName = executiveName;
    }

    public Long getLeadTechnologist() {
        return leadTechnologist;
    }

    public void setLeadTechnologist(Long leadTechnologist) {
        this.leadTechnologist = leadTechnologist;
    }

    public String getLeadTechnologistName() {
        return leadTechnologistName;
    }

    public void setLeadTechnologistName(String leadTechnologistName) {
        this.leadTechnologistName = leadTechnologistName;
    }

    public String getPafCode() {
        return pafCode;
    }

    public void setPafCode(String pafCode) {
        this.pafCode = pafCode;
    }

    public String getBudgetCode() {
        return budgetCode;
    }

    public void setBudgetCode(String budgetCode) {
        this.budgetCode = budgetCode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getCompetitionType() {
        return competitionType;
    }

    public void setCompetitionType(Long competitionType) {
        this.competitionType = competitionType;
    }

    public String getCompetitionTypeName() {
        return competitionTypeName;
    }

    public void setCompetitionTypeName(String competitionTypeName) {
        this.competitionTypeName = competitionTypeName;
    }

    public Long getInnovationSector() {
        return innovationSector;
    }

    public void setInnovationSector(Long innovationSector) {
        this.innovationSector = innovationSector;
    }

    public Set<Long> getInnovationAreas() {
        return innovationAreas;
    }

    public void setInnovationAreas(Set<Long> innovationAreas) {
        this.innovationAreas = innovationAreas;
    }

    public String getInnovationSectorName() {
        return innovationSectorName;
    }

    public void setInnovationSectorName(String innovationSectorName) {
        this.innovationSectorName = innovationSectorName;
    }

    public Set<String> getInnovationAreaNames() {
        return innovationAreaNames;
    }

    public void setInnovationAreaNames(Set<String> innovationAreaNames) {
        this.innovationAreaNames = new TreeSet<>(innovationAreaNames);
    }

    public Set<Long> getResearchCategories() {
        return researchCategories;
    }

    public void setResearchCategories(Set<Long> researchCategories) {
        this.researchCategories = researchCategories;
    }

    public List<Long> getMilestones() {
        return milestones;
    }

    public void setMilestones(List<Long> milestones) {
        this.milestones = milestones;
    }

    public Boolean isMultiStream() {
        return multiStream;
    }

    public void setMultiStream(Boolean multiStream) {
        this.multiStream = multiStream;
    }

    public Boolean getResubmission() {
        return resubmission;
    }

    public void setResubmission(Boolean resubmission) {
        this.resubmission = resubmission;
    }

    public String getStreamName() {
        return streamName;
    }

    public void setStreamName(String streamName) {
        this.streamName = streamName;
    }

    public CollaborationLevel getCollaborationLevel() {
        return collaborationLevel;
    }

    public void setCollaborationLevel(CollaborationLevel collaborationLevel) {
        this.collaborationLevel = collaborationLevel;
    }

    public List<Long> getLeadApplicantTypes() {
        return leadApplicantTypes;
    }

    public void setLeadApplicantTypes(List<Long> leadApplicantTypes) {
        this.leadApplicantTypes = leadApplicantTypes;
    }

    public String getActivityCode() {
        return activityCode;
    }

    public void setActivityCode(String activityCode) {
        this.activityCode = activityCode;
    }

    public List<CompetitionFunderResource> getFunders() {
        return funders;
    }

    public void setFunders(List<CompetitionFunderResource> funders) {
        this.funders = funders;
    }

    public Boolean getUseResubmissionQuestion() {
        return useResubmissionQuestion;
    }

    public void setUseResubmissionQuestion(Boolean useResubmissionQuestion) {
        this.useResubmissionQuestion = useResubmissionQuestion;
    }

    public Integer getAssessorCount() {
        return assessorCount;
    }

    public void setAssessorCount(Integer assessorCount) {
        this.assessorCount = assessorCount;
    }

    public BigDecimal getAssessorPay() {
        return assessorPay;
    }

    public void setAssessorPay(BigDecimal assessorPay) {
        this.assessorPay = assessorPay;
    }

    public Boolean isFullApplicationFinance() {
        return fullApplicationFinance;
    }

    public void setFullApplicationFinance(Boolean fullApplicationFinance) {
        this.fullApplicationFinance = fullApplicationFinance;
    }

    public boolean getSetupComplete() {
        return setupComplete;
    }

    public void setSetupComplete(boolean setupComplete) {
        this.setupComplete = setupComplete;
    }

    public boolean isNonIfs() {
        return nonIfs;
    }

    public void setNonIfs(boolean nonIfs) {
        this.nonIfs = nonIfs;
    }

    public String getNonIfsUrl() {
        return nonIfsUrl;
    }

    public void setNonIfsUrl(String nonIfsUrl) {
        this.nonIfsUrl = nonIfsUrl;
    }

    public Boolean isHasAssessmentPanel() {
        return hasAssessmentPanel;
    }

    public void setHasAssessmentPanel(Boolean hasAssessmentPanel) {
        this.hasAssessmentPanel = hasAssessmentPanel;
    }

    public Boolean isHasInterviewStage() {
        return hasInterviewStage;
    }

    public void setHasInterviewStage(Boolean hasInterviewStage) {
        this.hasInterviewStage = hasInterviewStage;
    }

    public AssessorFinanceView getAssessorFinanceView() {
        return assessorFinanceView;
    }

    public void setAssessorFinanceView(AssessorFinanceView assessorFinanceView) {
        this.assessorFinanceView = assessorFinanceView;
    }

    @JsonIgnore
    public boolean isNonFinanceType() {
        return NON_FINANCE_TYPES.contains(competitionTypeName);
    }

    @JsonIgnore
    public boolean isFinanceType() {
        return !isNonFinanceType();
    }

    public GrantTermsAndConditionsResource getTermsAndConditions() {
        return termsAndConditions;
    }

    public void setTermsAndConditions(GrantTermsAndConditionsResource termsAndConditions) {
        this.termsAndConditions = termsAndConditions;
    }

    public boolean isLocationPerPartner() {
        return locationPerPartner;
    }

    public void setLocationPerPartner(boolean locationPerPartner) {
        this.locationPerPartner = locationPerPartner;
    }

    public Integer getMinProjectDuration() {
        return minProjectDuration;
    }

    public void setMinProjectDuration(Integer minProjectDuration) {
        this.minProjectDuration = minProjectDuration;
    }

    public Integer getMaxProjectDuration() {
        return maxProjectDuration;
    }

    public void setMaxProjectDuration(Integer maxProjectDuration) {
        this.maxProjectDuration = maxProjectDuration;
    }

    public Boolean getStateAid() {
        return stateAid;
    }

    public void setStateAid(final Boolean stateAid) {
        this.stateAid = stateAid;
    }

    public boolean getUseNewApplicantMenu() {
        return useNewApplicantMenu;
    }

    public void setUseNewApplicantMenu(boolean useNewApplicantMenu) {
        this.useNewApplicantMenu = useNewApplicantMenu;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        CompetitionResource that = (CompetitionResource) o;

        return new EqualsBuilder()
                .append(setupComplete, that.setupComplete)
                .append(useResubmissionQuestion, that.useResubmissionQuestion)
                .append(nonIfs, that.nonIfs)
                .append(id, that.id)
                .append(milestones, that.milestones)
                .append(funders, that.funders)
                .append(name, that.name)
                .append(startDate, that.startDate)
                .append(endDate, that.endDate)
                .append(registrationDate, that.registrationDate)
                .append(assessorAcceptsDate, that.assessorAcceptsDate)
                .append(assessorDeadlineDate, that.assessorDeadlineDate)
                .append(releaseFeedbackDate, that.releaseFeedbackDate)
                .append(feedbackReleasedDate, that.feedbackReleasedDate)
                .append(fundersPanelDate, that.fundersPanelDate)
                .append(fundersPanelEndDate, that.fundersPanelEndDate)
                .append(assessorFeedbackDate, that.assessorFeedbackDate)
                .append(assessorBriefingDate, that.assessorBriefingDate)
                .append(competitionStatus, that.competitionStatus)
                .append(maxResearchRatio, that.maxResearchRatio)
                .append(academicGrantPercentage, that.academicGrantPercentage)
                .append(competitionType, that.competitionType)
                .append(competitionTypeName, that.competitionTypeName)
                .append(executive, that.executive)
                .append(executiveName, that.executiveName)
                .append(leadTechnologist, that.leadTechnologist)
                .append(leadTechnologistName, that.leadTechnologistName)
                .append(innovationSector, that.innovationSector)
                .append(innovationSectorName, that.innovationSectorName)
                .append(innovationAreas, that.innovationAreas)
                .append(innovationAreaNames, that.innovationAreaNames)
                .append(pafCode, that.pafCode)
                .append(budgetCode, that.budgetCode)
                .append(code, that.code)
                .append(resubmission, that.resubmission)
                .append(multiStream, that.multiStream)
                .append(streamName, that.streamName)
                .append(collaborationLevel, that.collaborationLevel)
                .append(leadApplicantTypes, that.leadApplicantTypes)
                .append(researchCategories, that.researchCategories)
                .append(assessorCount, that.assessorCount)
                .append(assessorPay, that.assessorPay)
                .append(activityCode, that.activityCode)
                .append(fullApplicationFinance, that.fullApplicationFinance)
                .append(hasAssessmentPanel, that.hasAssessmentPanel)
                .append(hasInterviewStage, that.hasInterviewStage)
                .append(assessorFinanceView, that.assessorFinanceView)
                .append(nonIfsUrl, that.nonIfsUrl)
                .append(termsAndConditions, that.termsAndConditions)
                .append(locationPerPartner, that.locationPerPartner)
                .append(stateAid, that.stateAid)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(milestones)
                .append(funders)
                .append(name)
                .append(startDate)
                .append(endDate)
                .append(registrationDate)
                .append(assessorAcceptsDate)
                .append(assessorDeadlineDate)
                .append(releaseFeedbackDate)
                .append(feedbackReleasedDate)
                .append(fundersPanelDate)
                .append(fundersPanelEndDate)
                .append(assessorFeedbackDate)
                .append(assessorBriefingDate)
                .append(competitionStatus)
                .append(maxResearchRatio)
                .append(academicGrantPercentage)
                .append(competitionType)
                .append(competitionTypeName)
                .append(executive)
                .append(executiveName)
                .append(leadTechnologist)
                .append(leadTechnologistName)
                .append(innovationSector)
                .append(innovationSectorName)
                .append(innovationAreas)
                .append(innovationAreaNames)
                .append(pafCode)
                .append(budgetCode)
                .append(code)
                .append(resubmission)
                .append(multiStream)
                .append(streamName)
                .append(collaborationLevel)
                .append(leadApplicantTypes)
                .append(researchCategories)
                .append(assessorCount)
                .append(assessorPay)
                .append(activityCode)
                .append(fullApplicationFinance)
                .append(setupComplete)
                .append(useResubmissionQuestion)
                .append(hasAssessmentPanel)
                .append(hasInterviewStage)
                .append(assessorFinanceView)
                .append(nonIfs)
                .append(nonIfsUrl)
                .append(termsAndConditions)
                .append(locationPerPartner)
                .append(stateAid)
                .toHashCode();
    }
}