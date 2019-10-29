package org.innovateuk.ifs.project.core.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.project.core.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.core.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.PROJECT_MANAGER;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.PROJECT_PARTNER;
import static org.junit.Assert.assertEquals;

public class PartnerOrganisationRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<PartnerOrganisationRepository> {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ProjectUserRepository projectUserRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private OrganisationRepository organisationRepository;

    private User user;
    private User leadApplicant;
    private User collaborator;
    private Project project;
    private Organisation empire;
    private Organisation ludlow;
    List<ProjectUser> projectUsers;

    @Autowired
    @Override
    protected void setRepository(final PartnerOrganisationRepository repository) {
        this.repository = repository;
    }

    @Before
    public void setup() {
        user = userRepository.findByEmail("lee.bowman@innovateuk.test").orElseThrow(() -> new IllegalStateException("Expected to find test user for email lee.bowman@innovateuk.test"));
        leadApplicant = userRepository.findByEmail("steve.smith@empire.com").orElseThrow(() -> new IllegalStateException("Expected to find test user for email steve.smith@empire.com"));
        collaborator = userRepository.findByEmail("jessica.doe@ludlow.co.uk").orElseThrow(() -> new IllegalStateException("Expected to find test user for email jessica.doe@ludlow.co.uk"));

        Application application = applicationRepository.findById(1L).get();
        empire = organisationRepository.findOneByName("Empire Ltd");
        ludlow = organisationRepository.findOneByName("Ludlow");
        project = projectRepository.findById(20L).get();

        ProjectUser projectManager = projectUserRepository.save(newProjectUser()
                .withId(123L)
                .withProject(project)
                .withUser(leadApplicant)
                .withRole(PROJECT_MANAGER)
                .withOrganisation(empire)
                .build());

        ProjectUser projectPartner = projectUserRepository.save(newProjectUser()
                .withId(144L)
                .withProject(project)
                .withUser(collaborator)
                .withRole(PROJECT_PARTNER)
                .withOrganisation(ludlow)
                .build());

        projectUsers = asList(projectManager, projectPartner);

        project = newProject()
                .withId(90L)
                .withApplication(application)
                .withProjectUsers(projectUsers)
                .build();
    }

    @Test
    public void getProjectPartnerOrganisations() {

        PartnerOrganisation partnerOrganisation = repository.findOneByProjectIdAndOrganisationId(project.getId(), ludlow.getId());
        assertEquals("Ludlow", partnerOrganisation);
    }
}