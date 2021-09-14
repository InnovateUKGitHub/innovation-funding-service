package org.innovateuk.ifs.async.controller.endtoend;

import com.jayway.awaitility.Awaitility;
import com.jayway.awaitility.core.ConditionTimeoutException;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.async.exceptions.AsyncException;
import org.innovateuk.ifs.async.generation.AsyncFuturesHolder;
import org.innovateuk.ifs.async.util.AsyncAllowedThreadLocal;
import org.innovateuk.ifs.commons.BaseIntegrationTest;
import org.innovateuk.ifs.commons.exception.ForbiddenActionException;
import org.innovateuk.ifs.commons.security.authentication.user.UserAuthentication;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.service.DefaultRestTemplateAdaptor;
import org.innovateuk.ifs.user.resource.ManageUserPageResource;
import org.innovateuk.ifs.user.resource.ManageUserResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.Model;
import org.springframework.validation.support.BindingAwareModelMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.commons.security.ProxyUtils.unwrapProxy;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.user.builder.ManageUserResourceBuilder.newManageUserResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST;

/**
 * An end-to-end test scenario for using the async mechanism provided in the {@link org.innovateuk.ifs.async} package.
 *
 * This test simulates a Controller performing async work itself, and in addition working with Services that
 * perform a variety of async behaviours (some better practice than others).
 *
 * This test proves that when a variety of Futures are generated inside the Controller, they are all allowed to
 * resolve successfully before the Controller method is allowed to finish.
 *
 * This test mocks out contact with the data layer by swapping the RestTemplate for a mock one.  Results from the mocked
 * out data layer return after a randomised delay to simulate real latency, and thus this allows our Futures to execute
 * in a realistic, less predictable order of execution.
 */
@DirtiesContext
public class EndToEndAsyncControllerIntegrationTest extends BaseIntegrationTest {

    private enum ExpectedExecutionBehaviour {
        ASYNC_THREADS,
        MAIN_THREAD
    }

    @Autowired
    private EndToEndAsyncControllerTestController controller;

    @Autowired
    private GenericApplicationContext applicationContext;

    @Value("${ifs.data.service.rest.baseURL}")
    private String baseRestUrl;

    private RestTemplate restTemplateMock;

    private UserResource loggedInUser = newUserResource().build();

    private String requestCachingUuid = "1234567890";

    /**
     * Swap out the real RestTemplate temporarily for a mock one so that we can mock out communication with the data
     * layer
     */
    @Before
    public void swapOutRestTemplateForMock() {

        DefaultRestTemplateAdaptor restTemplateAdaptor = getRestTemplateAdaptorFromApplicationContext();

        restTemplateMock = mock(RestTemplate.class);
        ReflectionTestUtils.setField(restTemplateAdaptor, "restTemplate", restTemplateMock);
    }

    /**
     * Setup ThreadLocals that we would expect to have set on the main Thread prior to the Controller being called
     */
    @Before
    public void setupNewWorkerThreadLocals() {
        setLoggedInUser(loggedInUser);
        ServletRequestAttributes requestAttributes = new ServletRequestAttributes(new MockHttpServletRequest());
        RequestContextHolder.setRequestAttributes(requestAttributes);
        requestAttributes.setAttribute("REQUEST_UUID_KEY", requestCachingUuid, SCOPE_REQUEST);
        AsyncAllowedThreadLocal.clearAsyncAllowed();
    }

    /**
     * Cleanup ThreadLocals that we would expect to have set on the main Thread prior to the Controller being called
     */
    @After
    public void cleanupNewWorkerThreadLocals() {
        setLoggedInUser(null);
        RequestContextHolder.setRequestAttributes(null);
        AsyncAllowedThreadLocal.clearAsyncAllowed();
    }

    @Test
    public void testEndToEndControllerAsyncBehaviour() {
        assertFutureWorkCompletsSuccessfullyWhenControllerCompletes(controller::asyncMethod, ExpectedExecutionBehaviour.ASYNC_THREADS);
    }

    @Test
    public void testEndToEndControllerAsyncBehaviourExecutedInMainThreadWhenAsyncDisabled() {
        assertFutureWorkCompletsSuccessfullyWhenControllerCompletes(controller::nonAsyncMethod, ExpectedExecutionBehaviour.MAIN_THREAD);
    }

    private <T> void assertFutureWorkCompletsSuccessfullyWhenControllerCompletes(
            BiFunction<Long, Model, String> methodUnderTest,
            ExpectedExecutionBehaviour executionBehaviour) {

        BindingAwareModelMap model = new BindingAwareModelMap();

        CompetitionResource competition = newCompetitionResource().
                withId(456L).
                withActivityCode("The Activity Code").
                build();

        ApplicationResource application = newApplicationResource().
                withId(123L).
                withCompetition(competition.getId()).
                withName("My Application").
                withInnovationArea(newInnovationAreaResource().withSectorName("The Sector").build()).
                build();

        ManageUserPageResource page = new ManageUserPageResource(1L, 1, newManageUserResource()
                .withId(2L, 4L, 6L)
                .build(3), 1, 1);

        when(restTemplateMock.exchange(eq(baseRestUrl + "/application/123"), eq(HttpMethod.GET), isA(HttpEntity.class),
                eq(ApplicationResource.class))).thenAnswer(invocation -> dataLayerResponse(application, executionBehaviour));

        when(restTemplateMock.exchange(eq(baseRestUrl + "/competition/456"), eq(HttpMethod.GET), isA(HttpEntity.class),
                eq(CompetitionResource.class))).thenAnswer(invocation -> dataLayerResponse(competition, executionBehaviour));

        when(restTemplateMock.exchange(eq(baseRestUrl + "/user/active?filter=&page=1&size=10"), eq(HttpMethod.GET), isA(HttpEntity.class),
                eq(ManageUserPageResource.class))).thenAnswer(invocation -> dataLayerResponse(page, executionBehaviour));

        // set up expectations for the retrieval of the Lead Organisation
        List<Long> users = page.getContent().stream().map(ManageUserResource::getId).collect(Collectors.toList());

        //
        // call the method under test
        //
        String result = methodUnderTest.apply(application.getId(), model);

        //
        // assert we get a decision on the correct page to display
        //
        assertThat(result, equalTo("/application/My Application"));

        //
        // assert that the values added directly to the model in the Controller itself are present
        //
        assertThat(model.get("applicationSectorAndCompetitionCode"), equalTo("The Sector-The Activity Code"));
        assertThat(model.get("users"), equalTo(users));

        //
        // and assert that the values added as a Future directly to the model in the Controller have resolved correctly
        //
        assertThat((List<String>) model.get("explicitlyAsyncResultsAddedAsAFutureToTheModel"), containsInAnyOrder(
                "doExplicitAsyncActivities2ThenAmended",
                "doExplicitAsyncActivities4ThenAmended",
                "doExplicitAsyncActivities6ThenAmended"));

        //
        // assert that the values added by the hidden async activities in
        // {@link EndToEndAsyncControllerTestService#doSomeHiddenAsyncActivities} have resolved
        //
        assertThat(model.get("doSomeHiddenAsyncActivitiesUser2"), equalTo(2L));
        assertThat(model.get("doSomeHiddenAsyncActivitiesUser4"), equalTo(4L));
        assertThat(model.get("doSomeHiddenAsyncActivitiesUser6"), equalTo(6L));

        //
        // assert that the values added by the hidden async activities in
        // {@link EndToEndAsyncControllerTestService#doSomeHiddenButSafeBlockingAsyncActivities} have resolved
        //
        assertThat(model.get("doSomeHiddenButSafeBlockingAsyncActivitiesUser2"), equalTo(2L));
        assertThat(model.get("doSomeHiddenButSafeBlockingAsyncActivitiesUser4"), equalTo(4L));
        assertThat(model.get("doSomeHiddenButSafeBlockingAsyncActivitiesUser6"), equalTo(6L));
    }

    @Test
    public void testHandlingExceptionsWithinAsyncBlocks() {

        try {
            controller.methodThatThrowsExceptionWithinNestedFuture();
            fail("Controller should have thrown a ForbiddenActionException");

        } catch (ForbiddenActionException e) {

            // expected behaviour.  Now assert that any ongoing Futures were cleared after an exception was thrown
            assertThat(AsyncFuturesHolder.getFuturesOrInitialise(), empty());
        }
    }

    @Test
    public void testHandlingAsyncExceptionWithinAsyncBlocks() {

        try {
            controller.methodThatThrowsAsyncExceptionWithinNestedFuture();
            fail("Controller should have thrown an AsyncException");

        } catch (AsyncException e) {

            // expected behaviour - Now assert that any ongoing Futures were cleared after an exception was thrown
            assertThat(AsyncFuturesHolder.getFuturesOrInitialise(), empty());
        }
    }

    private <T> ResponseEntity<T> entity(T result) {
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    /**
     * This method asserts that our async work is being done on the expected Threads, and that the
     * expected ThreadLocal values to support the RestTemplateAdaptors are present.
     *
     */
    private <T> ResponseEntity<T> dataLayerResponse(T response, ExpectedExecutionBehaviour executionBehaviour) {

        assertThat(((UserAuthentication) SecurityContextHolder.getContext().getAuthentication()).getDetails(), sameInstance(loggedInUser));
        assertThat(RequestContextHolder.getRequestAttributes().getAttribute("REQUEST_UUID_KEY", SCOPE_REQUEST),
                equalTo(requestCachingUuid));

        if (ExpectedExecutionBehaviour.ASYNC_THREADS.equals(executionBehaviour)) {
            assertThat(Thread.currentThread().getName(), startsWith("IFS-Async-Executor-"));
            return delayedResponse(entity(response));
        } else {
            assertThat(Thread.currentThread().getName(), not(startsWith("IFS-Async-Executor-")));
            return nonAsyncResponse(entity(response));
        }
    }

    /**
     * This method simulates a random time for responses to return from the data layer to ensure that our Futures execute
     * in different orders, as in real life.
     */
    private <T> T delayedResponse(T response) {

        sleepQuietlyForRandomInterval();
        return response;
    }

    /**
     * This method asserts that our async work is being done on the main Thread because async is not currently allowed.
     */
    private <T> T nonAsyncResponse(T response) {
        return response;
    }

    // a SonarQube-compliant way to sleep the Thread for an interval
    static void sleepQuietlyForRandomInterval() {
        try {
            int randomMillis = (int) (Math.random() * 10) + 10;

            Awaitility.await().pollDelay(randomMillis, TimeUnit.MILLISECONDS).
                    timeout(randomMillis + 100, TimeUnit.MILLISECONDS).
                    until(() -> false);

        } catch (ConditionTimeoutException e) {
            // expected behaviour when our delay is over
        }
    }

    private DefaultRestTemplateAdaptor getRestTemplateAdaptorFromApplicationContext() {
        return (DefaultRestTemplateAdaptor) unwrapProxy(applicationContext.getBean(DefaultRestTemplateAdaptor.class));
    }

}
