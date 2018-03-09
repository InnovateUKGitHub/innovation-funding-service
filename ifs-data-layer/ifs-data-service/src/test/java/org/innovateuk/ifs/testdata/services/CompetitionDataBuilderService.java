package org.innovateuk.ifs.testdata.services;

import org.innovateuk.ifs.competition.resource.AssessorFinanceView;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.innovateuk.ifs.testdata.builders.*;
import org.innovateuk.ifs.testdata.builders.data.CompetitionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.time.ZonedDateTime.now;
import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.testdata.builders.CompetitionDataBuilder.newCompetitionData;
import static org.innovateuk.ifs.testdata.builders.CompetitionFunderDataBuilder.newCompetitionFunderData;
import static org.innovateuk.ifs.testdata.builders.PublicContentDateDataBuilder.newPublicContentDateDataBuilder;
import static org.innovateuk.ifs.testdata.builders.PublicContentGroupDataBuilder.newPublicContentGroupDataBuilder;
import static org.innovateuk.ifs.testdata.services.CsvUtils.readCompetitionFunders;
import static org.innovateuk.ifs.testdata.services.CsvUtils.readCompetitions;
import static org.innovateuk.ifs.util.CollectionFunctions.*;

/**
 * A service that {@link org.innovateuk.ifs.testdata.BaseGenerateTestData} uses to generate Competition data.  While
 * {@link org.innovateuk.ifs.testdata.BaseGenerateTestData} is responsible for gathering CSV information and
 * orchestarting the building of it, this service is responsible for taking the CSV data passed to it and using
 * the appropriate builders to generate and update entities.
 */
@Component
@Lazy
public class CompetitionDataBuilderService extends BaseDataBuilderService {

    @Autowired
    private GenericApplicationContext applicationContext;

    private CompetitionDataBuilder competitionDataBuilder;
    private PublicContentGroupDataBuilder publicContentGroupDataBuilder;
    private PublicContentDateDataBuilder publicContentDateDataBuilder;
    private CompetitionFunderDataBuilder competitionFunderDataBuilder;

    private List<CsvUtils.CompetitionLine> competitionLines;
    private static List<CsvUtils.CompetitionFunderLine> competitionFunderLines;

    @PostConstruct
    public void readCsvs() {
        ServiceLocator serviceLocator = new ServiceLocator(applicationContext, COMP_ADMIN_EMAIL, PROJECT_FINANCE_EMAIL);
        competitionDataBuilder = newCompetitionData(serviceLocator);
        publicContentGroupDataBuilder = newPublicContentGroupDataBuilder(serviceLocator);
        publicContentDateDataBuilder = newPublicContentDateDataBuilder(serviceLocator);
        competitionFunderDataBuilder = newCompetitionFunderData(serviceLocator);

        competitionLines = readCompetitions();
        competitionFunderLines = readCompetitionFunders();
    }

    public void moveCompetitionsToCorrectFinalState(List<CompetitionData> competitions) {

        competitions.forEach(competition -> {

            CsvUtils.CompetitionLine line = simpleFindFirstMandatory(competitionLines, l ->
                    Objects.equals(l.name, competition.getCompetition().getName()));

            CompetitionDataBuilder builder = this.competitionDataBuilder.
                    withExistingCompetition(competition);

            getCompetitionWithMilestones(line, builder).build();
        });
    }

    public void createPublicContentGroup(CsvUtils.PublicContentGroupLine line) {
        publicContentGroupDataBuilder.withPublicContentGroup(line.competitionName, line.heading, line.content, line.section)
                .build();
    }

    public void createPublicContentDate(CsvUtils.PublicContentDateLine line) {
        publicContentDateDataBuilder.withPublicContentDate(line.competitionName, line.date, line.content)
                .build();
    }

    public void createCompetitionFunder(CompetitionData competition) {

        Optional<CsvUtils.CompetitionFunderLine> funderLine = simpleFindFirst(competitionFunderLines, l ->
                Objects.equals(competition.getCompetition().getName(), l.competitionName));

        funderLine.ifPresent(line ->
                competitionFunderDataBuilder.
                        withCompetitionFunderData(line.competitionName, line.funder, line.funder_budget, line.co_funder).
                        build());
    }

    public CompetitionData createCompetition(CsvUtils.CompetitionLine competitionLine) {
        return competitionBuilderWithBasicInformation(competitionLine).build();
    }

    public void moveCompetitionIntoOpenStatus(CompetitionData competition) {
        CompetitionDataBuilder basicCompetitionInformation = competitionDataBuilder.withExistingCompetition(competition);
        basicCompetitionInformation.moveCompetitionIntoOpenStatus().build();
    }

    private CompetitionDataBuilder competitionBuilderWithBasicInformation(CsvUtils.CompetitionLine line) {
        if (line.nonIfs) {
            return nonIfsCompetitionDataBuilder(line);
        } else {
            return ifsCompetitionDataBuilder(line);
        }
    }

    private CompetitionDataBuilder nonIfsCompetitionDataBuilder(CsvUtils.CompetitionLine line) {

        CompetitionDataBuilder competitionWithoutMilestones = this.competitionDataBuilder
                .createNonIfsCompetition()
                .withBasicData(line.name, null, line.innovationAreas,
                        line.innovationSector, null, null, null,
                        null, null, null, null, null, null, null, null, AssessorFinanceView.OVERVIEW, null,
                        null, emptyList(), null, null, line.nonIfsUrl);

        CompetitionDataBuilder competitionWithMilestones = getCompetitionWithMilestones(line, competitionWithoutMilestones);

        return competitionWithMilestones
                .withPublicContent(
                        line.published, line.shortDescription, line.fundingRange, line.eligibilitySummary,
                        line.competitionDescription, line.fundingType, line.projectSize, line.keywords, line.inviteOnly);
    }

    private CompetitionDataBuilder ifsCompetitionDataBuilder(CsvUtils.CompetitionLine line) {

        CompetitionDataBuilder competitionBeforeMilestones = this.competitionDataBuilder.
                createCompetition().
                withBasicData(line.name, line.type, line.innovationAreas,
                    line.innovationSector, line.researchCategory, line.leadTechnologist, line.compExecutive,
                    line.budgetCode, line.pafCode, line.code, line.activityCode, line.assessorCount, line.assessorPay, line.hasAssessmentPanel, line.hasInterviewStage, line.assessorFinanceView,
                    line.multiStream, line.collaborationLevel, line.leadApplicantTypes, line.researchRatio, line.resubmission, null).
                withApplicationFormFromTemplate().
                withNewMilestones();

        CompetitionDataBuilder competitionWithMilestones = getCompetitionWithMilestones(line, competitionBeforeMilestones);
        return competitionWithMilestones.
                withPublicContent(
                    line.published, line.shortDescription, line.fundingRange, line.eligibilitySummary,
                    line.competitionDescription, line.fundingType, line.projectSize, line.keywords,
                    line.inviteOnly);
    }

    private CompetitionDataBuilder getCompetitionWithMilestones(CsvUtils.CompetitionLine line, CompetitionDataBuilder competitionBeforeMilestones) {
        switch (line.competitionStatus) {

            case OPEN: return line.nonIfs ?
                    withOpenStatusNonIfs(competitionBeforeMilestones) :
                    withOpenStatus(competitionBeforeMilestones);

            case ASSESSOR_FEEDBACK: return withAssessorFeedbackStatus(competitionBeforeMilestones);
            case CLOSED: return withClosedStatus(competitionBeforeMilestones);
            case COMPETITION_SETUP: return withCompetitionSetupStatus(competitionBeforeMilestones);
            case FUNDERS_PANEL: return withFundersPanelStatus(competitionBeforeMilestones);
            case IN_ASSESSMENT: return withInAssessmentStatus(competitionBeforeMilestones);
            case PROJECT_SETUP: return withProjectSetupStatus(competitionBeforeMilestones);
            case READY_TO_OPEN: return line.nonIfs ?
                    withReadyToOpenStatusNonIfs(competitionBeforeMilestones) :
                    withReadyToOpenStatus(competitionBeforeMilestones);

            default: throw new IllegalArgumentException("Unknown CompetitionStatus value of " + line.competitionStatus.name());
        }
    }

    private CompetitionDataBuilder withReadyToOpenStatus(CompetitionDataBuilder competitionBeforeMilestones) {
        return withCalculatedMilestones(competitionBeforeMilestones, MilestoneType.OPEN_DATE).
                withSetupComplete();
    }

    private CompetitionDataBuilder withReadyToOpenStatusNonIfs(CompetitionDataBuilder competitionBeforeMilestones) {
        return competitionBeforeMilestones.
                withOpenDate(now().plusYears(2)).
                withRegistrationDate(now().plusYears(2).plusDays(10)).
                withSubmissionDate(now().plusYears(2).plusDays(20)).
                withFundersPanelEndDate(now().plusYears(2).plusDays(20));
    }

    private CompetitionDataBuilder withProjectSetupStatus(CompetitionDataBuilder competitionBeforeMilestones) {
        return withCalculatedMilestones(competitionBeforeMilestones, null).
                withAssessorsNotifiedDate(now().minusDays(10)).
                withAssessmentClosedDate(now().minusDays(5)).
                withFeedbackReleasedDate(now().minusDays(2)).
                withSetupComplete();
    }

    private CompetitionDataBuilder withInAssessmentStatus(CompetitionDataBuilder competitionBeforeMilestones) {
        return withCalculatedMilestones(competitionBeforeMilestones, MilestoneType.ASSESSOR_ACCEPTS).
                withAssessorsNotifiedDate(now().minusDays(2)).
//                withMilestoneUpdate(now().plusYears(2), MilestoneType.ASSESSMENT_CLOSED).
                withSetupComplete();
    }

    private CompetitionDataBuilder withFundersPanelStatus(CompetitionDataBuilder competitionBeforeMilestones) {
        return withCalculatedMilestones(competitionBeforeMilestones, MilestoneType.ASSESSMENT_PANEL).//MilestoneType.ASSESSOR_DEADLINE).
                withAssessorsNotifiedDate(now().minusDays(10)).
                withAssessmentClosedDate(now().minusDays(2)).
                withSetupComplete();
    }

    private CompetitionDataBuilder withCompetitionSetupStatus(CompetitionDataBuilder competitionBeforeMilestones) {
        return competitionBeforeMilestones;
    }

    private CompetitionDataBuilder withClosedStatus(CompetitionDataBuilder competitionBeforeMilestones) {
        return withCalculatedMilestones(competitionBeforeMilestones, MilestoneType.ASSESSOR_ACCEPTS).
                withSetupComplete();
    }

    private CompetitionDataBuilder withAssessorFeedbackStatus(CompetitionDataBuilder competitionBeforeMilestones) {
        return withCalculatedMilestones(competitionBeforeMilestones, MilestoneType.RELEASE_FEEDBACK).
                withAssessorsNotifiedDate(now().minusDays(10)).
                withAssessmentClosedDate(now().minusDays(2)).
                withAssessorEndDate(now().plusYears(2)).
                withSetupComplete();
    }

    private CompetitionDataBuilder withOpenStatus(CompetitionDataBuilder competitionBeforeMilestones) {
        return withCalculatedMilestones(competitionBeforeMilestones, MilestoneType.SUBMISSION_DATE).
                withSetupComplete();
    }

    private CompetitionDataBuilder withOpenStatusNonIfs(CompetitionDataBuilder competitionBeforeMilestones) {
        return competitionBeforeMilestones.
                withOpenDate(now().minusDays(2)).
                withRegistrationDate(now().plusYears(2)).
                withSubmissionDate(now().plusYears(2).plusDays(10)).
                withFundersPanelEndDate(now().plusYears(2).plusDays(20));
    }

    private CompetitionDataBuilder withCalculatedMilestones(CompetitionDataBuilder competitionBeforeMilestones,
                                                            MilestoneType milestoneWhereDatesStartInTheFuture) {

        List<MilestoneType> milestoneTypes = simpleFilter(MilestoneType.values(), type -> type.isPresetDate() && !type.equals(MilestoneType.REGISTRATION_DATE));

        CompetitionDataBuilder competitionBuilder = competitionBeforeMilestones;

        int indexWhereDatesStartInTheFuture = milestoneWhereDatesStartInTheFuture != null ?
                milestoneTypes.indexOf(milestoneWhereDatesStartInTheFuture) :
                milestoneTypes.size() - 1;

        for (int i = 0; i < indexWhereDatesStartInTheFuture; i++) {
            competitionBuilder = competitionBuilder.withMilestoneUpdate(now().minusYears(2).plusDays(i * 10), milestoneTypes.get(i));
        }

        for (int i = indexWhereDatesStartInTheFuture; i < milestoneTypes.size(); i++) {
            competitionBuilder = competitionBuilder.withMilestoneUpdate(now().plusYears(2).plusDays(i * 10), milestoneTypes.get(i));
        }

        return competitionBuilder;
    }
}
