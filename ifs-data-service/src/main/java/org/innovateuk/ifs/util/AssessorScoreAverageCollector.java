package org.innovateuk.ifs.util;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class AssessorScoreAverageCollector implements Collector<String, AssessorScoreAverageCollector.ScoreAccumulator, BigDecimal> {

    @Override
    public Supplier<ScoreAccumulator> supplier() {
        return ScoreAccumulator::new;
    }

    @Override
    public BiConsumer<ScoreAccumulator, String> accumulator() {
        return ScoreAccumulator::add;
    }

    @Override
    public BinaryOperator<ScoreAccumulator> combiner() {
        return ScoreAccumulator::combine;
    }

    @Override
    public Function<ScoreAccumulator, BigDecimal> finisher() {
        return ScoreAccumulator::getAverage;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Collections.emptySet();
    }

    static class ScoreAccumulator {
        private BigDecimal sum = BigDecimal.ZERO;
        private BigDecimal count = BigDecimal.ZERO;

        public ScoreAccumulator() {
        }

        public BigDecimal getSum() {
            return sum;
        }

        public BigDecimal getCount() {
            return count;
        }

        public ScoreAccumulator(BigDecimal sum, BigDecimal count) {
            this.sum = sum;
            this.count = count;
        }

        BigDecimal getAverage() {
            return BigDecimal.ZERO.compareTo(count) == 0 ?
                    BigDecimal.ZERO :
                    sum.divide(count, 0, BigDecimal.ROUND_HALF_UP);
        }

        ScoreAccumulator combine(ScoreAccumulator another) {
            return new ScoreAccumulator(
                    sum.add(another.getSum()),
                    count.add(another.getCount())
            );
        }

        void add(String successRate) {
            count = count.add(BigDecimal.ONE);
            sum = sum.add(new BigDecimal(successRate));
        }
    }
}
