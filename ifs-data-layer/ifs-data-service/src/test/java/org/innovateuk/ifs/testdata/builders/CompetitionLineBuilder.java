package org.innovateuk.ifs.testdata.builders;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.testdata.builders.data.CompetitionLine;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class CompetitionLineBuilder extends BaseBuilder<CompetitionLine, CompetitionLineBuilder> {

    private CompetitionLineBuilder(List<BiConsumer<Integer, CompetitionLine>> newMultiActions) {
        super(newMultiActions);
    }

    public static CompetitionLineBuilder newCompetitionLine() {
        return new CompetitionLineBuilder(emptyList()).with(uniqueIds());
    }

    public CompetitionLineBuilder withName(String name) {
        return with(competition -> setField("name", name, competition));
    }

    public CompetitionLineBuilder withStartDate(ZonedDateTime startDate) {
        return with(competition -> setField("startDate", startDate, competition));
    }

    public CompetitionLineBuilder withPublished(boolean published) {
        return with(competition -> setField("published", published, competition));
    }

    public CompetitionLineBuilder withEndDate(ZonedDateTime endDate) {
        return with(competition -> setField("endDate", endDate, competition));
    }

    public CompetitionLineBuilder withResearchCategories(Set<Long> categories) {
        return with(competition -> competition.setResearchCategory(categories));
    }

//    public CompetitionLineBuilder withMinProjectDuration(Integer minProjectDuration) {
//        return with(competition -> competition.setMinProjectDuration(minProjectDuration));
//    }

//    public CompetitionLineBuilder withMaxProjectDuration(Integer maxProjectDuration) {
//        return with(competition -> competition.setMaxProjectDuration(maxProjectDuration));
//    }

    public CompetitionLineBuilder withResubmission(Boolean resubmission) {
        return with(competition -> competition.setResubmission(resubmission));
    }

    public CompetitionLineBuilder withMultiStream(boolean... multiStream) {
        return withArraySetFieldByReflection("multiStream", multiStream);
    }

    public CompetitionLineBuilder withStreamName(String... streamNames) {
        return withArraySetFieldByReflection("streamName", streamNames);
    }

    public CompetitionLineBuilder withId(Long... ids) {
        return withArraySetFieldByReflection("id", ids);
    }

    public CompetitionLineBuilder withName(String... names) {
        return withArraySetFieldByReflection("name", names);
    }

    public CompetitionLineBuilder withAssessorAcceptsDate(ZonedDateTime... assessorAcceptsDates) {
        return withArraySetFieldByReflection("assessorAcceptsDate", assessorAcceptsDates);
    }

    public CompetitionLineBuilder withAssessorDeadlineDate(ZonedDateTime... assessorDeadlineDates) {
        return withArraySetFieldByReflection("assessorDeadlineDate", assessorDeadlineDates);
    }

    public CompetitionLineBuilder withReleaseFeedbackDate(ZonedDateTime... releaseFeedbackDates) {
        return withArraySetFieldByReflection("releaseFeedbackDate", releaseFeedbackDates);
    }

    public CompetitionLineBuilder withRegistrationCloseDate(ZonedDateTime... registrationCloseDates) {
        return withArraySetFieldByReflection("registrationDate", registrationCloseDates);
    }

    public CompetitionLineBuilder withFundersPanelDate(ZonedDateTime... fundersPanelDates) {
        return withArraySetFieldByReflection("fundersPanelDate", fundersPanelDates);
    }

    public CompetitionLineBuilder withFundersPanelEndDate(ZonedDateTime... fundersPanelEndDates) {
        return withArraySetFieldByReflection("fundersPanelEndDate", fundersPanelEndDates);
    }

//    public CompetitionLineBuilder withAssessorFeedbackDate(ZonedDateTime... assessorFeedbackDate) {
//        return withArray((date, object) -> object.setAssessorFeedbackDate(date), assessorFeedbackDate);
//    }

    public CompetitionLineBuilder withMaxResearchRatio(Integer... maxResearchRatios) {
        return withArraySetFieldByReflection("maxResearchRatio", maxResearchRatios);
    }

    public CompetitionLineBuilder withAcademicGrantClaimPercentage(Integer... grantClaimPercentages) {
        return withArraySetFieldByReflection("academicGrantPercentage", grantClaimPercentages);
    }

    public CompetitionLineBuilder withCompetitionStatus(CompetitionStatus... statuses) {
        return withArraySetFieldByReflection("competitionStatus", statuses);
    }

    public CompetitionLineBuilder withLeadTechnologist(Long... userIds) {
        return withArraySetFieldByReflection("leadTechnologist", userIds);
    }

    public CompetitionLineBuilder withLeadTechnologistName(String... names) {
        return withArraySetFieldByReflection("leadTechnologistName", names);
    }

    public CompetitionLineBuilder withExecutive(Long... userIds) {
        return withArraySetFieldByReflection("executive", userIds);
    }

    public CompetitionLineBuilder withExecutiveName(String... names) {
        return withArraySetFieldByReflection("executiveName", names);
    }

    public CompetitionLineBuilder withCompetitionType(Long... typeId) {
        return withArraySetFieldByReflection("competitionType", typeId);
    }

    public CompetitionLineBuilder withCompetitionTypeName(String... names) {
        return withArraySetFieldByReflection("competitionTypeName", names);
    }

    public CompetitionLineBuilder withInnovationSector(Long... ids) {
        return withArraySetFieldByReflection("innovationSector", ids);
    }

    public CompetitionLineBuilder withInnovationSectorName(String... names) {
        return withArraySetFieldByReflection("innovationSectorName", names);
    }

    public CompetitionLineBuilder withInnovationAreas(Set<Long>... ids) {
        return withArraySetFieldByReflection("innovationAreas", ids);
    }

    public CompetitionLineBuilder withInnovationAreaNames(Set<String>... names) {
        return withArraySetFieldByReflection("innovationAreaNames", names);
    }

    public CompetitionLineBuilder withPafCode(String... codes) {
        return withArraySetFieldByReflection("pafCode", codes);
    }

    public CompetitionLineBuilder withBudgetCode(String... codes) {
        return withArraySetFieldByReflection("budgetCode", codes);
    }

    public CompetitionLineBuilder withCompetitionCode(String... codes) {
        return withArraySetFieldByReflection("code", codes);
    }

    public CompetitionLineBuilder withCollaborationLevel(CollaborationLevel... collaborationLevels) {
        return withArraySetFieldByReflection("collaborationLevel", collaborationLevels);
    }

    public CompetitionLineBuilder withLeadApplicantType(List<Long>... leadApplicantTypes) {
        return withArraySetFieldByReflection("leadApplicantTypes", leadApplicantTypes);
    }

    public CompetitionLineBuilder withActivityCode(String... activityCodes) {
        return withArraySetFieldByReflection("activityCode", activityCodes);
    }

//    public CompetitionLineBuilder withFunders(List<CompetitionFunderResource> fundersList) {
//        return withList(fundersList, (funders, section) -> section.setFunders(fundersList));
//    }
//
//    public CompetitionLineBuilder withMilestones(List<Long> milestonesList) {
//        return withList(milestonesList, (milestones, object) -> object.setMilestones(milestonesList));
//    }
//
//    public CompetitionLineBuilder withUseResubmissionQuestion(Boolean useResubmissionQuestion) {
//        return with(competition -> competition.setUseResubmissionQuestion(useResubmissionQuestion));
//    }

    public CompetitionLineBuilder withSetupComplete(Boolean setupComplete) {
        return with(competition -> competition.setSetupComplete(setupComplete));
    }

    public CompetitionLineBuilder withApplicationFinanceType(ApplicationFinanceType... applicationFinanceType) {
        return withArraySetFieldByReflection("applicationFinanceType", applicationFinanceType);
    }

//    public CompetitionLineBuilder withProjectDocument(List<CompetitionDocumentResource> competitionDocumentResourcesList) {
//        return withList(competitionDocumentResourcesList, (projectDocumentResource, section) -> section.setCompetitionDocuments(competitionDocumentResourcesList));
//    }
//
//    public CompetitionLineBuilder withProjectSetupStages(List<ProjectSetupStage> projectSetupStages) {
//        return withList(projectSetupStages, (projectSetupStage, section) -> section.setProjectSetupStages(projectSetupStages));
//    }

    public CompetitionLineBuilder withNonIfs(Boolean... nonIfs) {
        return withArraySetFieldByReflection("nonIfs", nonIfs);
    }

    public CompetitionLineBuilder withAssessorFinanceView(AssessorFinanceView... assessorFinanceView) {
        return withArraySetFieldByReflection("assessorFinanceView", assessorFinanceView);
    }

    public CompetitionLineBuilder withNonIfsUrl(String... nonIfsUrl) {
        return withArraySetFieldByReflection("nonIfsUrl", nonIfsUrl);
    }

//    public CompetitionLineBuilder withTermsAndConditions(GrantTermsAndConditionsResource... value) {
//        return withArray((template, competition) -> competition.setTermsAndConditions(template), value);
//    }

    public CompetitionLineBuilder withFundingRules(FundingRules... fundingRules) {
        return withArraySetFieldByReflection("fundingRules", fundingRules);
    }

    public CompetitionLineBuilder withIncludeYourOrganisationSection(Boolean... includeYourOrganisationSection) {
        return withArraySetFieldByReflection("includeYourOrganisationSection", includeYourOrganisationSection);
    }

    public CompetitionLineBuilder withGrantClaimMaximums(Set<Long>... grantClaimMaximums) {
        return withArraySetFieldByReflection("grantClaimMaximums", grantClaimMaximums);
    }

    public CompetitionLineBuilder withNonFinanceType(boolean... nonFinanceTypes) {
        return withArraySetFieldByReflection("nonFinanceType", nonFinanceTypes);
    }

    public CompetitionLineBuilder withIncludeJesForm(Boolean... includeJesForms) {
        return withArray((includeJesForm, competitionSetupFinance) -> setField("includeJesForm", includeJesForm, competitionSetupFinance), includeJesForms);
    }

    public CompetitionLineBuilder withIncludeProjectGrowthTable(Boolean... includeProjectGrowthTable) {
        return withArraySetFieldByReflection("includeProjectGrowthTable", includeProjectGrowthTable);
    }

    public CompetitionLineBuilder withCreatedBy(String... users) {
        return withArraySetFieldByReflection("createdBy", users);
    }

    public CompetitionLineBuilder withCreatedOn(ZonedDateTime... createdOns) {
        return withArraySetFieldByReflection("createdOn", createdOns);
    }

    public CompetitionLineBuilder withModifiedBy(String... users) {
        return withArraySetFieldByReflection("modifiedBy", users);
    }

    public CompetitionLineBuilder withModifiedOn(ZonedDateTime... modifiedOns) {
        return withArraySetFieldByReflection("modifiedOn", modifiedOns);
    }

    public CompetitionLineBuilder withCompletionStage(CompetitionCompletionStage... completionStage) {
        return withArraySetFieldByReflection("completionStage", completionStage);
    }

    public CompetitionLineBuilder withFundingType(FundingType... fundingTypes) {
        return withArray((fundingType, CompetitionLine) -> CompetitionLine.setFundingType(fundingType), fundingTypes);
    }

//    @SafeVarargs
//    public final CompetitionLineBuilder withFinanceRowTypes(List<FinanceRowType>... financeRowTypes) {
//        return withArray((financeRowType, CompetitionLine) -> CompetitionLine.setFinanceRowTypes(financeRowType), financeRowTypes);
//    }
//
//    public CompetitionLineBuilder withCompetitionTerms(FileEntryResource... competitionTermsItems) {
//        return withArray((competitionTerms, CompetitionLine) -> CompetitionLine.setCompetitionTerms(competitionTerms), competitionTermsItems);
//    }

//    public CompetitionLineBuilder withHasAssessmentStage(Boolean... hasAssessmentStages) {
//        return withArray((hasAssessmentStage, CompetitionLine) -> CompetitionLine.setHasAssessmentStage(hasAssessmentStage), hasAssessmentStages);
//    }

//    @SafeVarargs
//    public final CompetitionLineBuilder withProjectSetupStages(List<ProjectSetupStage>... projectSetupStages) {
//        return withArray((projectSetupStage, CompetitionLine) -> CompetitionLine.setProjectSetupStages(projectSetupStage), projectSetupStages);
//    }
//
//    public CompetitionLineBuilder withCompetitionTypeEnum(CompetitionTypeEnum... types) {
//        return withArray((type, CompetitionLine) -> CompetitionLine.setCompetitionTypeEnum(type), types);
//    }
//
//    public CompetitionLineBuilder withGolTemplate(GolTemplateResource... golTemplates) {
//        return withArray((golTemplate, CompetitionLine) -> {
//            CompetitionLine.setGolTemplate(golTemplate);
//        }, golTemplates);
//    }

    public CompetitionLineBuilder withAlwaysOpen(Boolean... alwaysOpens) {
        return withArray((alwaysOpen, CompetitionLine) -> CompetitionLine.setAlwaysOpen(alwaysOpen), alwaysOpens);
    }

    public CompetitionLineBuilder withCompetitionApplicationConfig(CompetitionApplicationConfigResource... competitionApplicationConfigResources) {
        return withArraySetFieldByReflection("competitionApplicationConfig", competitionApplicationConfigResources);
    }

    @Override
    protected CompetitionLineBuilder createNewBuilderWithActions(List<BiConsumer<Integer, CompetitionLine>> actions) {
        return new CompetitionLineBuilder(actions);
    }

    @Override
    protected CompetitionLine createInitial() {
        return new CompetitionLine();
    }

}