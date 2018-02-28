package org.innovateuk.ifs.testdata.services;

import org.innovateuk.ifs.testdata.builders.*;
import org.innovateuk.ifs.testdata.builders.data.CompetitionData;
import org.innovateuk.ifs.user.transactional.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.testdata.builders.CompetitionDataBuilder.newCompetitionData;
import static org.innovateuk.ifs.testdata.builders.CompetitionFunderDataBuilder.newCompetitionFunderData;
import static org.innovateuk.ifs.testdata.builders.PublicContentDateDataBuilder.newPublicContentDateDataBuilder;
import static org.innovateuk.ifs.testdata.builders.PublicContentGroupDataBuilder.newPublicContentGroupDataBuilder;
import static org.innovateuk.ifs.testdata.services.CsvUtils.readCompetitionFunders;
import static org.innovateuk.ifs.testdata.services.CsvUtils.readCompetitions;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.SYSTEM_REGISTRATION_USER;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirstMandatory;

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
    private TestService testService;

    @Autowired
    private UserService userService;

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

            competitionDataBuilder.
                    withExistingCompetition(competition).
                    withOpenDate(line.openDate).
                    withBriefingDate(line.briefingDate).
                    withSubmissionDate(line.submissionDate).
                    withAllocateAssesorsDate(line.allocateAssessorDate).
                    withAssessorBriefingDate(line.assessorBriefingDate).
                    withAssessorAcceptsDate(line.assessorAcceptsDate).
                    withAssessorsNotifiedDate(line.assessorsNotifiedDate).
                    withAssessorEndDate(line.assessorEndDate).
                    withAssessmentClosedDate(line.assessmentClosedDate).
                    withLineDrawDate(line.drawLineDate).
                    withAsessmentPanelDate(line.assessmentPanelDate).
                    withPanelDate(line.panelDate).
                    withFundersPanelDate(line.fundersPanelDate).
                    withFundersPanelEndDate(line.fundersPanelEndDate).
                    withReleaseFeedbackDate(line.releaseFeedback).
                    withFeedbackReleasedDate(line.feedbackReleased).
                    build();
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
        CompetitionDataBuilder basicInformation;
        if (line.nonIfs) {
            basicInformation = nonIfsCompetitionDataBuilder(line);
        } else {
            basicInformation = ifsCompetitionDataBuilder(line);
        }

        return line.setupComplete ? basicInformation.withSetupComplete() : basicInformation;
    }

    private CompetitionDataBuilder nonIfsCompetitionDataBuilder(CsvUtils.CompetitionLine line) {

        return competitionDataBuilder
                .createNonIfsCompetition()
                .withBasicData(line.name, null, line.innovationAreas,
                        line.innovationSector, null, null, null,
                        null, null, null, null, null, null, null, null, null, null,
                        null, emptyList(), null, null, line.nonIfsUrl)
                .withOpenDate(line.openDate)
                .withSubmissionDate(line.submissionDate)
                .withFundersPanelEndDate(line.fundersPanelEndDate)
                .withReleaseFeedbackDate(line.releaseFeedback)
                .withRegistrationDate(line.registrationDate)
                .withPublicContent(
                        line.published, line.shortDescription, line.fundingRange, line.eligibilitySummary,
                        line.competitionDescription, line.fundingType, line.projectSize, line.keywords, line.inviteOnly);
    }

    private CompetitionDataBuilder ifsCompetitionDataBuilder(CsvUtils.CompetitionLine line) {

        return competitionDataBuilder.
                createCompetition().
                withBasicData(line.name, line.type, line.innovationAreas,
                        line.innovationSector, line.researchCategory, line.leadTechnologist, line.compExecutive,
                        line.budgetCode, line.pafCode, line.code, line.activityCode, line.assessorCount, line.assessorPay, line.hasAssessmentPanel, line.hasInterviewStage, line.assessorFinanceView,
                        line.multiStream, line.collaborationLevel, line.leadApplicantTypes, line.researchRatio, line.resubmission, null).
                withApplicationFormFromTemplate().
                withNewMilestones().
                withOpenDate(line.openDate).
                withBriefingDate(line.briefingDate).
                withSubmissionDate(line.submissionDate).
                withAllocateAssesorsDate(line.allocateAssessorDate).
                withAssessorBriefingDate(line.assessorBriefingDate).
                withAssessorAcceptsDate(line.assessorAcceptsDate).
                withAssessorsNotifiedDate(line.assessorsNotifiedDate).
                withAssessorEndDate(line.assessorEndDate).
                withAssessmentClosedDate(line.assessmentClosedDate).
                withLineDrawDate(line.drawLineDate).
                withAsessmentPanelDate(line.assessmentPanelDate).
                withPanelDate(line.panelDate).
                withFundersPanelDate(line.fundersPanelDate).
                withFundersPanelEndDate(line.fundersPanelEndDate).
                withReleaseFeedbackDate(line.releaseFeedback).
                withFeedbackReleasedDate(line.feedbackReleased).
                withPublicContent(
                        line.published, line.shortDescription, line.fundingRange, line.eligibilitySummary,
                        line.competitionDescription, line.fundingType, line.projectSize, line.keywords,
                        line.inviteOnly);
    }

    private void setDefaultCompAdmin() {
        setLoggedInUser(newUserResource().withRolesGlobal(newRoleResource().withType(SYSTEM_REGISTRATION_USER).build(1)).build());
        testService.doWithinTransaction(() ->
                setLoggedInUser(userService.findByEmail(COMP_ADMIN_EMAIL).getSuccess())
        );
    }
}
