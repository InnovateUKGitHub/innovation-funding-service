package org.innovateuk.ifs.competition.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.form.domain.Section;
import org.innovateuk.ifs.category.domain.*;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.organisation.domain.OrganisationType;
import org.innovateuk.ifs.user.domain.ProcessActivity;
import org.innovateuk.ifs.user.domain.User;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.competition.resource.CompetitionStatus.*;
import static org.innovateuk.ifs.competition.resource.MilestoneType.*;

/**
 * Competition defines database relations and a model to use client side and server side.
 */
@Entity
public class Competition implements ProcessActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(mappedBy = "competition", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Question> questions = new ArrayList<>();

    @OneToMany(mappedBy = "competition", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<CompetitionFunder> funders = new ArrayList<>();

    @OneToMany(mappedBy = "competition", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @OrderBy("priority ASC")
    private List<Section> sections = new ArrayList<>();

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competitionTypeId", referencedColumnName = "id")
    private CompetitionType competitionType;

    private Integer assessorCount;
    private BigDecimal assessorPay;

    @OneToMany(mappedBy = "competition", cascade = CascadeType.PERSIST)
    private List<Milestone> milestones = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "executiveUserId", referencedColumnName = "id")
    private User executive;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leadTechnologistUserId", referencedColumnName = "id")
    private User leadTechnologist;

    @OneToOne(mappedBy = "template", fetch = FetchType.LAZY)
    private CompetitionType templateForType;

    private String pafCode;
    private String budgetCode;
    private String code;

    private Integer maxResearchRatio;
    private Integer academicGrantPercentage;

    private Integer maxProjectDuration;
    private Integer minProjectDuration;

    @OneToOne(mappedBy = "competition", cascade = CascadeType.ALL, orphanRemoval = true)
    private CompetitionInnovationSectorLink innovationSector;

    @OneToMany(mappedBy = "competition", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CompetitionInnovationAreaLink> innovationAreas = new HashSet<>();

    @OneToMany(mappedBy = "competition", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CompetitionResearchCategoryLink> researchCategories = new HashSet<>();

    private String activityCode;

    private boolean multiStream;
    private Boolean resubmission;
    private Boolean hasAssessmentPanel;
    private Boolean hasInterviewStage;

    @Enumerated(EnumType.STRING)
    private AssessorFinanceView assessorFinanceView = AssessorFinanceView.OVERVIEW;

    private String streamName;
    @Enumerated(EnumType.STRING)
    private CollaborationLevel collaborationLevel;

    @ManyToMany
    @JoinTable(name = "lead_applicant_type",
            joinColumns = @JoinColumn(name = "competition_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "organisation_type_id", referencedColumnName = "id"))
    private List<OrganisationType> leadApplicantTypes;

    private Boolean fullApplicationFinance = true;
    private Boolean setupComplete;

    private Boolean useResubmissionQuestion = true;

    private boolean template = false;

    private boolean nonIfs = false;
    private String nonIfsUrl;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "termsAndConditionsId", referencedColumnName = "id")
    private GrantTermsAndConditions termsAndConditions;

    private boolean locationPerPartner = true;

    private Boolean stateAid;

    public Competition() {
        setupComplete = false;
    }

    public Competition(Long id,
                       List<Question> questions,
                       List<Section> sections,
                       String name,
                       ZonedDateTime startDate,
                       ZonedDateTime endDate,
                       ZonedDateTime registrationDate,
                       GrantTermsAndConditions termsAndConditions) {
        this.id = id;
        this.questions = questions;
        this.sections = sections;
        this.name = name;
        this.setStartDate(startDate);
        this.setEndDate(endDate);
        this.setRegistrationDate(registrationDate);
        this.setupComplete = true;
        this.termsAndConditions = termsAndConditions;
    }

    public Competition(long id, String name, ZonedDateTime startDate, ZonedDateTime endDate) {
        this.id = id;
        this.name = name;
        this.setStartDate(startDate);
        this.setEndDate(endDate);
        this.setupComplete = true;
    }

    public CompetitionStatus getCompetitionStatus() {
        if (setupComplete != null && setupComplete) {
            if (!isMilestoneReached(OPEN_DATE)) {
                return READY_TO_OPEN;
            } else if (!isMilestoneReached(SUBMISSION_DATE)) {
                return OPEN;
            } else if (!isMilestoneReached(ASSESSORS_NOTIFIED)) {
                return CLOSED;
            } else if (!isMilestoneReached(MilestoneType.ASSESSMENT_CLOSED)) {
                return IN_ASSESSMENT;
            } else if (!isMilestoneReached(MilestoneType.NOTIFICATIONS)) {
                return CompetitionStatus.FUNDERS_PANEL;
            } else if (!isMilestoneReached(MilestoneType.FEEDBACK_RELEASED)) {
                return ASSESSOR_FEEDBACK;
            } else {
                return PROJECT_SETUP;
            }
        } else {
            return COMPETITION_SETUP;
        }
    }

    public List<Section> getSections() {
        return sections;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Boolean getSetupComplete() {
        return setupComplete;
    }

    public void setSetupComplete(Boolean setupComplete) {
        this.setupComplete = setupComplete;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    @JsonIgnore
    public long getDaysLeft() {
        return getDaysBetween(ZonedDateTime.now(), this.getEndDate());
    }

    @JsonIgnore
    public long getTotalDays() {
        return getDaysBetween(this.getStartDate(), this.getEndDate());
    }

    @JsonIgnore
    public long getStartDateToEndDatePercentage() {
        return getDaysLeftPercentage(getDaysLeft(), getTotalDays());
    }

    @JsonIgnore
    public List<Question> getQuestions() {
        return questions;
    }

    @JsonIgnore
    public boolean inProjectSetup() { return PROJECT_SETUP.equals(getCompetitionStatus()); }

    public void setName(String name) {
        this.name = name;
    }

    public ZonedDateTime getEndDate() {
        return getMilestoneDate(SUBMISSION_DATE).orElse(null);
    }

    public void setEndDate(ZonedDateTime endDate) {
        setMilestoneDate(SUBMISSION_DATE, endDate);
    }

    public ZonedDateTime getRegistrationDate() {
        return getMilestoneDate(REGISTRATION_DATE).orElse(null);
    }

    public void setRegistrationDate(ZonedDateTime endDate) {
        setMilestoneDate(REGISTRATION_DATE, endDate);
    }

    public ZonedDateTime getStartDate() {
        return getMilestoneDate(OPEN_DATE).orElse(null);
    }

    public void setStartDate(ZonedDateTime startDate) {
        setMilestoneDate(OPEN_DATE, startDate);
    }

    public ZonedDateTime getAssessorAcceptsDate() {
        return getMilestoneDate(ASSESSOR_ACCEPTS).orElse(null);
    }

    public void setAssessorAcceptsDate(ZonedDateTime assessorAcceptsDate) {
        setMilestoneDate(ASSESSOR_ACCEPTS, assessorAcceptsDate);
    }

    public ZonedDateTime getAssessorDeadlineDate() {
        return getMilestoneDate(MilestoneType.ASSESSOR_DEADLINE).orElse(null);
    }

    public void setAssessorDeadlineDate(ZonedDateTime assessorDeadlineDate) {
        setMilestoneDate(MilestoneType.ASSESSOR_DEADLINE, assessorDeadlineDate);
    }

    public ZonedDateTime getReleaseFeedbackDate() {
        return getMilestoneDate(MilestoneType.RELEASE_FEEDBACK).orElse(null);
    }

    public void setReleaseFeedbackDate(ZonedDateTime releaseFeedbackDate) {
        setMilestoneDate(MilestoneType.RELEASE_FEEDBACK, releaseFeedbackDate);
    }

    public void setFeedbackReleasedDate(ZonedDateTime feedbackReleasedDate) {
        setMilestoneDate(MilestoneType.FEEDBACK_RELEASED, feedbackReleasedDate);
    }

    public ZonedDateTime getFeedbackReleasedDate() {
        return getMilestoneDate(MilestoneType.FEEDBACK_RELEASED).orElse(null);
    }

    public ZonedDateTime getAssessmentPanelDate() {
        return getMilestoneDate(MilestoneType.ASSESSMENT_PANEL).orElse(null);
    }

    public void setAssessmentPanelDate(ZonedDateTime assessmentPanelDate) {
        setMilestoneDate(MilestoneType.ASSESSMENT_PANEL, assessmentPanelDate);
    }

    public ZonedDateTime getPanelDate() {
        return getMilestoneDate(MilestoneType.PANEL_DATE).orElse(null);
    }

    public void setPanelDate(ZonedDateTime panelDate) {
        setMilestoneDate(MilestoneType.PANEL_DATE, panelDate);
    }

    public ZonedDateTime getFundersPanelDate() {
        return getMilestoneDate(MilestoneType.FUNDERS_PANEL).orElse(null);
    }

    public void setFundersPanelDate(ZonedDateTime fundersPanelDate) {
        setMilestoneDate(MilestoneType.FUNDERS_PANEL, fundersPanelDate);
    }

    public ZonedDateTime getAssessorFeedbackDate() {
        return getMilestoneDate(MilestoneType.ASSESSOR_DEADLINE).orElse(null);
    }

    public void setAssessorFeedbackDate(ZonedDateTime assessorFeedbackDate) {
        setMilestoneDate(MilestoneType.ASSESSOR_DEADLINE, assessorFeedbackDate);
    }

    public ZonedDateTime getFundersPanelEndDate() {
        return getMilestoneDate(MilestoneType.NOTIFICATIONS).orElse(null);
    }

    public void setFundersPanelEndDate(ZonedDateTime fundersPanelEndDate) {
        setMilestoneDate(MilestoneType.NOTIFICATIONS, fundersPanelEndDate);
    }

    public ZonedDateTime getAssessorBriefingDate() {
        return getMilestoneDate(MilestoneType.ASSESSOR_BRIEFING).orElse(null);
    }

    public void setAssessorBriefingDate(ZonedDateTime assessorBriefingDate) {
        setMilestoneDate(MilestoneType.ASSESSOR_BRIEFING, assessorBriefingDate);
    }

    private void setMilestoneDate(MilestoneType milestoneType, ZonedDateTime dateTime) {
        Milestone milestone = milestones.stream().filter(m -> m.getType() == milestoneType).findAny().orElseGet(() -> {
            Milestone m = new Milestone(milestoneType,this);
            milestones.add(m);
            return m;
        });
        // IFS-2263 truncation to avoid mysql rounding up datetimes to the nearest second
        milestone.setDate(dateTime == null ? null : dateTime.truncatedTo(ChronoUnit.SECONDS));
    }

    private Optional<Milestone> getMilestone(MilestoneType milestoneType) {
        return milestones.stream().filter(m -> m.getType() == milestoneType).findAny();
    }

    private boolean isMilestoneReached(MilestoneType milestoneType) {
        ZonedDateTime today = ZonedDateTime.now();
        return getMilestone(milestoneType).map(milestone -> milestone.isReached(today)).orElse(false);
    }

    private boolean isMilestoneSet(MilestoneType milestoneType) {
        return getMilestone(milestoneType).isPresent();
    }

    private Optional<ZonedDateTime> getMilestoneDate(MilestoneType milestoneType) {
        return getMilestone(milestoneType).map(Milestone::getDate);
    }

    private long getDaysBetween(ZonedDateTime dateA, ZonedDateTime dateB) {
        return ChronoUnit.DAYS.between(dateA, dateB);
    }

    private long getDaysLeftPercentage(long daysLeft, long totalDays) {
        if (daysLeft <= 0) {
            return 100;
        }
        double deadlineProgress = 100 - (((double) daysLeft / (double) totalDays) * 100);
        return (long) deadlineProgress;
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

    public void setId(Long id) {
        this.id = id;
    }

    public User getExecutive() {
        return executive;
    }

    public void setExecutive(User executive) {
        this.executive = executive;
    }

    public User getLeadTechnologist() {
        return leadTechnologist;
    }

    public void setLeadTechnologist(User leadTechnologist) {
        this.leadTechnologist = leadTechnologist;
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

    public CompetitionType getCompetitionType() {
        return competitionType;
    }

    public void setCompetitionType(CompetitionType competitionType) {
        this.competitionType = competitionType;
    }

    public InnovationSector getInnovationSector() {
        if (innovationSector == null) {
            return null;
        }

        return innovationSector.getCategory();
    }

    public void setInnovationSector(InnovationSector innovationSector) {
        if (innovationSector == null) {
            return;
        }

        this.innovationSector = new CompetitionInnovationSectorLink(this, innovationSector);
    }

    public Set<InnovationArea> getInnovationAreas() {
        return innovationAreas.stream()
                .map(CompetitionInnovationAreaLink::getCategory)
                .collect(Collectors.toSet());
    }

    public void addInnovationArea(InnovationArea innovationArea) {
        if (innovationArea == null) {
            throw new NullPointerException("innovationArea cannot be null");
        }

        this.innovationAreas.add(new CompetitionInnovationAreaLink(this, innovationArea));
    }

    public void setInnovationAreas(Set<InnovationArea> innovationAreas) {
        this.innovationAreas = new HashSet<>();

        if (innovationAreas == null) {
            return;
        }

        innovationAreas.forEach(this::addInnovationArea);
    }

    public Set<ResearchCategory> getResearchCategories() {
        return researchCategories.stream()
                .map(CompetitionResearchCategoryLink::getCategory)
                .collect(Collectors.toSet());
    }

    public void addResearchCategory(ResearchCategory researchCategory) {
        if (researchCategory == null) {
            throw new NullPointerException("researchCategory cannot be null");
        }

        this.researchCategories.add(new CompetitionResearchCategoryLink(this, researchCategory));
    }

    public void setResearchCategories(Set<ResearchCategory> researchCategories) {
        this.researchCategories = new HashSet<>();

        if (researchCategories == null) {
            return;
        }

        researchCategories.forEach(this::addResearchCategory);
	}

    public List<Milestone> getMilestones() {
        return milestones;
    }

    public void setMilestones(List<Milestone> milestones) {
        this.milestones = milestones;
    }

    public boolean isMultiStream() {
        return multiStream;
    }

    public void setMultiStream(boolean multiStream) {
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

    public List<OrganisationType> getLeadApplicantTypes() {
        return leadApplicantTypes;
    }

    public void setLeadApplicantTypes(List<OrganisationType> leadApplicantTypes) {
        this.leadApplicantTypes = leadApplicantTypes;
    }

    public String getActivityCode() {
        return activityCode;
    }

    public void setActivityCode(String activityCode) {
        this.activityCode = activityCode;
    }

    public List<CompetitionFunder> getFunders() {
        return funders;
    }

    public void setFunders(List<CompetitionFunder> funders) {
        this.funders = funders;
    }

    public String startDateDisplay() {
        return displayDate(getStartDate(), CompetitionResource.START_DATE_FORMAT);
    }

    private String displayDate(ZonedDateTime date, DateTimeFormatter formatter) {
        if (date != null) {
            return date.format(formatter);
        }
        return "";
    }

    public Boolean isFullApplicationFinance() {
        return fullApplicationFinance;
    }

    public void setFullApplicationFinance(Boolean fullApplicationFinance) {
        this.fullApplicationFinance = fullApplicationFinance;
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

    public boolean isTemplate() {
        return template;
    }

    public void setTemplate(boolean template) {
        this.template = template;
    }

    public Boolean getUseResubmissionQuestion() {
        return useResubmissionQuestion;
    }

    public void setUseResubmissionQuestion(Boolean useResubmissionQuestion) {
        this.useResubmissionQuestion = useResubmissionQuestion;
    }

    public void notifyAssessors(ZonedDateTime date) {
        if (getCompetitionStatus() == CompetitionStatus.IN_ASSESSMENT) {
            return;
        }

        if (getCompetitionStatus() != CompetitionStatus.CLOSED) {
            throw new IllegalStateException("Tried to notify assessors when in competitionStatus=" +
                    getCompetitionStatus() + ". Applications can only be distributed when competitionStatus=" +
                    CompetitionStatus.CLOSED);
        }
        setMilestoneDate(MilestoneType.ASSESSORS_NOTIFIED, date);
    }

    public void releaseFeedback(ZonedDateTime date) {
        setMilestoneDate(MilestoneType.FEEDBACK_RELEASED, date);
    }

    public void closeAssessment(ZonedDateTime date) {
        setMilestoneDate(MilestoneType.ASSESSMENT_CLOSED, date);
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

    public Boolean isHasInterviewStage(){
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

    public GrantTermsAndConditions getTermsAndConditions() {
        return termsAndConditions;
    }

    public void setTermsAndConditions(GrantTermsAndConditions termsAndConditions) {
        this.termsAndConditions = termsAndConditions;
    }

    public boolean isLocationPerPartner() {
        return locationPerPartner;
    }

    public void setLocationPerPartner(boolean locationPerPartner) {
        this.locationPerPartner = locationPerPartner;
    }

    public Integer getMaxProjectDuration() {
        return maxProjectDuration;
    }

    public void setMaxProjectDuration(Integer maxProjectDuration) {
        this.maxProjectDuration = maxProjectDuration;
    }

    public Integer getMinProjectDuration() {
        return minProjectDuration;
    }

    public void setMinProjectDuration(Integer minProjectDuration) {
        this.minProjectDuration = minProjectDuration;
    }

    public Boolean getStateAid() {
        return stateAid;
    }

    public void setStateAid(final Boolean stateAid) {
        this.stateAid = stateAid;
    }

    public boolean getUseNewApplicantMenu() {

        return questions.stream().anyMatch(
                question -> (question.getQuestionSetupType() == CompetitionSetupQuestionType.APPLICATION_TEAM)
        );
    }
}

