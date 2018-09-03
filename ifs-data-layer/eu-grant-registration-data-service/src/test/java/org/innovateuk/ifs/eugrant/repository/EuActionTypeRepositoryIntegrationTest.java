package org.innovateuk.ifs.eugrant.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.eugrant.domain.EuActionType;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertFalse;

public class EuActionTypeRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<EuActionTypeRepository> {

    @Autowired
    @Override
    protected void setRepository(EuActionTypeRepository repository) {
        this.repository = repository;
    }

    @Test
    public void findAllByOrderByPriorityAsc() {
        List<EuActionType> actionTypes = repository.findAllByOrderByPriorityAsc();

        assertFalse(actionTypes.isEmpty());
    }
}