package org.innovateuk.ifs.project.setup;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

@Component
public class SetupViewModelPopulator {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private CompetitionRestService competitionRestService;

    public SetupViewModel populate(long projectId) {
        ProjectResource project = projectService.getById(projectId);
        CompetitionResource competition = competitionRestService.getCompetitionById(project.getCompetition()).getSuccess();

        return new SetupViewModel(
                competition,
                project,
                ZonedDateTime.now());
    }
}
