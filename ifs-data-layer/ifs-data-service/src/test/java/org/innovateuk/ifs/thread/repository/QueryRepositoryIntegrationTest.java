package org.innovateuk.ifs.thread.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.threads.domain.Query;
import org.innovateuk.ifs.threads.repository.QueryRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class QueryRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<QueryRepository> {

    @Autowired
    @Override
    protected void setRepository(QueryRepository repository) {
        this.repository = repository;
    }

    @Test
    public void test_findAll() throws Exception {
        List<Query> found = repository.findAllByClassPkAndClassName(6L, "org.innovateuk.ifs.finance.domain.ProjectFinance");
        assertEquals(2, found.size());
        assertEquals(Long.valueOf(1L), found.get(0).id());
        assertEquals(Long.valueOf(2L), found.get(1).id());

        assertEquals("org.innovateuk.ifs.finance.domain.ProjectFinance", found.get(0).contextClassName());
        assertEquals(Long.valueOf(6L), found.get(0).contextClassPk());
        assertEquals("org.innovateuk.ifs.finance.domain.ProjectFinance", found.get(1).contextClassName());
        assertEquals(Long.valueOf(6L), found.get(1).contextClassPk());
    }
}