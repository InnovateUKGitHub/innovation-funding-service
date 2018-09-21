package org.innovateuk.ifs.async.controller.endtoend;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.async.annotations.AsyncMethod;
import org.innovateuk.ifs.async.exceptions.AsyncException;
import org.innovateuk.ifs.async.generation.AsyncAdaptor;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.innovateuk.ifs.commons.error.CommonErrors.forbiddenError;
import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;

/**
 * Controller to use in {@link EndToEndAsyncControllerTestController}
 */
@Controller
public class EndToEndAsyncControllerTestController extends AsyncAdaptor {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private EndToEndAsyncControllerTestService testService;

    @AsyncMethod
    @GetMapping
    public String asyncMethod(Long applicationId, Model model) {
        return executeNestedFutureWork(applicationId, model);
    }

    @GetMapping("/nonasync")
    public String nonAsyncMethod(Long applicationId, Model model) {
        return executeNestedFutureWork(applicationId, model);
    }

    @AsyncMethod
    @GetMapping("/2")
    public String methodThatThrowsExceptionWithinNestedFuture() {

        async(() -> {

            CompletableFuture<Void> childFuture = async(() -> {});

            @SuppressWarnings("unused")
            CompletableFuture<Void> aChildFutureToBeCancelled = async(EndToEndAsyncControllerIntegrationTest::sleepQuietlyForRandomInterval);

            awaitAll(childFuture).thenAccept(done ->
                    async(() -> restFailure(forbiddenError()).getSuccess()));
        });

        return null;
    }

    @AsyncMethod
    @GetMapping("/3")
    public String methodThatThrowsThrowableWithinNestedFuture() {

        async(() -> {

            CompletableFuture<Void> childFuture = async(() -> {});

            @SuppressWarnings("unused")
            CompletableFuture<Void> aChildFutureToBeCancelled = async(EndToEndAsyncControllerIntegrationTest::sleepQuietlyForRandomInterval);

            awaitAll(childFuture).thenAccept(done ->
                    async(() -> {
                        assert false;
                    }));
        });

        return null;
    }

    @AsyncMethod
    @GetMapping("/4")
    public String methodThatThrowsAsyncExceptionWithinNestedFuture() {

        async(() -> {

            CompletableFuture<Void> childFuture = async(() -> {});

            @SuppressWarnings("unused")
            CompletableFuture<Void> aChildFutureToBeCancelled = async(EndToEndAsyncControllerIntegrationTest::sleepQuietlyForRandomInterval);

            awaitAll(childFuture).thenAccept(done ->
                    async(() -> {
                        throw new AsyncException("Root cause is an AsyncException!", null);
                    }));
        });

        return null;
    }

    private String executeNestedFutureWork(Long applicationId, Model model) {
        CompletableFuture<ApplicationResource> applicationResult = async(() ->
                applicationService.getById(applicationId));

        CompletableFuture<OrganisationResource> leadOrganisationResult = async(() ->
                applicationService.getLeadOrganisation(applicationId));

        CompletableFuture<CompetitionResource> competitionResult = awaitAll(applicationResult).thenApply(application ->
                competitionRestService.getCompetitionById(application.getCompetition()).getSuccess());

        awaitAll(applicationResult, competitionResult).thenAccept((application, competition) -> {

            String applicationSector = application.getInnovationArea().getSectorName();
            String competitionActivityCode = competition.getActivityCode();

            model.addAttribute("applicationSectorAndCompetitionCode", applicationSector + "-" + competitionActivityCode);
        });

        awaitAll(leadOrganisationResult).thenAccept(organisation -> {

            model.addAttribute("leadOrganisationUsers", organisation.getUsers());

            testService.doSomeHiddenAsyncActivities(model, organisation.getUsers());

            testService.doSomeHiddenButSafeBlockingAsyncActivities(model, organisation.getUsers());

            CompletableFuture<List<String>> explicitlyAsyncResults =
                    testService.doExplicitAsyncActivities(organisation.getUsers());

            model.addAttribute("explicitlyAsyncResultsAddedAsAFutureToTheModel", explicitlyAsyncResults);

            awaitAll(explicitlyAsyncResults).thenAccept(results ->
                    model.addAttribute("explicitlyAsyncResultsAddedWhenFutureResolved", results));
        });

        CompletableFuture<String> pageResult = awaitAll(applicationResult).thenApply(application -> "/application/" + application.getName());
        return awaitAll(pageResult).thenReturn();
    }
}
