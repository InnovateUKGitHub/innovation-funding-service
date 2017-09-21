package org.innovateuk.ifs.project.transactional;

import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.commons.BaseIntegrationTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.repository.ProjectRepository;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.innovateuk.ifs.util.CallsRealMethodsWithDelay.doAnswerWithRealMethodAndDelay;

public class ProjectFromApplicationServiceImplTransactionTest extends BaseIntegrationTest {

    @Autowired
    private ProjectService projectService;

    @Autowired
    @Spy
    private ProjectRepository projectRepository;


    private Long threadCount = 2L;

    @Test
    public void testCreateProjectFromApplicationIsInOwnTransaction() {
        // start thread 1 (thread 2 pauses immediately after starting), create 1st application in new transaction which completes, pause before creating 2nd application,
        // start 2nd thread which starts new transaction, which checks for first application - it's present, fails
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount.intValue());
        Map<Long, FundingDecision> fundingDecisionMap = new HashMap<>();
        fundingDecisionMap.put(1L, FundingDecision.FUNDED);
        fundingDecisionMap.put(2L, FundingDecision.FUNDED);
        List<Future<Void>> futures = new ArrayList<>();

        // use real methods apart from checking for presence of 2nd application, in which case pause then call real method
        doAnswerWithRealMethodAndDelay(60000).when(projectRepository).findOneByApplicationId(2L);

        List<Long> threadCounts = Stream.generate(() -> 0L).limit(threadCount).collect(Collectors.toList());
        threadCounts.forEach(x -> {
            Callable<Void> callable = new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    // let first thread start transaction before 2nd thread
                    if (x > 0) {
                        Thread.sleep(30000);
                    }
                    ServiceResult<Void> result = projectService.createProjectsFromFundingDecisions(fundingDecisionMap);
                    if (x == 0) {
                        Assert.assertTrue(result.isSuccess());
                    } else {
                        Assert.assertFalse(result.isSuccess());
                    }
                    return null;
                }
            };
            Future<Void> submit = executorService.submit(callable);
            futures.add(submit);
            });

        List<Exception> exceptions = new ArrayList<Exception>();
        for (Future<Void> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                exceptions.add(e);
                e.printStackTrace(System.err);
            }
        }

        executorService.shutdown();
    }

}
