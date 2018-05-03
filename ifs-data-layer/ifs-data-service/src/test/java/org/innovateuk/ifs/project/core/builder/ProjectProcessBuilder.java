package org.innovateuk.ifs.project.core.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.project.core.domain.ProjectProcess;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectUser;

/**
 * Builder for {@link ProjectProcess} entities.
 */
public class ProjectProcessBuilder extends BaseBuilder<ProjectProcess, ProjectProcessBuilder> {

    private ProjectProcessBuilder(List<BiConsumer<Integer, ProjectProcess>> multiActions) {
        super(multiActions);
    }

    public static ProjectProcessBuilder newProjectProcess() {
        return new ProjectProcessBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected ProjectProcessBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ProjectProcess>> actions) {
        return new ProjectProcessBuilder(actions);
    }

    @Override
    protected ProjectProcess createInitial() {
        return new ProjectProcess();
    }

    public ProjectProcessBuilder withId(Long... ids) {
        return withArray((id, projectProcess) -> projectProcess.setId(id), ids);
    }

    public ProjectProcessBuilder withProject(Project... projects) {
        return withArray((project, projectProcess) -> setField("target", project, projectProcess), projects);
    }

    public ProjectProcessBuilder withProjectUser(ProjectUser... projectUsers){
        return withArray((projectUser, projectProcess) -> projectProcess.setParticipant(projectUser), projectUsers);
    }

    public ProjectProcessBuilder withActivityState(ProjectState... activityStates) {
        return withArray((activityState, projectProcess) -> projectProcess.setProcessState(activityState), activityStates);
    }
}