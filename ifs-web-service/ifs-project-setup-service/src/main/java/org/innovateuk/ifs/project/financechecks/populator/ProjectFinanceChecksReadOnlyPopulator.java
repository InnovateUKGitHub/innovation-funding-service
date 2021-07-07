package org.innovateuk.ifs.project.financechecks.populator;

import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.financechecks.viewmodel.ProjectFinanceChecksReadOnlyViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProjectFinanceChecksReadOnlyPopulator {

    @Autowired
    private ProjectService projectService;

    public ProjectFinanceChecksReadOnlyViewModel populate(long projectId) {

        ProjectResource project = projectService.getById(projectId);

        return new ProjectFinanceChecksReadOnlyViewModel(project.getId(), project.getName(), project.getApplication());
    }
}
