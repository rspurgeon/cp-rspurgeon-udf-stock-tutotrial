package io.confluent.tutorials.udf;

import io.confluent.ksql.function.udaf.Udaf;
import io.confluent.ksql.function.udaf.UdafDescription;
import io.confluent.ksql.function.udaf.UdafFactory;

import java.util.Optional;

@UdafDescription(name = "ema", description = "exponential moving average aggregate function")
public class EmaUdaf {
    @UdafFactory(description = "sums double")
    public static Udaf<Double, Double> createEmaDouble() {
        return new Udaf<Double, Double>() {

            private Optional<Double> last = Optional.empty();
            private final int periods = 10; // todo: make parameter
            private final double k = 2 / periods;

            @Override
            public Double initialize() {
                return null;
            }

            @Override
            public Double aggregate(final Double val, final Double aggregate) {
                if (last.isPresent()) {
                    last = Optional.of(val * k + last.get() * (1-k));
                    return last.get();
                }
                else {
                    last = Optional.of(val);
                    return val;
                }
            }

            @Override
            public Double merge(final Double aggOne, final Double aggTwo) {
                return (aggOne + aggTwo) / 2; //todo: understand merge scenarios
            }
        };
  }
}

