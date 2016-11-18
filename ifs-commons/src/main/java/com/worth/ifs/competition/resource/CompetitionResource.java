package com.worth.ifs.competition.resource;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime assessorAcceptsDate;
    private LocalDateTime assessorDeadlineDate;
    private LocalDateTime fundersPanelDate;
    private LocalDateTime fundersPanelEndDate;
    private LocalDateTime assessorFeedbackDate;
    private Status competitionStatus;
    @Min(0)
    @Max(100)
    private Integer maxResearchRatio;
    @Min(0)
    @Max(100)
    private Integer academicGrantPercentage;
    private Long competitionType;
    private String competitionTypeName;
    private Long executive;
    private Long leadTechnologist;
    private String leadTechnologistName;
    private Long innovationSector;
    private String innovationSectorName;
    private Long innovationArea;
    private String innovationAreaName;

    private String pafCode;
    private String budgetCode;
    private String code;

    private Boolean resubmission;
    private Boolean multiStream;
    private String streamName;
    private CollaborationLevel collaborationLevel;
    private LeadApplicantType leadApplicantType;
    private Set<Long> researchCategories;

    private boolean fullApplicationFinance;
    private boolean includeGrowthTable;

    private Integer assessorCount;
    private BigDecimal assessorPay;


    private Map<CompetitionSetupSection, Boolean> sectionSetupStatus = new HashMap<>();

    private String activityCode;
    private String innovateBudget;

    public CompetitionResource() {
        // no-arg constructor
    }

    public CompetitionResource(Long id, List<Long> applications, List<Long> questions, List<Long> sections, String name, String description, LocalDateTime startDate, LocalDateTime endDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public CompetitionResource(long id, String name, String description, LocalDateTime startDate, LocalDateTime endDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @JsonIgnore
    public boolean isOpen() {
        return Status.OPEN.equals(competitionStatus);
    }

    public Status getCompetitionStatus() {
        return competitionStatus;
    }

    public void setCompetitionStatus(Status competitionStatus) {
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

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public String assementEndDateDisplay() {
        return displayDate(getFundersPanelDate(), ASSESSMENT_DATE_FORMAT);
    }

    public String startDateDisplay() {
        return displayDate(getStartDate(), START_DATE_FORMAT);
    }

    private String displayDate(LocalDateTime date, DateTimeFormatter formatter) {
        if (date != null) {
            return date.format(formatter);
        }
        return "";
    }

    public LocalDateTime getAssessorAcceptsDate() {
        return assessorAcceptsDate;
    }

    public void setAssessorAcceptsDate(LocalDateTime assessorAcceptsDate) {
        this.assessorAcceptsDate = assessorAcceptsDate;
    }

    public LocalDateTime getAssessorDeadlineDate() {
        return assessorDeadlineDate;
    }

    public void setAssessorDeadlineDate(LocalDateTime assessorDeadlineDate) {
        this.assessorDeadlineDate = assessorDeadlineDate;
    }

    public LocalDateTime getFundersPanelDate() {
        return fundersPanelDate;
    }

    public void setFundersPanelDate(LocalDateTime fundersPanelDate) {
        this.fundersPanelDate = fundersPanelDate;
    }

    public LocalDateTime getAssessorFeedbackDate() {
        return assessorFeedbackDate;
    }

    public void setAssessorFeedbackDate(LocalDateTime assessorFeedbackDate) {
        this.assessorFeedbackDate = assessorFeedbackDate;
    }

    @JsonIgnore
    public long getDaysLeft() {
        return DAYS.between(LocalDateTime.now(), this.endDate);
    }

    @JsonIgnore
    public long getAssessmentDaysLeft() {
        return DAYS.between(LocalDateTime.now(), this.assessorDeadlineDate);
    }

    @JsonIgnore
    public long getTotalDays() {
        return DAYS.between(this.startDate, this.endDate);
    }

    @JsonIgnore
    public boolean isClosingSoon() {
        long hoursToGo = CLOSING_SOON_CHRONOUNIT.between(LocalDateTime.now(), this.endDate);
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

    public LocalDateTime getFundersPanelEndDate() {
        return fundersPanelEndDate;
    }

    public void setFundersPanelEndDate(LocalDateTime fundersPanelEndDate) {
        this.fundersPanelEndDate = fundersPanelEndDate;
    }

    public Long getExecutive() {
        return executive;
    }

    public void setExecutive(Long executive) {
        this.executive = executive;
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

    public void setCompetitionTypeName(String competitionTypeName) { this.competitionTypeName = competitionTypeName; }

    public Long getInnovationSector() {
        return innovationSector;
    }

    public void setInnovationSector(Long innovationSector) {
        this.innovationSector = innovationSector;
    }

    public Long getInnovationArea() {
        return innovationArea;
    }

    public void setInnovationArea(Long innovationArea) {
        this.innovationArea = innovationArea;
    }

    public String getInnovationSectorName() {
        return innovationSectorName;
    }

    public void setInnovationSectorName(String innovationSectorName) {
        this.innovationSectorName = innovationSectorName;
    }

    public String getInnovationAreaName() {
        return innovationAreaName;
    }

    public void setInnovationAreaName(String innovationAreaName) {
        this.innovationAreaName = innovationAreaName;
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

    public LeadApplicantType getLeadApplicantType() {
        return leadApplicantType;
    }

    public void setLeadApplicantType(LeadApplicantType leadApplicantType) {
        this.leadApplicantType = leadApplicantType;
    }

    public Map<CompetitionSetupSection, Boolean> getSectionSetupStatus() {
        return sectionSetupStatus;
    }

    public void setSectionSetupStatus(Map<CompetitionSetupSection, Boolean> sectionSetupStatus) {
        this.sectionSetupStatus = sectionSetupStatus;
    }

    public enum Status {
        COMPETITION_SETUP,READY_TO_OPEN,OPEN,CLOSED,IN_ASSESSMENT,FUNDERS_PANEL,ASSESSOR_FEEDBACK,PROJECT_SETUP
    }

    public String getActivityCode() {
        return activityCode;
    }

    public void setActivityCode(String activityCode) {
        this.activityCode = activityCode;
    }

    public String getInnovateBudget() {
        return innovateBudget;
    }

    public void setInnovateBudget(String innovateBudget) {
        this.innovateBudget = innovateBudget;
    }

    public List<CompetitionFunderResource> getFunders() {
        return funders;
    }

    public void setFunders(List<CompetitionFunderResource> funders) {
        this.funders = funders;
    }

    public boolean isFullApplicationFinance() {
        return fullApplicationFinance;
    }

    public void setFullApplicationFinance(boolean fullApplicationFinance) {
        this.fullApplicationFinance = fullApplicationFinance;
    }

    public boolean isIncludeGrowthTable() {
        return includeGrowthTable;
    }

    public void setIncludeGrowthTable(boolean includeGrowthTable) {
        this.includeGrowthTable = includeGrowthTable;
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
}