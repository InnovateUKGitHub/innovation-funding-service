package org.innovateuk.ifs.project.core.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.project.core.domain.ProjectStages;
import org.innovateuk.ifs.project.internal.ProjectSetupStage;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class ProjectStageBuilder extends BaseBuilder<ProjectStages, ProjectStageBuilder> {

    private ProjectStageBuilder(List<BiConsumer<Integer, ProjectStages>> multiActions) {
        super(multiActions);
    }

    public static ProjectStageBuilder newProjectStages() {
        return new ProjectStageBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected ProjectStageBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ProjectStages>> actions) {
        return new ProjectStageBuilder(actions);
    }

    @Override
    protected ProjectStages createInitial() {
        return new ProjectStages();
    }

    public ProjectStageBuilder withId(Long... ids) {
        return withArray((id, project) -> setField("id", id, project), ids);
    }

    public ProjectStageBuilder withProjectSetupStage(ProjectSetupStage... stages) {
        return withArray((stage, project) -> project.setProjectSetupStage(stage), stages);
    }
}