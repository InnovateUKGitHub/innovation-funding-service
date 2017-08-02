package org.innovateuk.ifs.benchmark;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * A controller that can be used for load testing purposes
 */
@Controller
@RequestMapping("/benchmark")
public class BenchmarkController {

    private static final Log LOG = LogFactory.getLog(BenchmarkController.class);

    @GetMapping("/isolated")
    public @ResponseBody
    String process(@RequestParam("process_time_millis") long processTimeMillis) {

        long start = System.currentTimeMillis();

        double i = 0;
        do {
            Math.tanh(i++);
        } while ((System.currentTimeMillis() - start) < processTimeMillis);

        String response = "Took " + (System.currentTimeMillis() - start) + " milliseconds to run isolated test";
        LOG.debug(response);
        return response;
    }
}
