package org.innovateuk.ifs.project.setupcomplete.populator;

import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.setupcomplete.viewmodel.SetupCompleteViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

import static org.innovateuk.ifs.project.resource.ProjectState.SETUP;

@Component
public class SetupCompleteViewModelPopulator {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private CompetitionRestService competitionRestService;

    public SetupCompleteViewModel populate(long projectId) {
        ProjectResource project = projectService.getById(projectId);

        return new SetupCompleteViewModel(
                project.getCompetition(),
                project,
                getSubmittedTime(project));
    }

    private ZonedDateTime getSubmittedTime(ProjectResource project) {
        if (SETUP.equals(project.getProjectState())) {
            return project.getSpendProfileSubmittedDate();
        } else {
            return project.getProcessLastModifiedDate();
        }
    }

}
