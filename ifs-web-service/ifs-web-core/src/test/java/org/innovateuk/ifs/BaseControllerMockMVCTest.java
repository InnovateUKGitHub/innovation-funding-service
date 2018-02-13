package org.innovateuk.ifs;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.async.generation.AsyncFuturesGenerator;
import org.innovateuk.ifs.async.util.CompletableFutureTuple1Handler;
import org.innovateuk.ifs.async.util.CompletableFutureTuple2Handler;
import org.innovateuk.ifs.async.util.CompletableFutureTuple3Handler;
import org.innovateuk.ifs.async.util.CompletableFutureTupleNHandler;
import org.innovateuk.ifs.commons.security.UserAuthenticationService;
import org.innovateuk.ifs.commons.security.authentication.user.UserAuthentication;
import org.innovateuk.ifs.commons.service.ExceptionThrowingFunction;
import org.innovateuk.ifs.controller.CustomFormBindingControllerAdvice;
import org.innovateuk.ifs.controller.LoggedInUserMethodArgumentResolver;
import org.innovateuk.ifs.controller.ValidationHandlerMethodArgumentResolver;
import org.innovateuk.ifs.exception.ErrorControllerAdvice;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.invite.formatter.RejectionReasonFormatter;
import org.innovateuk.ifs.user.formatter.EthnicityFormatter;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.util.*;
import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.stubbing.Answer;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.ExceptionHandlerMethodResolver;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.*;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.util.CollectionFunctions.combineLists;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * This is the base class for testing Controllers using MockMVC in addition to standard Mockito mocks.  Using MockMVC
 * allows Controllers to be tested via their routes and their responses' HTTP responses tested also.
 */
public abstract class BaseControllerMockMVCTest<ControllerType> extends BaseUnitTest {
    public static final Log LOG = LogFactory.getLog(BaseControllerMockMVCTest.class);

    @InjectMocks
    protected ControllerType controller = supplyControllerUnderTest();

    protected MockMvc mockMvc;

    protected abstract ControllerType supplyControllerUnderTest();

    @Mock
    private AsyncFuturesGenerator futuresGeneratorMock;

    @Before
    public void setUp() {

        super.setup();

        mockMvc = setupMockMvc(controller, () -> getLoggedInUser(), env, messageSource);

        setLoggedInUser(loggedInUser);

        setFutureExpectations();
    }

    public static <ControllerType> MockMvc setupMockMvc(ControllerType controller, Supplier<UserResource> loggedInUserSupplier, Environment environment, MessageSource messageSource) {

        CookieLocaleResolver localeResolver = new CookieLocaleResolver();
        localeResolver.setCookieDomain("domain");

        FormattingConversionService formattingConversionService = new DefaultFormattingConversionService();
        formattingConversionService.addFormatter(new RejectionReasonFormatter());
        formattingConversionService.addFormatter(new EthnicityFormatter());

        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setConversionService(formattingConversionService)
                .setControllerAdvice(
                        new ErrorControllerAdvice(),
                        new CustomFormBindingControllerAdvice()
                )
                .addFilter(new CookieFlashMessageFilter())
                .setLocaleResolver(localeResolver)
                .setHandlerExceptionResolvers(createExceptionResolver(environment, messageSource))
                .setCustomArgumentResolvers(
                        new ValidationHandlerMethodArgumentResolver(),
                        getLoggedInUserMethodArgumentResolver(loggedInUserSupplier)

                )
                .setViewResolvers(viewResolver())
                .build();

        return mockMvc;
    }

    private static LoggedInUserMethodArgumentResolver getLoggedInUserMethodArgumentResolver(Supplier<UserResource> loggedInUserSupplier) {

        LoggedInUserMethodArgumentResolver argumentResolver = new LoggedInUserMethodArgumentResolver();

        ReflectionTestUtils.setField(argumentResolver, "userAuthenticationService", new UserAuthenticationService() {

            @Override
            public Authentication getAuthentication(HttpServletRequest request) {
                return getAuthentication(request, false);
            }

            @Override
            public UserResource getAuthenticatedUser(HttpServletRequest request) {
                return getAuthenticatedUser(request, false);
            }

            @Override
            public Authentication getAuthentication(HttpServletRequest request, boolean expireCache) {
                return new UserAuthentication(loggedInUserSupplier.get());
            }

            @Override
            public UserResource getAuthenticatedUser(HttpServletRequest request, boolean expireCache) {
                return loggedInUserSupplier.get();
            }
        });

        return argumentResolver;
    }

    public static ExceptionHandlerExceptionResolver createExceptionResolver(Environment env, MessageSource messageSource) {
        ExceptionHandlerExceptionResolver exceptionResolver = new ExceptionHandlerExceptionResolver() {
            protected ServletInvocableHandlerMethod getExceptionHandlerMethod(HandlerMethod handlerMethod, Exception exception) {
                Method method = new ExceptionHandlerMethodResolver(ErrorControllerAdvice.class).resolveMethod(exception);
                return new ServletInvocableHandlerMethod(new ErrorControllerAdvice(env, messageSource), method);
            }
        };
        exceptionResolver.afterPropertiesSet();
        return exceptionResolver;
    }

    protected void setLoggedInUserAuthentication(UserAuthentication user) {
        SecurityContextHolder.getContext().setAuthentication(user);
    }

    /**
     * Get the user on the Spring Security ThreadLocals
     */
    protected UserResource getLoggedInUser() {
        UserAuthentication authentication = (UserAuthentication) SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getDetails() : null;
    }

    /**
     * Set a user on the Spring Security ThreadLocals
     *
     * @param user
     */
    protected void setLoggedInUser(UserResource user) {
        SecurityContextHolder.getContext().setAuthentication(new UserAuthentication(user));
    }

    protected void logoutCurrentUser() {
        setLoggedInUser(null);
    }

    /**
     * Setup expectations around the various {@link AsyncFuturesGenerator} methods that generate CompletableFutures
     * and helper classes
     */
    private void setFutureExpectations() {
        setAsyncMethodExpectations();
        setupAwaitAllMethodExpectations();
    }

    /**
     * Setup expectations around the various {@link AsyncFuturesGenerator#async} methods such that they will invoke
     * their callbacks immediately and return a completed future with the callback's result in it  (or simply invoke
     * the callback if it has no return value)
     */
    private void setAsyncMethodExpectations() {

        Answer futureSupplierAnswer = invocation -> {
            ExceptionThrowingSupplier supplier = (ExceptionThrowingSupplier) invocation.getArguments()[0];
            return CompletableFuture.completedFuture(supplier.get());
        };

        Answer<Object> futureConsumerAnswer = invocation -> {
            ExceptionThrowingRunnable supplier = (ExceptionThrowingRunnable) invocation.getArguments()[0];
            supplier.run();
            return CompletableFuture.completedFuture(null);
        };

        when(futuresGeneratorMock.async(isA(ExceptionThrowingSupplier.class))).thenAnswer(futureSupplierAnswer);
        when(futuresGeneratorMock.async(isA(String.class), isA(ExceptionThrowingSupplier.class))).thenAnswer(futureSupplierAnswer);
        when(futuresGeneratorMock.async(isA(ExceptionThrowingRunnable.class))).thenAnswer(futureConsumerAnswer);
        when(futuresGeneratorMock.async(isA(String.class), isA(ExceptionThrowingRunnable.class))).thenAnswer(futureConsumerAnswer);
    }

    /**
     * Setup expectations around the various {@link AsyncFuturesGenerator#awaitAll} methods such that they will invoke
     * their callbacks immediately with the arguments they require and return a completed future with the callback's
     * result in it (or simply invoke the callback if it has no return value)
     */
    private void setupAwaitAllMethodExpectations() {
        setupAwaitAllWithOneFutureExpectations();
        setupAwaitAllWithTwoMethodsExpectations();
        setupAwaitAllWithThreeFuturesExpectations();
        setupAwaitAllWithNFuturesExpectations();
    }

    private void setupAwaitAllWithOneFutureExpectations() {
        // expectations for when awaitAll() is called with a single future
        Answer<Object> tuple1HandlerAnswer = invocation -> {

            CompletableFuture<?> future1 = (CompletableFuture<?>) invocation.getArguments()[0];

            CompletableFutureTuple1Handler tupleFutureHandler = mock(CompletableFutureTuple1Handler.class);

            // expectations for when thenApply() is called
            when(tupleFutureHandler.thenApply(isA(Function.class))).thenAnswer(thenApplyInvocation -> {

                Function function = (Function) thenApplyInvocation.getArguments()[0];
                return CompletableFuture.completedFuture(function.apply(future1.get()));
            });

            // expectations for when thenAccept() is called
            when(tupleFutureHandler.thenAccept(isA(ExceptionThrowingConsumer.class))).thenAnswer(thenAnswerInvocation -> {

                Consumer consumer = (Consumer) thenAnswerInvocation.getArguments()[0];
                consumer.accept(future1.get());
                return CompletableFuture.completedFuture(null);
            });

            // expectations for when thenReturn() is called
            when(tupleFutureHandler.thenReturn()).thenReturn(future1.get());

            return tupleFutureHandler;
        };

        when(futuresGeneratorMock.awaitAll(isA(CompletableFuture.class))).thenAnswer(tuple1HandlerAnswer);
        when(futuresGeneratorMock.awaitAll(isA(String.class), isA(CompletableFuture.class))).thenAnswer(tuple1HandlerAnswer);
    }

    private void setupAwaitAllWithTwoMethodsExpectations() {
        // expectations for when awaitAll() is called with 2 futures
        Answer<Object> tuple2HanderAnswer = invocation -> {

            CompletableFuture<?> future1 = (CompletableFuture<?>) invocation.getArguments()[0];
            CompletableFuture<?> future2 = (CompletableFuture<?>) invocation.getArguments()[1];

            CompletableFutureTuple2Handler tupleFutureHandler = mock(CompletableFutureTuple2Handler.class);

            // expectations for when thenApply() is called
            when(tupleFutureHandler.thenApply(isA(BiFunction.class))).thenAnswer(thenApplyInvocation -> {

                BiFunction function = (BiFunction) thenApplyInvocation.getArguments()[0];
                return CompletableFuture.completedFuture(function.apply(future1.get(), future2.get()));
            });

            // expectations for when thenAccept() is called
            when(tupleFutureHandler.thenAccept(isA(BiConsumer.class))).thenAnswer(thenAnswerInvocation -> {

                BiConsumer consumer = (BiConsumer) thenAnswerInvocation.getArguments()[0];
                consumer.accept(future1.get(), future2.get());
                return CompletableFuture.completedFuture(null);
            });

            // expectations for when thenReturn() is called
            when(tupleFutureHandler.thenReturn()).thenReturn(Pair.of(future1.get(), future2.get()));

            return tupleFutureHandler;
        };

        when(futuresGeneratorMock.awaitAll(isA(CompletableFuture.class), isA(CompletableFuture.class))).thenAnswer(tuple2HanderAnswer);
        when(futuresGeneratorMock.awaitAll(isA(String.class), isA(CompletableFuture.class), isA(CompletableFuture.class))).thenAnswer(tuple2HanderAnswer);
    }

    private void setupAwaitAllWithThreeFuturesExpectations() {
        // expectations for when awaitAll() is called with 3 futures
        Answer<Object> tuple3HandlerAnswer = invocation -> {

            CompletableFuture<?> future1 = (CompletableFuture<?>) invocation.getArguments()[0];
            CompletableFuture<?> future2 = (CompletableFuture<?>) invocation.getArguments()[1];
            CompletableFuture<?> future3 = (CompletableFuture<?>) invocation.getArguments()[2];

            CompletableFutureTuple3Handler tupleFutureHandler = mock(CompletableFutureTuple3Handler.class);

            // expectations for when thenApply() is called
            when(tupleFutureHandler.thenApply(isA(TriFunction.class))).thenAnswer(thenApplyInvocation -> {

                TriFunction function = (TriFunction) thenApplyInvocation.getArguments()[0];
                return CompletableFuture.completedFuture(function.apply(future1.get(), future2.get(), future3.get()));
            });

            // expectations for when thenAccept() is called
            when(tupleFutureHandler.thenAccept(isA(TriConsumer.class))).thenAnswer(thenAnswerInvocation -> {

                TriConsumer consumer = (TriConsumer) thenAnswerInvocation.getArguments()[0];
                consumer.accept(future1.get(), future2.get(), future3.get());
                return CompletableFuture.completedFuture(null);
            });

            // expectations for when thenReturn() is called
            when(tupleFutureHandler.thenReturn()).thenReturn(Triple.of(future1.get(), future2.get(), future2.get()));

            return tupleFutureHandler;
        };

        when(futuresGeneratorMock.awaitAll(isA(CompletableFuture.class), isA(CompletableFuture.class), isA(CompletableFuture.class))).thenAnswer(tuple3HandlerAnswer);
        when(futuresGeneratorMock.awaitAll(isA(String.class), isA(CompletableFuture.class), isA(CompletableFuture.class), isA(CompletableFuture.class))).thenAnswer(tuple3HandlerAnswer);
    }

    private void setupAwaitAllWithNFuturesExpectations() {

        // expectations for when awaitAll() is called with n futures with varargs
        Answer<Object> tupleNFromVarargsAnswer = invocation -> {

            CompletableFuture<?> future1 = (CompletableFuture<?>) invocation.getArguments()[0];
            CompletableFuture<?> future2 = (CompletableFuture<?>) invocation.getArguments()[1];
            CompletableFuture<?> future3 = (CompletableFuture<?>) invocation.getArguments()[2];
            CompletableFuture[] otherFutures = (CompletableFuture[]) invocation.getArguments()[3];

            List<CompletableFuture<?>> futures = combineLists(asList(future1, future2, future3), otherFutures);
            return createTupleNHandlerMockFromFutureList(futures);
        };

        when(futuresGeneratorMock.awaitAll(isA(CompletableFuture.class), isA(CompletableFuture.class), isA(CompletableFuture.class), isA(CompletableFuture[].class))).thenAnswer(tupleNFromVarargsAnswer);
        when(futuresGeneratorMock.awaitAll(isA(String.class), isA(CompletableFuture.class), isA(CompletableFuture.class), isA(CompletableFuture.class), isA(CompletableFuture[].class))).thenAnswer(tupleNFromVarargsAnswer);

        // expectations for when awaitAll() is called with n futures with a List
        Answer<Object> tupleNFromListAnswer = invocation -> {

            List<CompletableFuture<?>> futures = (List<CompletableFuture<?>>) invocation.getArguments()[0];
            return createTupleNHandlerMockFromFutureList(futures);
        };

        when(futuresGeneratorMock.awaitAll(isA(List.class))).thenAnswer(tupleNFromListAnswer);
        when(futuresGeneratorMock.awaitAll(isA(String.class), isA(List.class))).thenAnswer(tupleNFromListAnswer);
    }

    private Object createTupleNHandlerMockFromFutureList(List<CompletableFuture<?>> futures) {

        List<?> futureResults = simpleMap(futures, f -> {
            try {
                return f.get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        CompletableFutureTupleNHandler tupleFutureHandler = mock(CompletableFutureTupleNHandler.class);

        // expectations for when thenApply() is called
        when(tupleFutureHandler.thenApply(isA(ExceptionThrowingFunction.class))).thenAnswer(thenApplyInvocation -> {

            ExceptionThrowingFunction function = (ExceptionThrowingFunction) thenApplyInvocation.getArguments()[0];
            return CompletableFuture.completedFuture(function.apply(futureResults));
        });

        // expectations for when thenAccept() is called
        when(tupleFutureHandler.thenAccept(isA(ExceptionThrowingConsumer.class))).thenAnswer(thenAnswerInvocation -> {

            ExceptionThrowingConsumer consumer = (ExceptionThrowingConsumer) thenAnswerInvocation.getArguments()[0];
            consumer.accept(futureResults);
            return CompletableFuture.completedFuture(null);
        });

        return tupleFutureHandler;
    }
}
