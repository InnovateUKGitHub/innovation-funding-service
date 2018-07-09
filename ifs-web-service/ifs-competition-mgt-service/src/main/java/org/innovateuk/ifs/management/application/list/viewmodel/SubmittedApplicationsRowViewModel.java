package org.innovateuk.ifs.management.application.list.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.math.BigDecimal;

/**
 * View model for Competition Management Submitted Applications table rows.
 */
public class SubmittedApplicationsRowViewModel extends BaseApplicationsRowViewModel {

    private String innovationArea;
    private int numberOfPartners;
    private BigDecimal grantRequested;
    private BigDecimal totalProjectCost;
    private long durationInMonths;

    public SubmittedApplicationsRowViewModel(long id,
                                             String projectTitle,
                                             String lead,
                                             String innovationArea,
                                             int numberOfPartners,
                                             BigDecimal grantRequested,
                                             BigDecimal totalProjectCost,
                                             long durationInMonths) {
        super(id, projectTitle, lead);
        this.innovationArea = innovationArea;
        this.numberOfPartners = numberOfPartners;
        this.grantRequested = grantRequested;
        this.totalProjectCost = totalProjectCost;
        this.durationInMonths = durationInMonths;
    }

    public String getInnovationArea() {
        return innovationArea;
    }

    public int getNumberOfPartners() {
        return numberOfPartners;
    }

    public BigDecimal getGrantRequested() {
        return grantRequested;
    }

    public BigDecimal getTotalProjectCost() {
        return totalProjectCost;
    }

    public long getDurationInMonths() {
        return durationInMonths;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        SubmittedApplicationsRowViewModel that = (SubmittedApplicationsRowViewModel) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(numberOfPartners, that.numberOfPartners)
                .append(durationInMonths, that.durationInMonths)
                .append(innovationArea, that.innovationArea)
                .append(grantRequested, that.grantRequested)
                .append(totalProjectCost, that.totalProjectCost)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(innovationArea)
                .append(numberOfPartners)
                .append(grantRequested)
                .append(totalProjectCost)
                .append(durationInMonths)
                .toHashCode();
    }
}
