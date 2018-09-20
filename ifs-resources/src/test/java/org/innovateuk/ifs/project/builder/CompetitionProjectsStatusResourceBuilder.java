package org.innovateuk.ifs.project.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.project.status.resource.CompetitionProjectsStatusResource;
import org.innovateuk.ifs.project.status.resource.ProjectStatusResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;

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
