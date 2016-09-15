package com.worth.ifs.workflow.repository;

import com.worth.ifs.BaseRepositoryIntegrationTest;
import com.worth.ifs.workflow.domain.ActivityState;
import com.worth.ifs.workflow.domain.ActivityType;
import com.worth.ifs.workflow.resource.State;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ActivityStateRepositoryTest extends BaseRepositoryIntegrationTest<ActivityStateRepository> {

    @Test
    public void testFindOneByActivityTypeAndState() {
        ActivityState existingState = repository.findOneByActivityTypeAndState(ActivityType.APPLICATION_ASSESSMENT, State.OPEN);
        assertEquals(ActivityType.APPLICATION_ASSESSMENT, existingState.getActivityType());
        assertEquals(State.OPEN, existingState.getState());
        assertNotNull(existingState.getId());
    }

    @Override
    @Autowired
    protected void setRepository(ActivityStateRepository repository) {
        this.repository = repository;
    }
}
