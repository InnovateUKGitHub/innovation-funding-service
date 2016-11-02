package com.worth.ifs.competition.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.BaseBuilderAmendFunctions;
import com.worth.ifs.competition.resource.CompetitionProjectsCountResource;
import com.worth.ifs.competition.resource.MilestoneResource;
import com.worth.ifs.competition.resource.MilestoneType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class CompetitionProjectsCountResourceBuilder extends BaseBuilder<CompetitionProjectsCountResource, CompetitionProjectsCountResourceBuilder> {

    private CompetitionProjectsCountResourceBuilder(List<BiConsumer<Integer, CompetitionProjectsCountResource>> newMultiActions) {
        super(newMultiActions);
    }

    public static CompetitionProjectsCountResourceBuilder newCompetitionProjectsCountResource() {
        return new CompetitionProjectsCountResourceBuilder(emptyList());
    }

    public CompetitionProjectsCountResourceBuilder withCompetitionId(Long... competitions) {
        return withArray((competition, object) -> BaseBuilderAmendFunctions.setField("competitionId", competition, object), competitions);
    }

    public CompetitionProjectsCountResourceBuilder withNumProjects(Integer... numProjects) {
        return withArray((id, object) -> BaseBuilderAmendFunctions.setField("numProjects", id, object), numProjects);
    }

    @Override
    protected CompetitionProjectsCountResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, CompetitionProjectsCountResource>> actions) {
        return new CompetitionProjectsCountResourceBuilder(actions);
    }

    @Override
    protected CompetitionProjectsCountResource createInitial() {
        return new CompetitionProjectsCountResource();
    }
}
