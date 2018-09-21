package org.innovateuk.ifs.async.controller.endtoend;

import org.innovateuk.ifs.async.generation.AsyncAdaptor;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.innovateuk.ifs.async.controller.endtoend.EndToEndAsyncControllerIntegrationTest.sleepQuietlyForRandomInterval;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Service to use in {@link EndToEndAsyncControllerTestController}
 */
@Service
public class EndToEndAsyncControllerTestService extends AsyncAdaptor {

    /**
     * This method performs some hidden async activities.  That is, there is no evidence externally that this method
     * will be performing async work and returning prior to the work finishing.
     *
     * This isn't recommended practice as the behaviour could be inconsistent to client code.  However, it is in here
     * to prove that the Controller calls will block until any Futures here have completed.
     */
    public void doSomeHiddenAsyncActivities(Model model, List<Long> users) {
        users.forEach(userId -> async(() -> {
            sleepQuietlyForRandomInterval();
            model.addAttribute("doSomeHiddenAsyncActivitiesUser" + userId, userId);
        }));
    }

    /**
     * This method, like {@link EndToEndAsyncControllerTestService#doSomeHiddenAsyncActivities}, performs some hidden
     * async work but, in a safer way than the other method, this method blocks on all of these Futures (and any
     * descendents that they may create) to finish prior to returning, thus allowing client code to use this safely
     * without needing to know of the asynchronous work hiding inside.
     *
     * This is safe to use provided that the specific order of execution of async calls created in here does not matter.
     *
     * This could be a good way to work with adding parallelisation to legacy code that is already in use by several
     * pieces of client code.
     */
    public void doSomeHiddenButSafeBlockingAsyncActivities(Model model, List<Long> users) {

        List<CompletableFuture<Model>> futuresList = simpleMap(users, userId -> async(() -> {
            sleepQuietlyForRandomInterval();
            return model.addAttribute("doSomeHiddenButSafeBlockingAsyncActivitiesUser" + userId, userId);
        }));

        waitForFuturesAndChildFuturesToCompleteFrom(futuresList);
    }

    /**
     * This method is explicit in its signature of being asynchronous because it is returning a Future, and is probably
     * the preferred way of writing new code that can make use of parallelisation.  The client code can then decide how
     * best to use that Future.
     */
    public CompletableFuture<List<String>> doExplicitAsyncActivities(List<Long> users) {

        List<CompletableFuture<String>> createNewStringsFuture = simpleMap(users, user -> async(() -> {
            sleepQuietlyForRandomInterval();
            return "doExplicitAsyncActivities" + user;
        }));

        return awaitAll(createNewStringsFuture).thenApply(futureResults -> {
            sleepQuietlyForRandomInterval();
            List<String> newStrings = (List<String>) futureResults;
            return simpleMap(newStrings, string -> string + "ThenAmended");
        });
    }
}
