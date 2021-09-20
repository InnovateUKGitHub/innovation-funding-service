package org.innovateuk.ifs.monitoring.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.project.core.ProjectParticipantRole;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectProcess;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.core.repository.ProjectProcessRepository;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.project.monitoring.domain.MonitoringOfficer;
import org.innovateuk.ifs.project.monitoring.repository.MonitoringOfficerRepository;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.Rollback;

import java.time.ZonedDateTime;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.core.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.project.resource.ProjectState.LIVE;
import static org.innovateuk.ifs.project.resource.ProjectState.SETUP;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MonitoringOfficerRepositoryTest extends BaseRepositoryIntegrationTest<MonitoringOfficerRepository> {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectProcessRepository projectProcessRepository;

    @Autowired
    private MonitoringOfficerRepository monitoringOfficerRepository;

    @Override
    @Autowired
    protected void setRepository(MonitoringOfficerRepository repository) {
        this.repository = repository;
    }

    private User monitoringOfficer;

    private Project project1;

    private Project project2;

    private MonitoringOfficer monitoringOfficerProject1;

    private MonitoringOfficer monitoringOfficerProject2;

    @Before
    public void setup() {
        loginCompAdmin();

        monitoringOfficer = userRepository.save(newUser()
                .with(id(null))
                .withCreatedBy(newUser().with(id(null)).build())
                .withUid("uid-1")
                .build());

        Competition competition = competitionRepository.save(newCompetition()
                .with(id(null))
                .withName("competition")
                .withCreatedBy(newUser().build())
                .withCreatedOn(ZonedDateTime.now())
                .build());

        Application application1 = applicationRepository.save(newApplication()
                .with(id(null))
                .withName("application1")
                .withCompetition(competition)
                .build());

        Application application2 = applicationRepository.save(newApplication()
                .with(id(null))
                .withName("application2")
                .withCompetition(competition)
                .build());

        project1 = projectRepository.save(newProject()
                .with(id(null))
                .withName("project1")
                .withApplication(application1)
                .build());

        project2 = projectRepository.save(newProject()
                .with(id(null))
                .withName("project2")
                .withApplication(application2)
                .build());

        monitoringOfficerProject1 = new MonitoringOfficer(monitoringOfficer, project1, ProjectParticipantRole.MONITORING_OFFICER);
        monitoringOfficerProject2 = new MonitoringOfficer(monitoringOfficer, project2, ProjectParticipantRole.MONITORING_OFFICER);
        monitoringOfficerRepository.saveAll(asList(monitoringOfficerProject1, monitoringOfficerProject2));

        ProjectUser projectUser = newProjectUser()
                .with(id(null))
                .withProject(project1, project2)
                .withRole(ProjectParticipantRole.PROJECT_MANAGER)
                .build();

        ProjectProcess projectProcess1 = new ProjectProcess(projectUser, project1, SETUP);
        ProjectProcess projectProcess2 = new ProjectProcess(projectUser, project2, LIVE);
        projectProcessRepository.saveAll(asList(projectProcess1, projectProcess2));

        flushAndClearSession();
    }

    @Rollback
    @Test
    public void filterMonitoringOfficerProjectsByStates() {
        Page<MonitoringOfficer> monitoringOfficerPages = repository.filterMonitoringOfficerProjectsByStates(monitoringOfficer.getId(),
                asList(SETUP, LIVE), PageRequest.of(0, 10) );

        List<MonitoringOfficer> monitoringOfficerOnProjects = monitoringOfficerPages.getContent();
        assertEquals(2, monitoringOfficerOnProjects.size());

        assertTrue(monitoringOfficerOnProjects.stream()
                .anyMatch(mp -> mp.getUser().getId().equals(monitoringOfficer.getId())
                        && mp.getProject().getId().equals(project1.getId())
                        && mp.getProject().getProjectState().equals(SETUP)));
        assertTrue(monitoringOfficerOnProjects.stream()
                .anyMatch(mp -> mp.getUser().getId().equals(monitoringOfficer.getId())
                        && mp.getProject().getId().equals(project2.getId())
                        && mp.getProject().getProjectState().equals(LIVE)));

        assertTrue(monitoringOfficerOnProjects.get(0).getProject().getId() <  monitoringOfficerOnProjects.get(1).getProject().getId());
        assertThat(monitoringOfficerOnProjects).containsExactlyInAnyOrder(monitoringOfficerProject1, monitoringOfficerProject2);
    }

    @Rollback
    @Test
    public void filterMonitoringOfficerProjectsByProjectId() {
        Long project1DisplayId =  project1.getApplication().getId();

        Page<MonitoringOfficer> monitoringOfficerPages = repository.filterMonitoringOfficerProjectsByKeywordsByStates(monitoringOfficer.getId(),
                project1DisplayId.toString(), asList(SETUP, LIVE), PageRequest.of(0, 10));

        List<MonitoringOfficer> monitoringOfficerOnProjects = monitoringOfficerPages.getContent();
        assertEquals(1, monitoringOfficerOnProjects.size());

        assertTrue(monitoringOfficerOnProjects.stream()
                .anyMatch(mp -> mp.getUser().getId().equals(monitoringOfficer.getId())
                        && mp.getProject().getId().equals(project1.getId())
                        && mp.getProject().getProjectState().equals(SETUP)));

        assertThat(monitoringOfficerOnProjects).contains(monitoringOfficerProject1);
    }

    @Rollback
    @Test
    public void filterMonitoringOfficerProjectsByKeyword() {
        Page<MonitoringOfficer> monitoringOfficerPages = repository.filterMonitoringOfficerProjectsByKeywordsByStates(monitoringOfficer.getId(),
                "%competition%", asList(SETUP, LIVE), PageRequest.of(0, 10));

        List<MonitoringOfficer> monitoringOfficersContent = monitoringOfficerPages.getContent();

        assertEquals(2, monitoringOfficersContent.size());

        assertTrue(monitoringOfficersContent.stream()
                .anyMatch(mp -> mp.getUser().getId().equals(monitoringOfficer.getId())
                        && mp.getProject().getId().equals(project1.getId())
                        && mp.getProject().getProjectState().equals(SETUP)));
        assertTrue(monitoringOfficersContent.stream()
                .anyMatch(mp -> mp.getUser().getId().equals(monitoringOfficer.getId())
                        && mp.getProject().getId().equals(project2.getId())
                        && mp.getProject().getProjectState().equals(LIVE)));


        assertTrue(monitoringOfficersContent.get(0).getProject().getId() <  monitoringOfficersContent.get(1).getProject().getId());
        assertThat(monitoringOfficersContent).containsExactlyInAnyOrder(monitoringOfficerProject1, monitoringOfficerProject2);
    }
}
