package org.innovateuk.ifs.applicant.resource.dashboard;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class ApplicantDashboardResource implements Serializable {

    private List<DashboardInSetupRowResource> inSetup;
    private List<DashboardEuGrantTransferRowResource> euGrantTransfer;
    private List<DashboardInProgressRowResource> inProgress;
    private List<DashboardPreviousRowResource> previous;

    private ApplicantDashboardResource() {}

    public List<DashboardInSetupRowResource> getInSetup() {
        return inSetup;
    }

    public List<DashboardEuGrantTransferRowResource> getEuGrantTransfer() {
        return euGrantTransfer;
    }

    public List<DashboardInProgressRowResource> getInProgress() {
        return inProgress;
    }

    public List<DashboardPreviousRowResource> getPrevious() {
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
        private List<DashboardInSetupRowResource> inSetup;
        private List<DashboardEuGrantTransferRowResource> euGrantTransfer;
        private List<DashboardInProgressRowResource> inProgress;
        private List<DashboardPreviousRowResource> previous;

        public ApplicantDashboardResourceBuilder withInSetup(List<DashboardInSetupRowResource> inSetup) {
            this.inSetup = inSetup;
            return this;
        }

        public ApplicantDashboardResourceBuilder withEuGrantTransfer(List<DashboardEuGrantTransferRowResource> euGrantTransfer) {
            this.euGrantTransfer = euGrantTransfer;
            return this;
        }

        public ApplicantDashboardResourceBuilder withInProgress(List<DashboardInProgressRowResource> inProgress) {
            this.inProgress = inProgress;
            return this;
        }

        public ApplicantDashboardResourceBuilder withPrevious(List<DashboardPreviousRowResource> previous) {
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
