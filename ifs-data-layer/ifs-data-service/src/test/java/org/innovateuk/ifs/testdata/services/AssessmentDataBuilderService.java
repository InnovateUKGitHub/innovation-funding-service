package org.innovateuk.ifs.testdata.services;

import org.innovateuk.ifs.testdata.builders.CompetitionDataBuilder;
import org.innovateuk.ifs.testdata.builders.ServiceLocator;
import org.innovateuk.ifs.testdata.builders.TestService;
import org.innovateuk.ifs.testdata.builders.data.CompetitionData;
import org.innovateuk.ifs.user.transactional.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.testdata.ListenableFutureToCompletableFutureHelper.future;
import static org.innovateuk.ifs.testdata.builders.CompetitionDataBuilder.newCompetitionData;
import static org.innovateuk.ifs.testdata.services.CsvUtils.readApplications;
import static org.innovateuk.ifs.testdata.services.CsvUtils.readCompetitions;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.SYSTEM_REGISTRATION_USER;
import static org.innovateuk.ifs.util.CollectionFunctions.removeDuplicates;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * TODO DW - document this class
 */
@Component
public class AssessmentDataBuilderService extends BaseDataBuilderService {

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    private TestService testService;

    @Autowired
    private UserService userService;

    @Autowired
    private GenericApplicationContext applicationContext;

    private CompetitionDataBuilder competitionDataBuilder;

    private List<CsvUtils.CompetitionLine> competitionLines;

    private List<CsvUtils.ApplicationLine> applicationLines;

    @PostConstruct
    public void readCsvs() {
        ServiceLocator serviceLocator = new ServiceLocator(applicationContext, COMP_ADMIN_EMAIL, PROJECT_FINANCE_EMAIL);
        competitionDataBuilder = newCompetitionData(serviceLocator);
        competitionLines = readCompetitions();
        applicationLines = readApplications();
    }

    public List<CompletableFuture<CompetitionData>> createCompetitions() {

        List<CompletableFuture<CompetitionData>> futures = simpleMap(competitionLines, line -> {

            return future(taskExecutor.submitListenable(() -> {

                testService.doWithinTransaction(this::setDefaultCompAdmin);

                return createCompetition(line);
            }));
        });

        return futures;
    }

    public void moveCompetitionsToCorrectFinalState() {

        List<String> competitionsToAddApplicationsTo = removeDuplicates(simpleMap(applicationLines, line -> line.competitionName));

        competitionsToAddApplicationsTo.forEach(competitionName -> {

            Long competitionId = competitionRepository.findByName(competitionName).get(0).getId();

            CompetitionDataBuilder basicCompetitionInformation = competitionDataBuilder.withExistingCompetition(competitionId);

            basicCompetitionInformation.restoreOriginalMilestones().build();
        });
    }

    private CompetitionData createCompetition(CsvUtils.CompetitionLine competitionLine) {
        return competitionBuilderWithBasicInformation(competitionLine).build();
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
                        null, null, null, null, null, null, null, null, null,
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
                        line.budgetCode, line.pafCode, line.code, line.activityCode, line.assessorCount, line.assessorPay, line.hasAssessmentPanel, line.hasInterviewStage,
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
                setLoggedInUser(userService.findByEmail(COMP_ADMIN_EMAIL).getSuccessObjectOrThrowException())
        );
    }
}
