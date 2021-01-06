package org.innovateuk.ifs.application.forms.sections.procurement.milestones.viewmodel;

import org.innovateuk.ifs.finance.resource.BaseFinanceResource;

import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.LongStream;

import static java.util.stream.Collectors.toList;

public abstract class AbstractProcurementMilestoneViewModel {

    private final List<Long> durations;
    private final BigInteger fundingAmount;

    public AbstractProcurementMilestoneViewModel(Long duration, BaseFinanceResource finance) {
        this.durations = LongStream.rangeClosed(1, duration).boxed().collect(toList());
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
