package org.innovateuk.ifs.project.monitoring.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.project.monitoring.domain.MonitoringOfficer;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Stream;

import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.core.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.PROJECT_FINANCE_CONTACT;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.PROJECT_PARTNER;
import static org.innovateuk.ifs.project.monitoring.builder.MonitoringOfficerBuilder.newProjectMonitoringOfficer;
import static org.junit.Assert.assertEquals;

public class MonitoringOfficerRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<MonitoringOfficerRepository> {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    @Override
    protected void setRepository(MonitoringOfficerRepository repository) {
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
                                .withRole(PROJECT_PARTNER, PROJECT_FINANCE_CONTACT)
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

        Iterable<MonitoringOfficer> projectMonitoringOfficers = repository.findAll();
        assertEquals(1, Stream.of(projectMonitoringOfficers).count());
    }
}