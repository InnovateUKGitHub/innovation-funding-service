package org.innovateuk.ifs.eugrant.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.eugrant.domain.EuGrant;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static java.util.Arrays.asList;
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
}