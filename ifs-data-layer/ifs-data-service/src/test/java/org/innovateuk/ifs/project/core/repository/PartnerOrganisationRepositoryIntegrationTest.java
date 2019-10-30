package org.innovateuk.ifs.project.core.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.address.domain.Address;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.project.core.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;
import java.util.List;

import static java.time.LocalDate.now;
import static org.innovateuk.ifs.project.core.builder.PartnerOrganisationBuilder.newPartnerOrganisation;
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

//    private User user;
    private User leadApplicant;
    private User collaborator;
    private Project project;
    private Organisation empire;
    private Organisation ludlow;
    List<ProjectUser> projectUsers;
    List<PartnerOrganisation> partnerOrganisations;

    @Autowired
    @Override
    protected void setRepository(final PartnerOrganisationRepository repository) {
        this.repository = repository;
    }

    @Before
    public void setup() {
//        user = userRepository.findByEmail("lee.bowman@innovateuk.test").orElseThrow(() -> new IllegalStateException("Expected to find test user for email lee.bowman@innovateuk.test"));
        leadApplicant = userRepository.findByEmail("steve.smith@empire.com").get();
        collaborator = userRepository.findByEmail("jessica.doe@ludlow.co.uk").get();

        Application application = applicationRepository.findById(1L).get();
        empire = organisationRepository.findOneByName("Empire Ltd");
        ludlow = organisationRepository.findOneByName("Ludlow");

        project = newProject()
                .withApplication(application)
                .withDateSubmitted(ZonedDateTime.now())
                .withDuration(6L)
                .withName("My test project")
                .withAddress(new Address("2 Polaris House", "Swindon", "Wiltshire", null, null, "SN2 1EU"))
                .withOtherDocumentsApproved(ApprovalType.APPROVED)
                .withTargetStartDate(now()).build();

        projectRepository.save(project);

//        ProjectUser projectManager = projectUserRepository.save(newProjectUser()
//                .withId(123L)
//                .withProject(project)
//                .withUser(leadApplicant)
//                .withRole(PROJECT_MANAGER)
//                .withOrganisation(empire)
//                .build());
//
//        ProjectUser projectPartner = projectUserRepository.save(newProjectUser()
//                .withId(144L)
//                .withProject(project)
//                .withUser(collaborator)
//                .withRole(PROJECT_PARTNER)
//                .withOrganisation(ludlow)
//                .build());

//        projectUsers = asList(projectManager, projectPartner);

        partnerOrganisations = newPartnerOrganisation().withProject(project).withId(30L, 40L).withOrganisation(empire,ludlow).withLeadOrganisation(true,false).build(2);
        repository.save(partnerOrganisations.get(0));
        repository.save(partnerOrganisations.get(1));

    }

    @Test
    public void getProjectPartnerOrganisations() {

        List<PartnerOrganisation> partners = repository.findByProjectId(project.getId());

        assertEquals(empire, partners.get(0).getOrganisation());
        assertEquals(ludlow, partners.get(1).getOrganisation());
    }

    @Test
    public void deleteOneByProjectIdAndOrganisationId() {
        repository.deleteOneByProjectIdAndOrganisationId(project.getId(), ludlow.getId());

        assertEquals(1, repository.findByProjectId(project.getId()).size());
    }
}