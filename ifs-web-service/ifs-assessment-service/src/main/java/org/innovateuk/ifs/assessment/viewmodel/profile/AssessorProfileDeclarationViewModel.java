package org.innovateuk.ifs.assessment.viewmodel.profile;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.user.resource.AffiliationResource;

import java.util.ArrayList;
import java.util.List;

/**
 * Holder of model attributes for the Declaration of Interest view.
 */
public class AssessorProfileDeclarationViewModel {

    private final boolean completed;
    private final String principalEmployer;
    private final String role;
    private final String professionalAffiliations;
    private final List<AffiliationResource> appointments = new ArrayList<>();
    private final String financialInterests;
    private final List<AffiliationResource> familyAffiliations = new ArrayList<>();
    private final String familyFinancialInterests;

    public AssessorProfileDeclarationViewModel(
            boolean completed,
            String principalEmployer,
            String role,
            String professionalAffiliations,
            List<AffiliationResource> appointments,
            String financialInterests,
            List<AffiliationResource> familyAffiliations,
            String familyFinancialInterests
    ) {
        this.completed = completed;
        this.principalEmployer = principalEmployer;
        this.role = role;
        this.professionalAffiliations = professionalAffiliations;
        this.financialInterests = financialInterests;
        this.familyFinancialInterests = familyFinancialInterests;

        if (appointments != null) {
            this.appointments.addAll(appointments);
        }

        if (familyAffiliations != null) {
            this.familyAffiliations.addAll(familyAffiliations);
        }
    }

    public boolean isCompleted() {
        return completed;
    }

    public String getPrincipalEmployer() {
        return principalEmployer;
    }

    public String getRole() {
        return role;
    }

    public String getProfessionalAffiliations() {
        return professionalAffiliations;
    }

    public List<AffiliationResource> getAppointments() {
        return appointments;
    }

    public String getFinancialInterests() {
        return financialInterests;
    }

    public List<AffiliationResource> getFamilyAffiliations() {
        return familyAffiliations;
    }

    public String getFamilyFinancialInterests() {
        return familyFinancialInterests;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AssessorProfileDeclarationViewModel viewModel = (AssessorProfileDeclarationViewModel) o;

        return new EqualsBuilder()
                .append(completed, viewModel.completed)
                .append(principalEmployer, viewModel.principalEmployer)
                .append(role, viewModel.role)
                .append(professionalAffiliations, viewModel.professionalAffiliations)
                .append(appointments, viewModel.appointments)
                .append(financialInterests, viewModel.financialInterests)
                .append(familyAffiliations, viewModel.familyAffiliations)
                .append(familyFinancialInterests, viewModel.familyFinancialInterests)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(completed)
                .append(principalEmployer)
                .append(role)
                .append(professionalAffiliations)
                .append(appointments)
                .append(financialInterests)
                .append(familyAffiliations)
                .append(familyFinancialInterests)
                .toHashCode();
    }
}
