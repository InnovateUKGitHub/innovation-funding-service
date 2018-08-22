package org.innovateuk.ifs.eugrant.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.eugrant.domain.EuGrant;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class EuGrantRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<EuGrantRepository> {

    @Autowired
    @Override
    protected void setRepository(EuGrantRepository repository) {
        this.repository = repository;
    }

    @Test
    public void findAll() {
        repository.save(asList(new EuGrant(), new EuGrant()));
        flushAndClearSession();

        assertEquals(2, repository.count());
    }

    @Test
    public void save() {
        EuGrant grant = new EuGrant();
        repository.save(grant);
        flushAndClearSession();

        assertNotNull(repository.findOne(grant.getId()));
    }
}