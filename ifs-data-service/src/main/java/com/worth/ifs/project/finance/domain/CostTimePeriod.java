package com.worth.ifs.project.finance.domain;

import com.worth.ifs.project.finance.resource.TimeUnit;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import static com.worth.ifs.util.MapFunctions.asMap;

/**
 * Entity representing a span of time for which a cost is recorded
 */
@Entity
public class CostTimePeriod {

    private static Map<TimeUnit, ChronoUnit> TIME_UNIT_TO_CHRONO_UNIT = asMap(
            TimeUnit.DAY, ChronoUnit.DAYS,
            TimeUnit.MONTH, ChronoUnit.MONTHS,
            TimeUnit.YEAR, ChronoUnit.YEARS);

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(optional = false)
    private Cost cost;

    @Column(nullable = false)
    private Integer offsetAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TimeUnit offsetUnit;

    @Column(nullable = false)
    private Integer durationAmount;

    @Enumerated(EnumType.STRING)
    private TimeUnit durationUnit;

    public CostTimePeriod() {
        // for ORM use
    }

    public CostTimePeriod(Cost cost, Integer offsetAmount, TimeUnit offsetUnit, Integer durationAmount, TimeUnit durationUnit) {
        this.cost = cost;
        this.offsetAmount = offsetAmount;
        this.offsetUnit = offsetUnit;
        this.durationAmount = durationAmount;
        this.durationUnit = durationUnit;
    }

    public CostTimePeriod(Integer offsetAmount, TimeUnit offsetUnit, Integer durationAmount, TimeUnit durationUnit) {
        this(null, offsetAmount, offsetUnit, durationAmount, durationUnit);
    }

    public CostTimePeriod(Integer durationAmount, TimeUnit durationUnit) {
        this(null, 0, TimeUnit.DAY, durationAmount, durationUnit);
    }

    public Long getId() {
        return id;
    }

    public Cost getCost() {
        return cost;
    }

    public void setCost(Cost cost) {
        this.cost = cost;
    }

    public Integer getOffsetAmount() {
        return offsetAmount;
    }

    public TimeUnit getOffsetUnit() {
        return offsetUnit;
    }

    public Integer getDurationAmount() {
        return durationAmount;
    }

    public TimeUnit getDurationUnit() {
        return durationUnit;
    }

    public LocalDateTime getStartDateTime(LocalDateTime from) {
        return from.plus(offsetAmount, offsetAsChronoUnit());
    }

    public LocalDateTime getEndDateTime(LocalDateTime from) {
        return getStartDateTime(from).plus(durationAmount, durationAsChronoUnit());
    }

    public LocalDate getStartDate(LocalDate from) {
        return from.plus(offsetAmount, offsetAsChronoUnit());
    }

    public LocalDate getEndDate(LocalDate from) {
        return getStartDate(from).plus(durationAmount, durationAsChronoUnit());
    }

    private ChronoUnit offsetAsChronoUnit() {
        return TIME_UNIT_TO_CHRONO_UNIT.get(offsetUnit);
    }

    private ChronoUnit durationAsChronoUnit() {
        return TIME_UNIT_TO_CHRONO_UNIT.get(durationUnit);
    }
}
