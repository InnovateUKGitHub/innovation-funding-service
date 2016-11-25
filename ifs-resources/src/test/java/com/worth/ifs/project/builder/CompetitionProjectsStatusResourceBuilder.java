package com.worth.ifs.project.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.project.status.resource.CompetitionProjectsStatusResource;
import com.worth.ifs.project.status.resource.ProjectStatusResource;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static java.util.Collections.emptyList;

public class CompetitionProjectsStatusResourceBuilder extends BaseBuilder<CompetitionProjectsStatusResource, CompetitionProjectsStatusResourceBuilder> {
    public CompetitionProjectsStatusResourceBuilder(List<BiConsumer<Integer, CompetitionProjectsStatusResource>> newActions) {
        super(newActions);
    }

    @Override
    protected CompetitionProjectsStatusResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, CompetitionProjectsStatusResource>> actions) {
        return new CompetitionProjectsStatusResourceBuilder(actions);
    }

    @Override
    protected CompetitionProjectsStatusResource createInitial() {
        return new CompetitionProjectsStatusResource();
    }

    public static CompetitionProjectsStatusResourceBuilder newCompetitionProjectsStatusResource(){
        return new CompetitionProjectsStatusResourceBuilder(emptyList());
    }

    public CompetitionProjectsStatusResourceBuilder withCompetitionNumber(Long ... competitionNumbers) {
        return withArray((competitionNumber, cps) -> setField("competitionNumber", competitionNumber, cps), competitionNumbers);
    }

    public CompetitionProjectsStatusResourceBuilder withCompetitionName(String... competitionNames) {
        return withArray((competitionName, cps) -> setField("competitionName", competitionName, cps), competitionNames);
    }

    @SafeVarargs
    public final CompetitionProjectsStatusResourceBuilder withProjectStatusResources(List<ProjectStatusResource>... projectStatusResourcesList) {
        return withArray((projectStatusResources, cps) -> setField("projectStatusResources", projectStatusResources, cps), projectStatusResourcesList);
    }
}
