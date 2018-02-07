package org.innovateuk.ifs.application.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.workflow.domain.ActivityType;
import org.innovateuk.ifs.workflow.repository.ActivityStateRepository;
import org.innovateuk.ifs.workflow.resource.State;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.junit.Assert.assertEquals;

public class ApplicationRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<ApplicationRepository> {
    @Autowired
    private ActivityStateRepository activityStateRepository;

    @Autowired
    @Override
    protected void setRepository(ApplicationRepository repository) {
        this.repository = repository;
    }

    @Test
    @Rollback
    public void findByApplicationProcessActivityStateStateIn() {
        Collection<State> states = ApplicationState.submittedStates.stream().map(ApplicationState::getBackingState).collect(Collectors.toList());

        List<ApplicationState> applicationStates = Arrays.asList(ApplicationState.values());
        List<Application> applicationList = applicationStates.stream()
                .filter(state -> state != ApplicationState.IN_PANEL)
                .map(state -> createApplicationByState(state)).collect(Collectors
                .toList());

        Long initial = repository.findByApplicationProcessActivityStateStateIn(states).count();

        repository.save(applicationList);
        Stream<Application> applications = repository.findByApplicationProcessActivityStateStateIn(states);

        assertEquals(initial + 5, applications.count());
    }

    private Application createApplicationByState(ApplicationState applicationState) {
        Application application = newApplication()
                .with(id(null))
                .withApplicationState(applicationState)
                .build();
        application.getApplicationProcess()
                .setActivityState(activityStateRepository.findOneByActivityTypeAndState(
                        ActivityType.APPLICATION,
                        applicationState.getBackingState()));
        return application;
    }
}
