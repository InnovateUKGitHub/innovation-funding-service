package org.innovateuk.ifs.invite.controller;

import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.InviteOrganisation;
import org.innovateuk.ifs.invite.mapper.InviteOrganisationMapper;
import org.innovateuk.ifs.invite.repository.InviteOrganisationRepository;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.Supplier;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.commons.error.CommonErrors.forbiddenError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GENERAL_SPRING_SECURITY_FORBIDDEN_ACTION;
import static org.innovateuk.ifs.invite.builder.ApplicationInviteBuilder.newApplicationInvite;
import static org.innovateuk.ifs.invite.builder.InviteOrganisationBuilder.newInviteOrganisation;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class InviteOrganisationControllerIntegrationTest extends BaseControllerIntegrationTest<InviteOrganisationController> {

    @Autowired
    private ApplicationRepository applicationRepository;

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
    private UserRepository userRepository;

    @Autowired
    @Override
    protected void setControllerUnderTest(InviteOrganisationController controller) {
        this.controller = controller;
    }

    @Test
    public void getById_userIsLeadApplicant() {
        Application application = applicationRepository.findById(1L).get();
        Organisation organisation = setupOrganisation("Quick Sustainability Limited");
        InviteOrganisation inviteOrganisation = setupInviteOrganisation(application, organisation);

        setLoggedInUser(getLeadApplicantForApplication(application));
        assertInviteOrganisationIsFound(inviteOrganisation, organisation, () -> controller.getById(inviteOrganisation.getId()));
    }

    @Test
    public void getById_userIsCollaborator() {
        loginSteveSmith();
        Application application = applicationRepository.findById(1L).get();
        Organisation organisation = setupOrganisation("Quick Sustainability Limited");
        InviteOrganisation inviteOrganisation = setupInviteOrganisation(application, organisation);

        setLoggedInUser(setupCollaboratorForApplicationAndOrganisation(application, organisation));
        assertInviteOrganisationIsFound(inviteOrganisation, organisation, () -> controller.getById(inviteOrganisation.getId()));
    }

    @Test
    public void getById_userIsCollaboratorAndOrganisationUnconfirmed() {
        loginSteveSmith();
        Application application = applicationRepository.findById(1L).get();
        InviteOrganisation inviteOrganisation = setupInviteOrganisationUnconfirmed(application);

        setLoggedInUser(setupCollaboratorForApplicationAndOrganisation(application, setupOrganisation("Other Company Limited")));
        assertAccessDeniedForInviteOrganisation(() -> controller.getById(inviteOrganisation.getId()));
    }

    @Test
    public void getById_userIsCollaboratorForDifferentOrganisation() {
        Application application = applicationRepository.findById(1L).get();
        Organisation organisation = setupOrganisation("Quick Sustainability Limited");
        InviteOrganisation inviteOrganisation = setupInviteOrganisation(application, organisation);

        setLoggedInUser(setupCollaboratorForApplicationAndOrganisation(application, setupOrganisation("Other Company Limited")));
        assertAccessDeniedForInviteOrganisation(() -> controller.getById(inviteOrganisation.getId()));
    }

    @Test
    public void getByOrganisationIdWithInvitesForApplication_userIsLeadApplicant() {
        Application application = applicationRepository.findById(1L).get();
        Organisation organisation = setupOrganisation("Quick Sustainability Limited");
        InviteOrganisation inviteOrganisation = setupInviteOrganisation(application, organisation);

        setLoggedInUser(getLeadApplicantForApplication(application));
        assertInviteOrganisationIsFound(inviteOrganisation, organisation, () -> controller.getByOrganisationIdWithInvitesForApplication(organisation.getId(), application.getId()));
    }

    @Test
    public void getByOrganisationIdWithInvitesForApplication_userIsCollaborator() {
        loginSteveSmith();
        Application application = applicationRepository.findById(1L).get();
        Organisation organisation = setupOrganisation("Quick Sustainability Limited");
        InviteOrganisation inviteOrganisation = setupInviteOrganisation(application, organisation);

        setLoggedInUser(setupCollaboratorForApplicationAndOrganisation(application, organisation));
        assertInviteOrganisationIsFound(inviteOrganisation, organisation, () -> controller.getByOrganisationIdWithInvitesForApplication(organisation.getId(), application.getId()));
    }

    @Test
    public void getByOrganisationIdWithInvitesForApplication_userIsCollaboratorForDifferentOrganisation() {
        Application application = applicationRepository.findById(1L).get();
        Organisation organisation = setupOrganisation("Quick Sustainability Limited");
        setupInviteOrganisation(application, organisation);

        setLoggedInUser(setupCollaboratorForApplicationAndOrganisation(application, setupOrganisation("Other Company Limited")));
        assertAccessDeniedForInviteOrganisation(() -> controller.getByOrganisationIdWithInvitesForApplication(organisation.getId(), application.getId()));
    }

    @Test
    public void put() {
        Application application = applicationRepository.findById(1L).get();
        Organisation organisation = setupOrganisation("Quick Sustainability Limited");

        InviteOrganisation inviteOrganisation = setupInviteOrganisation(application, organisation);

        setLoggedInUser(getLeadApplicantForApplication(application));
        InviteOrganisationResource inviteOrganisationResource = inviteOrganisationMapper.mapToResource(inviteOrganisation);

        assertTrue(controller.put(inviteOrganisationResource).isSuccess());
        InviteOrganisationResource found = controller.getById(inviteOrganisation.getId()).getSuccess();

        InviteOrganisationResource expectedOrganisationResource = inviteOrganisationResource;
        expectedOrganisationResource.getInviteResources().get(0).setSentOn(null);
        expectedOrganisationResource.getInviteResources().get(0).setStatus(InviteStatus.CREATED);

        assertEquals(inviteOrganisationResource, found);
    }

    private Organisation setupOrganisation(String name) {
        Organisation organisation = organisationRepository.save(newOrganisation()
                .with(id(null))
                .withName(name)
                .build());

        flushAndClearSession();
        return organisation;
    }

    private User getLeadApplicantForApplication(Application application) {
        List<ProcessRole> processRoles = processRoleRepository.findByApplicationIdAndRole(application.getId(), Role.LEADAPPLICANT);
        assertEquals(1, processRoles.size());
        return processRoles.get(0).getUser();
    }

    private User setupCollaboratorForApplicationAndOrganisation(Application application, Organisation organisation) {
        loginSteveSmith();

        application = applicationRepository.findById(application.getId()).get();

        User user = userRepository.save(newUser()
                .with(id(null))
                .withFirstName("Example")
                .withLastName("User1")
                .withEmailAddress("example+user1@example.com")
                .withUid("f6b9ddeb-f169-4ac4-b606-90cb877ce8c8")
                .build());

        ProcessRole processRole = newProcessRole()
                .with(id(null))
                .withUser(user)
                .withRole(Role.COLLABORATOR)
                .withOrganisationId(organisation.getId())
                .withApplication(application)
                .build();

        processRoleRepository.save(processRole);

        flushAndClearSession();

        return user;
    }

    private InviteOrganisation setupInviteOrganisation(Application application, Organisation organisation) {
        InviteOrganisation inviteOrganisation = inviteOrganisationRepository.save(newInviteOrganisation()
                .with(id(null))
                .withOrganisationName("Quick Sustainability")
                .withOrganisation(organisation)
                .build());

        // The postProcess method of the following ApplicationInviteBuilder adds the invite to the InviteOrganisation
        newApplicationInvite()
                .with(id(null))
                .withApplication(application)
                .withName("Example User1")
                .withEmail("example+user1@example.com")
                .withStatus(InviteStatus.SENT)
                .withInviteOrganisation(inviteOrganisation)
                .withSentBy(newUser().withId(1L).build())
                .withSentOn(ZonedDateTime.now())
                .build();

        flushAndClearSession();
        return inviteOrganisationRepository.findById(inviteOrganisation.getId()).get();
    }

    private InviteOrganisation setupInviteOrganisationUnconfirmed(Application application) {
        InviteOrganisation inviteOrganisation = inviteOrganisationRepository.save(newInviteOrganisation()
                .with(id(null))
                .withOrganisationName("Quick Sustainability")
                .build());

        // The postProcess method of the following ApplicationInviteBuilder adds the invite to the InviteOrganisation
        newApplicationInvite()
                .with(id(null))
                .withApplication(application)
                .withName("Example User1")
                .withEmail("example+user1@example.com")
                .withStatus(InviteStatus.SENT)
                .withInviteOrganisation(inviteOrganisation)
                .withSentBy(newUser().withId(1L).build())
                .withSentOn(ZonedDateTime.now())
                .build();

        flushAndClearSession();
        return inviteOrganisation;
    }

    private void setLoggedInUser(User user) {
        setLoggedInUser(userMapper.mapToResource(user));
    }

    private void assertInviteOrganisationIsFound(InviteOrganisation expectedInviteOrganisation,
                                                 Organisation expectedOrganisation,
                                                 Supplier<RestResult<InviteOrganisationResource>> inviteOrganisationSupplier) {
        InviteOrganisationResource found = inviteOrganisationSupplier.get().getSuccess();

        assertEquals(expectedInviteOrganisation.getId(), found.getId());
        assertEquals(expectedInviteOrganisation.getOrganisationName(), found.getOrganisationName());
        assertEquals(expectedOrganisation.getId(), found.getOrganisation());
        assertEquals(expectedOrganisation.getName(), found.getOrganisationNameConfirmed());
        assertEquals(1, found.getInviteResources().size());
        assertEquals("example+user1@example.com", found.getInviteResources().get(0).getEmail());
    }

    private void assertAccessDeniedForInviteOrganisation(Supplier<RestResult<InviteOrganisationResource>> inviteOrganisationSupplier) {
        assertTrue(inviteOrganisationSupplier.get().getFailure().is(forbiddenError(GENERAL_SPRING_SECURITY_FORBIDDEN_ACTION)));
    }
}