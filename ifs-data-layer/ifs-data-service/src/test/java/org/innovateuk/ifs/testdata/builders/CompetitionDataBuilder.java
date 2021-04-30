package org.innovateuk.ifs.testdata.builders;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.application.resource.FundingNotificationResource;
import org.innovateuk.ifs.competition.domain.CompetitionType;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.finance.resource.GrantClaimMaximumResource;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.form.resource.MultipleChoiceOptionResource;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.testdata.builders.data.CompetitionData;
import org.innovateuk.ifs.testdata.builders.data.CompetitionLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.google.common.collect.Lists.newArrayList;
import static java.time.ZonedDateTime.now;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.innovateuk.ifs.competition.resource.MilestoneType.*;
import static org.innovateuk.ifs.util.CollectionFunctions.*;

/**
 * Generates data from Competitions, including any Applications taking part in this Competition
 */
public class CompetitionDataBuilder extends BaseDataBuilder<CompetitionData, CompetitionDataBuilder> {

    private static final Logger LOG = LoggerFactory.getLogger(CompetitionDataBuilder.class);

    public CompetitionDataBuilder createCompetition() {

        return asCompAdmin(data -> {

            CompetitionResource competitionWithId = competitionSetupService.
                    create().
                    getSuccess();

            updateCompetitionInCompetitionData(data, competitionWithId.getId());
        });
    }

    public CompetitionDataBuilder createNonIfsCompetition() {

        return asCompAdmin(data -> {

            CompetitionResource competitionWithId = competitionSetupService.
                    createNonIfs().
                    getSuccess();

            updateCompetitionInCompetitionData(data, competitionWithId.getId());
        });
    }

    public CompetitionDataBuilder withExistingCompetition(CompetitionData competitionData) {

        return with(data -> {
            data.setCompetition(competitionData.getCompetition());
            competitionData.getOriginalMilestones().forEach(data::addOriginalMilestone);
        });
    }

    public CompetitionDataBuilder withBasicData(CompetitionLine line) {

        return asCompAdmin(data -> {

            doCompetitionDetailsUpdate(data, competition -> {

                if (line.getCompetitionType() != null) {
                    CompetitionType competitionType = competitionTypeRepository.findByName(line.getCompetitionType().getText());
                    competition.setCompetitionType(competitionType.getId());
                }

                Long innovationSector = getInnovationSectorIdOrNull(line.getInnovationSector());

                CollaborationLevel collaborationLevel = line.getCollaborationLevel();

                if (!isEmpty(line.getLeadApplicantTypes())) {
                    List<Long> leadApplicantTypeIds = line.getLeadApplicantTypes()
                            .stream()
                            .map(OrganisationTypeEnum::getId)
                            .collect(Collectors.toList());

                    competition.setLeadApplicantTypes(leadApplicantTypeIds);
                }

                competition.setName(line.getName());
                if (line.getInnovationAreas() != null) {
                    competition.setInnovationAreas(line.getInnovationAreas());
                }
                competition.setInnovationSector(innovationSector);
                competition.setResearchCategories(line.getResearchCategory());
                competition.setFundingRules(line.getFundingRules());
                competition.setMaxResearchRatio(line.getResearchRatio());
                competition.setAcademicGrantPercentage(100);
                competition.setLeadTechnologist(line.getLeadTechnologist());
                competition.setExecutive(line.getCompExecutive());
                competition.setPafCode(line.getPafCode());
                competition.setCode(line.getCode());
                competition.setBudgetCode(line.getBudgetCode());
                competition.setActivityCode(line.getActivityCode());
                competition.setCollaborationLevel(collaborationLevel);
                competition.setResubmission(line.getResubmission());
                competition.setMultiStream(line.getMultiStream());
                competition.setNonIfsUrl(line.getNonIfsUrl());
                competition.setIncludeJesForm(line.getIncludeJesForm());
                competition.setApplicationFinanceType(line.getApplicationFinanceType());
                competition.setIncludeProjectGrowthTable(line.getIncludeProjectGrowth());
                competition.setIncludeYourOrganisationSection(line.getIncludeYourOrganisation());
                competition.setFundingType(line.getFundingType());
                competition.setCompletionStage(line.getCompetitionCompletionStage());
                competition.setAlwaysOpen(isAlwaysOpen(line));
            });
        });
    }

    private boolean isAlwaysOpen(CompetitionLine line) {
        return line.getAlwaysOpen() != null ? line.getAlwaysOpen() : false;
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

        competitionSetupService.save(competition.getId(), competition).getSuccess();

        updateCompetitionInCompetitionData(data, competition.getId());
    }

    public CompetitionDataBuilder withApplicationFormFromTemplate() {

        return asCompAdmin(data -> {

            CompetitionResource competition = data.getCompetition();

            competitionSetupService.copyFromCompetitionTypeTemplate(competition.getId(), competition.getCompetitionType()).
                    getSuccess();

            updateCompetitionInCompetitionData(data, competition.getId());

            setGrantClaimMaximums(competition);

            if (data.getCompetition().getCompetitionTypeName().equals("Generic")) {

                List<Question> questions = questionRepository.findByCompetitionIdAndSectionTypeOrderByPriorityAsc(competition.getId(), SectionType.APPLICATION_QUESTIONS);
                Question question = questions.get(0);
                question.setName("Generic question heading");
                question.setShortName("Generic question title");
                question.setDescription("Generic question description");
                questionRepository.save(question);
            }

            if (data.getCompetition().getName().contains("Multiple choice")) {
                CompetitionSetupQuestionResource yesNoQuestion = addMultipleChoiceQuestion(data.getCompetition().getId());
                yesNoQuestion.setShortTitle("Can you answer this question?");
                yesNoQuestion.setTitle("Answer this.");
                yesNoQuestion.setSubTitle("<strong>Try picking an answer</strong>");
                yesNoQuestion.setChoices(newArrayList(new MultipleChoiceOptionResource("Yes"), new MultipleChoiceOptionResource("No")));
                questionSetupCompetitionService.update(yesNoQuestion);
                CompetitionSetupQuestionResource surveyQuestion = addMultipleChoiceQuestion(data.getCompetition().getId());
                surveyQuestion.setShortTitle("IFS is the best govuk sevice.");
                surveyQuestion.setTitle("Do you agree?");
                surveyQuestion.setSubTitle("<strong>You do...</strong>");
                surveyQuestion.setChoices(newArrayList(new MultipleChoiceOptionResource("Strongly agree"), new MultipleChoiceOptionResource("Agree"),
                        new MultipleChoiceOptionResource("Don't care"), new MultipleChoiceOptionResource("Disagree"), new MultipleChoiceOptionResource("Strongly disagree")));
                questionSetupCompetitionService.update(surveyQuestion);
                CompetitionSetupQuestionResource manyAnswers = addMultipleChoiceQuestion(data.getCompetition().getId());
                manyAnswers.setShortTitle("This question has loads of answers");
                manyAnswers.setTitle("so many answers.");
                manyAnswers.setSubTitle("<strong>Best letter?</strong>");
                manyAnswers.setChoices(newArrayList(new MultipleChoiceOptionResource("A"), new MultipleChoiceOptionResource("B"),
                        new MultipleChoiceOptionResource("C"), new MultipleChoiceOptionResource("D"), new MultipleChoiceOptionResource("E"),
                        new MultipleChoiceOptionResource("F"), new MultipleChoiceOptionResource("G"), new MultipleChoiceOptionResource("H"),
                        new MultipleChoiceOptionResource("I"), new MultipleChoiceOptionResource("J"), new MultipleChoiceOptionResource("K")));
                questionSetupCompetitionService.update(manyAnswers);
            }
        });
    }

    private void setGrantClaimMaximums(CompetitionResource competition) {
        grantClaimMaximumService.revertToDefault(competition.getId()).getSuccess();

        List<GrantClaimMaximumResource> maximumsNeedingALevel = grantClaimMaximumService.getGrantClaimMaximumByCompetitionId(competition.getId()).toOptionalIfNotFound().getSuccess()
                .map(list ->
                list.stream()
                .filter(max -> max.getMaximum() == null)
                .collect(Collectors.toList()))
                .orElse(emptyList());
        IntStream.range(0, maximumsNeedingALevel.size()).forEach(i -> {
            GrantClaimMaximumResource maximum = maximumsNeedingALevel.get(i);
            maximum.setMaximum(10 * i);
            grantClaimMaximumService.save(maximum).getSuccess();
        });
    }

    private CompetitionSetupQuestionResource addMultipleChoiceQuestion(long competitionId) {
        CompetitionSetupQuestionResource question = questionSetupCompetitionService.createByCompetitionId(competitionId).getSuccess();
        question.setTextArea(false);
        question.setMultipleChoice(true);
        question.setTemplateDocument(false);
        question.setScored(true);
        question.setWrittenFeedback(true);
        question.setScope(false);
        question.setResearchCategoryQuestion(false);
        question.setAssessmentGuidance("Assess this");
        question.setScoreTotal(10);
        question.setAssessmentMaxWords(500);
        return question;
    }

    public CompetitionDataBuilder withSetupComplete() {
        return asCompAdmin(data -> {
            markSetupSectionsAndSubsectionsAsComplete(data);
            markSetupApplicationQuestionsAsComplete(data);
        });
    }

    private void markSetupSectionsAndSubsectionsAsComplete(CompetitionData data) {
        Arrays.stream(CompetitionSetupSection.values())
                .filter(section -> section != CompetitionSetupSection.PROJECT_DOCUMENT)
                .forEach(competitionSetupSection -> {
                    competitionSetupService.markSectionComplete(data.getCompetition().getId(), competitionSetupSection);
                    competitionSetupSection.getSubsections().forEach(subsection -> {
                        competitionSetupService.markSubsectionComplete(data.getCompetition().getId(), competitionSetupSection, subsection);
                    });
                });
    }

    private void markSetupApplicationQuestionsAsComplete(CompetitionData data) {
        List<SectionResource> competitionSections = sectionService.getByCompetitionId(data.getCompetition().getId()).getSuccess();
        List<QuestionResource> questionResources = questionService.findByCompetition(data.getCompetition().getId()).getSuccess();

        // no application section or project details for h2020
        competitionSections.stream().filter(section -> section.getType() == SectionType.APPLICATION_QUESTIONS)
                .findFirst()
                .ifPresent(sectionResource -> markSectionQuestionsSetupComplete(questionResources, sectionResource, data));
        competitionSections.stream().filter(section -> section.getType() == SectionType.PROJECT_DETAILS)
                .findFirst()
                .ifPresent(sectionResource -> markSectionQuestionsSetupComplete(questionResources, sectionResource, data));
        // only for ktp competitions
        competitionSections.stream().filter(section -> section.getType() == SectionType.KTP_ASSESSMENT)
                .findFirst()
                .ifPresent(sectionResource -> markSectionQuestionsSetupComplete(questionResources, sectionResource, data));
    }

    private void markSectionQuestionsSetupComplete(List<QuestionResource> questionResources, SectionResource section, CompetitionData data) {
        questionResources.stream()
                .filter(question -> question.getSection().equals(section.getId()))
                .forEach(question -> questionSetupService.markQuestionInSetupAsComplete(question.getId(), data.getCompetition().getId(), CompetitionSetupSection.APPLICATION_FORM));

        competitionSetupService.markAsSetup(data.getCompetition().getId());
    }

    public CompetitionDataBuilder moveCompetitionIntoOpenStatus() {
        return asCompAdmin(data -> {
            shiftMilestoneToTomorrow(data, MilestoneType.SUBMISSION_DATE);
            shiftOpenDateToYesterday(data);
        });
    }

    public CompetitionDataBuilder moveCompetitionIntoFundersPanelStatus() {
        return asCompAdmin(data -> shiftMilestoneToTomorrow(data, MilestoneType.NOTIFICATIONS));
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

    private void shiftOpenDateToYesterday(CompetitionData data) {
        List<MilestoneResource> milestones = milestoneService.getAllMilestonesByCompetitionId(data.getCompetition().getId()).getSuccess();
        MilestoneResource openDate = simpleFindFirst(milestones, m -> OPEN_DATE.equals(m.getType())).get();
        openDate.setDate(now().minusDays(1));
        milestoneService.updateMilestone(openDate).getSuccess();
    }

    private void shiftMilestoneToTomorrow(CompetitionData data, MilestoneType milestoneType) {
        List<MilestoneResource> milestones = milestoneService.getAllMilestonesByCompetitionId(data.getCompetition().getId()).getSuccess();
        MilestoneResource submissionDateMilestone = simpleFindFirst(milestones, m -> milestoneType.equals(m.getType())).get();

        ZonedDateTime now = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS);
        ZonedDateTime submissionDeadline = submissionDateMilestone.getDate();

        if (submissionDeadline != null) {
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
    }

    public CompetitionDataBuilder withNewMilestones(CompetitionLine line) {
        return asCompAdmin(data ->
                (BooleanUtils.isTrue(line.getAlwaysOpen()) ? MilestoneType.alwaysOpenCompSetupMilestones().stream() : Stream.of(MilestoneType.presetValues()))
                        .filter(m -> !m.isOnlyNonIfs())
                        .filter(milestoneType -> milestoneType.getPriority() <= line.getCompetitionCompletionStage().getLastMilestone().getPriority())
                        .forEach(type ->
                                milestoneService.getMilestoneByTypeAndCompetitionId(type, data.getCompetition().getId())
                                        .handleSuccessOrFailure(
                                                failure -> milestoneService.create(new MilestoneResource(type, data.getCompetition().getId())).getSuccess(),
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
        if (date.isBefore(now())) {
            return asCompAdmin(data -> competitionService.closeAssessment(data.getCompetition().getId()).getSuccess());
        } else {
            return withMilestoneUpdate(date, ASSESSMENT_CLOSED);
        }
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
                            failure -> milestoneService.create(new MilestoneResource(milestoneType, data.getCompetition().getId())).getSuccess(),
                            success -> success
                    );

            milestone.setDate(adjustTimeForMilestoneType(date, milestoneType));
            milestoneService.updateMilestone(milestone);

            data.addOriginalMilestone(milestone);
        });
    }

    public CompetitionDataBuilder withDefaultPublicContent(CompetitionLine line) {
        return asCompAdmin(data -> publicContentService.findByCompetitionId(data.getCompetition().getId()).andOnSuccessReturnVoid(publicContent -> {

            if (line.isPublished()) {
                publicContent.setShortDescription("Innovate UK is investing up to £15 million in innovation projects to stimulate the new products and services of tomorrow");
                publicContent.setProjectFundingRange("Up to £35,000");
                publicContent.setEligibilitySummary("UK based business of any size. Must involve at least one SME");
                publicContent.setSummary("Innovate UK is investing up to £15 million in innovation projects to stimulate the new products and services of tomorrow.\n" +
                        "The aim of this competition is to help businesses innovate to find new revenue sources. Proposals should show how to achieve a step change in business growth, productivity and export opportunities for at least one UK small and medium-sized enterprise (SME).\n" +
                        "We expect projects to range from total costs of £35,000 to £2 million. Projects should last between 6 months and 3 years.\n" +
                        "There are 2 options to apply into this competition, dependent on project size and length, these are referred to as streams. Stream 1 is for projects under 12 months duration and under £100,000. Stream 2 is for projects lasting longer than 12 months or costing over £100,000.");
                publicContent.setProjectSize("£15 million");
                publicContent.setKeywords(asList(line.getName().split("\\s+"))); // keywords will now be competition name split
                publicContent.setInviteOnly(line.isInviteOnly());

                stream(PublicContentSectionType.values()).forEach(type -> publicContentService.markSectionAsComplete(publicContent, type).getSuccess());

                publicContentService.publishByCompetitionId(data.getCompetition().getId()).getSuccess();
            }
        }));
    }

    public CompetitionDataBuilder withApplicationFinances(CompetitionLine line) {

        return asCompAdmin(data -> {
            CompetitionSetupFinanceResource competitionSetupFinanceResource
                    = new CompetitionSetupFinanceResource();
            competitionSetupFinanceResource.setCompetitionId(data.getCompetition().getId());
            competitionSetupFinanceResource.setApplicationFinanceType(line.getApplicationFinanceType());
            competitionSetupFinanceResource.setIncludeGrowthTable(line.getIncludeProjectGrowth());

            competitionSetupFinanceResource.setIncludeYourOrganisationSection(line.getIncludeYourOrganisation());
            competitionSetupFinanceResource.setIncludeJesForm(line.getIncludeJesForm());
            competitionSetupFinanceService.save(competitionSetupFinanceResource);
        });
    }

    public CompetitionDataBuilder withAssessmentConfig(CompetitionLine line) {
        return asCompAdmin(data -> {
            CompetitionAssessmentConfigResource competitionAssessmentConfigResource = new CompetitionAssessmentConfigResource();
            competitionAssessmentConfigResource.setAssessorCount(line.getAssessorCount());
            competitionAssessmentConfigResource.setAssessorPay(BigDecimal.valueOf(100));
            competitionAssessmentConfigResource.setHasAssessmentPanel(line.getHasAssessmentPanel());
            competitionAssessmentConfigResource.setHasInterviewStage(line.getHasInterviewStage());
            competitionAssessmentConfigResource.setAssessorFinanceView(line.getAssessorFinanceView());
            competitionAssessmentConfigResource.setIncludeAverageAssessorScoreInNotifications(false);
            competitionAssessmentConfigService.update(data.getCompetition().getId(), competitionAssessmentConfigResource);
        });
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
