
package org.innovateuk.ifs;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.mockito.ArgumentMatcher;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static org.mockito.ArgumentMatchers.argThat;

/**
 * Custom Mockito matcher to leverage Java 8 lambdas
 *
 * @param <T>
 */
public class LambdaMatcher<T> extends BaseMatcher<T> implements ArgumentMatcher<T> {

    private final Predicate<T> matcher;
    private Optional<String> description = Optional.empty();

    public LambdaMatcher(Predicate<T> predicate) {
        this.matcher = value -> {

            if (value == null) {
                return false;
            }

            try {
                return predicate.test(value);
            } catch (AssertionError e) {
                this.description = Optional.of(e.getMessage());
                return false;
            }
        };
    }


    @SuppressWarnings("unchecked")
    @Override
    public boolean matches(Object argument) {
        return matcher.test((T) argument);
    }

    @Override
    public void describeTo(Description description) {
        this.description.ifPresent(description::appendText);
    }

    public static <T> LambdaMatcher<T> lambdaMatches(Predicate<T> predicate) {
        return new LambdaMatcher(predicate);
    }

    /**
     * Creates a new matcher that takes a lambda predicate and can optionally return false or throw an assertion error if
     * the parameter doesn't match the predicate test.  A null check is also performed as per all standard Mockito matchers.
     *
     * @param predicate
     * @param <T>
     * @return
     */
    public static <T> T createLambdaMatcher(Predicate<T> predicate) {
        return argThat(lambdaMatches(predicate));
    }

    /**
     * Creates a new matcher that takes a lambda consumer and can throw an assertion error in the consumer if it
     * doesn't pass the test.  A null check is also performed as per all standard Mockito matchers.
     *
     * @param consumer
     * @param <T>
     * @return
     */
    public static <T> T createLambdaMatcher(Consumer<T> consumer) {
        return argThat(lambdaMatches((T argument) -> {
            consumer.accept(argument);
            return true;
        }));
    }
}
