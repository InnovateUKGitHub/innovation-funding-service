package org.innovateuk.ifs.eu.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.eugrant.EuGrantResource;
import org.innovateuk.ifs.management.navigation.Pagination;

import java.util.List;

public class EuInviteViewModel {

    private final List<EuGrantResource> grants;

    private final Pagination pagination;

    private final long totalNotified;

    private final long totalNonNotified;

    private boolean emailSuccessMessage;

    private long numEmailsSent;

    public EuInviteViewModel(List<EuGrantResource> grants,
                             Pagination pagination,
                             long totalNotified,
                             long totalNonNotified,
                             boolean emailSuccessMessage,
                             long numEmailsSent) {
        this.grants = grants;
        this.pagination = pagination;
        this.totalNotified = totalNotified;
        this.totalNonNotified = totalNonNotified;
        this.emailSuccessMessage = emailSuccessMessage;
        this.numEmailsSent = numEmailsSent;
    }

    public List<EuGrantResource> getGrants() {
        return grants;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public long getTotalNotified() {
        return totalNotified;
    }

    public long getTotalNonNotified() {
        return totalNonNotified;
    }

    public boolean isEmailSuccessMessage() {
        return emailSuccessMessage;
    }

    public long getNumEmailsSent() {
        return numEmailsSent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        EuInviteViewModel viewModel = (EuInviteViewModel) o;

        return new EqualsBuilder()
                .append(grants, viewModel.grants)
                .append(pagination, viewModel.pagination)
                .append(totalNotified, viewModel.totalNotified)
                .append(totalNonNotified, viewModel.totalNonNotified)
                .append(emailSuccessMessage, viewModel.emailSuccessMessage)
                .append(numEmailsSent, viewModel.numEmailsSent)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(grants)
                .append(pagination)
                .append(totalNotified)
                .append(totalNonNotified)
                .append(emailSuccessMessage)
                .append(numEmailsSent)
                .toHashCode();
    }
}