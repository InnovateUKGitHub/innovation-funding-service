package org.innovateuk.ifs.testdata.builders;

import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.application.resource.FundingNotificationResource;
import org.innovateuk.ifs.competition.domain.CompetitionType;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.innovateuk.ifs.testdata.builders.data.CompetitionData;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.*;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.resource.MilestoneType.*;
import static org.innovateuk.ifs.testdata.builders.ApplicationDataBuilder.newApplicationData;
import static org.innovateuk.ifs.util.CollectionFunctions.*;

/**
 * Generates data from Competitions, including any Applications taking part in this Competition
 */
public class CompetitionDataBuilder extends BaseDataBuilder<CompetitionData, CompetitionDataBuilder> {

    public CompetitionDataBuilder createCompetition() {

        return asCompAdmin(data -> {

            CompetitionResource newCompetition = competitionSetupService.
                    create().
                    getSuccessObjectOrThrowException();

            updateCompetitionInCompetitionData(data, newCompetition.getId());
        });
    }

    public CompetitionDataBuilder createNonIfsCompetition() {

        return asCompAdmin(data -> {

            CompetitionResource newCompetition = competitionSetupService.
                    createNonIfs().
                    getSuccessObjectOrThrowException();

            updateCompetitionInCompetitionData(data, newCompetition.getId());
        });
    }

    public CompetitionDataBuilder withExistingCompetition(Long competitionId) {

        return asCompAdmin(data -> {
            CompetitionResource existingCompetition = competitionService.getCompetitionById(competitionId).getSuccessObjectOrThrowException();
            updateCompetitionInCompetitionData(data, existingCompetition.getId());
            publicContentService.initialiseByCompetitionId(competitionId).getSuccessObjectOrThrowException();
        });
    }

    public CompetitionDataBuilder withBasicData(String name, String description, String competitionTypeName, String innovationAreaName,
                                                String innovationSectorName, String researchCategoryName, String leadTechnologist,
                                                String compExecutive, String budgetCode, String pafCode, String code, String activityCode, Integer assessorCount, BigDecimal assessorPay,
                                                Boolean multiStream, String collaborationLevelCode, String leadApplicantTypeCode, Integer researchRatio, Boolean resubmission, String nonIfsUrl) {

        return asCompAdmin(data -> {

            doCompetitionDetailsUpdate(data, competition -> {

                if (competitionTypeName != null) {
                    CompetitionType competitionType = competitionTypeRepository.findByName(competitionTypeName).get(0);
                    competition.setCompetitionType(competitionType.getId());
                }
                Long innovationArea = getInnovationAreaIdOrNull(innovationAreaName);
                Long innovationSector = getInnovationSectorIdOrNull(innovationSectorName);
                Long researchCategory = getResearchCategoryIdOrNull(researchCategoryName);

                CollaborationLevel collaborationLevel = CollaborationLevel.fromCode(collaborationLevelCode);
                LeadApplicantType leadApplicantType = LeadApplicantType.BUSINESS.fromCode(leadApplicantTypeCode);

                competition.setName(name);
                competition.setDescription(description);
                competition.setInnovationAreas(innovationArea == null ? emptySet() : singleton(innovationArea));
                competition.setInnovationSector(innovationSector);
                competition.setResearchCategories(researchCategory == null ? emptySet() : singleton(researchCategory));
                competition.setMaxResearchRatio(30);
                competition.setAcademicGrantPercentage(100);
                competition.setLeadTechnologist(userRepository.findByEmail(leadTechnologist).map(User::getId).orElse(null));
                competition.setExecutive(userRepository.findByEmail(compExecutive).map(User::getId).orElse(null));
                competition.setPafCode(pafCode);
                competition.setCode(code);
                competition.setBudgetCode(budgetCode);
                competition.setActivityCode(activityCode);
                competition.setCollaborationLevel(collaborationLevel);
                competition.setLeadApplicantType(leadApplicantType);
                competition.setMaxResearchRatio(researchRatio);
                competition.setResubmission(resubmission);
                competition.setMultiStream(multiStream);
                competition.setAssessorPay(assessorPay);
                competition.setAssessorCount(assessorCount);
                competition.setNonIfsUrl(nonIfsUrl);
            });
        });
    }

    private Long getInnovationAreaIdOrNull(String name) {
        return !isBlank(name) ? simpleFindFirst(innovationAreaRepository.findAll(), c -> name.equals(c.getName())).get().getId() : null;
    }

    private Long getInnovationSectorIdOrNull(String name) {
        return !isBlank(name) ? simpleFindFirst(innovationSectorRepository.findAll(), c -> name.equals(c.getName())).get().getId() : null;
    }

    private Long getResearchCategoryIdOrNull(String name) {
        return !isBlank(name) ? simpleFindFirst(researchCategoryRepository.findAll(), c -> name.equals(c.getName())).get().getId() : null;
    }

    private void doCompetitionDetailsUpdate(CompetitionData data, Consumer<CompetitionResource> updateFn) {

        CompetitionResource competition =
                competitionService.getCompetitionById(data.getCompetition().getId()).getSuccessObjectOrThrowException();

        updateFn.accept(competition);

        competitionSetupService.update(competition.getId(), competition).getSuccessObjectOrThrowException();

        updateCompetitionInCompetitionData(data, competition.getId());
    }

    public CompetitionDataBuilder withApplicationFormFromTemplate() {

        return asCompAdmin(data -> {

            CompetitionResource competition = data.getCompetition();

            competitionSetupService.copyFromCompetitionTypeTemplate(competition.getId(), competition.getCompetitionType()).
                    getSuccessObjectOrThrowException();

            updateCompetitionInCompetitionData(data, competition.getId());
        });
    }

    public CompetitionDataBuilder withSetupComplete() {
        return asCompAdmin(data -> {
            asList(CompetitionSetupSection.values()).forEach(competitionSetupSection -> {
                competitionSetupService.markSectionComplete(data.getCompetition().getId(), competitionSetupSection);
            });
            competitionSetupService.markAsSetup(data.getCompetition().getId());
        });
    }

    public CompetitionDataBuilder moveCompetitionIntoOpenStatus() {
        return asCompAdmin(data -> {
            shiftMilestoneToTomorrow(data, MilestoneType.SUBMISSION_DATE);

        });
    }

    public CompetitionDataBuilder moveCompetitionIntoFundersPanelStatus() {
        return asCompAdmin(data -> shiftMilestoneToTomorrow(data, MilestoneType.NOTIFICATIONS));
    }

    public CompetitionDataBuilder sendFundingDecisions(Pair<String, FundingDecision>... fundingDecisions) {
        return sendFundingDecisions(asList(fundingDecisions));
    }

    public CompetitionDataBuilder sendFundingDecisions(List<Pair<String, FundingDecision>> fundingDecisions) {
        return asCompAdmin(data -> {

            List<Pair<Long, FundingDecision>> applicationIdAndDecisions = simpleMap(fundingDecisions, decisionInfo -> {
                FundingDecision decision = decisionInfo.getRight();
                Application application = applicationRepository.findByName(decisionInfo.getLeft()).get(0);
                return Pair.of(application.getId(), decision);
            });

            applicationFundingService.saveFundingDecisionData(data.getCompetition().getId(), pairsToMap(applicationIdAndDecisions)).
                    getSuccessObjectOrThrowException();
            FundingNotificationResource fundingNotificationResource = new FundingNotificationResource("Body", pairsToMap(applicationIdAndDecisions));
            applicationFundingService.notifyLeadApplicantsOfFundingDecisions(fundingNotificationResource).
                    getSuccessObjectOrThrowException();

            doAs(anyProjectFinanceUser(),
                    () -> projectService.createProjectsFromFundingDecisions(pairsToMap(applicationIdAndDecisions)).getSuccessObjectOrThrowException());

        });
    }

    private UserResource anyProjectFinanceUser() {
        List<User> projectFinanceUsers = userRepository.findByRolesName(UserRoleType.PROJECT_FINANCE.getName());
        return retrieveUserById(projectFinanceUsers.get(0).getId());
    }

    private void shiftMilestoneToTomorrow(CompetitionData data, MilestoneType milestoneType) {
        List<MilestoneResource> milestones = milestoneService.getAllMilestonesByCompetitionId(data.getCompetition().getId()).getSuccessObjectOrThrowException();
        MilestoneResource submissionDateMilestone = simpleFindFirst(milestones, m -> milestoneType.equals(m.getType())).get();

        ZonedDateTime now = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS);
        ZonedDateTime submissionDeadline = submissionDateMilestone.getDate();

        final long daysPassedSinceSubmissionEnded;
        if (ZonedDateTime.now().withZoneSameInstant(submissionDeadline.getZone()).toLocalTime().isAfter(submissionDeadline.toLocalTime())) {
            daysPassedSinceSubmissionEnded = submissionDeadline.until(now, ChronoUnit.DAYS) + 1;
        } else {
            daysPassedSinceSubmissionEnded = submissionDeadline.until(now, ChronoUnit.DAYS);
        }

        milestones.forEach(m -> {
            if (m.getDate() != null) {
                m.setDate(m.getDate().plusDays(daysPassedSinceSubmissionEnded + 1));
                milestoneService.updateMilestone(m).getSuccessObjectOrThrowException();
            }
        });
    }

    public CompetitionDataBuilder restoreOriginalMilestones() {
        return asCompAdmin(data -> {

            data.getOriginalMilestones().forEach(original -> {

                MilestoneResource amendedMilestone =
                        milestoneService.getMilestoneByTypeAndCompetitionId(original.getType(), data.getCompetition().getId()).
                                getSuccessObjectOrThrowException();

                amendedMilestone.setDate(original.getDate());

                milestoneService.updateMilestone(amendedMilestone).getSuccessObjectOrThrowException();
            });
        });
    }

    public CompetitionDataBuilder withNewMilestones() {

        return asCompAdmin(data -> {
            Stream.of(MilestoneType.presetValues()).forEach(type -> {
                milestoneService.getMilestoneByTypeAndCompetitionId(type, data.getCompetition().getId()).
                        andOnSuccess((milestoneResource) -> {
                            if (milestoneResource.getId() == null) {
                                milestoneService.create(type, data.getCompetition().getId());
                            }
                            return serviceSuccess();
                        });
            });
        });
    }

    public CompetitionDataBuilder withOpenDate(ZonedDateTime date) {
        return withMilestoneUpdate(date, OPEN_DATE);
    }

    public CompetitionDataBuilder withBriefingDate(ZonedDateTime date) {
        return withMilestoneUpdate(date, BRIEFING_EVENT);
    }

    public CompetitionDataBuilder withSubmissionDate(ZonedDateTime date) {
        return withMilestoneUpdate(date, SUBMISSION_DATE);
    }

    public CompetitionDataBuilder withAllocateAssesorsDate(ZonedDateTime date) {
        return withMilestoneUpdate(date, ALLOCATE_ASSESSORS);
    }

    public CompetitionDataBuilder withAssessorBriefingDate(ZonedDateTime date) {
        return withMilestoneUpdate(date, ASSESSOR_BRIEFING);
    }

    public CompetitionDataBuilder withAssessorsNotifiedDate(ZonedDateTime date) {
        return withMilestoneUpdate(date, ASSESSORS_NOTIFIED);
    }

    public CompetitionDataBuilder withAssessorAcceptsDate(ZonedDateTime date) {
        return withMilestoneUpdate(date, ASSESSOR_ACCEPTS);
    }

    public CompetitionDataBuilder withAssessorEndDate(ZonedDateTime date) {
        return withMilestoneUpdate(date, ASSESSOR_DEADLINE);
    }

    public CompetitionDataBuilder withAssessmentClosedDate(ZonedDateTime date) {
        return withMilestoneUpdate(date, ASSESSMENT_CLOSED);
    }

    public CompetitionDataBuilder withLineDrawDate(ZonedDateTime date) {
        return withMilestoneUpdate(date, LINE_DRAW);
    }

    public CompetitionDataBuilder withAsessmentPanelDate(ZonedDateTime date) {
        return withMilestoneUpdate(date, ASSESSMENT_PANEL);
    }

    public CompetitionDataBuilder withPanelDate(ZonedDateTime date) {
        return withMilestoneUpdate(date, PANEL_DATE);
    }

    public CompetitionDataBuilder withFundersPanelDate(ZonedDateTime date) {
        return withMilestoneUpdate(date, FUNDERS_PANEL);
    }

    public CompetitionDataBuilder withFundersPanelEndDate(ZonedDateTime date) {
        return withMilestoneUpdate(date, NOTIFICATIONS);
    }
    public CompetitionDataBuilder withReleaseFeedbackDate(ZonedDateTime date) {
        return withMilestoneUpdate(date, RELEASE_FEEDBACK);
    }
    public CompetitionDataBuilder withFeedbackReleasedDate(ZonedDateTime date) {
        return withMilestoneUpdate(date, FEEDBACK_RELEASED);
    }

    private CompetitionDataBuilder withMilestoneUpdate(ZonedDateTime date, MilestoneType milestoneType) {

        if (date == null) {
            return this;
        }

        return asCompAdmin(data -> {

            MilestoneResource milestone =
                    milestoneService.getMilestoneByTypeAndCompetitionId(milestoneType, data.getCompetition().getId()).getSuccessObjectOrThrowException();

            if (milestone.getId() == null) {
                milestone = milestoneService.create(milestoneType, data.getCompetition().getId()).getSuccessObjectOrThrowException();
            }

            milestone.setDate(date);
            milestoneService.updateMilestone(milestone);

            data.addOriginalMilestone(milestone);
        });
    }

    public CompetitionDataBuilder withApplications(UnaryOperator<ApplicationDataBuilder>... applicationDataBuilders) {
        return withApplications(asList(applicationDataBuilders));
    }

    public CompetitionDataBuilder withApplications(List<UnaryOperator<ApplicationDataBuilder>> applicationDataBuilders) {
        return with(data -> applicationDataBuilders.forEach(fn -> fn.apply(newApplicationData(serviceLocator).withCompetition(data.getCompetition())).build()));
    }

    public CompetitionDataBuilder withPublicContent(boolean published, String shortDescription, String fundingRange, String eligibilitySummary, String competitionDescription, FundingType fundingType, String projectSize, List<String> keywords) {
        return asCompAdmin(data -> publicContentService.findByCompetitionId(data.getCompetition().getId()).andOnSuccessReturnVoid(publicContent -> {

            if (published) {
                publicContent.setShortDescription(shortDescription);
                publicContent.setProjectFundingRange(fundingRange);
                publicContent.setEligibilitySummary(eligibilitySummary);
                publicContent.setSummary(competitionDescription);
                publicContent.setFundingType(fundingType);
                publicContent.setProjectSize(projectSize);
                publicContent.setKeywords(keywords);

                stream(PublicContentSectionType.values()).forEach(type -> publicContentService.markSectionAsComplete(publicContent, type).getSuccessObjectOrThrowException());

                publicContentService.publishByCompetitionId(data.getCompetition().getId()).getSuccessObjectOrThrowException();
            }

        }));
    }

    private void updateCompetitionInCompetitionData(CompetitionData competitionData, Long competitionId) {
        CompetitionResource newCompetitionSaved = competitionService.getCompetitionById(competitionId).getSuccessObjectOrThrowException();
        competitionData.setCompetition(newCompetitionSaved);
    }

    private CompetitionDataBuilder asCompAdmin(Consumer<CompetitionData> action) {
        return with(data -> {
            doAs(compAdmin(), () -> action.accept(data));
        });
    }

    public static CompetitionDataBuilder newCompetitionData(ServiceLocator serviceLocator) {
        return new CompetitionDataBuilder(emptyList(), serviceLocator);
    }

    private CompetitionDataBuilder(List<BiConsumer<Integer, CompetitionData>> multiActions,
                                   ServiceLocator serviceLocator) {
        super(multiActions, serviceLocator);
    }

    @Override
    protected CompetitionDataBuilder createNewBuilderWithActions(List<BiConsumer<Integer, CompetitionData>> actions) {
        return new CompetitionDataBuilder(actions, serviceLocator);
    }

    @Override
    protected CompetitionData createInitial() {
        return new CompetitionData();
    }
}
