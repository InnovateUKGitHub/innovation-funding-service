package org.innovateuk.ifs.project.core.schedule;

import org.innovateuk.ifs.project.core.transactional.ProjectToBeCreatedService;
import org.innovateuk.ifs.schedule.transactional.ScheduleStatusWrapper;
import org.innovateuk.ifs.util.AuthenticationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Profile("!integration-test")
public class ProjectToBeCreatedSchedule {
    @Autowired
    private ScheduleStatusWrapper wrapper;

    @Autowired
    private ProjectToBeCreatedService projectToBeCreatedService;

    @Autowired
    private AuthenticationHelper authenticationHelper;

    private static final String JOB_NAME = "CREATE_PROJECT_%d";

    @Scheduled(fixedDelayString = "${ifs.data.service.schedule.project.creation.delay.millis:30000}")
    public void schedule() {
        createProject(0);
    }

    private void createProject(int timesCalled) {
        authenticationHelper.loginSystemUser();
        Optional<Long> projectToCreated = projectToBeCreatedService.findProjectToCreate(timesCalled);
        projectToCreated.ifPresent(id -> wrapper.doScheduledJob(String.format(JOB_NAME, id), () ->
                        projectToBeCreatedService.createProject(id),
                () -> {
                    if (timesCalled < 10) {
                        createProject(timesCalled + 1);
                    }
                }));
    }
}
