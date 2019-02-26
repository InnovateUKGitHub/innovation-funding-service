package org.innovateuk.ifs.eugrant.repository;

import junit.framework.TestCase;
import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.eugrant.domain.EuContact;
import org.innovateuk.ifs.eugrant.domain.EuGrant;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.eugrant.domain.EuContactBuilder.newEuContact;
import static org.innovateuk.ifs.eugrant.domain.EuGrantBuilder.newEuGrant;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class EuGrantRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<EuGrantRepository> {

    @Autowired
    @Override
    protected void setRepository(EuGrantRepository repository) {
        this.repository = repository;
    }

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
                .withNotified(true)
                .build();

        EuGrant euGrant = newEuGrant()
                .withContact(euContact)
                .withSubmitted(true)
                .build();

        repository.save(euGrant);
        flushAndClearSession();

        List<EuGrant> savedNotifiedContactGrants = repository.findBySubmittedTrueAndContactNotifiedTrue(pageable).getContent();
        assertEquals(1, savedNotifiedContactGrants.size());
        EuContact savedEuContact = savedNotifiedContactGrants.get(0).getContact();
        assertEquals(euContact.getName(), savedEuContact.getName());
        assertEquals(euContact.getEmail(), savedEuContact.getEmail());
        assertEquals(euContact.getTelephone(), savedEuContact.getTelephone());
        assertEquals(euContact.getJobTitle(), savedEuContact.getJobTitle());
        assertEquals(euContact.getNotified(), savedEuContact.getNotified());

        List<EuGrant> savedNonNotifiedContactGrants = repository.findBySubmittedTrueAndContactNotifiedFalse(pageable).getContent();
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
                .withNotified(false)
                .build();

        EuGrant euGrant = newEuGrant()
                .withContact(euContact)
                .withSubmitted(true)
                .build();

        repository.save(euGrant);
        flushAndClearSession();

        List<EuGrant> savedNonNotifiedContactGrants = repository.findBySubmittedTrueAndContactNotifiedFalse(pageable).getContent();
        assertEquals(1, savedNonNotifiedContactGrants.size());
        EuContact savedEuContact = savedNonNotifiedContactGrants.get(0).getContact();
        assertEquals(euContact.getName(), savedEuContact.getName());
        assertEquals(euContact.getEmail(), savedEuContact.getEmail());
        assertEquals(euContact.getTelephone(), savedEuContact.getTelephone());
        assertEquals(euContact.getJobTitle(), savedEuContact.getJobTitle());
        assertEquals(euContact.getNotified(), savedEuContact.getNotified());

        List<EuGrant> savedNotifiedContacts = repository.findBySubmittedTrueAndContactNotifiedTrue(pageable).getContent();
        assertTrue(savedNotifiedContacts.isEmpty());
    }
}