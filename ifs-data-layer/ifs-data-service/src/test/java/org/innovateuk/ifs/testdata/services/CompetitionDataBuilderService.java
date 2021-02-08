package org.innovateuk.ifs.testdata.services;

import org.apache.commons.lang3.BooleanUtils;
import org.innovateuk.ifs.competition.resource.CompetitionCompletionStage;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.innovateuk.ifs.testdata.CompetitionOrganisationConfigDataBuilder;
import org.innovateuk.ifs.testdata.builders.*;
import org.innovateuk.ifs.testdata.builders.data.CompetitionData;
import org.innovateuk.ifs.testdata.builders.data.CompetitionLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.time.ZonedDateTime.now;
import static org.innovateuk.ifs.competition.resource.MilestoneType.ASSESSOR_ACCEPTS;
import static org.innovateuk.ifs.competition.resource.MilestoneType.SUBMISSION_DATE;
import static org.innovateuk.ifs.testdata.CompetitionOrganisationConfigDataBuilder.newCompetitionConfigData;
import static org.innovateuk.ifs.testdata.builders.AssessmentPeriodDataBuilder.newCompetitionAssessmentPeriods;
import static org.innovateuk.ifs.testdata.builders.CompetitionDataBuilder.newCompetitionData;
import static org.innovateuk.ifs.testdata.builders.CompetitionFunderDataBuilder.newCompetitionFunderData;
import static org.innovateuk.ifs.testdata.builders.PublicContentDateDataBuilder.newPublicContentDateDataBuilder;
import static org.innovateuk.ifs.testdata.builders.PublicContentGroupDataBuilder.newPublicContentGroupDataBuilder;
import static org.innovateuk.ifs.testdata.data.CompetitionWebTestData.buildCompetitionLines;
import static org.innovateuk.ifs.testdata.services.CsvUtils.*;
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
    private CompetitionOrganisationConfigDataBuilder competitionOrganisationConfigDataBuilder;
    private AssessmentPeriodDataBuilder assessmentPeriodDataBuilder;

    private List<CompetitionLine> competitionLines;
    private static List<CsvUtils.CompetitionFunderLine> competitionFunderLines;
    private static List<CsvUtils.CompetitionOrganisationConfigLine> competitionOrganisationConfigLines;
    private static List<CsvUtils.AssessmentPeriodLine> competitionAssessmentPeriodLines;

    @PostConstruct
    public void readCsvs() {
        ServiceLocator serviceLocator = new ServiceLocator(applicationContext, COMP_ADMIN_EMAIL, PROJECT_FINANCE_EMAIL);
        competitionDataBuilder = newCompetitionData(serviceLocator);
        publicContentGroupDataBuilder = newPublicContentGroupDataBuilder(serviceLocator);
        publicContentDateDataBuilder = newPublicContentDateDataBuilder(serviceLocator);
        competitionFunderDataBuilder = newCompetitionFunderData(serviceLocator);
        competitionOrganisationConfigDataBuilder = newCompetitionConfigData(serviceLocator);
        assessmentPeriodDataBuilder = newCompetitionAssessmentPeriods(serviceLocator);

        competitionLines = buildCompetitionLines();
        competitionFunderLines = readCompetitionFunders();
        competitionOrganisationConfigLines = readCompetitionOrganisationConfig();
        competitionAssessmentPeriodLines = readCompetitionAssessmentPeriods();
    }

    public void moveCompetitionsToCorrectFinalState(List<CompetitionData> competitions) {

        competitions.forEach(competition -> {

            CompetitionLine line = simpleFindFirstMandatory(competitionLines, l ->
                    Objects.equals(l.getName(), competition.getCompetition().getName()));

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
                competition.getCompetition().getName().equals(l.competitionName));

        funderLine.ifPresent(line ->
                competitionFunderDataBuilder.
                        withCompetitionFunderData(line.competitionName, line.funder, line.funder_budget, line.co_funder).
                        build());
    }

    public void createCompetitionOrganisationConfig(CompetitionData competition) {

        Optional<CsvUtils.CompetitionOrganisationConfigLine> competitionOrganisationConfigLine = simpleFindFirst(competitionOrganisationConfigLines, l ->
                competition.getCompetition().getName().equals(l.competition));

        if (competitionOrganisationConfigLine.isPresent()) {
            competitionOrganisationConfigLine.ifPresent(line ->
                    competitionOrganisationConfigDataBuilder.
                            withCompetitionOrganisationConfigData(line.competition, line.internationalOrganisationsAllowed, line.internationalLeadOrganisationAllowed).
                            build());
        } else {
            competitionOrganisationConfigDataBuilder.
                    withCompetitionOrganisationConfigData(competition.getCompetition().getName(), false, false).
                    build();
        }
    }

    public void createCompetitionAssessmentPeriods(CompetitionData competition) {

        List<CsvUtils.AssessmentPeriodLine> competitionAssessmentPeriods = simpleFilter(competitionAssessmentPeriodLines, l ->
                competition.getCompetition().getName().equals(l.competition));

        competitionAssessmentPeriods.forEach(assessmentPeriodLine ->
                assessmentPeriodDataBuilder.
                        withCompetitionAssessmentPeriods(assessmentPeriodLine.competition, assessmentPeriodLine.index,
                                assessmentPeriodLine.assessorBriefing, assessmentPeriodLine.assessorAccepts, assessmentPeriodLine.assessorDeadline).build()
        );
    }

    public CompetitionData createCompetition(CompetitionLine line) {
        if (line.isNonIfs()) {
            return nonIfsCompetitionDataBuilder(line).build();
        } else {
            return ifsCompetitionDataBuilder(line).build();
        }
    }

    public void moveCompetitionIntoOpenStatus(CompetitionData competition) {
        CompetitionDataBuilder basicCompetitionInformation = competitionDataBuilder.withExistingCompetition(competition);
        basicCompetitionInformation.moveCompetitionIntoOpenStatus().build();
    }

    private CompetitionDataBuilder nonIfsCompetitionDataBuilder(CompetitionLine line) {

        CompetitionDataBuilder competitionWithoutMilestones = this.competitionDataBuilder
                .createNonIfsCompetition()
                .withApplicationFinances()
                .withAssessmentConfig(line);

        CompetitionDataBuilder competitionWithMilestones = getCompetitionWithMilestones(line, competitionWithoutMilestones);

        return competitionWithMilestones
                .withDefaultPublicContent(line);
    }

    private CompetitionDataBuilder ifsCompetitionDataBuilder(CompetitionLine line) {

        CompetitionDataBuilder competitionBeforeMilestones = this.competitionDataBuilder.
                createCompetition().
                withApplicationFormFromTemplate().
                withApplicationFinances().
                withAssessmentConfig(line).
                withNewMilestones();

        CompetitionDataBuilder competitionWithMilestones = getCompetitionWithMilestones(line, competitionBeforeMilestones);
        return competitionWithMilestones.
                withDefaultPublicContent(line);
    }

    private CompetitionDataBuilder getCompetitionWithMilestones(CompetitionLine line, CompetitionDataBuilder competitionBeforeMilestones) {

        switch (line.getCompetitionStatus()) {

            case OPEN:
                return line.isNonIfs() ?
                        withOpenStatusNonIfs(competitionBeforeMilestones) :
                        withOpenStatus(competitionBeforeMilestones, line.getCompetitionCompletionStage(), line.getAlwaysOpen());

            case ASSESSOR_FEEDBACK:
                return withAssessorFeedbackStatus(competitionBeforeMilestones, line.getCompetitionCompletionStage());
            case CLOSED:
                return withClosedStatus(competitionBeforeMilestones, line.getCompetitionCompletionStage());
            case COMPETITION_SETUP:
                return withCompetitionSetupStatus(competitionBeforeMilestones);
            case FUNDERS_PANEL:
                return withFundersPanelStatus(competitionBeforeMilestones, line.getCompetitionCompletionStage());
            case IN_ASSESSMENT:
                return withInAssessmentStatus(competitionBeforeMilestones, line.getCompetitionCompletionStage());
            case PROJECT_SETUP:
                return withProjectSetupStatus(competitionBeforeMilestones, line.getCompetitionCompletionStage());
            case READY_TO_OPEN:
                return line.isNonIfs() ?
                        withReadyToOpenStatusNonIfs(competitionBeforeMilestones) :
                        withReadyToOpenStatus(competitionBeforeMilestones, line.getCompetitionCompletionStage());

            default:
                throw new IllegalArgumentException("Unknown CompetitionStatus value of " + line.getCompetitionStatus().name());
        }
    }

    private CompetitionDataBuilder withReadyToOpenStatus(CompetitionDataBuilder competitionBeforeMilestones, CompetitionCompletionStage competitionCompletionStage) {
        return withCalculatedMilestones(competitionBeforeMilestones, MilestoneType.OPEN_DATE, competitionCompletionStage).
                withSetupComplete();
    }

    private CompetitionDataBuilder withReadyToOpenStatusNonIfs(CompetitionDataBuilder competitionBeforeMilestones) {
        return competitionBeforeMilestones.
                withOpenDate(startOfDay().plusYears(2)).
                withRegistrationDate(startOfDay().plusYears(2).plusDays(10)).
                withSubmissionDate(startOfDay().plusYears(2).plusDays(20)).
                withFundersPanelEndDate(startOfDay().plusYears(2).plusDays(20));
    }

    private CompetitionDataBuilder withProjectSetupStatus(CompetitionDataBuilder competitionBeforeMilestones, CompetitionCompletionStage competitionCompletionStage) {
        return withCalculatedMilestones(competitionBeforeMilestones, null, competitionCompletionStage).
                withAssessorsNotifiedDate(startOfDay().minusDays(10)).
                withAssessmentClosedDate(startOfDay().minusDays(5)).
                withFeedbackReleasedDate(startOfDay().minusDays(2)).
                withSetupComplete();
    }

    private CompetitionDataBuilder withInAssessmentStatus(CompetitionDataBuilder competitionBeforeMilestones, CompetitionCompletionStage competitionCompletionStage) {
        return withCalculatedMilestones(competitionBeforeMilestones, ASSESSOR_ACCEPTS, competitionCompletionStage).
                withAssessorsNotifiedDate(startOfDay().minusDays(2)).
                withSetupComplete();
    }

    private CompetitionDataBuilder withFundersPanelStatus(CompetitionDataBuilder competitionBeforeMilestones, CompetitionCompletionStage competitionCompletionStage) {
        return withCalculatedMilestones(competitionBeforeMilestones, MilestoneType.ASSESSMENT_PANEL, competitionCompletionStage).//MilestoneType.ASSESSOR_DEADLINE).
                withAssessorsNotifiedDate(startOfDay().minusDays(10)).
                withAssessmentClosedDate(startOfDay().minusDays(2)).
                withSetupComplete();
    }

    private CompetitionDataBuilder withCompetitionSetupStatus(CompetitionDataBuilder competitionBeforeMilestones) {
        return competitionBeforeMilestones;
    }

    private CompetitionDataBuilder withClosedStatus(CompetitionDataBuilder competitionBeforeMilestones, CompetitionCompletionStage competitionCompletionStage) {
        return withCalculatedMilestones(competitionBeforeMilestones, ASSESSOR_ACCEPTS, competitionCompletionStage).
                withSetupComplete();
    }

    private CompetitionDataBuilder withAssessorFeedbackStatus(CompetitionDataBuilder competitionBeforeMilestones, CompetitionCompletionStage competitionCompletionStage) {
        return withCalculatedMilestones(competitionBeforeMilestones, MilestoneType.RELEASE_FEEDBACK, competitionCompletionStage).
                withAssessorsNotifiedDate(startOfDay().minusDays(10)).
                withAssessmentClosedDate(startOfDay().minusDays(2)).
                withAssessorEndDate(startOfDay().plusYears(2)).
                withSetupComplete();
    }

    private CompetitionDataBuilder withOpenStatus(CompetitionDataBuilder competitionBeforeMilestones, CompetitionCompletionStage competitionCompletionStage, Boolean alwaysOpen) {
        return withCalculatedMilestones(competitionBeforeMilestones, SUBMISSION_DATE, competitionCompletionStage, BooleanUtils.toBoolean(alwaysOpen)).
                withSetupComplete();
    }

    private CompetitionDataBuilder withOpenStatusNonIfs(CompetitionDataBuilder competitionBeforeMilestones) {

        return competitionBeforeMilestones.
                withOpenDate(startOfDay().minusDays(2)).
                withRegistrationDate(startOfDay().plusYears(2)).
                withSubmissionDate(startOfDay().plusYears(2).plusDays(10)).
                withFundersPanelEndDate(startOfDay().plusYears(2).plusDays(20));
    }

    private CompetitionDataBuilder withCalculatedMilestones(CompetitionDataBuilder competitionBeforeMilestones,
                                                            MilestoneType milestoneWhereDatesStartInTheFuture,
                                                            CompetitionCompletionStage competitionCompletionStage) {
        return withCalculatedMilestones(competitionBeforeMilestones, milestoneWhereDatesStartInTheFuture, competitionCompletionStage, false);
    }

    private CompetitionDataBuilder withCalculatedMilestones(CompetitionDataBuilder competitionBeforeMilestones,
                                                            MilestoneType milestoneWhereDatesStartInTheFuture,
                                                            CompetitionCompletionStage competitionCompletionStage,
                                                            boolean isAlwaysOpen) {

        ZonedDateTime earliestDate = startOfDay().minusYears(2).plusDays(5);
        ZonedDateTime firstFutureDate = startOfDay().plusYears(2).plusDays(5);

        List<MilestoneType> presetMilestoneTypes = Arrays.stream(isAlwaysOpen ? MilestoneType.alwaysOpenValues() : MilestoneType.values())
                .filter(type -> type.isPresetDate() && !type.equals(MilestoneType.REGISTRATION_DATE))
                .filter(milestoneType -> milestoneType.getPriority() <= competitionCompletionStage.getLastMilestone().getPriority())
                .collect(Collectors.toList());

        CompetitionDataBuilder competitionBuilder = competitionBeforeMilestones;

        int indexWhereDatesStartInTheFuture = milestoneWhereDatesStartInTheFuture != null ?
                presetMilestoneTypes.indexOf(milestoneWhereDatesStartInTheFuture) :
                presetMilestoneTypes.size() - 1;

        for (int i = 0; i < indexWhereDatesStartInTheFuture; i++) {
            competitionBuilder = competitionBuilder.withMilestoneUpdate(earliestDate.plusDays(i * 10), presetMilestoneTypes.get(i));
        }

        for (int i = indexWhereDatesStartInTheFuture; i < presetMilestoneTypes.size(); i++) {
            competitionBuilder = competitionBuilder.withMilestoneUpdate(firstFutureDate.plusDays(i * 10), presetMilestoneTypes.get(i));
        }

        return competitionBuilder;
    }

    private ZonedDateTime startOfDay() {
        return now().truncatedTo(ChronoUnit.DAYS);
    }
}
