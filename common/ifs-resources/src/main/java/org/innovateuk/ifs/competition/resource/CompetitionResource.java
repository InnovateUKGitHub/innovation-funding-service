package org.innovateuk.ifs.competition.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.project.grantofferletter.template.resource.GolTemplateResource;
import org.innovateuk.ifs.project.internal.ProjectSetupStage;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.*;
import static org.innovateuk.ifs.util.TimeZoneUtil.toUkTimeZone;

public class CompetitionResource implements ApplicationConfiguration, ProjectConfiguration {

    public static final DateTimeFormatter START_DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final String H2020_TYPE_NAME = "Horizon 2020";

    private static final ChronoUnit CLOSING_SOON_CHRONOUNIT = ChronoUnit.HOURS;
    private static final int CLOSING_SOON_AMOUNT = 3;
    private static final DateTimeFormatter ASSESSMENT_DATE_FORMAT = DateTimeFormatter.ofPattern("MMMM yyyy");


    private Long id;
    private List<Long> assessmentPeriods = new ArrayList<>();
    private List<Long> milestones = new ArrayList<>();
    private List<CompetitionFunderResource> funders = new ArrayList<>();
    private List<CompetitionDocumentResource> competitionDocuments = new ArrayList<>();
    private List<ProjectSetupStage> projectSetupStages = new ArrayList<>();

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
    private CompetitionTypeEnum competitionTypeEnum;
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
    private String activityCode;
    private boolean setupComplete = false;
    private Boolean useResubmissionQuestion;
    private AssessorFinanceView assessorFinanceView = AssessorFinanceView.OVERVIEW;
    private boolean nonIfs = false;
    private String nonIfsUrl;
    private GrantTermsAndConditionsResource termsAndConditions;
    private GrantTermsAndConditionsResource otherFundingRulesTermsAndConditions;
    private GolTemplateResource golTemplate;
    private FundingRules fundingRules;
    private Boolean includeYourOrganisationSection;
    private Set<Long> grantClaimMaximums;
    private ApplicationFinanceType applicationFinanceType;
    private Boolean includeProjectGrowthTable;
    private String createdBy;
    private ZonedDateTime createdOn;
    private String modifiedBy;
    private ZonedDateTime modifiedOn;
    private Boolean includeJesForm;
    private boolean nonFinanceType;
    private CompetitionCompletionStage completionStage;
    private FundingType fundingType;
    private List<FinanceRowType> financeRowTypes;
    private FileEntryResource competitionTerms;
    private boolean hasAssessmentStage;
    private boolean procurementMilestones;
    private CovidType covidType;
    private boolean alwaysOpen;
    private boolean subsidyControl;
    private boolean hasBusinessAndFinancialInformationQuestion;
    private CompetitionThirdPartyConfigResource competitionThirdPartyConfigResource;

    public CompetitionResource() {
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
    public boolean isInProjectSetup() {
        return CompetitionStatus.PROJECT_SETUP.equals(competitionStatus);
    }

    @JsonIgnore
    public boolean isH2020() {
        return competitionTypeEnum == CompetitionTypeEnum.HORIZON_2020;
    }

    @JsonIgnore
    public boolean isHesta() {
        return competitionTypeEnum == CompetitionTypeEnum.HESTA;
    }

    @JsonIgnore
    public boolean isExpressionOfInterest() {
        return competitionTypeEnum == CompetitionTypeEnum.EXPRESSION_OF_INTEREST;
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

    @JsonIgnore
    public boolean isFullyFunded() {
        // Competitions which always have 100% funding level
        return isH2020() || isProcurement();
    }

    @JsonIgnore
    public boolean isProcurement() {
        return PROCUREMENT.equals(fundingType);
    }

    @JsonIgnore
    public boolean isKtp() {
        return KTP.equals(fundingType);
    }

    @JsonIgnore
    public boolean isLoan() {
        return LOAN.equals(fundingType);
    }

    @JsonIgnore
    public boolean onlyOneOrgAllowedPerApplication() {
        return isH2020() || isProcurement();
    }

    public List<Long> getAssessmentPeriods() {
        return assessmentPeriods;
    }

    public void setAssessmentPeriods(List<Long> assessmentPeriods) {
        this.assessmentPeriods = assessmentPeriods;
    }

    public CompetitionStatus getCompetitionStatus() {
        return competitionStatus;
    }

    public void setCompetitionStatus(CompetitionStatus competitionStatus) {
        this.competitionStatus = competitionStatus;
    }

    public List<ProjectSetupStage> getProjectSetupStages() {
        return projectSetupStages;
    }

    public void setProjectSetupStages(List<ProjectSetupStage> projectSetupStages) {
        this.projectSetupStages = projectSetupStages;
    }

    public List<FinanceRowType> getFinanceRowTypes() {
        return financeRowTypes;
    }

    public List<FinanceRowType> getFinanceRowTypesByFinance(Optional<? extends BaseFinanceResource> finance) {
        List<FinanceRowType> financeRowTypes = this.getFinanceRowTypes();

        if (this.isKtp() && finance.isPresent()) {
            BaseFinanceResource orgFinance = finance.get();
            financeRowTypes = financeRowTypes.stream()
                    .filter(financeRowType -> BooleanUtils.isFalse(orgFinance.getFecModelEnabled())
                            ? !FinanceRowType.getFecSpecificFinanceRowTypes().contains(financeRowType)
                            : !FinanceRowType.getNonFecSpecificFinanceRowTypes().contains(financeRowType))
                    .collect(Collectors.toList());
        }

        return financeRowTypes;
    }

    public void setFinanceRowTypes(List<FinanceRowType> financeRowTypes) {
        this.financeRowTypes = financeRowTypes;
    }

    public boolean isNonFinanceType() {
        return nonFinanceType;
    }

    public void setNonFinanceType(boolean nonFinanceType) {
        this.nonFinanceType = nonFinanceType;
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
            return toUkTimeZone(date).format(formatter);
        }
        return "";
    }

    public boolean isHasAssessmentStage() {
        return hasAssessmentStage;
    }

    public void setHasAssessmentStage(boolean hasAssessmentStage) {
        this.hasAssessmentStage = hasAssessmentStage;
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

    public CompetitionTypeEnum getCompetitionTypeEnum() {
        return competitionTypeEnum;
    }

    public void setCompetitionTypeEnum(CompetitionTypeEnum competitionTypeEnum) {
        this.competitionTypeEnum = competitionTypeEnum;
    }

    public GolTemplateResource getGolTemplate() {
        return golTemplate;
    }

    public void setGolTemplate(GolTemplateResource golTemplate) {
        this.golTemplate = golTemplate;
    }

    @JsonIgnore
    public Long getDaysLeft() {
        return this.endDate == null ? null : DAYS.between(ZonedDateTime.now(), this.endDate);
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
        if (this.endDate == null) {
            return false;
        }
        long hoursToGo = CLOSING_SOON_CHRONOUNIT.between(ZonedDateTime.now(), this.endDate);
        return isOpen() && hoursToGo < CLOSING_SOON_AMOUNT;
    }

    @JsonIgnore
    public long getAssessmentTotalDays() {
        return DAYS.between(this.assessorAcceptsDate, this.assessorDeadlineDate);
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

    public List<CompetitionDocumentResource> getCompetitionDocuments() {
        return competitionDocuments;
    }

    public void setCompetitionDocuments(List<CompetitionDocumentResource> competitionDocuments) {
        this.competitionDocuments = competitionDocuments;
    }

    public Boolean getUseResubmissionQuestion() {
        return useResubmissionQuestion;
    }

    public void setUseResubmissionQuestion(Boolean useResubmissionQuestion) {
        this.useResubmissionQuestion = useResubmissionQuestion;
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

    public AssessorFinanceView getAssessorFinanceView() {
        return assessorFinanceView;
    }

    public void setAssessorFinanceView(AssessorFinanceView assessorFinanceView) {
        this.assessorFinanceView = assessorFinanceView;
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

    public GrantTermsAndConditionsResource getOtherFundingRulesTermsAndConditions() {
        return otherFundingRulesTermsAndConditions;
    }

    public void setOtherFundingRulesTermsAndConditions(GrantTermsAndConditionsResource otherFundingRulesTermsAndConditions) {
        this.otherFundingRulesTermsAndConditions = otherFundingRulesTermsAndConditions;
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

    public FundingRules getFundingRules() {
        return fundingRules;
    }

    public void setFundingRules(FundingRules fundingRules) {
        this.fundingRules = fundingRules;
    }

    public Boolean getIncludeYourOrganisationSection() {
        return includeYourOrganisationSection;
    }

    public void setIncludeYourOrganisationSection(final Boolean includeYourOrganisationSection) {
        this.includeYourOrganisationSection = includeYourOrganisationSection;
    }

    public Set<Long> getGrantClaimMaximums() {
        return grantClaimMaximums;
    }

    public void setGrantClaimMaximums(final Set<Long> grantClaimMaximums) {
        this.grantClaimMaximums = grantClaimMaximums;
    }

    public ApplicationFinanceType getApplicationFinanceType() {
        return applicationFinanceType;
    }

    public void setApplicationFinanceType(final ApplicationFinanceType applicationFinanceType) {
        this.applicationFinanceType = applicationFinanceType;
    }

    public Boolean getIncludeProjectGrowthTable() {
        return includeProjectGrowthTable;
    }

    public void setIncludeProjectGrowthTable(final Boolean includeProjectGrowthTable) {
        this.includeProjectGrowthTable = includeProjectGrowthTable;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    public ZonedDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(final ZonedDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(final String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public ZonedDateTime getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(final ZonedDateTime modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    public Boolean getIncludeJesForm() {
        return includeJesForm;
    }

    public void setIncludeJesForm(Boolean includeJesForm) {
        this.includeJesForm = includeJesForm;
    }

    public CompetitionCompletionStage getCompletionStage() {
        return completionStage;
    }

    public void setCompletionStage(CompetitionCompletionStage completionStage) {
        this.completionStage = completionStage;
    }

    public FundingType getFundingType() {
        return fundingType;
    }

    public void setFundingType(FundingType fundingType) {
        this.fundingType = fundingType;
    }

    public FileEntryResource getCompetitionTerms() {
        return competitionTerms;
    }

    public void setCompetitionTerms(FileEntryResource competitionTerms) {
        this.competitionTerms = competitionTerms;
    }

    public CovidType getCovidType() {
        return covidType;
    }

    public void setCovidType(CovidType covidType) {
        this.covidType = covidType;
    }

    public boolean isAlwaysOpen() {
        return alwaysOpen;
    }

    public void setAlwaysOpen(boolean alwaysOpen) {
        this.alwaysOpen = alwaysOpen;
    }

    @Override
    public boolean isProcurementMilestones() {
        return procurementMilestones;
    }

    public void setProcurementMilestones(boolean procurementMilestones) {
        this.procurementMilestones = procurementMilestones;
    }

    @JsonIgnore
    public boolean isCompetitionTermsUploaded() {
        return competitionTerms != null;
    }

    public boolean isSubsidyControl() {
        return subsidyControl;
    }

    public void setSubsidyControl(boolean subsidyControl) {
        this.subsidyControl = subsidyControl;
    }

    public CompetitionThirdPartyConfigResource getCompetitionThirdPartyConfigResource() {
        return competitionThirdPartyConfigResource;
    }

    public void setCompetitionThirdPartyConfigResource(CompetitionThirdPartyConfigResource competitionThirdPartyConfigResource) {
        this.competitionThirdPartyConfigResource = competitionThirdPartyConfigResource;
    }

    @JsonIgnore
    public boolean isOfGemCompetition() {
        return isProcurement()
                && isOfGemFunder()
                && isProcurementThirdPartyTermsAndConditions();
    }

    private boolean isOfGemFunder() {
        return funders
                .stream()
                .anyMatch(CompetitionFunderResource::isOfGem);
    }

    private boolean isProcurementThirdPartyTermsAndConditions() {
        return termsAndConditions != null
                && termsAndConditions.isProcurementThirdParty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final CompetitionResource that = (CompetitionResource) o;

        return new EqualsBuilder()
                .append(setupComplete, that.setupComplete)
                .append(nonIfs, that.nonIfs)
                .append(id, that.id)
                .append(milestones, that.milestones)
                .append(funders, that.funders)
                .append(competitionDocuments, that.competitionDocuments)
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
                .append(minProjectDuration, that.minProjectDuration)
                .append(maxProjectDuration, that.maxProjectDuration)
                .append(activityCode, that.activityCode)
                .append(useResubmissionQuestion, that.useResubmissionQuestion)
                .append(nonIfsUrl, that.nonIfsUrl)
                .append(termsAndConditions, that.termsAndConditions)
                .append(otherFundingRulesTermsAndConditions, that.otherFundingRulesTermsAndConditions)
                .append(fundingRules, that.fundingRules)
                .append(includeYourOrganisationSection, that.includeYourOrganisationSection)
                .append(grantClaimMaximums, that.grantClaimMaximums)
                .append(applicationFinanceType, that.applicationFinanceType)
                .append(includeProjectGrowthTable, that.includeProjectGrowthTable)
                .append(fundingType, that.fundingType)
                .append(competitionTerms, that.competitionTerms)
                .append(createdBy, that.createdBy)
                .append(createdOn, that.createdOn)
                .append(modifiedBy, that.modifiedBy)
                .append(modifiedOn, that.modifiedOn)
                .append(alwaysOpen, that.alwaysOpen)
                .append(subsidyControl, that.subsidyControl)
                .append(assessmentPeriods, that.assessmentPeriods)
                .append(competitionThirdPartyConfigResource, that.competitionThirdPartyConfigResource)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(milestones)
                .append(funders)
                .append(competitionDocuments)
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
                .append(minProjectDuration)
                .append(maxProjectDuration)
                .append(activityCode)
                .append(setupComplete)
                .append(useResubmissionQuestion)
                .append(nonIfs)
                .append(nonIfsUrl)
                .append(termsAndConditions)
                .append(otherFundingRulesTermsAndConditions)
                .append(fundingRules)
                .append(includeYourOrganisationSection)
                .append(grantClaimMaximums)
                .append(applicationFinanceType)
                .append(includeProjectGrowthTable)
                .append(fundingType)
                .append(competitionTerms)
                .append(createdBy)
                .append(createdOn)
                .append(modifiedBy)
                .append(modifiedOn)
                .append(alwaysOpen)
                .append(subsidyControl)
                .append(assessmentPeriods)
                .append(competitionThirdPartyConfigResource)
                .toHashCode();
    }

    @Override
    @JsonIgnore
    public ApplicationConfiguration getApplicationConfiguration() {
        return this;
    }

    @JsonIgnore
    public boolean isCovidCompetition() {
        return covidType != null;
    }

    @JsonIgnore
    public boolean isOverheadsAlwaysTwenty() {
        return covidType != null && (
                covidType == CovidType.DE_MINIMIS ||
                        covidType == CovidType.DE_MINIMIS_ROUND_2
        );
    }


    public boolean isHasBusinessAndFinancialInformationQuestion() {
        return hasBusinessAndFinancialInformationQuestion;
    }

    public CompetitionResource setHasBusinessAndFinancialInformationQuestion(boolean hasBusinessAndFinancialInformationQuestion) {
        this.hasBusinessAndFinancialInformationQuestion = hasBusinessAndFinancialInformationQuestion;
        return this;
    }

    @JsonIgnore
    public boolean isApplicationCreatedOrOpenedCompStatusOpen(ApplicationState applicationState) {
        return (applicationState == ApplicationState.CREATED || applicationState == ApplicationState.OPENED )
                && !competitionStatus.name().equals("OPEN");
    }

    @JsonIgnore
    public boolean isApprovedApplicationState(ApplicationState applicationState) {
        return applicationState == ApplicationState.APPROVED;
    }

    @JsonIgnore
    public boolean isRejectedApplicationState(ApplicationState applicationState) {
        return applicationState == ApplicationState.REJECTED;
    }

}
