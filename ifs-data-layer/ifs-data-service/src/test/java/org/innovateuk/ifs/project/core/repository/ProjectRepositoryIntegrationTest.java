package org.innovateuk.ifs.project.core.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.project.core.domain.Project;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.test.annotation.Rollback;

import java.time.ZonedDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;

public class ProjectRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<ProjectRepository> {
    @Override
    protected void setRepository(ProjectRepository repository) {
        this.repository = repository;
    }

    @Ignore("Understand how to create and run an integration test")
    @Test
    @Rollback
    public void ready() {
        Project project = newProject()
                .withId(1L, 2L).withOfferSubmittedDate(ZonedDateTime.now(), null).build();
        repository.save(project);
        List<Project> projects = repository.findReadyToSend();
        assertThat(projects.size(), equalTo(1));
        assertThat(projects.get(0).getId(), equalTo(1));
    }
}
