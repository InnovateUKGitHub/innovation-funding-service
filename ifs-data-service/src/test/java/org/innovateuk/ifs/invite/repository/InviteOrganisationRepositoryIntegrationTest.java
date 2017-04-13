package org.innovateuk.ifs.invite.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.invite.domain.ApplicationInvite;
import org.innovateuk.ifs.invite.domain.InviteOrganisation;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.repository.OrganisationRepository;
import org.innovateuk.ifs.workflow.domain.ActivityState;
import org.innovateuk.ifs.workflow.domain.ActivityType;
import org.innovateuk.ifs.workflow.repository.ActivityStateRepository;
import org.innovateuk.ifs.workflow.resource.State;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.idBasedNames;
import static org.innovateuk.ifs.invite.builder.ApplicationInviteBuilder.newApplicationInvite;
import static org.innovateuk.ifs.invite.builder.InviteOrganisationBuilder.newInviteOrganisation;
import static org.innovateuk.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class InviteOrganisationRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<InviteOrganisationRepository> {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private ActivityStateRepository activityStateRepository;

    private Organisation organisation1;
    private Organisation organisation2;
    private Application application1;
    private Application application2;
    private InviteOrganisation inviteOrgApplication1Org1;
    private InviteOrganisation inviteOrgApplication1Org2;
    private InviteOrganisation inviteOrgApplication2Org1;

    @Autowired
    @Override
    protected void setRepository(InviteOrganisationRepository repository) {
        this.repository = repository;
    }

    @Before
    public void setUp() throws Exception {
        List<Organisation> organisations = newOrganisation()
                .with(idBasedNames("Organisation "))
                .build(2)
                .stream().map(organisation -> organisationRepository.save(organisation)).collect(toList());

        organisation1 = organisations.get(0);
        organisation2 = organisations.get(1);

        List<InviteOrganisation> inviteOrganisations = newInviteOrganisation()
                .with(id(null))
                .withOrganisation(organisation1, organisation2, organisation1)
                .build(3)
                .stream().map(inviteOrganisation -> repository.save(inviteOrganisation)).collect(toList());
        inviteOrgApplication1Org1 = inviteOrganisations.get(0);
        inviteOrgApplication1Org2 = inviteOrganisations.get(1);
        inviteOrgApplication2Org1 = inviteOrganisations.get(2);

        ActivityState createdActivityState = activityStateRepository.findOneByActivityTypeAndState(ActivityType.APPLICATION, State.CREATED);
        application1 = new Application(null, "", createdActivityState);
        application2 = new Application(null, "", createdActivityState);

        applicationRepository.save(application1);
        applicationRepository.save(application2);

        createNewApplicationInvite(application1, inviteOrgApplication1Org1, "app1.user1@org1.com");
        createNewApplicationInvite(application1, inviteOrgApplication1Org1, "app1.user2@org1.com");

        createNewApplicationInvite(application1, inviteOrgApplication1Org2, "app1.user1@org2.com");
        createNewApplicationInvite(application1, inviteOrgApplication1Org2, "app1.user2@org2.com");

        createNewApplicationInvite(application2, inviteOrgApplication2Org1, "app2.user1@org1.com");
        createNewApplicationInvite(application2, inviteOrgApplication2Org1, "app2.user2@org1.com");

        flushAndClearSession();
    }

    @Test
    public void findOneByOrganisationIdAndInvitesApplicationId() throws Exception {
        assertEquals(inviteOrgApplication1Org1.getId(), repository.findOneByOrganisationIdAndInvitesApplicationId(organisation1.getId(), application1.getId()).getId());
        assertEquals(inviteOrgApplication1Org2.getId(), repository.findOneByOrganisationIdAndInvitesApplicationId(organisation2.getId(), application1.getId()).getId());
        assertEquals(inviteOrgApplication2Org1.getId(), repository.findOneByOrganisationIdAndInvitesApplicationId(organisation1.getId(), application2.getId()).getId());
        assertNull(repository.findOneByOrganisationIdAndInvitesApplicationId(organisation2.getId(), application2.getId()));
    }

    @Test
    public void findDistinctByInvitesApplicationId() throws Exception {
        List<InviteOrganisation> inviteOrganisationsForApplication1 = repository.findDistinctByInvitesApplicationId(application1.getId());
        List<InviteOrganisation> inviteOrganisationsForApplication2 = repository.findDistinctByInvitesApplicationId(application2.getId());

        assertEquals(2, inviteOrganisationsForApplication1.size());
        assertEquals(inviteOrgApplication1Org1.getId(), inviteOrganisationsForApplication1.get(0).getId());
        assertEquals(inviteOrgApplication1Org2.getId(), inviteOrganisationsForApplication1.get(1).getId());

        assertEquals(1, inviteOrganisationsForApplication2.size());
        assertEquals(inviteOrgApplication2Org1.getId(), inviteOrganisationsForApplication2.get(0).getId());
    }

    private ApplicationInvite createNewApplicationInvite(Application application, InviteOrganisation inviteOrganisation, String email) {
        // The postProcess method of ApplicationInviteBuilder adds the invite to the InviteOrganisation
        return newApplicationInvite()
                .with(id(null))
                .withApplication(application)
                .with(idBasedNames("User "))
                .withEmail(email)
                .withInviteOrganisation(inviteOrganisation)
                .build();
    }
}