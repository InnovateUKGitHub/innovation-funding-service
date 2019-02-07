package org.innovateuk.ifs.registration.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Holder of model attributes for registration of monitoring officers
 */
public class MonitoringOfficerRegistrationViewModel {
    private String email;

    public MonitoringOfficerRegistrationViewModel(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        MonitoringOfficerRegistrationViewModel viewModel = (MonitoringOfficerRegistrationViewModel) o;

        return new EqualsBuilder()
                .append(email, viewModel.email)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(email)
                .toHashCode();
    }
}