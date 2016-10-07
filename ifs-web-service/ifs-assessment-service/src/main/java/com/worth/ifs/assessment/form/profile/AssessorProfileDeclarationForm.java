package com.worth.ifs.assessment.form.profile;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Form field model for the Assessor Profile Declaration of Interest page
 */
public class AssessorProfileDeclarationForm {

    @NotEmpty(message = "{validation.assessorprofiledeclarationform.principalEmployer.required}")
    private String principalEmployer;
    @NotEmpty(message = "{validation.assessorprofiledeclarationform.role.required}")
    private String role;
    private String professionalAffiliations;

    @NotNull(message = "{validation.assessorprofiledeclarationform.hasAppointments.required}")
    private Boolean hasAppointments;
    @Valid
    private List<AssessorProfileAppointmentForm> appointments = new ArrayList<>();

    @NotNull(message = "{validation.assessorprofiledeclarationform.hasFinancialInterests.required}")
    private Boolean hasFinancialInterests;
    private String financialInterests;

    @NotNull(message = "{validation.assessorprofiledeclarationform.hasFamilyAffiliations.required}")
    private Boolean hasFamilyAffiliations;
    @Valid
    private List<AssessorProfileFamilyAffiliationForm> familyAffiliations = new ArrayList<>();

    @NotNull(message = "{validation.assessorprofiledeclarationform.hasFamilyFinancialInterests.required}")
    private Boolean hasFamilyFinancialInterests;
    private String familyFinancialInterests;

    @AssertTrue(message = "{validation.assessorprofiledeclarationform.accurateAccount.required}")
    private Boolean accurateAccount;

    public AssessorProfileDeclarationForm() {
    }

    public AssessorProfileDeclarationForm(String principalEmployer, String role, String professionalAffiliations, Boolean hasAppointments, List<AssessorProfileAppointmentForm> appointments, Boolean hasFinancialInterests, String financialInterests, Boolean hasFamilyAffiliations, List<AssessorProfileFamilyAffiliationForm> familyAffiliations, Boolean hasFamilyFinancialInterests, String familyFinancialInterests, Boolean accurateAccount) {
        this.principalEmployer = principalEmployer;
        this.role = role;
        this.professionalAffiliations = professionalAffiliations;
        this.hasAppointments = hasAppointments;
        this.appointments = appointments;
        this.hasFinancialInterests = hasFinancialInterests;
        this.financialInterests = financialInterests;
        this.hasFamilyAffiliations = hasFamilyAffiliations;
        this.familyAffiliations = familyAffiliations;
        this.hasFamilyFinancialInterests = hasFamilyFinancialInterests;
        this.familyFinancialInterests = familyFinancialInterests;
        this.accurateAccount = accurateAccount;
    }

    public String getPrincipalEmployer() {
        return principalEmployer;
    }

    public void setPrincipalEmployer(String principalEmployer) {
        this.principalEmployer = principalEmployer;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getProfessionalAffiliations() {
        return professionalAffiliations;
    }

    public void setProfessionalAffiliations(String professionalAffiliations) {
        this.professionalAffiliations = professionalAffiliations;
    }

    public Boolean getHasAppointments() {
        return hasAppointments;
    }

    public void setHasAppointments(Boolean hasAppointments) {
        this.hasAppointments = hasAppointments;
    }

    public List<AssessorProfileAppointmentForm> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<AssessorProfileAppointmentForm> appointments) {
        this.appointments = appointments;
    }

    public Boolean getHasFinancialInterests() {
        return hasFinancialInterests;
    }

    public void setHasFinancialInterests(Boolean hasFinancialInterests) {
        this.hasFinancialInterests = hasFinancialInterests;
    }

    public String getFinancialInterests() {
        return financialInterests;
    }

    public void setFinancialInterests(String financialInterests) {
        this.financialInterests = financialInterests;
    }

    public Boolean getHasFamilyAffiliations() {
        return hasFamilyAffiliations;
    }

    public void setHasFamilyAffiliations(Boolean hasFamilyAffiliations) {
        this.hasFamilyAffiliations = hasFamilyAffiliations;
    }

    public List<AssessorProfileFamilyAffiliationForm> getFamilyAffiliations() {
        return familyAffiliations;
    }

    public void setFamilyAffiliations(List<AssessorProfileFamilyAffiliationForm> familyAffiliations) {
        this.familyAffiliations = familyAffiliations;
    }

    public Boolean getHasFamilyFinancialInterests() {
        return hasFamilyFinancialInterests;
    }

    public void setHasFamilyFinancialInterests(Boolean hasFamilyFinancialInterests) {
        this.hasFamilyFinancialInterests = hasFamilyFinancialInterests;
    }

    public String getFamilyFinancialInterests() {
        return familyFinancialInterests;
    }

    public void setFamilyFinancialInterests(String familyFinancialInterests) {
        this.familyFinancialInterests = familyFinancialInterests;
    }

    public Boolean getAccurateAccount() {
        return accurateAccount;
    }

    public void setAccurateAccount(Boolean accurateAccount) {
        this.accurateAccount = accurateAccount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AssessorProfileDeclarationForm that = (AssessorProfileDeclarationForm) o;

        return new EqualsBuilder()
                .append(principalEmployer, that.principalEmployer)
                .append(role, that.role)
                .append(professionalAffiliations, that.professionalAffiliations)
                .append(hasAppointments, that.hasAppointments)
                .append(appointments, that.appointments)
                .append(hasFinancialInterests, that.hasFinancialInterests)
                .append(financialInterests, that.financialInterests)
                .append(hasFamilyAffiliations, that.hasFamilyAffiliations)
                .append(familyAffiliations, that.familyAffiliations)
                .append(hasFamilyFinancialInterests, that.hasFamilyFinancialInterests)
                .append(familyFinancialInterests, that.familyFinancialInterests)
                .append(accurateAccount, that.accurateAccount)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(principalEmployer)
                .append(role)
                .append(professionalAffiliations)
                .append(hasAppointments)
                .append(appointments)
                .append(hasFinancialInterests)
                .append(financialInterests)
                .append(hasFamilyAffiliations)
                .append(familyAffiliations)
                .append(hasFamilyFinancialInterests)
                .append(familyFinancialInterests)
                .append(accurateAccount)
                .toHashCode();
    }
}
