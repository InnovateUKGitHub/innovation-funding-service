package org.innovateuk.ifs.eucontact.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;

import org.innovateuk.ifs.eugrant.domain.EuContact;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.innovateuk.ifs.eugrant.domain.EuContactBuilder.newEuContact;
import static org.junit.Assert.assertEquals;


public class EuContactRepositoryIntegrationTest  extends BaseRepositoryIntegrationTest<EuContactRepository> {

    @Autowired
    @Override
    protected void setRepository(EuContactRepository repository) {
        this.repository = repository;
    }

    @Test
    public void findByNotifiedForNotifiedUsers() {
        Pageable pageable = new PageRequest(0,20, new Sort("id"));
        EuContact euContact = newEuContact()
                .withName("Horatio Nelson")
                .withEmail("horatio@nelson.com")
                .withJobTitle("General")
                .withTelephone("7654")
                .withNotified(true)
                .build();

        repository.save(euContact);
        flushAndClearSession();

        List<EuContact> savedNotifiedContacts = repository.findByNotifiedTrue(pageable).getContent();
        assertEquals(1, savedNotifiedContacts.size());
        assertEquals(euContact.getName(), savedNotifiedContacts.get((0)).getName());
        assertEquals(euContact.getEmail(), savedNotifiedContacts.get((0)).getEmail());
        assertEquals(euContact.getTelephone(), savedNotifiedContacts.get((0)).getTelephone());
        assertEquals(euContact.getJobTitle(), savedNotifiedContacts.get((0)).getJobTitle());
        assertEquals(euContact.getNotified(), savedNotifiedContacts.get((0)).getNotified());

        List<EuContact> savedNonNotifiedContacts = repository.findByNotifiedFalse(pageable).getContent();
        assertTrue(savedNonNotifiedContacts.isEmpty());
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

        repository.save(euContact);
        flushAndClearSession();

        List<EuContact> savedNonNotifiedContacts = repository.findByNotifiedFalse(pageable).getContent();
        assertEquals(1, savedNonNotifiedContacts.size());
        assertEquals(euContact.getName(), savedNonNotifiedContacts.get((0)).getName());
        assertEquals(euContact.getEmail(), savedNonNotifiedContacts.get((0)).getEmail());
        assertEquals(euContact.getTelephone(), savedNonNotifiedContacts.get((0)).getTelephone());
        assertEquals(euContact.getJobTitle(), savedNonNotifiedContacts.get((0)).getJobTitle());
        assertEquals(euContact.getNotified(), savedNonNotifiedContacts.get((0)).getNotified());

        List<EuContact> savedNotifiedContacts = repository.findByNotifiedTrue(pageable).getContent();
        assertTrue(savedNotifiedContacts.isEmpty());
    }
}
