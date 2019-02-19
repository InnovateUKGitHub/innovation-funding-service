package org.innovateuk.ifs.invite.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.invite.resource.EuContactResource;
import org.innovateuk.ifs.management.navigation.Pagination;

import java.util.List;

public class EuInviteViewModel {

    private final List<EuContactResource> contacts;

    private final Pagination pagination;

    public EuInviteViewModel(List<EuContactResource> contacts, Pagination pagination) {
        this.contacts = contacts;
        this.pagination = pagination;
    }

    public List<EuContactResource> getContacts() {
        return contacts;
    }

    public Pagination getPagination() {
        return pagination;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        EuInviteViewModel viewModel = (EuInviteViewModel) o;

        return new EqualsBuilder()
                .append(contacts, viewModel.contacts)
                .append(pagination, viewModel.pagination)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(contacts)
                .append(pagination)
                .toHashCode();
    }
}
