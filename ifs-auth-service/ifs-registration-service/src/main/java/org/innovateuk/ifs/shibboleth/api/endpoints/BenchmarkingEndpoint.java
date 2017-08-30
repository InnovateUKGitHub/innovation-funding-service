package org.innovateuk.ifs.shibboleth.api.endpoints;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * An endpoint that can be used for load testing purposes
 */
@Controller
@RequestMapping("/benchmark")
public class BenchmarkingEndpoint {

    private static final Log LOG = LogFactory.getLog(BenchmarkingEndpoint.class);


    @GetMapping("/isolated")
    public @ResponseBody
    String isolated(@RequestParam("process_time_millis") long processTimeMillis) {

        return processForMillis(processTimeMillis, () -> Math.tanh(Math.random()), "isolated regapi benchmarking test");
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
