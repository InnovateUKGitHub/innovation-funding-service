
package org.innovateuk.ifs.project.monitoring.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.project.core.builder.ProjectBuilder;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.repository.ProjectProcessRepository;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.project.monitoring.domain.MonitoringOfficer;
import org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerAssignedProjectResource;
import org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerUnassignedProjectResource;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.core.builder.ProjectProcessBuilder.newProjectProcess;
import static org.innovateuk.ifs.project.core.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.PROJECT_FINANCE_CONTACT;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.PROJECT_PARTNER;
import static org.innovateuk.ifs.project.resource.ProjectState.*;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.junit.Assert.*;

@Rollback
public class MonitoringOfficerRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<MonitoringOfficerRepository> {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectProcessRepository projectProcessRepository;

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    @Override
    protected void setRepository(MonitoringOfficerRepository repository) {
        this.repository = repository;
    }

    @Test
    public void findAll() {
        setLoggedInUser(getSteveSmith());

        aProjectWithMonitoringOfficerIn(SETUP);

        flushAndClearSession();

        Iterable<MonitoringOfficer> projectMonitoringOfficers = repository.findAll();
        assertEquals(1, Stream.of(projectMonitoringOfficers).count());
    }

    @Test
    public void findAllUnassignedProjects() {
        setLoggedInUser(getSteveSmith());

        Project assignedProject = aProjectWithMonitoringOfficerIn(SETUP);
        Project unassignedProject = aProjectIn(SETUP);
        Project withdrawnProject = aProjectIn(WITHDRAWN, true);
        Project offlineProject = aProjectIn(HANDLED_OFFLINE);
        Project completeOfflineProject = aProjectIn(COMPLETED_OFFLINE, true);
        flushAndClearSession();

        List<MonitoringOfficerUnassignedProjectResource> unassignedProjectResources = repository.findAllUnassignedProjects();

        List<Long> unassignedProjectIds = unassignedProjectResources
                .stream()
                .map(MonitoringOfficerUnassignedProjectResource::getProjectId)
                .collect(toList());

        assertTrue(unassignedProjectIds.contains(unassignedProject.getId()));

        assertFalse(unassignedProjectIds.contains(assignedProject.getId()));
        assertFalse(unassignedProjectIds.contains(withdrawnProject.getId()));
        assertFalse(unassignedProjectIds.contains(offlineProject.getId()));
        assertFalse(unassignedProjectIds.contains(completeOfflineProject.getId()));
    }

    @Test
    public void findUnassignedNonKTPProject() {
        setLoggedInUser(getSteveSmith());

        Project assignedProject = aProjectWithMonitoringOfficerIn(SETUP);
        Project unassignedProject = aProjectIn(SETUP);
        Project withdrawnProject = aProjectIn(WITHDRAWN);
        Project offlineProject = aProjectIn(HANDLED_OFFLINE);
        Project completeOfflineProject = aProjectIn(COMPLETED_OFFLINE);
        flushAndClearSession();

        List<MonitoringOfficerUnassignedProjectResource> unassignedProjectResources = repository.findUnassignedNonKTPProjects();

        List<Long> unassignedProjectIds = unassignedProjectResources
                .stream()
                .map(MonitoringOfficerUnassignedProjectResource::getProjectId)
                .collect(toList());

        assertTrue(unassignedProjectIds.contains(unassignedProject.getId()));

        assertFalse(unassignedProjectIds.contains(assignedProject.getId()));
        assertFalse(unassignedProjectIds.contains(withdrawnProject.getId()));
        assertFalse(unassignedProjectIds.contains(offlineProject.getId()));
        assertFalse(unassignedProjectIds.contains(completeOfflineProject.getId()));
    }

    @Test
    public void findUnassignedKTPProject() {
        setLoggedInUser(getSteveSmith());

        Project assignedProject = aProjectWithMonitoringOfficerIn(SETUP, true);
        Project unassignedProject = aProjectIn(SETUP, true);
        Project withdrawnProject = aProjectIn(WITHDRAWN, true);
        Project offlineProject = aProjectIn(HANDLED_OFFLINE, true);
        Project completeOfflineProject = aProjectIn(COMPLETED_OFFLINE, true);
        flushAndClearSession();

        List<MonitoringOfficerUnassignedProjectResource> unassignedProjectResources = repository.findUnassignedKTPProjects();

        List<Long> unassignedProjectIds = unassignedProjectResources
                .stream()
                .map(MonitoringOfficerUnassignedProjectResource::getProjectId)
                .collect(toList());

        assertTrue(unassignedProjectIds.contains(unassignedProject.getId()));

        assertFalse(unassignedProjectIds.contains(assignedProject.getId()));
        assertFalse(unassignedProjectIds.contains(withdrawnProject.getId()));
        assertFalse(unassignedProjectIds.contains(offlineProject.getId()));
        assertFalse(unassignedProjectIds.contains(completeOfflineProject.getId()));
    }

    @Test
    public void findAllAssingedProjects() {
        setLoggedInUser(getSteveSmith());

        Project assignedProject = aProjectWithMonitoringOfficerIn(SETUP);
        Project unassignedProject = aProjectIn(SETUP);
        Project withdrawnProject = aProjectWithMonitoringOfficerIn(WITHDRAWN, true);
        Project offlineProject = aProjectWithMonitoringOfficerIn(HANDLED_OFFLINE);
        Project completeOfflineProject = aProjectWithMonitoringOfficerIn(COMPLETED_OFFLINE, true);
        flushAndClearSession();

        List<MonitoringOfficerAssignedProjectResource> assignedProjectResources = repository.findAllAssignedProjects((getFelixWilson().getId()));

        List<Long> assignedProjectIds = assignedProjectResources
                .stream()
                .map(MonitoringOfficerAssignedProjectResource::getProjectId)
                .collect(toList());

        assertTrue(assignedProjectIds.contains(assignedProject.getId()));

        assertFalse(assignedProjectIds.contains(unassignedProject.getId()));
        assertFalse(assignedProjectIds.contains(withdrawnProject.getId()));
        assertFalse(assignedProjectIds.contains(offlineProject.getId()));
        assertFalse(assignedProjectIds.contains(completeOfflineProject.getId()));

        MonitoringOfficerAssignedProjectResource assignedProjectResource = assignedProjectResources
                .stream()
                .filter(a -> a.getProjectId() == assignedProject.getId())
                .findAny()
                .get();

        assertEquals("Lead organisation", assignedProjectResource.getLeadOrganisationName());
    }

    @Test
    public void findAssignedNonKTPProject() {
        setLoggedInUser(getSteveSmith());

        Project assignedProject = aProjectWithMonitoringOfficerIn(SETUP);
        Project unassignedProject = aProjectIn(SETUP);
        Project withdrawnProject = aProjectWithMonitoringOfficerIn(WITHDRAWN);
        Project offlineProject = aProjectWithMonitoringOfficerIn(HANDLED_OFFLINE);
        Project completeOfflineProject = aProjectWithMonitoringOfficerIn(COMPLETED_OFFLINE);
        flushAndClearSession();

        List<MonitoringOfficerAssignedProjectResource> assignedProjectResources = repository.findAssignedNonKTPProjects((getFelixWilson().getId()));

        List<Long> assignedProjectIds = assignedProjectResources
                .stream()
                .map(MonitoringOfficerAssignedProjectResource::getProjectId)
                .collect(toList());

        assertTrue(assignedProjectIds.contains(assignedProject.getId()));

        assertFalse(assignedProjectIds.contains(unassignedProject.getId()));
        assertFalse(assignedProjectIds.contains(withdrawnProject.getId()));
        assertFalse(assignedProjectIds.contains(offlineProject.getId()));
        assertFalse(assignedProjectIds.contains(completeOfflineProject.getId()));

        MonitoringOfficerAssignedProjectResource assignedProjectResource = assignedProjectResources
                .stream()
                .filter(a -> a.getProjectId() == assignedProject.getId())
                .findAny()
                .get();

        assertEquals("Lead organisation", assignedProjectResource.getLeadOrganisationName());
    }

    @Test
    public void findAssignedKTPProject() {
        setLoggedInUser(getSteveSmith());

        Project assignedProject = aProjectWithMonitoringOfficerIn(SETUP, true);
        Project unassignedProject = aProjectIn(SETUP, true);
        Project withdrawnProject = aProjectWithMonitoringOfficerIn(WITHDRAWN, true);
        Project offlineProject = aProjectWithMonitoringOfficerIn(HANDLED_OFFLINE, true);
        Project completeOfflineProject = aProjectWithMonitoringOfficerIn(COMPLETED_OFFLINE, true);
        flushAndClearSession();

        List<MonitoringOfficerAssignedProjectResource> assignedProjectResources = repository.findAssignedKTPProjects((getFelixWilson().getId()));

        List<Long> assignedProjectIds = assignedProjectResources
                .stream()
                .map(MonitoringOfficerAssignedProjectResource::getProjectId)
                .collect(toList());

        assertTrue(assignedProjectIds.contains(assignedProject.getId()));

        assertFalse(assignedProjectIds.contains(unassignedProject.getId()));
        assertFalse(assignedProjectIds.contains(withdrawnProject.getId()));
        assertFalse(assignedProjectIds.contains(offlineProject.getId()));
        assertFalse(assignedProjectIds.contains(completeOfflineProject.getId()));

        MonitoringOfficerAssignedProjectResource assignedProjectResource = assignedProjectResources
                .stream()
                .filter(a -> a.getProjectId() == assignedProject.getId())
                .findAny()
                .get();

        assertEquals("Lead organisation", assignedProjectResource.getLeadOrganisationName());
    }

    private Project aProjectWithMonitoringOfficerIn(ProjectState state) {
        return aProjectWithMonitoringOfficerIn(state, false);
    }

    private Project aProjectWithMonitoringOfficerIn(ProjectState state, boolean isKtp) {
        Project project = aProjectIn(state, isKtp);
        MonitoringOfficer monitoringOfficer = new MonitoringOfficer(getUserByEmail(getFelixWilson().getEmail()), project);
        repository.save(monitoringOfficer);
        project.setProjectMonitoringOfficer(monitoringOfficer);
        return project;
    }

    private Project aProjectIn(ProjectState state) {
        return aProjectIn(state, false);
    }

    private Project aProjectIn(ProjectState state, boolean isKtp) {
        Competition competition = newCompetition().withId((Long) null).build();
        if (isKtp) {
            competition.setFundingType(FundingType.KTP);
        }
        competition = competitionRepository.save(competition);
        Application application = new Application("application name");
        application.setCompetition(competition);
        application = applicationRepository.save(application);
        Organisation organisation = newOrganisation()
                .withId((Long) null)
                .withName("Lead organisation")
                .build();
        organisation = organisationRepository.save(organisation);
        ProcessRole leadRole = newProcessRole().withId((Long) null)
                .withApplication(application)
                .withOrganisationId(organisation.getId())
                .withRole(Role.LEADAPPLICANT)
                .withUser(getUserByEmail(getSteveSmith().getEmail()))
                .build();
        leadRole = processRoleRepository.save(leadRole);
        ProjectBuilder builder = newProject().withId((Long) null)
                .withApplication(application)
                .withName("project name")
                .withProjectUsers(
                        newProjectUser()
                                .withId((Long) null)
                                .withUser(getUserByEmail(getSteveSmith().getEmail()))
                                .withRole(PROJECT_PARTNER, PROJECT_FINANCE_CONTACT)
                                .build(2)
                );
        Project project = projectRepository.save(builder.build());
        projectProcessRepository.save(newProjectProcess().withActivityState(state).withProject(project).build());
        return project;
    }
}