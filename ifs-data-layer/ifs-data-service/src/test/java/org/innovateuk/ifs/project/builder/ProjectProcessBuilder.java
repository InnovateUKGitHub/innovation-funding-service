package org.innovateuk.ifs.project.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.assessment.builder.AssessmentBuilder;
import org.innovateuk.ifs.project.domain.*;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.domain.ProjectUser;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.workflow.domain.ActivityState;

/**
 * Builder for {@link ProjectUser} entities.
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

    public ProjectProcessBuilder withProject(Project... project) {
        return withArray((proj, projectProcess) -> setField("target", proj, projectProcess), project);
    }

    public ProjectProcessBuilder withProjectUser(ProjectUser... projectUser){
        return withArray((users, projectProcess) -> projectProcess.setParticipant(users), projectUser);
    }

    public ProjectProcessBuilder withActivityState(ActivityState... activityState) {
        return withArray((state, object) -> object.setActivityState(state), activityState);
    }

    //    @Override
//    public void postProcess(int index, ProjectProcess projectProcess) {
//
//        Project project = projectProcess.getTarget();
//
//        if (project != null) {
//
//            if (project.getp() == null) {
//                project.setProjectUsers(new ArrayList<>());
//            }
//            if (!project.getProjectUsers().contains(projectUser)) {
//                project.addProjectUser(projectUser);
//            }
//        }
//    }
}
