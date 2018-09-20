package org.innovateuk.ifs.util;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Utility class to provide useful reusable Functions around Pairs throughout the codebase
 */
public final class PairFunctions {

	private PairFunctions() {}

    public static <R, T> Predicate<Pair<R, Optional<T>>> rightPairIsPresent() {
        return pair -> pair.getRight().isPresent();
    }

    public static <R, T> Function<Pair<R, Optional<T>>, T> presentRightPair() {
        return pair -> pair.getRight().get();
    }

    public static <R, T> Function<Pair<R, T>, R> leftPair() {
        return Pair::getLeft;
    }

}
