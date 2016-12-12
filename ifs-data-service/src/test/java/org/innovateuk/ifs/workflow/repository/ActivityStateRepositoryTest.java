package org.innovateuk.ifs.workflow.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.workflow.domain.ActivityState;
import org.innovateuk.ifs.workflow.domain.ActivityType;
import org.innovateuk.ifs.workflow.resource.State;
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
