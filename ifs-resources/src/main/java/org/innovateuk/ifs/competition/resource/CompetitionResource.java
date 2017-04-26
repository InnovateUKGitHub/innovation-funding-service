package org.innovateuk.ifs.competition.resource;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static java.time.temporal.ChronoUnit.DAYS;

public class CompetitionResource {
    public static final ChronoUnit CLOSING_SOON_CHRONOUNIT = ChronoUnit.HOURS;
    public static final int CLOSING_SOON_AMOUNT = 3;
    private static final DateTimeFormatter ASSESSMENT_DATE_FORMAT = DateTimeFormatter.ofPattern("MMMM YYYY");
    public static final DateTimeFormatter START_DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/YYYY");

    private Long id;
    private List<Long> milestones = new ArrayList<>();
    private List<CompetitionFunderResource> funders = new ArrayList<>();

    private String name;
    private String description;
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;
    private ZonedDateTime assessorAcceptsDate;
    private ZonedDateTime assessorDeadlineDate;
    private ZonedDateTime releaseFeedbackDate;
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

    private Integer assessorCount;
    private BigDecimal assessorPay;


    private Map<CompetitionSetupSection, Boolean> sectionSetupStatus = new HashMap<>();

    private String activityCode;

    private Boolean setupComplete;

    private boolean useResubmissionQuestion;

    private boolean nonIfs = false;
    private String nonIfsUrl;

    public CompetitionResource() {
        // no-arg constructor
    }

    public CompetitionResource(Long id, List<Long> applications, List<Long> questions, List<Long> sections, String name, String description, ZonedDateTime startDate, ZonedDateTime endDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public CompetitionResource(long id, String name, String description, ZonedDateTime startDate, ZonedDateTime endDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @JsonIgnore
    public boolean isOpen() {
        return CompetitionStatus.OPEN.equals(competitionStatus);
    }

    @JsonIgnore
    public boolean isSetupAndLive() {
        return Boolean.TRUE.equals(setupComplete) && startDate.isBefore(ZonedDateTime.now());
    }

    @JsonIgnore
    public boolean isSetupAndAfterNotifications() {
        return Boolean.TRUE.equals(setupComplete) && fundersPanelDate.isBefore(ZonedDateTime.now());
    }

    public CompetitionStatus getCompetitionStatus() {
        return competitionStatus;
    }

    public void setCompetitionStatus(CompetitionStatus competitionStatus) {
        this.competitionStatus = competitionStatus;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
        this.innovationAreaNames = innovationAreaNames;
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

    public Map<CompetitionSetupSection, Boolean> getSectionSetupStatus() {
        return sectionSetupStatus;
    }

    public void setSectionSetupStatus(Map<CompetitionSetupSection, Boolean> sectionSetupStatus) {
        this.sectionSetupStatus = sectionSetupStatus;
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
    
    public boolean isUseResubmissionQuestion() {
        return useResubmissionQuestion;
    }

    public void setUseResubmissionQuestion(boolean useResubmissionQuestion) {
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

    public Boolean getSetupComplete() {
        return setupComplete;
    }

    public void setSetupComplete(Boolean setupComplete) {
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
}