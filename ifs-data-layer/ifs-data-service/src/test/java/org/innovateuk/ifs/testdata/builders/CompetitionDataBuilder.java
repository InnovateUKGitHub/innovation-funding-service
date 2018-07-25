package org.innovateuk.ifs.testdata.builders;

import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.application.resource.FundingNotificationResource;
import org.innovateuk.ifs.competition.domain.CompetitionType;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.testdata.builders.data.CompetitionData;
import org.innovateuk.ifs.user.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.*;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.APPLICATION_TEAM;
import static org.innovateuk.ifs.competition.resource.MilestoneType.*;
import static org.innovateuk.ifs.testdata.builders.ApplicationDataBuilder.newApplicationData;
import static org.innovateuk.ifs.util.CollectionFunctions.*;

/**
 * Generates data from Competitions, including any Applications taking part in this Competition
 */
public class CompetitionDataBuilder extends BaseDataBuilder<CompetitionData, CompetitionDataBuilder> {

    private static final Logger LOG = LoggerFactory.getLogger(CompetitionDataBuilder.class);

    public CompetitionDataBuilder createCompetition() {

        return asCompAdmin(data -> {

            CompetitionResource newCompetition = competitionSetupService.
                    create().
                    getSuccess();

            updateCompetitionInCompetitionData(data, newCompetition.getId());
        });
    }

    public CompetitionDataBuilder createNonIfsCompetition() {

        return asCompAdmin(data -> {

            CompetitionResource newCompetition = competitionSetupService.
                    createNonIfs().
                    getSuccess();

            updateCompetitionInCompetitionData(data, newCompetition.getId());
        });
    }

    public CompetitionDataBuilder withExistingCompetition(Long competitionId) {

        return asCompAdmin(data -> {
            CompetitionResource existingCompetition = competitionService.getCompetitionById(competitionId).getSuccess();
            updateCompetitionInCompetitionData(data, existingCompetition.getId());

            publicContentService.findByCompetitionId(competitionId).andOnFailure(() ->
                    publicContentService.initialiseByCompetitionId(competitionId).getSuccess());
        });
    }

    public CompetitionDataBuilder withExistingCompetition(CompetitionData competitionData) {

        return with(data -> {
            data.setCompetition(competitionData.getCompetition());
            competitionData.getOriginalMilestones().forEach(data::addOriginalMilestone);
        });
    }

    public CompetitionDataBuilder withBasicData(String name,
                                                String competitionTypeName,
                                                List<String> innovationAreaNames,
                                                String innovationSectorName,
                                                Boolean stateAidAllowed,
                                                String researchCategoryName,
                                                String leadTechnologist,
                                                String compExecutive,
                                                String budgetCode,
                                                String pafCode,
                                                String code,
                                                String activityCode,
                                                Integer assessorCount,
                                                BigDecimal assessorPay,
                                                Boolean hasAssessmentPanel,
                                                Boolean hasInterviewStage,
                                                AssessorFinanceView assessorFinanceView,
                                                Boolean multiStream,
                                                String collaborationLevelCode,
                                                List<OrganisationTypeEnum> leadApplicantTypes,
                                                Integer researchRatio,
                                                Boolean resubmission,
                                                String nonIfsUrl,
                                                String includeApplicationTeamQuestion) {

        return asCompAdmin(data -> {

            doCompetitionDetailsUpdate(data, competition -> {

                if (competitionTypeName != null) {
                    CompetitionType competitionType = competitionTypeRepository.findByName(competitionTypeName).get(0);
                    competition.setCompetitionType(competitionType.getId());
                }

                List<Long> innovationAreas = simpleFilter(
                        simpleMap(innovationAreaNames, this::getInnovationAreaIdOrNull),
                        Objects::nonNull
                );
                Long innovationSector = getInnovationSectorIdOrNull(innovationSectorName);
                Long researchCategory = getResearchCategoryIdOrNull(researchCategoryName);

                CollaborationLevel collaborationLevel = CollaborationLevel.fromCode(collaborationLevelCode);

                List<Long> leadApplicantTypeIds = simpleMap(leadApplicantTypes, OrganisationTypeEnum::getId);

                competition.setName(name);
                competition.setInnovationAreas(innovationAreas.isEmpty() ? emptySet() : newHashSet(innovationAreas));
                competition.setInnovationSector(innovationSector);
                competition.setResearchCategories(researchCategory == null ? emptySet() : singleton(researchCategory));
                competition.setStateAid(stateAidAllowed);
                competition.setMaxResearchRatio(30);
                competition.setAcademicGrantPercentage(100);
                competition.setLeadTechnologist(userRepository.findByEmail(leadTechnologist).map(User::getId).orElse(null));
                competition.setExecutive(userRepository.findByEmail(compExecutive).map(User::getId).orElse(null));
                competition.setPafCode(pafCode);
                competition.setCode(code);
                competition.setBudgetCode(budgetCode);
                competition.setActivityCode(activityCode);
                competition.setCollaborationLevel(collaborationLevel);
                competition.setLeadApplicantTypes(leadApplicantTypeIds);
                competition.setMaxResearchRatio(researchRatio);
                competition.setResubmission(resubmission);
                competition.setMultiStream(multiStream);
                competition.setAssessorPay(assessorPay);
                competition.setAssessorCount(assessorCount);
                competition.setHasAssessmentPanel(hasAssessmentPanel);
                competition.setHasInterviewStage(hasInterviewStage);
                competition.setAssessorFinanceView(assessorFinanceView);
                competition.setNonIfsUrl(nonIfsUrl);
                competition.setUseNewApplicantMenu(includeApplicationTeamQuestion.equals("Yes"));
            });
        });
    }

    private Long getInnovationAreaIdOrNull(String name) {
        return !isBlank(name) ? innovationAreaRepository.findByName(name).getId() : null;
    }

    private Long getInnovationSectorIdOrNull(String name) {
        return !isBlank(name) ? simpleFindFirst(innovationSectorRepository.findAll(), c -> name.equals(c.getName())).get().getId() : null;
    }

    private Long getResearchCategoryIdOrNull(String name) {
        return !isBlank(name) ? simpleFindFirst(researchCategoryRepository.findAll(), c -> name.equals(c.getName())).get().getId() : null;
    }

    private void doCompetitionDetailsUpdate(CompetitionData data, Consumer<CompetitionResource> updateFn) {

        CompetitionResource competition =
                competitionService.getCompetitionById(data.getCompetition().getId()).getSuccess();

        updateFn.accept(competition);

        // Copy the value of the useNewApplicantMenu flag so that it can restored in the resource after the
        // competition is updated. This is eventually used to determine whether the Application Team question should
        // be updated.
        boolean useNewApplicantMenu = competition.getUseNewApplicantMenu();

        competitionSetupService.save(competition.getId(), competition).getSuccess();

        updateCompetitionInCompetitionData(data, competition.getId());

        data.getCompetition().setUseNewApplicantMenu(useNewApplicantMenu);
    }

    public CompetitionDataBuilder withApplicationFormFromTemplate() {

        return asCompAdmin(data -> {

            CompetitionResource competition = data.getCompetition();

            competitionSetupService.copyFromCompetitionTypeTemplate(competition.getId(), competition.getCompetitionType()).
                    getSuccess();

            updateCompetitionInCompetitionData(data, competition.getId());

            if (data.getCompetition().getCompetitionTypeName().equals("Generic")) {

                List<Question> questions = questionRepository.findByCompetitionIdAndSectionNameOrderByPriorityAsc(competition.getId(), "Application questions");
                Question question = questions.get(0);
                question.setName("Generic question heading");
                question.setShortName("Generic question title");
                question.setDescription("Generic question description");
                questionRepository.save(question);
            }
        });
    }

    public CompetitionDataBuilder withSetupComplete() {
        return asCompAdmin(data -> {
            markSetupSectionsAndSubsectionsAsComplete(data);
            markSetupApplicationQuestionsAsComplete(data);
        });
    }

    private void markSetupSectionsAndSubsectionsAsComplete(CompetitionData data) {
        asList(CompetitionSetupSection.values()).forEach(competitionSetupSection -> {
            competitionSetupService.markSectionComplete(data.getCompetition().getId(), competitionSetupSection);
            competitionSetupSection.getSubsections().forEach(subsection -> {
                competitionSetupService.markSubsectionComplete(data.getCompetition().getId(), competitionSetupSection, subsection);
            });
        });
    }

    private void markSetupApplicationQuestionsAsComplete(CompetitionData data) {
        List<SectionResource> competitionSections = sectionService.getByCompetitionId(data.getCompetition().getId()).getSuccess();

        SectionResource applicationSection = competitionSections.stream().filter(section -> section.getName().equals("Application questions")).findFirst().get();
        SectionResource projectDetails = competitionSections.stream().filter(section -> section.getName().equals("Project details")).findFirst().get();

        List<QuestionResource> questionResources = questionService.findByCompetition(data.getCompetition().getId()).getSuccess();
        questionResources.stream()
                .filter(question -> question.getSection().equals(applicationSection.getId())
                        || question.getSection().equals(projectDetails.getId()))
                .forEach(question -> questionSetupService.markQuestionInSetupAsComplete(question.getId(), data.getCompetition().getId(), CompetitionSetupSection.APPLICATION_FORM));

        competitionSetupService.markAsSetup(data.getCompetition().getId());
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
                    getSuccess();
            FundingNotificationResource fundingNotificationResource = new FundingNotificationResource("Body", pairsToMap(applicationIdAndDecisions));
            applicationFundingService.notifyApplicantsOfFundingDecisions(fundingNotificationResource).
                    getSuccess();

            doAs(projectFinanceUser(),
                    () -> projectService.createProjectsFromFundingDecisions(pairsToMap(applicationIdAndDecisions)).getSuccess());

        });
    }

    private void shiftMilestoneToTomorrow(CompetitionData data, MilestoneType milestoneType) {
        List<MilestoneResource> milestones = milestoneService.getAllMilestonesByCompetitionId(data.getCompetition().getId()).getSuccess();
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
                milestoneService.updateMilestone(m).getSuccess();
            }
        });
    }

    public CompetitionDataBuilder restoreOriginalMilestones() {
        return asCompAdmin(data -> {

            data.getOriginalMilestones().forEach(original -> {

                MilestoneResource amendedMilestone =
                        milestoneService.getMilestoneByTypeAndCompetitionId(original.getType(), data.getCompetition().getId()).
                                getSuccess();

                amendedMilestone.setDate(original.getDate());

                milestoneService.updateMilestone(amendedMilestone).getSuccess();
            });
        });
    }

    public CompetitionDataBuilder withNewMilestones() {
        return asCompAdmin(data ->
            Stream.of(MilestoneType.presetValues())
                    .filter(m -> !m.isOnlyNonIfs())
                    .forEach(type ->
                milestoneService.getMilestoneByTypeAndCompetitionId(type, data.getCompetition().getId())
                        .handleSuccessOrFailure(
                                failure -> milestoneService.create(type, data.getCompetition().getId()).getSuccess(),
                                success -> success
                        )
            )
        );
    }

    public CompetitionDataBuilder withOpenDate(ZonedDateTime date) {
        return withMilestoneUpdate(date, OPEN_DATE);
    }

    public CompetitionDataBuilder withBriefingDate(ZonedDateTime date) {
        return withMilestoneUpdate(date, BRIEFING_EVENT);
    }

    public CompetitionDataBuilder withRegistrationDate(ZonedDateTime date) {
        return withMilestoneUpdate(date, REGISTRATION_DATE);
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

    public CompetitionDataBuilder withMilestoneUpdate(ZonedDateTime date, MilestoneType milestoneType) {

        if (date == null) {
            return this;
        }

        return asCompAdmin(data -> {

            MilestoneResource milestone = milestoneService.getMilestoneByTypeAndCompetitionId(milestoneType, data.getCompetition().getId())
                    .handleSuccessOrFailure(
                            failure -> milestoneService.create(milestoneType, data.getCompetition().getId()).getSuccess(),
                            success -> success
                    );

            milestone.setDate(adjustTimeForMilestoneType(date, milestoneType));
            milestoneService.updateMilestone(milestone);

            data.addOriginalMilestone(milestone);
        });
    }

    public CompetitionDataBuilder withApplications(UnaryOperator<ApplicationDataBuilder>... applicationDataBuilders) {
        return withApplications(asList(applicationDataBuilders));
    }

    public CompetitionDataBuilder withApplications(List<UnaryOperator<ApplicationDataBuilder>> applicationDataBuilders) {
        return with(data -> applicationDataBuilders.forEach(fn ->
            testService.doWithinTransaction(() -> fn.apply(newApplicationData(serviceLocator).withCompetition(data.getCompetition())).build())));
    }

    public CompetitionDataBuilder withPublicContent(boolean published, String shortDescription, String fundingRange, String eligibilitySummary, String competitionDescription, FundingType fundingType, String projectSize, List<String> keywords, boolean inviteOnly) {
        return asCompAdmin(data -> publicContentService.findByCompetitionId(data.getCompetition().getId()).andOnSuccessReturnVoid(publicContent -> {

            if (published) {
                publicContent.setShortDescription(shortDescription);
                publicContent.setProjectFundingRange(fundingRange);
                publicContent.setEligibilitySummary(eligibilitySummary);
                publicContent.setSummary(competitionDescription);
                publicContent.setFundingType(fundingType);
                publicContent.setProjectSize(projectSize);
                publicContent.setKeywords(keywords);
                publicContent.setInviteOnly(inviteOnly);

                stream(PublicContentSectionType.values()).forEach(type -> publicContentService.markSectionAsComplete(publicContent, type).getSuccess());

                publicContentService.publishByCompetitionId(data.getCompetition().getId()).getSuccess();
            }

        }));
    }

    public void removeApplicationTeamFromCompetition(Long competitionId) {
        asCompAdmin(data -> questionService
                .getQuestionByCompetitionIdAndQuestionSetupType
                        (competitionId, APPLICATION_TEAM).andOnSuccess(
                        question -> questionSetupTemplateService.deleteQuestionInCompetition(question
                                .getId())));
    }

    private void updateCompetitionInCompetitionData(CompetitionData competitionData, Long competitionId) {
        CompetitionResource newCompetitionSaved = competitionService.getCompetitionById(competitionId).getSuccess();
        competitionData.setCompetition(newCompetitionSaved);
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

    @Override
    protected void postProcess(int index, CompetitionData instance) {
        super.postProcess(index, instance);
        LOG.info("Created Competition '{}'", instance.getCompetition().getName());
    }

    private ZonedDateTime adjustTimeForMilestoneType(ZonedDateTime day, MilestoneType milestoneType) {
        return asList(SUBMISSION_DATE, ASSESSOR_ACCEPTS, ASSESSOR_DEADLINE).contains(milestoneType) ? day.withHour(12) : day;
    }
}
