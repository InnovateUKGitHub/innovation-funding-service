package com.worth.ifs.testdata.builders;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.Section;
import com.worth.ifs.application.resource.FundingDecision;
import com.worth.ifs.category.resource.CategoryType;
import com.worth.ifs.competition.domain.CompetitionType;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.MilestoneResource;
import com.worth.ifs.competition.resource.MilestoneType;
import com.worth.ifs.testdata.builders.data.CompetitionData;
import org.apache.commons.lang3.tuple.Pair;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import static com.worth.ifs.category.resource.CategoryType.*;
import static com.worth.ifs.competition.resource.MilestoneType.*;
import static com.worth.ifs.testdata.builders.ApplicationDataBuilder.newApplicationData;
import static com.worth.ifs.util.CollectionFunctions.*;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;
import static org.apache.commons.lang3.StringUtils.isBlank;


public class CompetitionDataBuilder extends BaseDataBuilder<CompetitionData, CompetitionDataBuilder> {

    public CompetitionDataBuilder createCompetition() {

        return asCompAdmin(data -> {

            CompetitionResource newCompetition = competitionSetupService.
                    create().
                    getSuccessObjectOrThrowException();

            updateCompetitionInCompetitionData(data, newCompetition.getId());
        });
    }

    public CompetitionDataBuilder withExistingCompetition(Long competitionId) {

        return asCompAdmin(data -> {
            CompetitionResource existingCompetition = competitionService.getCompetitionById(competitionId).getSuccessObjectOrThrowException();
            updateCompetitionInCompetitionData(data, existingCompetition.getId());
        });
    }

    public CompetitionDataBuilder withBasicData(String name, String description, String competitionTypeName, String innovationAreaName, String innovationSectorName, String researchCategoryName) {

        return asCompAdmin(data -> {

            doCompetitionDetailsUpdate(data, competition -> {

                CompetitionType competitionType = competitionTypeRepository.findByName(competitionTypeName).get(0);
                Long innovationArea = getCategoryIdOrNull(INNOVATION_AREA, innovationAreaName);
                Long innovationSector = getCategoryIdOrNull(INNOVATION_SECTOR, innovationSectorName);
                Long researchCategory = getCategoryIdOrNull(RESEARCH_CATEGORY, researchCategoryName);

                competition.setName(name);
                competition.setDescription(description);
                competition.setInnovationArea(innovationArea);
                competition.setInnovationSector(innovationSector);
                competition.setResearchCategories(singleton(researchCategory));
                competition.setMaxResearchRatio(30);
                competition.setAcademicGrantPercentage(100);
                competition.setCompetitionType(competitionType.getId());
            });
        });
    }

    private Long getCategoryIdOrNull(CategoryType type, String name) {
        return !isBlank(name) ? simpleFindFirst(categoryRepository.findByType(type), c -> name.equals(c.getName())).get().getId() : null;
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

            competitionSetupService.initialiseFormForCompetitionType(competition.getId(), competition.getCompetitionType()).
                    getSuccessObjectOrThrowException();

            // TODO DW - temporary fix for pulling over section priorities
            testService.doWithinTransaction(() -> {
                List<Section> newSections = sectionRepository.findByCompetitionIdOrderByParentSectionIdAscPriorityAsc(competition.getId());
                newSections.forEach(s -> {
                    Section originalSection = sectionRepository.findByNameAndCompetitionId(s.getName(), 1L);
                    s.setPriority(originalSection.getPriority());
                    sectionRepository.save(s);
                });
            });

            updateCompetitionInCompetitionData(data, competition.getId());
        });
    }

    public CompetitionDataBuilder withSetupComplete() {
        return asCompAdmin(data -> competitionSetupService.markAsSetup(data.getCompetition().getId()));
    }

    public CompetitionDataBuilder moveCompetitionIntoOpenStatus() {
        return asCompAdmin(data -> shiftMilestoneToTomorrow(data, MilestoneType.SUBMISSION_DATE));
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

            applicationFundingService.makeFundingDecision(data.getCompetition().getId(), pairsToMap(applicationIdAndDecisions)).
                    getSuccessObjectOrThrowException();

            projectService.createProjectsFromFundingDecisions(pairsToMap(applicationIdAndDecisions));
        });
    }

    private void shiftMilestoneToTomorrow(CompetitionData data, MilestoneType milestoneType) {
        List<MilestoneResource> milestones = milestoneService.getAllMilestonesByCompetitionId(data.getCompetition().getId()).getSuccessObjectOrThrowException();
        MilestoneResource submissionDateMilestone = simpleFindFirst(milestones, m -> milestoneType.equals(m.getType())).get();

        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
        LocalDateTime submissionDeadline = submissionDateMilestone.getDate();
        long daysPassedSinceSubmissionEnded = submissionDeadline.until(now, ChronoUnit.DAYS);

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

            Stream.of(MilestoneType.values()).forEach(type -> {
                milestoneService.create(type, data.getCompetition().getId());
            });
        });
    }

    public CompetitionDataBuilder withOpenDate(LocalDateTime date) {
        return withMilestoneUpdate(date, OPEN_DATE);
    }

    public CompetitionDataBuilder withSubmissionDate(LocalDateTime date) {
        return withMilestoneUpdate(date, SUBMISSION_DATE);
    }

    public CompetitionDataBuilder withFundersPanelDate(LocalDateTime date) {
        return withMilestoneUpdate(date, FUNDERS_PANEL);
    }

    public CompetitionDataBuilder withFundersPanelEndDate(LocalDateTime date) {
        return withMilestoneUpdate(date, NOTIFICATIONS);
    }

    public CompetitionDataBuilder withAssessorAcceptsDate(LocalDateTime date) {
        return withMilestoneUpdate(date, ASSESSOR_ACCEPTS);
    }

    public CompetitionDataBuilder withAssessorEndDate(LocalDateTime date) {
        return withMilestoneUpdate(date, ASSESSOR_DEADLINE);
    }

    private CompetitionDataBuilder withMilestoneUpdate(LocalDateTime date, MilestoneType milestoneType) {
        return asCompAdmin(data -> {

            MilestoneResource milestone =
                    milestoneService.getMilestoneByTypeAndCompetitionId(milestoneType, data.getCompetition().getId()).getSuccessObjectOrThrowException();
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
