package org.innovateuk.ifs.euinvite;

import org.innovateuk.ifs.commons.BaseIntegrationTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.euactiontype.repository.EuActionTypeRepository;
import org.innovateuk.ifs.eugrant.repository.EuContactRepository;
import org.innovateuk.ifs.eugrant.EuOrganisationType;
import org.innovateuk.ifs.eugrant.domain.EuContact;
import org.innovateuk.ifs.eugrant.domain.EuGrant;
import org.innovateuk.ifs.eugrant.repository.EuGrantRepository;
import org.innovateuk.ifs.eugrant.transactional.EuGrantService;
import org.innovateuk.ifs.euinvite.transactional.EuInviteService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.eugrant.domain.EuContactBuilder.newEuContact;
import static org.innovateuk.ifs.eugrant.domain.EuFundingBuilder.newEuFunding;
import static org.innovateuk.ifs.eugrant.domain.EuGrantBuilder.newEuGrant;
import static org.innovateuk.ifs.eugrant.domain.EuOrganisationBuilder.newEuOrganisation;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.SYSTEM_REGISTRATION_USER;
import static org.junit.Assert.assertTrue;

public class EuInviteServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private EuInviteService euInviteService;

    @Autowired
    private EuGrantService euGrantService;

    @Autowired
    private EuGrantRepository euGrantRepository;

    @Autowired
    private EuActionTypeRepository euActionTypeRepository;

    @Autowired
    private EuContactRepository euContactRepository;

    @Before
    public void cleanRepository() {
        euGrantRepository.deleteAll();
    }

    private UserResource webUser = newUserResource().withRoleGlobal(SYSTEM_REGISTRATION_USER).build();

    @Test
    public void invite() {
        setLoggedInUser(webUser);

        EuGrant euGrant = newEuGrant()
                .withContact(newEuContact()
                                     .withEmail("blah@example.com")
                                     .withJobTitle("King")
                                     .withName("Bob")
                                     .withTelephone("999")
                                     .build())
                .withOrganisation(newEuOrganisation()
                                          .withCompaniesHouseNumber("1234")
                                          .withName("Org")
                                          .withOrganisationType(EuOrganisationType.BUSINESS)
                                          .build())
                .withFunding(newEuFunding()
                                     .withActionType(euActionTypeRepository.findAllByOrderByPriorityAsc().get(0))
                                     .withFundingContribution(new BigDecimal(100))
                                     .withGrantAgreementNumber("12345")
                                     .withParticipantId("123456789")
                                     .withProjectCoordinator(true)
                                     .withProjectStartDate(LocalDate.now().minusYears(1))
                                     .withProjectEndDate(LocalDate.now().plusYears(1))
                                     .withProjectName("Project")
                                     .build())
                .build();

        euGrant = euGrantRepository.save(euGrant);
        euGrantService.submit(euGrant.getId(), true);
        long euContactId = euGrant.getContact().getId();

        ServiceResult<Void> result = euInviteService.sendInvites(singletonList(euGrant.getId()));
        assertTrue(result.isSuccess());
        EuContact euContact = euContactRepository.findById(euContactId).get();
        assertTrue(euContact.getNotified());
    }
}
