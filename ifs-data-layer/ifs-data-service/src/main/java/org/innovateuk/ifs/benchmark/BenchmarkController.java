package org.innovateuk.ifs.benchmark;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * A controller that can be used for load testing purposes
 */
@Controller
@RequestMapping("/monitoring/benchmark")
public class BenchmarkController {

    private static final Log LOG = LogFactory.getLog(BenchmarkController.class);

    @Autowired
    private BenchmarkingTestService benchmarkingTestService;

    @GetMapping("/isolated")
    public @ResponseBody
    String isolated(@RequestParam("process_time_millis") long processTimeMillis) {

        return processForMillis(processTimeMillis, () -> Math.tanh(Math.random()), "isolated data layer benchmarking test");
    }

    @GetMapping("/to-database")
    public @ResponseBody
    String toDatabase(@RequestParam("process_time_millis") long processTimeMillis) {

        return processForMillis(processTimeMillis, () -> benchmarkingTestService.interactWithDatabase(),
                "data layer plus database interaction benchmarking test");
    }

    private String processForMillis(long processTimeMillis, Runnable action, String processMessage) {

        long start = System.currentTimeMillis();

        do {
            action.run();
        } while ((System.currentTimeMillis() - start) < processTimeMillis);

        String response = "Took " + (System.currentTimeMillis() - start) + " milliseconds to process " + processMessage;
        LOG.debug(response);
        return response;
    }
}
