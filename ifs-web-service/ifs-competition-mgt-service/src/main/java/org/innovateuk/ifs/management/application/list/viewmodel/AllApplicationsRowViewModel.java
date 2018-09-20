package org.innovateuk.ifs.management.application.list.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * View model for a single row of the Competition All Applications paginated table.
 */
public class AllApplicationsRowViewModel extends BaseApplicationsRowViewModel {

    private String innovationArea;
    private String status;
    private int percentageComplete;

    public AllApplicationsRowViewModel(long id,
                                       String projectTitle,
                                       String lead,
                                       String innovationArea,
                                       String status,
                                       int percentageComplete) {
        super(id, projectTitle, lead);
        this.innovationArea = innovationArea;
        this.status = status;
        this.percentageComplete = percentageComplete;
    }

    public String getInnovationArea() {
        return innovationArea;
    }

    public String getStatus() {
        return status;
    }

    public int getPercentageComplete() {
        return percentageComplete;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AllApplicationsRowViewModel that = (AllApplicationsRowViewModel) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(percentageComplete, that.percentageComplete)
                .append(innovationArea, that.innovationArea)
                .append(status, that.status)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(innovationArea)
                .append(status)
                .append(percentageComplete)
                .toHashCode();
    }
}
