package org.innovateuk.ifs.testdata.builders;

import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.testdata.builders.data.CompetitionLine;

import java.util.Set;

public final class CompetitionLineBuilder {

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
    private BuilderOrder builderOrder = BuilderOrder.ANYWHERE;

    private CompetitionLineBuilder() {
    }

    public static CompetitionLineBuilder aCompetitionLine() {
        return new CompetitionLineBuilder();
    }

    public CompetitionLineBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public CompetitionLineBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public CompetitionLineBuilder withCompetitionType(CompetitionTypeEnum competitionType) {
        this.competitionType = competitionType;
        return this;
    }

    public CompetitionLineBuilder withInnovationAreas(Set<Long> innovationAreas) {
        this.innovationAreas = innovationAreas;
        return this;
    }

    public CompetitionLineBuilder withInnovationSector(String innovationSector) {
        this.innovationSector = innovationSector;
        return this;
    }

    public CompetitionLineBuilder withResearchCategory(Set<Long> researchCategory) {
        this.researchCategory = researchCategory;
        return this;
    }

    public CompetitionLineBuilder withCollaborationLevel(CollaborationLevel collaborationLevel) {
        this.collaborationLevel = collaborationLevel;
        return this;
    }

    public CompetitionLineBuilder withLeadApplicantTypes(Set<OrganisationTypeEnum> leadApplicantTypes) {
        this.leadApplicantTypes = leadApplicantTypes;
        return this;
    }

    public CompetitionLineBuilder withResearchRatio(Integer researchRatio) {
        this.researchRatio = researchRatio;
        return this;
    }

    public CompetitionLineBuilder withResubmission(Boolean resubmission) {
        this.resubmission = resubmission;
        return this;
    }

    public CompetitionLineBuilder withMultiStream(Boolean multiStream) {
        this.multiStream = multiStream;
        return this;
    }

    public CompetitionLineBuilder withCompetitionStatus(CompetitionStatus competitionStatus) {
        this.competitionStatus = competitionStatus;
        return this;
    }

    public CompetitionLineBuilder withLeadTechnologist(Long leadTechnologist) {
        this.leadTechnologist = leadTechnologist;
        return this;
    }

    public CompetitionLineBuilder withBuilderOrder(BuilderOrder builderOrder) {
        this.builderOrder = builderOrder;
        return this;
    }

    public CompetitionLineBuilder withCompExecutive(Long compExecutive) {
        this.compExecutive = compExecutive;
        return this;
    }

    public CompetitionLineBuilder withSetupComplete(boolean setupComplete) {
        this.setupComplete = setupComplete;
        return this;
    }

    public CompetitionLineBuilder withBudgetCode(String budgetCode) {
        this.budgetCode = budgetCode;
        return this;
    }

    public CompetitionLineBuilder withCode(String code) {
        this.code = code;
        return this;
    }

    public CompetitionLineBuilder withPafCode(String pafCode) {
        this.pafCode = pafCode;
        return this;
    }

    public CompetitionLineBuilder withActivityCode(String activityCode) {
        this.activityCode = activityCode;
        return this;
    }

    public CompetitionLineBuilder withAssessorCount(Integer assessorCount) {
        this.assessorCount = assessorCount;
        return this;
    }

    public CompetitionLineBuilder withHasAssessmentPanel(Boolean hasAssessmentPanel) {
        this.hasAssessmentPanel = hasAssessmentPanel;
        return this;
    }

    public CompetitionLineBuilder withHasInterviewStage(Boolean hasInterviewStage) {
        this.hasInterviewStage = hasInterviewStage;
        return this;
    }

    public CompetitionLineBuilder withAssessorFinanceView(AssessorFinanceView assessorFinanceView) {
        this.assessorFinanceView = assessorFinanceView;
        return this;
    }

    public CompetitionLineBuilder withPublished(boolean published) {
        this.published = published;
        return this;
    }

    public CompetitionLineBuilder withFundingType(FundingType fundingType) {
        this.fundingType = fundingType;
        return this;
    }

    public CompetitionLineBuilder withInviteOnly(boolean inviteOnly) {
        this.inviteOnly = inviteOnly;
        return this;
    }

    public CompetitionLineBuilder withNonIfs(boolean nonIfs) {
        this.nonIfs = nonIfs;
        return this;
    }

    public CompetitionLineBuilder withNonIfsUrl(String nonIfsUrl) {
        this.nonIfsUrl = nonIfsUrl;
        return this;
    }

    public CompetitionLineBuilder withCompetitionCompletionStage(CompetitionCompletionStage competitionCompletionStage) {
        this.competitionCompletionStage = competitionCompletionStage;
        return this;
    }

    public CompetitionLineBuilder withIncludeJesForm(Boolean includeJesForm) {
        this.includeJesForm = includeJesForm;
        return this;
    }

    public CompetitionLineBuilder withApplicationFinanceType(ApplicationFinanceType applicationFinanceType) {
        this.applicationFinanceType = applicationFinanceType;
        return this;
    }

    public CompetitionLineBuilder withIncludeProjectGrowth(Boolean includeProjectGrowth) {
        this.includeProjectGrowth = includeProjectGrowth;
        return this;
    }

    public CompetitionLineBuilder withIncludeYourOrganisation(Boolean includeYourOrganisation) {
        this.includeYourOrganisation = includeYourOrganisation;
        return this;
    }

    public CompetitionLineBuilder withFundingRules(FundingRules fundingRules) {
        this.fundingRules = fundingRules;
        return this;
    }

    public CompetitionLineBuilder withAlwaysOpen(Boolean alwaysOpen) {
        this.alwaysOpen = alwaysOpen;
        return this;
    }

    public CompetitionLine build() {
        CompetitionLine competitionLine = new CompetitionLine();
        competitionLine.setId(id);
        competitionLine.setName(name);
        competitionLine.setCompetitionType(competitionType);
        competitionLine.setInnovationAreas(innovationAreas);
        competitionLine.setInnovationSector(innovationSector);
        competitionLine.setResearchCategory(researchCategory);
        competitionLine.setCollaborationLevel(collaborationLevel);
        competitionLine.setLeadApplicantTypes(leadApplicantTypes);
        competitionLine.setResearchRatio(researchRatio);
        competitionLine.setResubmission(resubmission);
        competitionLine.setMultiStream(multiStream);
        competitionLine.setCompetitionStatus(competitionStatus);
        competitionLine.setLeadTechnologist(leadTechnologist);
        competitionLine.setCompExecutive(compExecutive);
        competitionLine.setSetupComplete(setupComplete);
        competitionLine.setBudgetCode(budgetCode);
        competitionLine.setCode(code);
        competitionLine.setPafCode(pafCode);
        competitionLine.setActivityCode(activityCode);
        competitionLine.setAssessorCount(assessorCount);
        competitionLine.setHasAssessmentPanel(hasAssessmentPanel);
        competitionLine.setHasInterviewStage(hasInterviewStage);
        competitionLine.setAssessorFinanceView(assessorFinanceView);
        competitionLine.setPublished(published);
        competitionLine.setFundingType(fundingType);
        competitionLine.setInviteOnly(inviteOnly);
        competitionLine.setNonIfs(nonIfs);
        competitionLine.setNonIfsUrl(nonIfsUrl);
        competitionLine.setCompetitionCompletionStage(competitionCompletionStage);
        competitionLine.setIncludeJesForm(includeJesForm);
        competitionLine.setApplicationFinanceType(applicationFinanceType);
        competitionLine.setIncludeProjectGrowth(includeProjectGrowth);
        competitionLine.setIncludeYourOrganisation(includeYourOrganisation);
        competitionLine.setFundingRules(fundingRules);
        competitionLine.setAlwaysOpen(alwaysOpen);
        return competitionLine;
    }

    public BuilderOrder getBuilderOrder() {
        return builderOrder;
    }

    /*
    Some ATs rely on competition ids being in an order so they appear on certain paginated pages, or so that
    Their applications are prioritised by scheduled jobs to create applications.
     */
    public enum BuilderOrder {
        FIRST,
        ANYWHERE,
        LAST;
    }
}
