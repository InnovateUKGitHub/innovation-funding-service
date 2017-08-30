package org.innovateuk.ifs.benchmark;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Random;
import java.util.stream.LongStream;

import static java.util.stream.Collectors.toList;

/**
 * A controller that can be used for load testing purposes
 */
@Controller
@RequestMapping("/monitoring/benchmark")
public class BenchmarkController extends BaseRestService {

    private static final Log LOG = LogFactory.getLog(BenchmarkController.class);
    private static final String THYMELEAF_TESTING = "thymeleaf-test";
    private static final Random r = new Random();

    @GetMapping("/isolated")
    public @ResponseBody String isolated(@RequestParam("process_time_millis") long processTimeMillis) {
        return processForMillis(processTimeMillis, () -> Math.tanh(Math.random()),"isolated web layer benchmarking test");
    }

    @GetMapping("/to-data-layer")
    public @ResponseBody String toDataLayer(@RequestParam("process_time_millis") long processTimeMillis,
                                            @RequestParam("data_layer_process_time_millis") long dataLayerProcessTimeMillis) {

        long start = System.currentTimeMillis();

        RestResult<String> dataLayerResponse = getWithRestResultAnonymous("/monitoring/benchmark/isolated?process_time_millis=" + dataLayerProcessTimeMillis, String.class);

        if (dataLayerResponse.isFailure()) {
            return "Error response from data layer whilst processing \"to-data-layer\" benchmarking test - " + dataLayerResponse.getFailure().getErrors();
        }

        String thisLayerProcessingMessage = processForMillis(processTimeMillis, () -> Math.tanh(Math.random()), "web layer portion");

        String totalTimeMessage = "Overall took " + (System.currentTimeMillis() - start) + " milliseconds to process " +
                "the web layer plus data layer benchmarking test";

        return dataLayerResponse.getSuccessObject() + "\n" + thisLayerProcessingMessage + "\n" + totalTimeMessage;
    }

    @GetMapping("/to-data-layer-with-database")
    public @ResponseBody String toDataLayerWithDatabase(@RequestParam("process_time_millis") long processTimeMillis,
                                            @RequestParam("data_layer_process_time_millis") long dataLayerProcessTimeMillis) {

        long start = System.currentTimeMillis();

        RestResult<String> dataLayerResponse = getWithRestResultAnonymous("/monitoring/benchmark/to-database?process_time_millis=" + dataLayerProcessTimeMillis, String.class);

        if (dataLayerResponse.isFailure()) {
            return "Error response from data layer whilst processing \"to-database\" benchmarking test - " + dataLayerResponse.getFailure().getErrors();
        }

        String thisLayerProcessingMessage = processForMillis(processTimeMillis, () -> Math.tanh(Math.random()), "web layer portion");

        String totalTimeMessage = "Overall took " + (System.currentTimeMillis() - start) + " milliseconds to process " +
                "the web layer plus \"data layer and database\" benchmarking test";

        return dataLayerResponse.getSuccessObject() + "\n" + thisLayerProcessingMessage + "\n" + totalTimeMessage;
    }

    @GetMapping("/thymeleaf")
    public String isolatedThymeleaf(@RequestParam("rows") long rows,
        @RequestParam("cols") long cols,
        Model model){

        model.addAttribute("table", generateTable(rows, cols));
        return THYMELEAF_TESTING;
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

    private List<List<String>> generateTable(long rows, long cols){
        return LongStream.range(0, rows)
            .mapToObj(
                row -> generateRandomList(cols)
            )
            .collect(toList());
    }


    private List<String> generateRandomList(long length) {
        return LongStream.range(0,length)
            .mapToObj(
                i -> Integer.toString(r.nextInt())
            ).collect(toList());
    }
}
