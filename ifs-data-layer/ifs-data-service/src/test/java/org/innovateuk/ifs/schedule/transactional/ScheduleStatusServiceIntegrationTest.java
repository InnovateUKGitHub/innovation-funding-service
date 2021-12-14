package org.innovateuk.ifs.schedule.transactional;

import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.BaseAuthenticationAwareIntegrationTest;
import org.innovateuk.ifs.schedule.repository.ScheduleStatusRepository;
import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Slf4j
public class ScheduleStatusServiceIntegrationTest extends BaseAuthenticationAwareIntegrationTest {

    @Autowired
    private ScheduleStatusService scheduleStatusService;

    @Autowired
    private ScheduleStatusRepository scheduleStatusRepository;

    private static final String JOB_NAME = "INTEGRATION_TEST";

    @After
    public void teardown() {
        scheduleStatusRepository.findByJobName(JOB_NAME).ifPresent(scheduleStatusRepository::delete);
    }

    @Test
    public void multipleServicesRunningSchedule_DifferentTimes() throws InterruptedException, ExecutionException {
        CompletableFuture<Optional<Exception>> future1 = new CompletableFuture<>();
        CompletableFuture<Optional<Exception>> future2 = new CompletableFuture<>();

        new Thread(new ScheduleTask(future1, "Task 1")).start();
        Thread.sleep(100); //wait so that Task 2 definitely can't start the job.
        new Thread(new ScheduleTask(future2, "Task 2")).start();

        assertFalse(taskWasSuccessful(future2));
        assertTrue(taskExists());

        assertTrue(taskWasSuccessful(future1));
        assertFalse(taskExists());
    }

    private boolean taskExists() {
        return scheduleStatusRepository.findByJobName(JOB_NAME).isPresent();
    }

    private boolean taskWasSuccessful(CompletableFuture<Optional<Exception>> task) throws ExecutionException, InterruptedException {
        return !task.get().isPresent();
    }

    private class ScheduleTask implements Runnable {
        CompletableFuture<Optional<Exception>> future;
        String name;

        private ScheduleTask(CompletableFuture<Optional<Exception>> future, String name) {
            this.future = future;
            this.name = name;
        }

        @Override
        public void run() {
            try {
                log.debug("Attempting to start " + name);
                scheduleStatusService.startJob(JOB_NAME);
                log.debug("Started " + name);
                Thread.sleep(   500);
                scheduleStatusService.endJob(JOB_NAME);
                log.debug("Ended " + name);
                future.complete(Optional.empty());
            } catch (Exception e) {
                log.debug("Failed to start " + name);
                future.complete(Optional.of(e));
            }
        }
    }
}
