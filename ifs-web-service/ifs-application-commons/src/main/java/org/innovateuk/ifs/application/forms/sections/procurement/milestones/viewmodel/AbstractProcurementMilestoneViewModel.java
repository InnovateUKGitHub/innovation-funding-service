package org.innovateuk.ifs.application.forms.sections.procurement.milestones.viewmodel;

import org.innovateuk.ifs.finance.resource.BaseFinanceResource;

import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.LongStream;

import static java.util.stream.Collectors.toList;

public abstract class AbstractProcurementMilestoneViewModel {

    private final List<Long> durations;
    private final BigInteger fundingAmount;

    public AbstractProcurementMilestoneViewModel(Long duration, BaseFinanceResource finance) {
        this.durations = Optional.ofNullable(duration)
                .map(d -> LongStream.rangeClosed(1, duration).boxed().collect(toList()))
                .orElse(Collections.emptyList());
        this.fundingAmount = finance.getTotalFundingSought().setScale(0, RoundingMode.HALF_UP).toBigInteger();
    }

    public List<Long> getDurations() {
        return durations;
    }

    public BigInteger getFundingAmount() {
        return fundingAmount;
    }

    public abstract boolean isReadOnly();
}