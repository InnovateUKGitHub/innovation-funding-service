package org.innovateuk.ifs.invite.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.invite.domain.ApplicationInvite;
import org.innovateuk.ifs.invite.domain.InviteOrganisation;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.repository.OrganisationRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.idBasedNames;
import static org.innovateuk.ifs.invite.builder.ApplicationInviteBuilder.newApplicationInvite;
import static org.innovateuk.ifs.invite.builder.InviteOrganisationBuilder.newInviteOrganisation;
import static org.innovateuk.ifs.user.builder.OrganisationBuilder.newOrganisation;

public class InviteOrganisationRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<InviteOrganisationRepository> {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private OrganisationRepository organisationRepository;

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
                .stream()
                .map(organisation -> organisationRepository.save(organisation))
                .collect(toList());

        organisation1 = organisations.get(0);
        organisation2 = organisations.get(1);

        List<InviteOrganisation> inviteOrganisations = newInviteOrganisation()
                .with(id(null))
                .withOrganisation(organisation1, organisation2, organisation1)
                .build(3)
                .stream()
                .map(inviteOrganisation -> repository.save(inviteOrganisation))
                .collect(toList());

        inviteOrgApplication1Org1 = inviteOrganisations.get(0);
        inviteOrgApplication1Org2 = inviteOrganisations.get(1);
        inviteOrgApplication2Org1 = inviteOrganisations.get(2);

        application1 = applicationRepository.save(new Application(""));
        application2 = applicationRepository.save(new Application(""));

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
        assertThat(repository.findOneByOrganisationIdAndInvitesApplicationId(organisation1.getId(), application1.getId()))
                .hasFieldOrPropertyWithValue("id", inviteOrgApplication1Org1.getId());

        assertThat(repository.findOneByOrganisationIdAndInvitesApplicationId(organisation2.getId(), application1.getId()))
                .hasFieldOrPropertyWithValue("id", inviteOrgApplication1Org2.getId());

        assertThat(repository.findOneByOrganisationIdAndInvitesApplicationId(organisation1.getId(), application2.getId()))
                .hasFieldOrPropertyWithValue("id", inviteOrgApplication2Org1.getId());

        assertThat(repository.findOneByOrganisationIdAndInvitesApplicationId(organisation2.getId(), application2.getId()))
                .isNull();
    }

    @Test
    public void findDistinctByInvitesApplicationId() throws Exception {
        List<InviteOrganisation> inviteOrganisationsForApplication1 =
                repository.findDistinctByInvitesApplicationId(application1.getId());
        List<InviteOrganisation> inviteOrganisationsForApplication2 =
                repository.findDistinctByInvitesApplicationId(application2.getId());

        assertThat(inviteOrganisationsForApplication1)
                .hasSize(2)
                .extracting("id")
                .contains(inviteOrgApplication1Org1.getId(), inviteOrgApplication1Org2.getId());

        assertThat(inviteOrganisationsForApplication2)
                .hasSize(1)
                .extracting("id")
                .contains(inviteOrgApplication2Org1.getId());
    }

    @Test
    public void findFirstByOrganisationIdAndInvitesApplicationId_doesNotThrowExceptionIfMultipleOrganisationsWithSameId() {
        InviteOrganisation inviteOrganisation = repository.save(
                newInviteOrganisation()
                        .with(id(null))
                        .withOrganisation(organisation1)
                        .build()
        );

        createNewApplicationInvite(application1, inviteOrganisation, "app1.user3@org1.com");
        createNewApplicationInvite(application1, inviteOrganisation, "app1.user4@org1.com");

        assertThat(repository.findFirstByOrganisationIdAndInvitesApplicationId(organisation1.getId(), application1.getId()))
                .isPresent()
                .get()
                .extracting("id")
                .containsAnyOf(inviteOrganisation.getId(), inviteOrgApplication1Org1.getId(), inviteOrgApplication1Org2.getId());

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