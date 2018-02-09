package org.innovateuk.ifs.benchmark;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.async.generation.AsyncFuturesGenerator;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.stream.LongStream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static org.innovateuk.ifs.commons.rest.RestResult.aggregate;

/**
 * A controller that can be used for load testing purposes
 */
@Controller
@RequestMapping("/monitoring/benchmark")
public class BenchmarkController extends BaseRestService {

    private static final Log LOG = LogFactory.getLog(BenchmarkController.class);
    private static final String THYMELEAF_TESTING = "thymeleaf-test";
    private static final Random r = new Random();

    @Autowired
    private AsyncFuturesGenerator asyncGenerator;

    @GetMapping("/isolated")
    public @ResponseBody String isolated(@RequestParam(name = "process_time_millis", defaultValue = "100") long processTimeMillis) {
        return processForMillis(processTimeMillis, () -> Math.tanh(Math.random()),"isolated web layer benchmarking test");
    }

    @GetMapping("/to-data-layer")
    public @ResponseBody String toDataLayer(@RequestParam(name = "process_time_millis", defaultValue = "100") long processTimeMillis,
                                            @RequestParam(name = "data_layer_process_time_millis", defaultValue = "100") long dataLayerProcessTimeMillis) {

        long start = System.currentTimeMillis();

        RestResult<String> dataLayerResponse = callIsolatedDataLayerEndpoint(dataLayerProcessTimeMillis);

        if (dataLayerResponse.isFailure()) {
            return "Error response from data layer whilst processing \"to-data-layer\" benchmarking test - " + dataLayerResponse.getFailure().getErrors();
        }

        String thisLayerProcessingMessage = processForMillis(processTimeMillis, () -> Math.tanh(Math.random()), "web layer portion");

        String totalTimeMessage = "Overall took " + (System.currentTimeMillis() - start) + " milliseconds to process " +
                "the web layer plus data layer benchmarking test";

        return dataLayerResponse.getSuccess() + "\n" + thisLayerProcessingMessage + "\n" + totalTimeMessage;
    }

    @GetMapping("/to-data-layer-with-database")
    public @ResponseBody String toDataLayerWithDatabase(@RequestParam(name = "process_time_millis", defaultValue = "100") long processTimeMillis,
                                            @RequestParam(name = "data_layer_process_time_millis", defaultValue = "100") long dataLayerProcessTimeMillis) {

        long start = System.currentTimeMillis();

        RestResult<String> dataLayerResponse = callDatabaseInteractionDataLayerEndpoint(dataLayerProcessTimeMillis);

        if (dataLayerResponse.isFailure()) {
            return "Error response from data layer whilst processing \"to-data-layer-with-database\" benchmarking test - " + dataLayerResponse.getFailure().getErrors();
        }

        String thisLayerProcessingMessage = processForMillis(processTimeMillis, () -> Math.tanh(Math.random()), "web layer portion");

        String totalTimeMessage = "Overall took " + (System.currentTimeMillis() - start) + " milliseconds to process " +
                "the web layer plus \"data layer and database\" benchmarking test";

        return dataLayerResponse.getSuccess() + "\n" + thisLayerProcessingMessage + "\n" + totalTimeMessage;
    }

    @GetMapping("/to-data-layer-with-async-calls")
    public @ResponseBody String toDataLayerWithAsyncCalls(
            @RequestParam(name = "process_time_millis", defaultValue = "100") long processTimeMillis,
            @RequestParam(name = "data_layer_process_time_millis", defaultValue = "100") long dataLayerProcessTimeMillis,
            @RequestParam(name = "number_of_parallel_calls_to_generate", defaultValue = "10") int numberOfParallelCalls) {

        long start = System.currentTimeMillis();

        List<CompletableFuture<RestResult<String>>> asyncResults = range(0, numberOfParallelCalls).
                mapToObj(i -> asyncGenerator.async(() -> {
                    RestResult<String> dataLayerResponse = callIsolatedDataLayerEndpoint(dataLayerProcessTimeMillis);
                    processForMillis(processTimeMillis, () -> Math.tanh(Math.random()), "web layer portion");
                    return dataLayerResponse;
                })).collect(toList());

        CompletableFuture<String> totalResults = asyncGenerator.awaitAll(asyncResults).thenApply(restResults -> {

            List<RestResult<String>> restResultsList = (List<RestResult<String>>) restResults;

            RestResult<List<String>> dataLayerResponses = aggregate(restResultsList);

            if (dataLayerResponses.isFailure()) {
                return "Error response from data layer whilst processing \"to-data-layer-with-async-calls\" benchmarking test - " + dataLayerResponses.getFailure().getErrors();
            }

            String totalTimeMessage = "Overall took " + (System.currentTimeMillis() - start) + " milliseconds to process " +
                    "the \"web layer plus data layer with async calls\" benchmarking test";

            return dataLayerResponses.getSuccess() + "\n" + totalTimeMessage;
        });

        return asyncGenerator.awaitAll(totalResults).thenReturn();
    }

    @GetMapping("/to-data-layer-with-database-with-async-calls")
    public @ResponseBody String toDataLayerWithDatabaseWithAsyncCalls(
            @RequestParam(name = "process_time_millis", defaultValue = "100") long processTimeMillis,
            @RequestParam(name = "data_layer_process_time_millis", defaultValue = "100") long dataLayerProcessTimeMillis,
            @RequestParam(name = "number_of_parallel_calls_to_generate", defaultValue = "10") int numberOfParallelCalls) {

        long start = System.currentTimeMillis();

        List<CompletableFuture<RestResult<String>>> asyncResults = range(0, numberOfParallelCalls).
                mapToObj(i -> asyncGenerator.async(() -> {
                    RestResult<String> dataLayerResponse = callDatabaseInteractionDataLayerEndpoint(dataLayerProcessTimeMillis);
                    processForMillis(processTimeMillis, () -> Math.tanh(Math.random()), "web layer portion");
                    return dataLayerResponse;
                })).collect(toList());

        CompletableFuture<String> totalResults = asyncGenerator.awaitAll(asyncResults).thenApply(restResults -> {

            List<RestResult<String>> restResultsList = (List<RestResult<String>>) restResults;

            RestResult<List<String>> dataLayerResponses = aggregate(restResultsList);

            if (dataLayerResponses.isFailure()) {
                return "Error response from data layer whilst processing \"to-data-layer-with-database-with-async-calls\" benchmarking test - " + dataLayerResponses.getFailure().getErrors();
            }

            String thisLayerProcessingMessage = processForMillis(processTimeMillis, () -> Math.tanh(Math.random()), "web layer portion");

            String totalTimeMessage = "Overall took " + (System.currentTimeMillis() - start) + " milliseconds to process " +
                    "the web layer plus \"data layer and database with async calls\" benchmarking test";

            return dataLayerResponses.getSuccess() + "\n" + thisLayerProcessingMessage + "\n" + totalTimeMessage;
        });

        return asyncGenerator.awaitAll(totalResults).thenReturn();
    }

    private RestResult<String> callDatabaseInteractionDataLayerEndpoint(@RequestParam(name = "data_layer_process_time_millis", defaultValue = "100") long dataLayerProcessTimeMillis) {
        return getWithRestResultAnonymous("/monitoring/benchmark/to-database?process_time_millis=" + dataLayerProcessTimeMillis, String.class);
    }

    @GetMapping("/thymeleaf")
    public String isolatedThymeleaf(@RequestParam(name = "rows", defaultValue = "100") long rows,
        @RequestParam(name = "cols", defaultValue = "100") long cols,
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

    private RestResult<String> callIsolatedDataLayerEndpoint(@RequestParam(name = "data_layer_process_time_millis", defaultValue = "100") long dataLayerProcessTimeMillis) {
        return getWithRestResultAnonymous("/monitoring/benchmark/isolated?process_time_millis=" + dataLayerProcessTimeMillis, String.class);
    }
}
