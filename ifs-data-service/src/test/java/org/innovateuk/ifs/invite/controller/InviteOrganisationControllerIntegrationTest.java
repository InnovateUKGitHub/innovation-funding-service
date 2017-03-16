package org.innovateuk.ifs.invite.controller;

import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.invite.domain.InviteOrganisation;
import org.innovateuk.ifs.invite.mapper.InviteOrganisationMapper;
import org.innovateuk.ifs.invite.repository.ApplicationInviteRepository;
import org.innovateuk.ifs.invite.repository.InviteOrganisationRepository;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.Role;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.repository.OrganisationRepository;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.repository.RoleRepository;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.invite.builder.ApplicationInviteBuilder.newApplicationInvite;
import static org.innovateuk.ifs.invite.builder.InviteOrganisationBuilder.newInviteOrganisation;
import static org.innovateuk.ifs.user.resource.UserRoleType.LEADAPPLICANT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class InviteOrganisationControllerIntegrationTest extends BaseControllerIntegrationTest<InviteOrganisationController> {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ApplicationInviteRepository applicationInviteRepository;

    @Autowired
    private InviteOrganisationRepository inviteOrganisationRepository;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private InviteOrganisationMapper inviteOrganisationMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    @Override
    protected void setControllerUnderTest(InviteOrganisationController controller) {
        this.controller = controller;
    }

    @Test
    public void findById() throws Exception {
        Application application = applicationRepository.findOne(1L);
        Organisation organisation = organisationRepository.findOneByName("Manchester University");
        InviteOrganisation inviteOrganisation = setupInviteOrganisation(application, organisation);

        User leadApplicant = getLeadApplicantForApplication(application);
        setLoggedInUser(userMapper.mapToResource(leadApplicant));

        InviteOrganisationResource found = controller.findById(inviteOrganisation.getId()).getSuccessObjectOrThrowException();

        assertEquals(inviteOrganisation.getId(), found.getId());
        assertEquals(inviteOrganisation.getOrganisationName(), found.getOrganisationName());
        assertEquals(organisation.getId(), found.getOrganisation());
        assertEquals(organisation.getName(), found.getOrganisationNameConfirmed());
        assertEquals(1, found.getInviteResources().size());
        assertEquals(application.getId(), found.getInviteResources().get(0).getApplication());
        assertEquals("example+user1@example.com", found.getInviteResources().get(0).getEmail());
    }

    @Test
    public void getByIdWithInvitesForApplication() throws Exception {
        Application application1 = applicationRepository.findOne(1L);
        Application application2 = applicationRepository.findOne(2L);
        Organisation organisation = organisationRepository.findOneByName("Manchester University");

        InviteOrganisation inviteOrganisation = setupInviteOrganisationForMultipleApplications(application1, application2, organisation);

        User leadApplicant = getLeadApplicantForApplication(application1);
        setLoggedInUser(userMapper.mapToResource(leadApplicant));

        InviteOrganisationResource found = controller.getByIdWithInvitesForApplication(inviteOrganisation.getId(), application1.getId()).getSuccessObjectOrThrowException();

        assertEquals(inviteOrganisation.getId(), found.getId());
        assertEquals(inviteOrganisation.getOrganisationName(), found.getOrganisationName());
        assertEquals(organisation.getId(), found.getOrganisation());
        assertEquals(organisation.getName(), found.getOrganisationNameConfirmed());
        assertEquals(1, found.getInviteResources().size());
        assertEquals(application1.getId(), found.getInviteResources().get(0).getApplication());
        assertEquals("example+user1@example.com", found.getInviteResources().get(0).getEmail());
    }

    @Test
    public void getByOrganisationIdWithInvitesForApplication() throws Exception {
        Application application1 = applicationRepository.findOne(1L);
        Application application2 = applicationRepository.findOne(2L);
        Organisation organisation = organisationRepository.findOneByName("Manchester University");

        InviteOrganisation inviteOrganisation = setupInviteOrganisationForMultipleApplications(application1, application2, organisation);

        User leadApplicant = getLeadApplicantForApplication(application1);
        setLoggedInUser(userMapper.mapToResource(leadApplicant));

        InviteOrganisationResource found = controller.getByOrganisationIdWithInvitesForApplication(organisation.getId(), application1.getId()).getSuccessObjectOrThrowException();

        assertEquals(inviteOrganisation.getId(), found.getId());
        assertEquals(inviteOrganisation.getOrganisationName(), found.getOrganisationName());
        assertEquals(organisation.getId(), found.getOrganisation());
        assertEquals(organisation.getName(), found.getOrganisationNameConfirmed());
        assertEquals(1, found.getInviteResources().size());
        assertEquals(application1.getId(), found.getInviteResources().get(0).getApplication());
        assertEquals("example+user1@example.com", found.getInviteResources().get(0).getEmail());
    }

    @Test
    public void put() throws Exception {
        Application application = applicationRepository.findOne(1L);
        Organisation organisation = organisationRepository.findOneByName("Manchester University");

        InviteOrganisation inviteOrganisation = setupInviteOrganisation(application, organisation);

        User leadApplicant = getLeadApplicantForApplication(application);
        setLoggedInUser(userMapper.mapToResource(leadApplicant));

        InviteOrganisationResource inviteOrganisationResource = inviteOrganisationMapper.mapToResource(inviteOrganisation);

        assertTrue(controller.put(inviteOrganisationResource).isSuccess());
        InviteOrganisationResource found = controller.findById(inviteOrganisation.getId()).getSuccessObjectOrThrowException();
        assertEquals(inviteOrganisationResource, found);
    }

    private Role getRoleByUserRoleType(UserRoleType userRoleType) {
        return roleRepository.findOneByName(userRoleType.getName());
    }

    private User getLeadApplicantForApplication(Application application) {
        List<ProcessRole> processRoles = processRoleRepository.findByApplicationIdAndRoleId(application.getId(),
                getRoleByUserRoleType(LEADAPPLICANT).getId());
        assertEquals(1, processRoles.size());
        return processRoles.get(0).getUser();
    }

    private InviteOrganisation setupInviteOrganisation(Application application, Organisation organisation) {
        InviteOrganisation inviteOrganisation = inviteOrganisationRepository.save(newInviteOrganisation()
                .with(id(null))
                .withOrganisationName("Hive")
                .withOrganisation(organisation)
                .build());

        applicationInviteRepository.save(newApplicationInvite()
                .with(id(null))
                .withApplication(application)
                .withName("Example User1")
                .withEmail("example+user1@example.com")
                .withInviteOrganisation(inviteOrganisation)
                .build());

        flushAndClearSession();
        return inviteOrganisationRepository.findOne(inviteOrganisation.getId());
    }

    private InviteOrganisation setupInviteOrganisationForMultipleApplications(Application application1, Application application2, Organisation organisation) {
        InviteOrganisation inviteOrganisation = inviteOrganisationRepository.save(newInviteOrganisation()
                .with(id(null))
                .withOrganisationName("Manchester Uni")
                .withOrganisation(organisation)
                .build());

        applicationInviteRepository.save(newApplicationInvite()
                .with(id(null))
                .withApplication(application1, application2)
                .withName("Example User1", "Example User2")
                .withEmail("example+user1@example.com", "example+user2@example.com")
                .withInviteOrganisation(inviteOrganisation)
                .build(2));

        flushAndClearSession();
        return inviteOrganisationRepository.findOne(inviteOrganisation.getId());
    }
}