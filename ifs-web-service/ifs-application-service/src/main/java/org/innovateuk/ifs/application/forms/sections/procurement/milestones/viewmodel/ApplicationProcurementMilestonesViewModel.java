package org.innovateuk.ifs.application.forms.sections.procurement.milestones.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationResource;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.LongStream;

import static java.util.stream.Collectors.toList;

public class ApplicationProcurementMilestonesViewModel {

    private final long applicationId;
    private final String applicationName;
    private List<Long> durations;
    private BigInteger fundingAmount;
    private final String financesUrl;
    private final boolean complete;
    private final boolean open;

    public ApplicationProcurementMilestonesViewModel(ApplicationResource application, BigDecimal fundingAmount, String financesUrl, boolean complete) {
        this.applicationId = application.getId();
        this.applicationName = application.getName();
        this.durations = LongStream.rangeClosed(1, application.getDurationInMonths()).boxed().collect(toList());
        this.fundingAmount = fundingAmount.setScale(0, RoundingMode.HALF_UP).toBigInteger();
        this.financesUrl = financesUrl;
        this.complete = complete;
        this.open = application.isOpen();
    }

    public long getApplicationId() {
        return applicationId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public List<Long> getDurations() {
        return durations;
    }

    public BigInteger getFundingAmount() {
        return fundingAmount;
    }

    public String getFinancesUrl() {
        return financesUrl;
    }

    public boolean isComplete() {
        return complete;
    }

    public boolean isOpen() {
        return open;
    }

    public boolean isReadOnly() {
        return complete || !open;
    }
}
