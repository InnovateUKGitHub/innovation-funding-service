package org.innovateuk.ifs.applicant.resource.dashboard;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

public class ApplicantDashboardResource {

    private List<DashboardApplicationInSetupResource> inSetup;
    private List<DashboardApplicationForEuGrantTransferResource> euGrantTransfer;
    private List<DashboardApplicationInProgressResource> inProgress;
    private List<DashboardPreviousApplicationResource> previous;

    private ApplicantDashboardResource() {}

    public List<DashboardApplicationInSetupResource> getInSetup() {
        return inSetup;
    }

    public List<DashboardApplicationForEuGrantTransferResource> getEuGrantTransfer() {
        return euGrantTransfer;
    }

    public List<DashboardApplicationInProgressResource> getInProgress() {
        return inProgress;
    }

    public List<DashboardPreviousApplicationResource> getPrevious() {
        return previous;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApplicantDashboardResource that = (ApplicantDashboardResource) o;
        return new EqualsBuilder()
                .append(inSetup, that.inSetup)
                .append(euGrantTransfer, that.euGrantTransfer)
                .append(inProgress, that.inProgress)
                .append(previous, that.previous)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(inSetup)
                .append(euGrantTransfer)
                .append(inProgress)
                .append(previous)
                .toHashCode();
    }

    public static class ApplicantDashboardResourceBuilder {
        private List<DashboardApplicationInSetupResource> inSetup;
        private List<DashboardApplicationForEuGrantTransferResource> euGrantTransfer;
        private List<DashboardApplicationInProgressResource> inProgress;
        private List<DashboardPreviousApplicationResource> previous;

        public ApplicantDashboardResourceBuilder withInSetup(List<DashboardApplicationInSetupResource> inSetup) {
            this.inSetup = inSetup;
            return this;
        }

        public ApplicantDashboardResourceBuilder withEuGrantTransfer(List<DashboardApplicationForEuGrantTransferResource> euGrantTransfer) {
            this.euGrantTransfer = euGrantTransfer;
            return this;
        }

        public ApplicantDashboardResourceBuilder withInProgress(List<DashboardApplicationInProgressResource> inProgress) {
            this.inProgress = inProgress;
            return this;
        }

        public ApplicantDashboardResourceBuilder withPrevious(List<DashboardPreviousApplicationResource> previous) {
            this.previous = previous;
            return this;
        }

        public ApplicantDashboardResource build() {
            ApplicantDashboardResource result = new ApplicantDashboardResource();
            result.inSetup = this.inSetup;
            result.euGrantTransfer = this.euGrantTransfer;
            result.inProgress = this.inProgress;
            result.previous = this.previous;

            return result;
        }

    }
}
