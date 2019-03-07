package org.innovateuk.ifs.eugrant.repository;

import junit.framework.TestCase;
import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.euactiontype.repository.EuActionTypeRepository;
import org.innovateuk.ifs.eugrant.EuOrganisationType;
import org.innovateuk.ifs.eugrant.domain.EuContact;
import org.innovateuk.ifs.eugrant.domain.EuFunding;
import org.innovateuk.ifs.eugrant.domain.EuGrant;
import org.innovateuk.ifs.eugrant.domain.EuOrganisation;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.eugrant.EuOrganisationType.RESEARCH;
import static org.innovateuk.ifs.eugrant.domain.EuContactBuilder.newEuContact;
import static org.innovateuk.ifs.eugrant.domain.EuFundingBuilder.newEuFunding;
import static org.innovateuk.ifs.eugrant.domain.EuGrantBuilder.newEuGrant;
import static org.innovateuk.ifs.eugrant.domain.EuOrganisationBuilder.newEuOrganisation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class EuGrantRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<EuGrantRepository> {

    @Autowired
    @Override
    protected void setRepository(EuGrantRepository repository) {
        this.repository = repository;
    }

    @Autowired
    private EuActionTypeRepository euActionTypeRepository;

    @Test
    public void findAll() {
        repository.saveAll(asList(new EuGrant(), new EuGrant()));
        flushAndClearSession();

        assertEquals(2, repository.count());
    }

    @Test
    public void save() {
        EuGrant grant = new EuGrant();
        repository.save(grant);
        flushAndClearSession();

        Optional<EuGrant> savedGrant = repository.findById(grant.getId());
        assertTrue(savedGrant.isPresent());
        assertNotNull(savedGrant.get().getCreatedOn());
        assertNotNull(savedGrant.get().getModifiedOn());
    }


    @Test
    public void findByNotifiedForNotifiedUsers() {
        Pageable pageable = new PageRequest(0, 20, new Sort("id"));
        EuContact euContact = newEuContact()
                .withName("Horatio Nelson")
                .withEmail("horatio@nelson.com")
                .withJobTitle("General")
                .withTelephone("7654")
                .build();

        EuFunding euFunding = newEuFunding()
                .withFundingContribution(BigDecimal.TEN)
                .withGrantAgreementNumber("123")
                .withParticipantId("987654321")
                .withProjectName("projectName")
                .withProjectStartDate(LocalDate.now())
                .withProjectEndDate(LocalDate.now())
                .withActionType(euActionTypeRepository.findAllByOrderByPriorityAsc().get(0))
                .build();

        EuOrganisation euOrganisation = newEuOrganisation()
                .withName("orgName")
                .withOrganisationType(EuOrganisationType.BUSINESS)
                .build();

        EuGrant euGrant = newEuGrant()
                .withContact(euContact)
                .withOrganisation(euOrganisation)
                .withFunding(euFunding)
                .build();

        euGrant.submit("abcd");
        euGrant.markNotificationSent();
        repository.save(euGrant);
        flushAndClearSession();

        List<EuGrant> savedNotifiedContactGrants =
                repository.findBySubmittedTrueAndNotifiedTrueAndOrganisationOrganisationTypeNot(RESEARCH, pageable).getContent();
        assertEquals(1, savedNotifiedContactGrants.size());
        EuContact savedEuContact = savedNotifiedContactGrants.get(0).getContact();
        assertEquals(euContact.getName(), savedEuContact.getName());
        assertEquals(euContact.getEmail(), savedEuContact.getEmail());
        assertEquals(euContact.getTelephone(), savedEuContact.getTelephone());
        assertEquals(euContact.getJobTitle(), savedEuContact.getJobTitle());

        List<EuGrant> savedNonNotifiedContactGrants =
                repository.findBySubmittedTrueAndNotifiedFalseAndOrganisationOrganisationTypeNot(RESEARCH, pageable).getContent();
        TestCase.assertTrue(savedNonNotifiedContactGrants.isEmpty());
    }

    @Test
    public void findByNotifiedForNonNotifiedUsers() {
        Pageable pageable = new PageRequest(0, 20, new Sort("id"));
        EuContact euContact = newEuContact()
                .withName("Willie Nelson")
                .withEmail("willie@nelson.com")
                .withJobTitle("Musician")
                .withTelephone("3210")
                .build();

        EuFunding euFunding = newEuFunding()
                .withFundingContribution(BigDecimal.TEN)
                .withGrantAgreementNumber("456")
                .withParticipantId("123456789")
                .withProjectName("projectName")
                .withProjectStartDate(LocalDate.now())
                .withProjectEndDate(LocalDate.now())
                .withActionType(euActionTypeRepository.findAllByOrderByPriorityAsc().get(0))
                .build();

        EuOrganisation euOrganisation = newEuOrganisation()
                .withName("orgName")
                .withOrganisationType(EuOrganisationType.BUSINESS)
                .build();

        EuGrant euGrant = newEuGrant()
                .withContact(euContact)
                .withOrganisation(euOrganisation)
                .withFunding(euFunding)
                .build();

        euGrant.submit("ghijk");
        repository.save(euGrant);
        flushAndClearSession();

        List<EuGrant> savedNonNotifiedContactGrants =
                repository.findBySubmittedTrueAndNotifiedFalseAndOrganisationOrganisationTypeNot(RESEARCH, pageable).getContent();
        assertEquals(1, savedNonNotifiedContactGrants.size());
        EuContact savedEuContact = savedNonNotifiedContactGrants.get(0).getContact();
        assertEquals(euContact.getName(), savedEuContact.getName());
        assertEquals(euContact.getEmail(), savedEuContact.getEmail());
        assertEquals(euContact.getTelephone(), savedEuContact.getTelephone());
        assertEquals(euContact.getJobTitle(), savedEuContact.getJobTitle());

        List<EuGrant> savedNotifiedContacts =
                repository.findBySubmittedTrueAndNotifiedTrueAndOrganisationOrganisationTypeNot(RESEARCH, pageable).getContent();
        assertTrue(savedNotifiedContacts.isEmpty());
    }
}