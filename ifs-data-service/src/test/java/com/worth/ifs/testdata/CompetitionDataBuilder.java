package com.worth.ifs.testdata;

import com.worth.ifs.category.domain.Category;
import com.worth.ifs.competition.domain.CompetitionType;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.MilestoneResource;
import com.worth.ifs.competition.resource.MilestoneType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.worth.ifs.category.resource.CategoryType.*;
import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static com.worth.ifs.competition.resource.MilestoneType.*;
import static com.worth.ifs.testdata.ApplicationDataBuilder.newApplicationData;
import static com.worth.ifs.util.CollectionFunctions.simpleFindFirst;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;


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

            CompetitionResource competition = data.getCompetition();

            CompetitionType competitionType = competitionTypeRepository.findByName(competitionTypeName).get(0);
            Category innovationArea = simpleFindFirst(categoryRepository.findByType(INNOVATION_AREA), c -> innovationAreaName.equals(c.getName())).get();
            Category innovationSector = simpleFindFirst(categoryRepository.findByType(INNOVATION_SECTOR), c -> innovationSectorName.equals(c.getName())).get();
            Category researchCategory = simpleFindFirst(categoryRepository.findByType(RESEARCH_CATEGORY), c -> researchCategoryName.equals(c.getName())).get();

            CompetitionResource newCompetitionDetails = newCompetitionResource().
                    withId(competition.getId()).
                    withName(name).
                    withDescription(description).
                    withInnovationArea(innovationArea.getId()).
                    withInnovationSector(innovationSector.getId()).
                    withResearchCategories(singleton(researchCategory.getId())).
                    withMaxResearchRatio(30).
                    withAcademicGrantClaimPercentage(100).
                    withCompetitionType(competitionType.getId()).
                    build();

            competitionSetupService.update(competition.getId(), newCompetitionDetails).getSuccessObjectOrThrowException();

            updateCompetitionInCompetitionData(data, competition.getId());
        });
    }

    public CompetitionDataBuilder withApplicationFormFromTemplate() {

        return asCompAdmin(data -> {

            CompetitionResource competition = data.getCompetition();

            competitionSetupService.initialiseFormForCompetitionType(competition.getId(), competition.getCompetitionType()).
                    getSuccessObjectOrThrowException();

            updateCompetitionInCompetitionData(data, competition.getId());
        });
    }

    public CompetitionDataBuilder withSetupComplete() {
        return asCompAdmin(data -> competitionSetupService.markAsSetup(data.getCompetition().getId()));
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
        });
    }

    public CompetitionDataBuilder withApplications(Function<ApplicationDataBuilder, ApplicationDataBuilder>... applicationDataBuilderFn) {
        return with(data -> asList(applicationDataBuilderFn).forEach(fn -> fn.apply(newApplicationData(serviceLocator).withCompetition(data.getCompetition())).build()));
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
