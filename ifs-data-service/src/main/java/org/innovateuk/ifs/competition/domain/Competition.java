package org.innovateuk.ifs.competition.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.application.domain.Section;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.category.domain.*;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.innovateuk.ifs.invite.domain.ProcessActivity;
import org.innovateuk.ifs.user.domain.User;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    @Transient
    private DateProvider dateProvider = new DateProvider();

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(mappedBy = "competition")
    private List<Application> applications = new ArrayList<>();

    @OneToMany(mappedBy = "competition")
    private List<Question> questions = new ArrayList<>();

    @OneToMany(mappedBy = "competition")
    private List<CompetitionFunder> funders = new ArrayList<>();

    @OneToMany(mappedBy = "competition")
    @OrderBy("priority ASC")
    private List<Section> sections = new ArrayList<>();

    private String name;

    @Column(length = 5000)
    private String description;

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

    @OneToOne(mappedBy = "template")
    private CompetitionType templateForType;

    private String pafCode;
    private String budgetCode;
    private String code;

    private Integer maxResearchRatio;
    private Integer academicGrantPercentage;

    @OneToOne(mappedBy = "competition", cascade = CascadeType.ALL, orphanRemoval = true)
    private CompetitionInnovationSectorLink innovationSector;

    @OneToMany(mappedBy = "competition", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CompetitionInnovationAreaLink> innovationAreas = new HashSet<>();

    @OneToMany(mappedBy = "competition", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CompetitionResearchCategoryLink> researchCategories = new HashSet<>();

    private String activityCode;

    private boolean multiStream;
    private Boolean resubmission;

    private String streamName;
    @Enumerated(EnumType.STRING)
    private CollaborationLevel collaborationLevel;
    @Enumerated(EnumType.STRING)
    private LeadApplicantType leadApplicantType;

    @ElementCollection
    @JoinTable(name = "competition_setup_status", joinColumns = @JoinColumn(name = "competition_id"))
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name = "section")
    @Column(name = "status")
    private Map<CompetitionSetupSection, Boolean> sectionSetupStatus = new HashMap<>();

    private boolean fullApplicationFinance = true;
    private Boolean setupComplete;

    private boolean useResubmissionQuestion = true;

    private boolean template = false;

    private boolean nonIfs = false;
    private String nonIfsUrl;

    public Competition() {
        setupComplete = false;
    }

    public Competition(Long id, List<Application> applications, List<Question> questions, List<Section> sections, String name, String description, LocalDateTime startDate, LocalDateTime endDate) {
        this.id = id;
        this.applications = applications;
        this.questions = questions;
        this.sections = sections;
        this.name = name;
        this.description = description;
        this.setStartDate(startDate);
        this.setEndDate(endDate);
        this.setupComplete = true;
    }

    public Competition(long id, String name, String description, LocalDateTime startDate, LocalDateTime endDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.setStartDate(startDate);
        this.setEndDate(endDate);
        this.setupComplete = true;
    }

    public CompetitionStatus getCompetitionStatus() {
        if (setupComplete) {
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

    public String getDescription() {
        return description;
    }


    public void addApplication(Application... apps) {
        if (applications == null) {
            applications = new ArrayList<>();
        }
        this.applications.addAll(Arrays.asList(apps));
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

    public void setApplications(List<Application> applications) {
        this.applications = applications;
    }

    @JsonIgnore
    public long getDaysLeft() {
        return getDaysBetween(LocalDateTime.now(), this.getEndDate());
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
    public List<Application> getApplications() {
        return applications;
    }

    @JsonIgnore
    public List<Question> getQuestions() {
        return questions;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getEndDate() {
        return getMilestoneDate(SUBMISSION_DATE).orElse(null);
    }

    public void setEndDate(LocalDateTime endDate) {
        setMilestoneDate(SUBMISSION_DATE, endDate);
    }

    public LocalDateTime getStartDate() {
        return getMilestoneDate(OPEN_DATE).orElse(null);
    }

    public void setStartDate(LocalDateTime startDate) {
        setMilestoneDate(OPEN_DATE, startDate);
    }

    public LocalDateTime getAssessorAcceptsDate() {
        return getMilestoneDate(ASSESSOR_ACCEPTS).orElse(null);
    }

    public void setAssessorAcceptsDate(LocalDateTime assessorAcceptsDate) {
        setMilestoneDate(ASSESSOR_ACCEPTS, assessorAcceptsDate);
    }

    public LocalDateTime getAssessorDeadlineDate() {
        return getMilestoneDate(MilestoneType.ASSESSOR_DEADLINE).orElse(null);
    }

    public void setAssessorDeadlineDate(LocalDateTime assessorDeadlineDate) {
        setMilestoneDate(MilestoneType.ASSESSOR_DEADLINE, assessorDeadlineDate);
    }

    public LocalDateTime getReleaseFeedbackDate() {
        return getMilestoneDate(MilestoneType.RELEASE_FEEDBACK).orElse(null);
    }

    public void setReleaseFeedbackDate(LocalDateTime releaseFeedbackDate) {
        setMilestoneDate(MilestoneType.RELEASE_FEEDBACK, releaseFeedbackDate);
    }

    public LocalDateTime getFundersPanelDate() {
        return getMilestoneDate(MilestoneType.FUNDERS_PANEL).orElse(null);
    }

    public void setFundersPanelDate(LocalDateTime fundersPanelDate) {
        setMilestoneDate(MilestoneType.FUNDERS_PANEL, fundersPanelDate);
    }

    public LocalDateTime getAssessorFeedbackDate() {
        return getMilestoneDate(MilestoneType.ASSESSOR_DEADLINE).orElse(null);
    }

    public void setAssessorFeedbackDate(LocalDateTime assessorFeedbackDate) {
        setMilestoneDate(MilestoneType.ASSESSOR_DEADLINE, assessorFeedbackDate);
    }

    public LocalDateTime getFundersPanelEndDate() {
        return getMilestoneDate(MilestoneType.NOTIFICATIONS).orElse(null);
    }

    public void setFundersPanelEndDate(LocalDateTime fundersPanelEndDate) {
        setMilestoneDate(MilestoneType.NOTIFICATIONS, fundersPanelEndDate);
    }

    public LocalDateTime getAssessorBriefingDate() {
        return getMilestoneDate(MilestoneType.ASSESSOR_BRIEFING).orElse(null);
    }

    public void setAssessorBriefingDate(LocalDateTime assessorBriefingDate) {
        setMilestoneDate(MilestoneType.ASSESSOR_BRIEFING, assessorBriefingDate);
    }

    private void setMilestoneDate(MilestoneType milestoneType, LocalDateTime dateTime) {
        Milestone milestone = milestones.stream().filter(m -> m.getType() == milestoneType).findAny().orElseGet(() -> {
            Milestone m = new Milestone();
            m.setType(milestoneType);
            m.setCompetition(this);
            milestones.add(m);
            return m;
        });
        milestone.setDate(dateTime);
    }

    private Optional<Milestone> getMilestone(MilestoneType milestoneType) {
        return milestones.stream().filter(m -> m.getType() == milestoneType).findAny();
    }

    private boolean isMilestoneReached(MilestoneType milestoneType) {
        LocalDateTime today = dateProvider.provideDate();
        return getMilestone(milestoneType).map(milestone -> milestone.isReached(today)).orElse(false);
    }

    private boolean isMilestoneSet(MilestoneType milestoneType) {
        return getMilestone(milestoneType).isPresent();
    }

    private Optional<LocalDateTime> getMilestoneDate(MilestoneType milestoneType) {
        return getMilestone(milestoneType).map(Milestone::getDate);
    }

    private long getDaysBetween(LocalDateTime dateA, LocalDateTime dateB) {
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

    protected void setDateProvider(DateProvider dateProvider) {
        this.dateProvider = dateProvider;
    }

    protected static class DateProvider {
        public LocalDateTime provideDate() {
            return LocalDateTime.now();
        }
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

    public LeadApplicantType getLeadApplicantType() {
        return leadApplicantType;
    }

    public void setLeadApplicantType(LeadApplicantType leadApplicantType) {
        this.leadApplicantType = leadApplicantType;
    }

    public Map<CompetitionSetupSection, Boolean> getSectionSetupStatus() {
        return sectionSetupStatus;
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

    private String displayDate(LocalDateTime date, DateTimeFormatter formatter) {
        if (date != null) {
            return date.format(formatter);
        }
        return "";
    }

    public boolean isFullApplicationFinance() {
        return fullApplicationFinance;
    }

    public void setFullApplicationFinance(boolean fullApplicationFinance) {
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

    public boolean isUseResubmissionQuestion() {
        return useResubmissionQuestion;
    }

    public void setUseResubmissionQuestion(boolean useResubmissionQuestion) {
        this.useResubmissionQuestion = useResubmissionQuestion;
    }

    public void notifyAssessors(LocalDateTime date) {
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

    public void releaseFeedback(LocalDateTime date) {
        setMilestoneDate(MilestoneType.FEEDBACK_RELEASED, date);
    }

    public void closeAssessment(LocalDateTime date) {
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
}

