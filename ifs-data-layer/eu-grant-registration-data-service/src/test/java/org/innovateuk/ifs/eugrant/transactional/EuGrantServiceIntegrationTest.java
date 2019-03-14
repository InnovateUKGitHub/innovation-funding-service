package org.innovateuk.ifs.eugrant.transactional;

import org.innovateuk.ifs.commons.BaseIntegrationTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.euactiontype.repository.EuActionTypeRepository;
import org.innovateuk.ifs.eugrant.*;
import org.innovateuk.ifs.eugrant.domain.EuContact;
import org.innovateuk.ifs.eugrant.domain.EuFunding;
import org.innovateuk.ifs.eugrant.domain.EuGrant;
import org.innovateuk.ifs.eugrant.domain.EuOrganisation;
import org.innovateuk.ifs.eugrant.repository.EuGrantRepository;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static junit.framework.TestCase.assertFalse;
import static org.innovateuk.ifs.eugrant.builder.EuContactResourceBuilder.newEuContactResource;
import static org.innovateuk.ifs.eugrant.builder.EuGrantResourceBuilder.newEuGrantResource;
import static org.innovateuk.ifs.eugrant.builder.EuOrganisationResourceBuilder.newEuOrganisationResource;
import static org.innovateuk.ifs.eugrant.domain.EuContactBuilder.newEuContact;
import static org.innovateuk.ifs.eugrant.domain.EuFundingBuilder.newEuFunding;
import static org.innovateuk.ifs.eugrant.domain.EuGrantBuilder.newEuGrant;
import static org.innovateuk.ifs.eugrant.domain.EuOrganisationBuilder.newEuOrganisation;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.SYSTEM_REGISTRATION_USER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EuGrantServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private EuGrantService euGrantService;

    @Autowired
    private EuGrantRepository euGrantRepository;

    @Autowired
    private EuActionTypeRepository euActionTypeRepository;

    private EuContact euContactOne;
    private EuContact euContactTwo;
    private EuFunding euFundingOne;
    private EuFunding euFundingTwo;
    private EuOrganisation euOrganisationOne;
    private EuOrganisation euOrganisationTwo;
    private EuGrant euGrantOne;
    private EuGrant euGrantTwo;

    @Before
    public void cleanRepository() {
        euGrantRepository.deleteAll();

        euContactOne = newEuContact()
                .withName("Barry Venison")
                .withEmail("barry@venison.com")
                .withJobTitle("Rollercoaster operator")
                .withTelephone("2468")
                .build();

        euFundingOne = newEuFunding()
                .withFundingContribution(BigDecimal.TEN)
                .withGrantAgreementNumber("456")
                .withParticipantId("123456789")
                .withProjectName("projectName")
                .withProjectStartDate(LocalDate.now())
                .withProjectEndDate(LocalDate.now())
                .withActionType(euActionTypeRepository.findAllByOrderByPriorityAsc().get(0))
                .build();

        euOrganisationOne = newEuOrganisation()
                .withName("orgName")
                .withOrganisationType(EuOrganisationType.BUSINESS)
                .build();

        euGrantOne = newEuGrant()
                .withContact(euContactOne)
                .withOrganisation(euOrganisationOne)
                .withFunding(euFundingOne)
                .build();


        euContactTwo = newEuContact()
                .withName("Garry Owen")
                .withEmail("garry@owen.com")
                .withJobTitle("Secretary")
                .withTelephone("1357")
                .build();


        euFundingTwo = newEuFunding()
                .withFundingContribution(BigDecimal.TEN)
                .withGrantAgreementNumber("456")
                .withParticipantId("123456789")
                .withProjectName("projectName")
                .withProjectStartDate(LocalDate.now())
                .withProjectEndDate(LocalDate.now())
                .withActionType(euActionTypeRepository.findAllByOrderByPriorityAsc().get(0))
                .build();

        euOrganisationTwo = newEuOrganisation()
                .withName("orgName")
                .withOrganisationType(EuOrganisationType.BUSINESS)
                .build();

        euGrantTwo = newEuGrant()
                .withContact(euContactTwo)
                .withOrganisation(euOrganisationTwo)
                .withFunding(euFundingTwo)
                .build();

        euGrantOne.submit("asdf");
        euGrantTwo.submit("hjkl");
        euGrantTwo.markNotificationSent();
        euGrantRepository.save(euGrantOne);
        euGrantRepository.save(euGrantTwo);
    }

    private UserResource webUser = newUserResource().withRoleGlobal(SYSTEM_REGISTRATION_USER).build();

    @Test
    public void update() {

        setLoggedInUser(webUser);

        EuGrant euGrant = euGrantRepository.save(newEuGrant().build());

        EuOrganisationResource euOrganisationResource = newEuOrganisationResource()
                .withName("worth")
                .withOrganisationType(EuOrganisationType.BUSINESS)
                .withCompaniesHouseNumber("1234")
                .build();

        EuContactResource euContactResource = newEuContactResource()
                .withName("Worth")
                .withEmail("Worth@gmail.com")
                .withJobTitle("worth employee")
                .withTelephone("0123456789")
                .build();

        EuGrantResource euGrantResource = newEuGrantResource()
                .withId(euGrant.getId())
                .withOrganisation(euOrganisationResource)
                .withContact(euContactResource)
                .build();

        ServiceResult<Void> result = euGrantService.update(euGrantResource.getId(), euGrantResource);

        assertTrue(result.isSuccess());

        List<EuGrant> grants = newArrayList(euGrantRepository.findAll());

        assertEquals(grants.size(), 3);

        EuGrant grant = grants.get(2);

        assertEquals(grant.getContact().getName(), euGrantResource.getContact().getName());
        assertEquals(grant.getContact().getJobTitle(), euGrantResource.getContact().getJobTitle());
        assertEquals(grant.getContact().getEmail(), euGrantResource.getContact().getEmail());
        assertEquals(grant.getContact().getTelephone(), euGrantResource.getContact().getTelephone());

        assertEquals(grant.getOrganisation().getName(), euGrantResource.getOrganisation().getName());
        assertEquals(grant.getOrganisation().getOrganisationType(), euGrantResource.getOrganisation().getOrganisationType());
        assertEquals(grant.getOrganisation().getCompaniesHouseNumber(), euGrantResource.getOrganisation().getCompaniesHouseNumber());
    }

    @Test
    public void findById() {
        setLoggedInUser(webUser);
        EuGrant grant = new EuGrant();
        grant = euGrantRepository.save(grant);

        ServiceResult<EuGrantResource> result = euGrantService.findById(grant.getId());

        assertTrue(result.isSuccess());
        assertEquals(result.getSuccess().getId().toString(), grant.getId().toString());
    }

    @Test
    public void create() {
        setLoggedInUser(webUser);
        ServiceResult<EuGrantResource> result = euGrantService.create();

        List<EuGrant> grants = newArrayList(euGrantRepository.findAll());

        assertTrue(result.isSuccess());
        assertFalse(grants.isEmpty());
    }

    @Test
    public void submit() {
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

        ServiceResult<EuGrantResource> result = euGrantService.submit(euGrant.getId(), true);

        assertTrue(result.isSuccess());

        euGrant = euGrantRepository.findById(euGrant.getId()).get();
        assertEquals(euGrant.getShortCode().length(), 5);
    }

    @Test
    public void getByNotified() {

        Pageable pageable = new PageRequest(0, 100 , new Sort("contact.id"));

        ServiceResult<EuGrantPageResource> resultOne = euGrantService.getEuGrantsByContactNotified(false, pageable);
        ServiceResult<EuGrantPageResource> resultTwo = euGrantService.getEuGrantsByContactNotified(true, pageable);

        assertTrue(resultOne.isSuccess());

        assertEquals(1, resultOne.getSuccess().getContent().size());
        EuContactResource resultingResource = resultOne.getSuccess().getContent().get(0).getContact();
        assertEquals(euContactOne.getEmail(), resultingResource.getEmail());
        assertEquals(euContactOne.getJobTitle(), resultingResource.getJobTitle());
        assertEquals(euContactOne.getName(), resultingResource.getName());
        assertEquals(euContactOne.getTelephone(), resultingResource.getTelephone());

        assertTrue(resultTwo.isSuccess());

        assertEquals(1, resultTwo.getSuccess().getContent().size());
        EuContactResource resultingResourceTwo = resultTwo.getSuccess().getContent().get(0).getContact();
        assertEquals(euContactTwo.getEmail(), resultingResourceTwo.getEmail());
        assertEquals(euContactTwo.getJobTitle(), resultingResourceTwo.getJobTitle());
        assertEquals(euContactTwo.getName(), resultingResourceTwo.getName());
        assertEquals(euContactTwo.getTelephone(), resultingResourceTwo.getTelephone());
    }

    @Test
    public void getTotalSubmitted() {
        ServiceResult<Long> result = euGrantService.getTotalSubmitted();

        assertTrue(result.isSuccess());
        assertTrue(result.getSuccess().equals(2L));
    }
}
