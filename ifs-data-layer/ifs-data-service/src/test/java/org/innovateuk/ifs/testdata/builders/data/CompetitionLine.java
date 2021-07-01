package org.innovateuk.ifs.testdata.builders.data;

import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;

import java.util.Set;

public class CompetitionLine {

    private Long id;
    private String name;
    private CompetitionTypeEnum competitionType;
    private Set<Long> innovationAreas;
    private String innovationSector;
    private Set<Long> researchCategory;
    private CollaborationLevel collaborationLevel;
    private Set<OrganisationTypeEnum> leadApplicantTypes;
    private Integer researchRatio;
    private Boolean resubmission;
    private Boolean multiStream;
    private CompetitionStatus competitionStatus;
    private Long leadTechnologist;
    private Long compExecutive;
    private boolean setupComplete;
    private String budgetCode;
    private String code;
    private String pafCode;
    private String activityCode;
    private Integer assessorCount;
    private Boolean hasAssessmentPanel;
    private Boolean hasInterviewStage;
    private AssessorFinanceView assessorFinanceView;
    private boolean published;
    private FundingType fundingType;
    private boolean inviteOnly;
    private boolean nonIfs;
    private String nonIfsUrl;
    private CompetitionCompletionStage competitionCompletionStage;
    private Boolean includeJesForm;
    private ApplicationFinanceType applicationFinanceType;
    private Boolean includeProjectGrowth;
    private Boolean includeYourOrganisation;
    private FundingRules fundingRules;
    private Boolean alwaysOpen;
    private boolean priority;

    public CompetitionLine() {
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

    public CompetitionTypeEnum getCompetitionType() {
        return competitionType;
    }

    public void setCompetitionType(CompetitionTypeEnum competitionType) {
        this.competitionType = competitionType;
    }

    public Set<Long> getInnovationAreas() {
        return innovationAreas;
    }

    public void setInnovationAreas(Set<Long> innovationAreas) {
        this.innovationAreas = innovationAreas;
    }

    public String getInnovationSector() {
        return innovationSector;
    }

    public void setInnovationSector(String innovationSector) {
        this.innovationSector = innovationSector;
    }

    public Set<Long> getResearchCategory() {
        return researchCategory;
    }

    public void setResearchCategory(Set<Long> researchCategory) {
        this.researchCategory = researchCategory;
    }

    public CollaborationLevel getCollaborationLevel() {
        return collaborationLevel;
    }

    public void setCollaborationLevel(CollaborationLevel collaborationLevel) {
        this.collaborationLevel = collaborationLevel;
    }

    public Set<OrganisationTypeEnum> getLeadApplicantTypes() {
        return leadApplicantTypes;
    }

    public void setLeadApplicantTypes(Set<OrganisationTypeEnum> leadApplicantTypes) {
        this.leadApplicantTypes = leadApplicantTypes;
    }

    public Integer getResearchRatio() {
        return researchRatio;
    }

    public void setResearchRatio(Integer researchRatio) {
        this.researchRatio = researchRatio;
    }

    public Boolean getResubmission() {
        return resubmission;
    }

    public void setResubmission(Boolean resubmission) {
        this.resubmission = resubmission;
    }

    public Boolean getMultiStream() {
        return multiStream;
    }

    public void setMultiStream(Boolean multiStream) {
        this.multiStream = multiStream;
    }

    public CompetitionStatus getCompetitionStatus() {
        return competitionStatus;
    }

    public void setCompetitionStatus(CompetitionStatus competitionStatus) {
        this.competitionStatus = competitionStatus;
    }

    public Long getLeadTechnologist() {
        return leadTechnologist;
    }

    public void setLeadTechnologist(Long leadTechnologist) {
        this.leadTechnologist = leadTechnologist;
    }

    public Long getCompExecutive() {
        return compExecutive;
    }

    public void setCompExecutive(Long compExecutive) {
        this.compExecutive = compExecutive;
    }

    public boolean isSetupComplete() {
        return setupComplete;
    }

    public void setSetupComplete(boolean setupComplete) {
        this.setupComplete = setupComplete;
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

    public String getPafCode() {
        return pafCode;
    }

    public void setPafCode(String pafCode) {
        this.pafCode = pafCode;
    }

    public String getActivityCode() {
        return activityCode;
    }

    public void setActivityCode(String activityCode) {
        this.activityCode = activityCode;
    }

    public Integer getAssessorCount() {
        return assessorCount;
    }

    public void setAssessorCount(Integer assessorCount) {
        this.assessorCount = assessorCount;
    }

    public Boolean getHasAssessmentPanel() {
        return hasAssessmentPanel;
    }

    public void setHasAssessmentPanel(Boolean hasAssessmentPanel) {
        this.hasAssessmentPanel = hasAssessmentPanel;
    }

    public Boolean getHasInterviewStage() {
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

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public FundingType getFundingType() {
        return fundingType;
    }

    public void setFundingType(FundingType fundingType) {
        this.fundingType = fundingType;
    }

    public boolean isInviteOnly() {
        return inviteOnly;
    }

    public void setInviteOnly(boolean inviteOnly) {
        this.inviteOnly = inviteOnly;
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

    public CompetitionCompletionStage getCompetitionCompletionStage() {
        return competitionCompletionStage;
    }

    public void setCompetitionCompletionStage(CompetitionCompletionStage competitionCompletionStage) {
        this.competitionCompletionStage = competitionCompletionStage;
    }

    public Boolean getIncludeJesForm() {
        return includeJesForm;
    }

    public void setIncludeJesForm(Boolean includeJesForm) {
        this.includeJesForm = includeJesForm;
    }

    public ApplicationFinanceType getApplicationFinanceType() {
        return applicationFinanceType;
    }

    public void setApplicationFinanceType(ApplicationFinanceType applicationFinanceType) {
        this.applicationFinanceType = applicationFinanceType;
    }

    public Boolean getIncludeProjectGrowth() {
        return includeProjectGrowth;
    }

    public void setIncludeProjectGrowth(Boolean includeProjectGrowth) {
        this.includeProjectGrowth = includeProjectGrowth;
    }

    public Boolean getIncludeYourOrganisation() {
        return includeYourOrganisation;
    }

    public void setIncludeYourOrganisation(Boolean includeYourOrganisation) {
        this.includeYourOrganisation = includeYourOrganisation;
    }

    public FundingRules getFundingRules() {
        return fundingRules;
    }

    public void setFundingRules(FundingRules fundingRules) {
        this.fundingRules = fundingRules;
    }

    public Boolean getAlwaysOpen() {
        return alwaysOpen;
    }

    public void setAlwaysOpen(Boolean alwaysOpen) {
        this.alwaysOpen = alwaysOpen;
    }
}
