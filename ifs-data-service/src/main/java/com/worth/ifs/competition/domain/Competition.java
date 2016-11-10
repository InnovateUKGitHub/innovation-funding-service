package com.worth.ifs.competition.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.domain.Section;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.category.domain.Category;
import com.worth.ifs.competition.resource.*;
import com.worth.ifs.invite.domain.ProcessActivity;
import com.worth.ifs.user.domain.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.worth.ifs.competition.resource.CompetitionStatus.*;

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

    @OneToMany(mappedBy="competition")
    private List<Application> applications = new ArrayList<>();

    @OneToMany(mappedBy="competition")
    private List<Question> questions = new ArrayList<>();

    @OneToMany(mappedBy="competition")
    private List<CompetitionFunder> funders = new ArrayList<>();

    @OneToMany(mappedBy="competition")
    @OrderBy("priority ASC")
    private List<Section> sections = new ArrayList<>();

    private String name;

    @Column( length = 5000 )
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="competitionTypeId", referencedColumnName="id")
    private CompetitionType competitionType;

    @OneToMany(mappedBy = "competition")
    private List<Milestone> milestones = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="executiveUserId", referencedColumnName="id")
    private User executive;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="leadTechnologistUserId", referencedColumnName="id")
    private User leadTechnologist;

    private String pafCode;
    private String budgetCode;
    private String code;

    private Integer maxResearchRatio;
    private Integer academicGrantPercentage;

    @Transient
    private Category innovationSector;
    @Transient
    private Category innovationArea;
    @Transient
    private Set<Category> researchCategories;

    private String activityCode;
    private String innovateBudget;

    private boolean multiStream;
    private Boolean resubmission;

    private String streamName;
    @Enumerated(EnumType.STRING)
    private CollaborationLevel collaborationLevel;
    @Enumerated(EnumType.STRING)
    private LeadApplicantType leadApplicantType;
    
    @ElementCollection
    @JoinTable(name="competition_setup_status", joinColumns=@JoinColumn(name="competition_id"))
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn (name="section")
    @Column(name="status")
    private Map<CompetitionSetupSection, Boolean> sectionSetupStatus = new HashMap<>();

    private boolean fullApplicationFinance = true;
    private boolean includeGrowthTable = true;

    private boolean setupComplete;

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
        LocalDateTime today = dateProvider.provideDate();
        if (setupComplete) {
            if (getStartDate() == null || getStartDate().isAfter(today)) {
                return READY_TO_OPEN;
            } else if (getEndDate() != null && getEndDate().isAfter(today)) {
                return OPEN;
            } else if (getAssessorAcceptsDate() != null && getAssessorAcceptsDate().isAfter(today)) {
                // TODO INFUND-5199 - Should not be using the assessor accepts deadline at all here
                // TODO INFUND-5199 - The competition is closed if we're past the submission date, but haven't distributed applications
                return CLOSED;
            } else if (getFundersPanelDate() != null && getFundersPanelDate().isAfter(today)) {
                return IN_ASSESSMENT;
            } else if (getFundersPanelEndDate() == null || getFundersPanelEndDate().isAfter(today)) {
                return FUNDERS_PANEL;
            } else if (getAssessorFeedbackDate() == null || getAssessorFeedbackDate().isAfter(today)) {
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


    public void addApplication(Application... apps){
        if(applications == null){
            applications = new ArrayList<>();
        }
        this.applications.addAll(Arrays.asList(apps));
    }

    public Long getId() {
        return id;
    }

    public String getFormattedId() {
        return ApplicationResource.formatter.format(id);
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
    public long getDaysLeft(){
        return getDaysBetween(LocalDateTime.now(), this.getEndDate());
    }
    @JsonIgnore
    public long getTotalDays(){
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
    public List<Question> getQuestions(){return questions;}

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getEndDate() {
        return getMilestoneValue(MilestoneType.SUBMISSION_DATE);
    }

    public void setEndDate(LocalDateTime endDate) {
    	setMilestoneValue(MilestoneType.SUBMISSION_DATE, endDate);
    }

    public LocalDateTime getStartDate() {
    	return getMilestoneValue(MilestoneType.OPEN_DATE);
    }

    public void setStartDate(LocalDateTime startDate) {
        setMilestoneValue(MilestoneType.OPEN_DATE, startDate);
    }

    public LocalDateTime getAssessorAcceptsDate() {
    	return getMilestoneValue(MilestoneType.ASSESSOR_ACCEPTS);
    }

    public void setAssessorAcceptsDate(LocalDateTime assessorAcceptsDate){
    	setMilestoneValue(MilestoneType.ASSESSOR_ACCEPTS, assessorAcceptsDate);
    }

    public LocalDateTime getAssessorDeadlineDate() {
        return getMilestoneValue(MilestoneType.ASSESSOR_DEADLINE);
    }

    public void setAssessorDeadlineDate(LocalDateTime assessorDeadlineDate){
        setMilestoneValue(MilestoneType.ASSESSOR_DEADLINE, assessorDeadlineDate);
    }

    public LocalDateTime getFundersPanelDate() {
        return getMilestoneValue(MilestoneType.FUNDERS_PANEL);
    }

    public void setFundersPanelDate(LocalDateTime fundersPanelDate) {
        setMilestoneValue(MilestoneType.FUNDERS_PANEL, fundersPanelDate);
    }

    public LocalDateTime getAssessorFeedbackDate() {
    	return getMilestoneValue(MilestoneType.ASSESSOR_DEADLINE);
    }

    public void setAssessorFeedbackDate(LocalDateTime assessorFeedbackDate) {
    	setMilestoneValue(MilestoneType.ASSESSOR_DEADLINE, assessorFeedbackDate);
    }

    public LocalDateTime getFundersPanelEndDate() {
    	return getMilestoneValue(MilestoneType.NOTIFICATIONS);
	}

    public void setFundersPanelEndDate(LocalDateTime fundersPanelEndDate) {
    	setMilestoneValue(MilestoneType.NOTIFICATIONS, fundersPanelEndDate);
	}

    private void setMilestoneValue(MilestoneType MilestoneType, LocalDateTime dateTime) {
		Milestone milestone = milestones.stream().filter(m -> MilestoneType.equals(m.getType())).findAny().orElseGet(() -> {
			Milestone m = new Milestone();
			m.setCompetition(this);
			m.setType(MilestoneType);
			milestones.add(m);
			return m;
		});

		milestone.setDate(dateTime);
	}

	private LocalDateTime getMilestoneValue(MilestoneType milestoneType) {
		Optional<Milestone> milestone = milestones.stream().filter(m -> milestoneType.equals(m.getType())).findAny();

		if(milestone.isPresent()) {
			return milestone.get().getDate();
		}

		return null;
	}

    private long getDaysBetween(LocalDateTime dateA, LocalDateTime dateB) {
        return ChronoUnit.DAYS.between(dateA, dateB);
    }

    private long getDaysLeftPercentage(long daysLeft, long totalDays ) {
        if(daysLeft <= 0){
            return 100;
        }
        double deadlineProgress = 100-( ( (double)daysLeft/(double)totalDays )* 100);
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

    public Category getInnovationSector() {
        return innovationSector;
    }

    public void setInnovationSector(Category innovationSector) {
        this.innovationSector = innovationSector;
    }

    public Category getInnovationArea() {
        return innovationArea;
    }

    public void setInnovationArea(Category innovationArea) {
        this.innovationArea = innovationArea;
    }
    
    public Set<Category> getResearchCategories() {
		return researchCategories;
	}
    
    public void setResearchCategories(Set<Category> researchCategories) {
		this.researchCategories = researchCategories;
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

    public String getInnovateBudget() {
        return innovateBudget;
    }

    public void setInnovateBudget(String innovateBudget) {
        this.innovateBudget = innovateBudget;
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

    public boolean isIncludeGrowthTable() {
        return includeGrowthTable;
    }

    public void setIncludeGrowthTable(boolean includeGrowthTable) {
        this.includeGrowthTable = includeGrowthTable;
    }

    public void distributeApplications() {
//        if (getCompetitionStatus() != CompetitionStatus.CLOSED) {
//            throw new IllegalStateException("Tried to distribute applications when in competitionStatus=" +
//                    getCompetitionStatus() + ". Applications can only be distributed when competitionStatus=" +
//                    CompetitionStatus.CLOSED);
//        }
//        this.applicationsDistributed = true;
    }

    public void closeAssessment() {
//        if (getCompetitionStatus() != CompetitionStatus.IN_ASSESSMENT) {
//
//        }
//
//        this.assessmentClosed = true;
    }
}

