package org.innovateuk.ifs.application.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.workflow.resource.State;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class ApplicationRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<ApplicationRepository> {
    @Autowired
    @Override
    protected void setRepository(ApplicationRepository repository) {
        this.repository = repository;
    }

    @Test
    public void findByApplicationProcessActivityStateStateIn() {
        Collection<ApplicationState> SUBMITTED_STATUSES = asList(
                ApplicationState.APPROVED,
                ApplicationState.REJECTED,
                ApplicationState.SUBMITTED);

        Collection<State> states = SUBMITTED_STATUSES.stream().map(ApplicationState::getBackingState).collect(Collectors.toList());

        List<Application> applications = repository.findByApplicationProcessActivityStateStateIn(states);
        assertEquals(5, applications.size());
    }
}
