package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.project.internal.ProjectSetupStage;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class CompetitionResourceBuilder extends BaseBuilder<CompetitionResource, CompetitionResourceBuilder> {

    private CompetitionResourceBuilder(List<BiConsumer<Integer, CompetitionResource>> newMultiActions) {
        super(newMultiActions);
    }

    public static CompetitionResourceBuilder newCompetitionResource() {
        return new CompetitionResourceBuilder(emptyList()).with(uniqueIds());
    }

    public CompetitionResourceBuilder withName(String name) {
        return with(competition -> setField("name", name, competition));
    }

    public CompetitionResourceBuilder withStartDate(ZonedDateTime startDate) {
        return with(competition -> setField("startDate", startDate, competition));
    }

    public CompetitionResourceBuilder withEndDate(ZonedDateTime endDate) {
        return with(competition -> setField("endDate", endDate, competition));
    }

    public CompetitionResourceBuilder withResearchCategories(Set<Long> categories) {
        return with(competition -> competition.setResearchCategories(categories));
    }

    public CompetitionResourceBuilder withMinProjectDuration(Integer minProjectDuration) {
        return with(competition -> competition.setMinProjectDuration(minProjectDuration));
    }

    public CompetitionResourceBuilder withMaxProjectDuration(Integer maxProjectDuration) {
        return with(competition -> competition.setMaxProjectDuration(maxProjectDuration));
    }

    public CompetitionResourceBuilder withResubmission(Boolean resubmission) {
        return with(competition -> competition.setResubmission(resubmission));
    }

    public CompetitionResourceBuilder withMultiStream(boolean... multiStream) {
        return withArraySetFieldByReflection("multiStream", multiStream);
    }

    public CompetitionResourceBuilder withStreamName(String... streamNames) {
        return withArraySetFieldByReflection("streamName", streamNames);
    }

    public CompetitionResourceBuilder withId(Long... ids) {
        return withArraySetFieldByReflection("id", ids);
    }

    public CompetitionResourceBuilder withName(String... names) {
        return withArraySetFieldByReflection("name", names);
    }

    public CompetitionResourceBuilder withAssessorAcceptsDate(ZonedDateTime... assessorAcceptsDates) {
        return withArraySetFieldByReflection("assessorAcceptsDate", assessorAcceptsDates);
    }

    public CompetitionResourceBuilder withAssessorDeadlineDate(ZonedDateTime... assessorDeadlineDates) {
        return withArraySetFieldByReflection("assessorDeadlineDate", assessorDeadlineDates);
    }

    public CompetitionResourceBuilder withReleaseFeedbackDate(ZonedDateTime... releaseFeedbackDates) {
        return withArraySetFieldByReflection("releaseFeedbackDate", releaseFeedbackDates);
    }

    public CompetitionResourceBuilder withRegistrationCloseDate(ZonedDateTime... registrationCloseDates) {
        return withArraySetFieldByReflection("registrationDate", registrationCloseDates);
    }

    public CompetitionResourceBuilder withFundersPanelDate(ZonedDateTime... fundersPanelDates) {
        return withArraySetFieldByReflection("fundersPanelDate", fundersPanelDates);
    }

    public CompetitionResourceBuilder withFundersPanelEndDate(ZonedDateTime... fundersPanelEndDates) {
        return withArraySetFieldByReflection("fundersPanelEndDate", fundersPanelEndDates);
    }

    public CompetitionResourceBuilder withAssessorFeedbackDate(ZonedDateTime... assessorFeedbackDate) {
        return withArray((date, object) -> object.setAssessorFeedbackDate(date), assessorFeedbackDate);
    }

    public CompetitionResourceBuilder withMaxResearchRatio(Integer... maxResearchRatios) {
        return withArraySetFieldByReflection("maxResearchRatio", maxResearchRatios);
    }

    public CompetitionResourceBuilder withAcademicGrantClaimPercentage(Integer... grantClaimPercentages) {
        return withArraySetFieldByReflection("academicGrantPercentage", grantClaimPercentages);
    }

    public CompetitionResourceBuilder withCompetitionStatus(CompetitionStatus... statuses) {
        return withArraySetFieldByReflection("competitionStatus", statuses);
    }

    public CompetitionResourceBuilder withLeadTechnologist(Long... userIds) {
        return withArraySetFieldByReflection("leadTechnologist", userIds);
    }

    public CompetitionResourceBuilder withLeadTechnologistName(String... names) {
        return withArraySetFieldByReflection("leadTechnologistName", names);
    }

    public CompetitionResourceBuilder withExecutive(Long... userIds) {
        return withArraySetFieldByReflection("executive", userIds);
    }

    public CompetitionResourceBuilder withExecutiveName(String... names) {
        return withArraySetFieldByReflection("executiveName", names);
    }

    public CompetitionResourceBuilder withCompetitionType(Long... typeId) {
        return withArraySetFieldByReflection("competitionType", typeId);
    }

    public CompetitionResourceBuilder withCompetitionTypeName(String... names) {
        return withArraySetFieldByReflection("competitionTypeName", names);
    }

    public CompetitionResourceBuilder withInnovationSector(Long... ids) {
        return withArraySetFieldByReflection("innovationSector", ids);
    }

    public CompetitionResourceBuilder withInnovationSectorName(String... names) {
        return withArraySetFieldByReflection("innovationSectorName", names);
    }

    public CompetitionResourceBuilder withInnovationAreas(Set<Long>... ids) {
        return withArraySetFieldByReflection("innovationAreas", ids);
    }

    public CompetitionResourceBuilder withInnovationAreaNames(Set<String>... names) {
        return withArraySetFieldByReflection("innovationAreaNames", names);
    }

    public CompetitionResourceBuilder withPafCode(String... codes) {
        return withArraySetFieldByReflection("pafCode", codes);
    }

    public CompetitionResourceBuilder withBudgetCode(String... codes) {
        return withArraySetFieldByReflection("budgetCode", codes);
    }

    public CompetitionResourceBuilder withCompetitionCode(String... codes) {
        return withArraySetFieldByReflection("code", codes);
    }

    public CompetitionResourceBuilder withCollaborationLevel(CollaborationLevel... collaborationLevels) {
        return withArraySetFieldByReflection("collaborationLevel", collaborationLevels);
    }

    public CompetitionResourceBuilder withLeadApplicantType(List<Long>... leadApplicantTypes) {
        return withArraySetFieldByReflection("leadApplicantTypes", leadApplicantTypes);
    }

    public CompetitionResourceBuilder withActivityCode(String... activityCodes) {
        return withArraySetFieldByReflection("activityCode", activityCodes);
    }

    public CompetitionResourceBuilder withFunders(List<CompetitionFunderResource> fundersList) {
        return withList(fundersList, (funders, section) -> section.setFunders(fundersList));
    }

    public CompetitionResourceBuilder withMilestones(List<Long> milestonesList) {
        return withList(milestonesList, (milestones, object) -> object.setMilestones(milestonesList));
    }

    public CompetitionResourceBuilder withUseResubmissionQuestion(Boolean useResubmissionQuestion) {
        return with(competition -> competition.setUseResubmissionQuestion(useResubmissionQuestion));
    }

    public CompetitionResourceBuilder withSetupComplete(Boolean setupComplete) {
        return with(competition -> competition.setSetupComplete(setupComplete));
    }

    public CompetitionResourceBuilder withLocationPerPartner(boolean... locationPerPartner) {
        return withArraySetFieldByReflection("locationPerPartner", locationPerPartner);
    }

    public CompetitionResourceBuilder withApplicationFinanceType(ApplicationFinanceType... applicationFinanceType) {
        return withArraySetFieldByReflection("applicationFinanceType", applicationFinanceType);
    }

    public CompetitionResourceBuilder withProjectDocument(List<CompetitionDocumentResource> competitionDocumentResourcesList) {
        return withList(competitionDocumentResourcesList, (projectDocumentResource, section) -> section.setCompetitionDocuments(competitionDocumentResourcesList));
    }

    public CompetitionResourceBuilder withProjectSetupStages(List<ProjectSetupStage> projectSetupStages) {
        return withList(projectSetupStages, (projectSetupStage, section) -> section.setProjectSetupStages(projectSetupStages));
    }

    public CompetitionResourceBuilder withNonIfs(Boolean... nonIfs) {
        return withArraySetFieldByReflection("nonIfs", nonIfs);
    }

    public CompetitionResourceBuilder withNonIfsUrl(String... nonIfsUrl) {
        return withArraySetFieldByReflection("nonIfsUrl", nonIfsUrl);
    }

    public CompetitionResourceBuilder withTermsAndConditions(GrantTermsAndConditionsResource... value) {
        return withArray((template, competition) -> competition.setTermsAndConditions(template), value);
    }

    public CompetitionResourceBuilder withStateAid(Boolean... stateAid) {
        return withArraySetFieldByReflection("stateAid", stateAid);
    }

    public CompetitionResourceBuilder withIncludeYourOrganisationSection(Boolean... includeYourOrganisationSection) {
        return withArraySetFieldByReflection("includeYourOrganisationSection", includeYourOrganisationSection);
    }

    public CompetitionResourceBuilder withGrantClaimMaximums(Set<Long>... grantClaimMaximums) {
        return withArraySetFieldByReflection("grantClaimMaximums", grantClaimMaximums);
    }

    public CompetitionResourceBuilder withNonFinanceType(boolean... nonFinanceTypes) {
        return withArraySetFieldByReflection("nonFinanceType", nonFinanceTypes);
    }

    public CompetitionResourceBuilder withIncludeJesForm(Boolean... includeJesForms) {
        return withArray((includeJesForm, competitionSetupFinance) -> setField("includeJesForm", includeJesForm, competitionSetupFinance), includeJesForms);
    }

    public CompetitionResourceBuilder withIncludeProjectGrowthTable(Boolean... includeProjectGrowthTable) {
        return withArraySetFieldByReflection("includeProjectGrowthTable", includeProjectGrowthTable);
    }

    public CompetitionResourceBuilder withCreatedBy(String... users) {
        return withArraySetFieldByReflection("createdBy", users);
    }

    public CompetitionResourceBuilder withCreatedOn(ZonedDateTime... createdOns) {
        return withArraySetFieldByReflection("createdOn", createdOns);
    }

    public CompetitionResourceBuilder withModifiedBy(String... users) {
        return withArraySetFieldByReflection("modifiedBy", users);
    }

    public CompetitionResourceBuilder withModifiedOn(ZonedDateTime... modifiedOns) {
        return withArraySetFieldByReflection("modifiedOn", modifiedOns);
    }

    public CompetitionResourceBuilder withCompletionStage(CompetitionCompletionStage... completionStage) {
        return withArraySetFieldByReflection("completionStage", completionStage);
    }

    public CompetitionResourceBuilder withFundingType(FundingType... fundingTypes) {
        return withArray((fundingType, competitionResource) -> competitionResource.setFundingType(fundingType), fundingTypes);
    }

    @SafeVarargs
    public final CompetitionResourceBuilder withFinanceRowTypes(Set<FinanceRowType>... financeRowTypes) {
        return withArray((financeRowType, competitionResource) -> competitionResource.setFinanceRowTypes(financeRowType), financeRowTypes);
    }

    public CompetitionResourceBuilder withCompetitionTerms(FileEntryResource... competitionTermsItems) {
        return withArray((competitionTerms, competitionResource) -> competitionResource.setCompetitionTerms(competitionTerms), competitionTermsItems);
    }

    public CompetitionResourceBuilder withHasAssessmentStage(Boolean... hasAssessmentStages) {
        return withArray((hasAssessmentStage, competitionResource) -> competitionResource.setHasAssessmentStage(hasAssessmentStage), hasAssessmentStages);
    }

    @SafeVarargs
    public final CompetitionResourceBuilder withProjectSetupStages(List<ProjectSetupStage>... projectSetupStages) {
        return withArray((projectSetupStage, competitionResource) -> competitionResource.setProjectSetupStages(projectSetupStage), projectSetupStages);
    }

    @Override
    protected CompetitionResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, CompetitionResource>> actions) {
        return new CompetitionResourceBuilder(actions);
    }

    @Override
    protected CompetitionResource createInitial() {
        return new CompetitionResource();
    }
}
