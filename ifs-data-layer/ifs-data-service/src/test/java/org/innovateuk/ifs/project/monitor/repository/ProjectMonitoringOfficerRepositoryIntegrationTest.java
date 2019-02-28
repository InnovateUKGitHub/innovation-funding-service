package org.innovateuk.ifs.project.monitor.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.project.monitor.domain.ProjectMonitoringOfficer;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Stream;

import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.core.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.PROJECT_PARTNER;
import static org.innovateuk.ifs.project.monitor.builder.ProjectMonitoringOfficerBuilder.newProjectMonitoringOfficer;
import static org.junit.Assert.assertEquals;

public class ProjectMonitoringOfficerRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<ProjectMonitoringOfficerRepository> {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    @Override
    protected void setRepository(ProjectMonitoringOfficerRepository repository) {
        this.repository = repository;
    }

    @Test
    public void findAll() {
        setLoggedInUser(getSteveSmith());

        Application application = new Application("application name");
        applicationRepository.save(application);

        Project p = newProject().withId((Long) null)
                .withApplication(application)
                .withName("project name")
                .withProjectUsers(
                        newProjectUser()
                                .withId((Long) null)
                                .withUser(getUserByEmail(getSteveSmith().getEmail()))
                                .withRole(PROJECT_PARTNER)
                                .build(2)
                )
                .withProjectMonitoringOfficer(
                        newProjectMonitoringOfficer()
                                .withId((Long) null)
                                .withUser(getUserByEmail(getFelixWilson().getEmail()))
                                .build()
                )
                .build();
        projectRepository.save(p);

        flushAndClearSession();

        Iterable<ProjectMonitoringOfficer> projectMonitoringOfficers = repository.findAll();
        assertEquals(1, Stream.of(projectMonitoringOfficers).count());
    }
}