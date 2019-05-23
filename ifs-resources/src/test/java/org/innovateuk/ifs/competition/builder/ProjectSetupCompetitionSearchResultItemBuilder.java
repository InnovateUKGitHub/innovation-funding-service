package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.competition.resource.search.ProjectSetupCompetitionSearchResultItem;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class ProjectSetupCompetitionSearchResultItemBuilder extends CompetitionSearchResultItemBuilder<ProjectSetupCompetitionSearchResultItem, ProjectSetupCompetitionSearchResultItemBuilder> {

    private ProjectSetupCompetitionSearchResultItemBuilder(List<BiConsumer<Integer, ProjectSetupCompetitionSearchResultItem>> newMultiActions) {
        super(newMultiActions);
    }

    public static ProjectSetupCompetitionSearchResultItemBuilder  newProjectSetupCompetitionSearchResultItem() {
        return new ProjectSetupCompetitionSearchResultItemBuilder(emptyList()).with(uniqueIds());
    }


    public ProjectSetupCompetitionSearchResultItemBuilder withProjectsCount(Integer... projectsCounts) {
        return withArray((projectsCount, competition) -> setField("projectsCount", projectsCount, competition), projectsCounts);
    }

    @Override
    protected ProjectSetupCompetitionSearchResultItemBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ProjectSetupCompetitionSearchResultItem>> actions) {
        return new ProjectSetupCompetitionSearchResultItemBuilder(actions);
    }

    @Override
    protected ProjectSetupCompetitionSearchResultItem createInitial() {
        return newInstance(ProjectSetupCompetitionSearchResultItem.class);
    }
}
