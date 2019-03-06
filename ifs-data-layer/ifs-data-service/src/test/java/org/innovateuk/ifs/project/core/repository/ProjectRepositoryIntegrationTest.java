package org.innovateuk.ifs.project.core.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.user.domain.User;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.core.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.PROJECT_PARTNER;
import static org.innovateuk.ifs.project.monitoring.builder.ProjectMonitoringOfficerBuilder.newProjectMonitoringOfficer;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ProjectRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<ProjectRepository> {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    @Override
    protected void setRepository(ProjectRepository repository) {
        this.repository = repository;
    }

    @Test
    public void findByProjectMonitoringOfficerIsNull() {
        List<Project> allProjects = repository.findAll();
        assertFalse(allProjects.isEmpty());

        List<Project> assignableProjects = repository.findAssignable();
        assertFalse(allProjects.isEmpty());

        List<Project> projects = repository.findByProjectMonitoringOfficerIdIsNull();
        assertFalse(projects.isEmpty());
    }

    @Test
    public void findByProjectMonitoringOfficerUserId() {
        setLoggedInUser(getSteveSmith());

        Application application = new Application("application name");
        applicationRepository.save(application);

        User felixWilson = getUserByEmail(getFelixWilson().getEmail());

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
                                .withUser(felixWilson)
                                .build()
                )
                .build();
        projectRepository.save(p);

        flushAndClearSession();

        // TODO check that assignable projects u unassigned = all

        List<Project> assignedProjects = repository.findByProjectMonitoringOfficerUserId(felixWilson.getId());
        assertEquals(1, assignedProjects.size());
    }
}