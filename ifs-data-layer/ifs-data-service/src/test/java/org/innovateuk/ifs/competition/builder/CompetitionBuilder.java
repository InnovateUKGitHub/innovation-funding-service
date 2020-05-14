package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.category.domain.InnovationSector;
import org.innovateuk.ifs.competition.domain.*;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.competitionsetup.domain.CompetitionDocument;
import org.innovateuk.ifs.finance.domain.GrantClaimMaximum;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.form.domain.Section;
import org.innovateuk.ifs.user.domain.User;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.*;
import static org.innovateuk.ifs.competition.builder.CompetitionTypeBuilder.newCompetitionType;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.*;

public class CompetitionBuilder extends BaseBuilder<Competition, CompetitionBuilder> {

    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private CompetitionBuilder(List<BiConsumer<Integer, Competition>> newMultiActions) {
        super(newMultiActions);
    }

    public static CompetitionBuilder newCompetition() {
        return new CompetitionBuilder(emptyList()).
                with(uniqueIds()).
                withFundingType(FundingType.GRANT).
                with(idBasedNames("Competition ")).
                with(competition -> {
                    GrantTermsAndConditions termsAndConditions = new GrantTermsAndConditions();
                    termsAndConditions.setId(1L);
                    competition.setTermsAndConditions(termsAndConditions);
                });
    }

    public CompetitionBuilder withSections(List<Section> sections) {
        return with(competition -> competition.setSections(sections));
    }

    public CompetitionBuilder withQuestions(List<Question>... questions) {
        return withArray((question, competition) -> competition.setQuestions(question), questions);
    }

    public CompetitionBuilder withSetupComplete(boolean... setupComplete) {
        return withArraySetFieldByReflection("setupComplete", setupComplete);
    }

    public CompetitionBuilder withLocationPerPartner(boolean... locationPerPartner) {
        return withArraySetFieldByReflection("locationPerPartner", locationPerPartner);
    }

    public CompetitionBuilder withStartDate(ZonedDateTime startDate) {
        return with(competition -> setField("startDate", startDate, competition));
    }
    public CompetitionBuilder withEndDate(ZonedDateTime endDate) {
        return with(competition -> setField("endDate", endDate, competition));
    }

    public CompetitionBuilder withAssessorAcceptsDate(ZonedDateTime assessorAcceptsDate) {
        return with(competition -> setField("assessorAcceptsDate", assessorAcceptsDate, competition));
    }

    public CompetitionBuilder withAssessorDeadlineDate(ZonedDateTime assessorDeadlineDate) {
        return with(competition -> setField("assessorDeadlineDate", assessorDeadlineDate, competition));
    }

    public CompetitionBuilder withReleaseFeedbackDate(ZonedDateTime releaseFeedbackDate) {
        return with(competition -> setField("releaseFeedbackDate", releaseFeedbackDate, competition));
    }

    public CompetitionBuilder withFundersPanelDate(ZonedDateTime fundersPanelDate) {
        return with(competition -> setField("fundersPanelDate", fundersPanelDate, competition));
    }

    public CompetitionBuilder withFundersPanelEndDate(ZonedDateTime endDate) {
        return with(competition -> competition.setFundersPanelEndDate(endDate));
    }

    public CompetitionBuilder withFeedbackReleased(ZonedDateTime feedbackReleasedDate) {
        return with(competition -> setField("feedbackReleasedDate", feedbackReleasedDate, competition));
    }

    public CompetitionBuilder withResubmission(Boolean resubmission) {
        return with(competition -> setField("resubmission", resubmission, competition));
    }

    public CompetitionBuilder withCompetitionType(CompetitionType competitionType) {
        return with(competition -> setField("competitionType", competitionType, competition));
    }

    public CompetitionBuilder withActivityCode(String activityCode) {
        return with(competition -> setField("activityCode", activityCode, competition));
    }

    public CompetitionBuilder withApplicationFinanceType(ApplicationFinanceType... applicationFinanceTypes) {
        return withArray((applicationFinanceType, competition) ->
                setField("applicationFinanceType", applicationFinanceType, competition), applicationFinanceTypes);
    }

    public CompetitionBuilder withInnovateBudget(String innovateBudget) {
        return with(competition -> setField("innovateBudget", innovateBudget, competition));
    }

    public CompetitionBuilder withMilestones(List<Milestone> milestones){
        return with(competition -> competition.setMilestones(milestones));
    }

    public CompetitionBuilder withLeadTechnologist(User leadTechnologist){
        return with(competition -> competition.setLeadTechnologist(leadTechnologist));
    }

    public CompetitionBuilder withName(String... names) {
        return withArray((competition, name) -> setField("name", competition, name), names);
    }

    public CompetitionBuilder withMaxProjectDuration(Integer... maxProjectDurations) {
        return withArray(
                (competition, maxProjectDuration) -> setField(
                        "maxProjectDuration",
                        competition,
                        maxProjectDuration),
                maxProjectDurations);
    }

    public CompetitionBuilder withMinProjectDuration(Integer... minProjectDurations) {
        return withArray(
                (competition, minProjectDuration) -> setField(
                        "minProjectDuration",
                        competition,
                        minProjectDuration),
                minProjectDurations);
    }

    public CompetitionBuilder withAssessorsNotifiedDate(ZonedDateTime... dates) {
        return withArray((date, competition) -> competition.notifyAssessors(date), dates);
    }

    public CompetitionBuilder withAssessmentClosedDate(ZonedDateTime... dates) {
        return withArray((date, competition) -> competition.closeAssessment(date), dates);
    }

    public CompetitionBuilder withCollaborationLevel(CollaborationLevel... collaborationLevels) {
        return withArray((collaborationLevel, competition) ->
                competition.setCollaborationLevel(collaborationLevel), collaborationLevels);
    }

    public CompetitionBuilder withCompetitionAssessmentConfig(CompetitionAssessmentConfig... competitionAssessmentConfigs) {
        return withArray((competitionAssessmentConfig, competition) -> competition.setCompetitionAssessmentConfig(competitionAssessmentConfig), competitionAssessmentConfigs);
    }

    public CompetitionBuilder withAssessorCount(Integer... assessorCounts) {
        return withArraySetFieldByReflection("assessorCount", assessorCounts);
    }
    public CompetitionBuilder withInnovationSector(InnovationSector... innovationSectors) {
        return withArray((innovationSector, competition) -> competition.setInnovationSector(innovationSector), innovationSectors);
    }

    public CompetitionBuilder withNonIfs(Boolean... nonIfs) {
        return withArraySetFieldByReflection("nonIfs", nonIfs);
    }

    public CompetitionBuilder withNonIfsUrl(String... nonIfsUrl) {
        return withArraySetFieldByReflection("nonIfsUrl", nonIfsUrl);
    }

    public CompetitionBuilder withAssessorFinanceView(AssessorFinanceView... assessorFinanceView) {
        return withArraySetFieldByReflection("assessorFinanceView", assessorFinanceView);
    }

    @SafeVarargs
    public final CompetitionBuilder withGrantClaimMaximums(List<GrantClaimMaximum>... grantClaimMaximumses) {
        return withArray((grantClaimMaximums, c) -> c.setGrantClaimMaximums(grantClaimMaximums), grantClaimMaximumses);
    }

    @SafeVarargs
    public final CompetitionBuilder withFinanceRowTypes(Set<FinanceRowType>... financeRowTypes) {
        return withArraySetFieldByReflection("financeRowTypes", financeRowTypes);
    }

    public CompetitionBuilder withTermsAndConditions(GrantTermsAndConditions... termsAndConditions) {
        return withArray((terms, competition) -> competition.setTermsAndConditions(terms), termsAndConditions);
    }

    public CompetitionBuilder withStateAid(Boolean... stateAid) {
        return withArraySetFieldByReflection("stateAid", stateAid);
    }

    public CompetitionBuilder withCompetitionDocuments(List<CompetitionDocument>... projectDocuments) {
        return withArraySetFieldByReflection("competitionDocuments", projectDocuments);
    }

    public CompetitionBuilder withIncludeYourOrganisationSection(Boolean... includeYourOrganisationSection) {
        return withArraySetFieldByReflection("includeYourOrganisationSection", includeYourOrganisationSection);
    }

    public CompetitionBuilder withCompetitionStatus(CompetitionStatus status) {
        ZonedDateTime now = ZonedDateTime.now();
        if(READY_TO_OPEN.equals(status)) {
    		return withSetupComplete(true)
                    .withStartDate(now.plusDays(1L));
    	} else if(OPEN.equals(status)) {
    		return withSetupComplete(true)
                    .withStartDate(now.minusDays(1L))
    				.withEndDate(now.plusDays(1L));
    	} else if(CLOSED.equals(status)) {
            return withSetupComplete(true)
                    .withStartDate(now.minusDays(2L))
                    .withEndDate(now.minusDays(1L))
    				.withAssessorAcceptsDate(now.plusDays(1L));
        } else if(IN_ASSESSMENT.equals(status)) {
    		return withSetupComplete(true)
                    .withStartDate(now.minusDays(2L))
    				.withEndDate(now.minusDays(1L))
    				.withFundersPanelDate(now.plusDays(1L));
    	} else if(FUNDERS_PANEL.equals(status)) {
    		return withSetupComplete(true)
                    .withStartDate(now.minusDays(5L))
    				.withEndDate(now.minusDays(4L))
                    .withAssessorsNotifiedDate(now.minusDays(3L))
                    .withAssessmentClosedDate(now.minusDays(2L))
    				.withFundersPanelDate(now.minusDays(1L));
    	} else if(ASSESSOR_FEEDBACK.equals(status)) {
            return withSetupComplete(true)
                    .withStartDate(now.minusDays(7L))
                    .withEndDate(now.minusDays(6L))
                    .withAssessorAcceptsDate(now.minusDays(5L))
                    .withAssessorsNotifiedDate(now.minusDays(4L))
                    .withFundersPanelDate(now.minusDays(3L))
                    .withFundersPanelEndDate(now.minusDays(2L))
                    .withAssessmentClosedDate(now.minusDays(1L));
        } else if(PROJECT_SETUP.equals(status)) {
            return withSetupComplete(true)
                    .withStartDate(now.minusDays(9L))
                    .withEndDate(now.minusDays(8L))
                    .withAssessorAcceptsDate(now.minusDays(7L))
                    .withAssessorsNotifiedDate(now.minusDays(6L))
                    .withFundersPanelDate(now.minusDays(5L))
                    .withFundersPanelEndDate(now.minusDays(4L))
                    .withAssessmentClosedDate(now.minusDays(3L))
                    .withAssessorFeedbackDate(now.minusDays(2L))
                    .withReleaseFeedbackDate(now.minusDays(10L))
                    .withFeedbackReleased(now.minusDays(1L));
        } else if(PREVIOUS.equals(status)) {
            return withSetupComplete(true)
                    .withStartDate(now.minusDays(9L))
                    .withEndDate(now.minusDays(8L))
                    .withAssessorAcceptsDate(now.minusDays(7L))
                    .withAssessorsNotifiedDate(now.minusDays(6L))
                    .withFundersPanelDate(now.minusDays(5L))
                    .withFundersPanelEndDate(now.minusDays(4L))
                    .withAssessmentClosedDate(now.minusDays(3L))
                    .withAssessorFeedbackDate(now.minusDays(2L))
                    .withReleaseFeedbackDate(now.minusDays(10L))
                    .withFeedbackReleased(now.minusDays(1L))
                    .withCompetitionType(newCompetitionType().withName("Expression of interest").build());
        } else if(COMPETITION_SETUP.equals(status)) {
            return withSetupComplete(false);
        } else {
                throw new RuntimeException("status " + status + " not yet supported by CompetitionBuilder.withCompetitionStatus method");
            }
        }

    public CompetitionBuilder withAssessorFeedbackDate(ZonedDateTime... endDate) {
        return withArray((date, competition) -> competition.setAssessorFeedbackDate(date), endDate);
    }

    /**
     * Convenience method to set dates with format dd/MM/yyyy
     *
     * @param endDate
     * @return
     */
    public CompetitionBuilder withAssessorFeedbackDate(String... endDate) {
        return withArray((date, competition) -> competition.setAssessorFeedbackDate(LocalDateTime.parse(date, DATE_FORMAT).atZone(ZoneId.systemDefault())), endDate);
    }

    @Override
    protected CompetitionBuilder createNewBuilderWithActions(List<BiConsumer<Integer, Competition>> actions) {
        return new CompetitionBuilder(actions);
    }

    @Override
    protected Competition createInitial() {
        return new Competition();
    }

    public CompetitionBuilder withId(Long... ids) {
        return withArray((id, object) -> setField("id", id, object), ids);
    }

    public CompetitionBuilder withMaxResearchRatio(Integer... ratios) { return withArray((ratio, competition) -> competition.setMaxResearchRatio(ratio), ratios); }

    public CompetitionBuilder withAcademicGrantPercentage(Integer... percentages) { return withArray((percentage, competition) -> competition.setAcademicGrantPercentage(percentage), percentages); }

    public CompetitionBuilder withLeadTechnologist(User... leadTechnologists) {
        return withArray((competition, leadTechnologist) -> setField("leadTechnologist", competition, leadTechnologist), leadTechnologists);
    }

    public CompetitionBuilder withIncludeProjectGrowthTable(Boolean... includeProjectGrowthTable) {
        return withArraySetFieldByReflection("includeProjectGrowthTable", includeProjectGrowthTable);
    }

    public CompetitionBuilder withCreatedBy(User... users) {
        return withArraySetFieldByReflection("createdBy", users);
    }

    public CompetitionBuilder withCreatedOn(ZonedDateTime... createdOns) {
        return withArraySetFieldByReflection("createdOn", createdOns);
    }

    public CompetitionBuilder withModifiedBy(User... users) {
        return withArraySetFieldByReflection("modifiedBy", users);
    }

    public CompetitionBuilder withModifiedOn(ZonedDateTime... modifiedOns) {
        return withArraySetFieldByReflection("modifiedOn", modifiedOns);
    }

    public CompetitionBuilder withIncludeJesForm(boolean... includeJesForms) {
        return withArraySetFieldByReflection("includeJesForm", includeJesForms);
    }

    public CompetitionBuilder withCompletionStage(CompetitionCompletionStage... completionStage) {
        return withArraySetFieldByReflection("completionStage", completionStage);
    }

    public CompetitionBuilder withFundingType(FundingType... fundingTypes) {
        return withArray((fundingType, competition) -> competition.setFundingType(fundingType), fundingTypes);
    }
}
