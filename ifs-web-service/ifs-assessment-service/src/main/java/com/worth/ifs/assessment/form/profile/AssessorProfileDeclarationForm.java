package com.worth.ifs.assessment.form.profile;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

/**
 * Form field model for the Assessor Profile Declaration of Interest page
 */
public class AssessorProfileDeclarationForm {

    private String principalEmployer;
    private String role;
    private String professionalAffiliations;

    private Boolean hasAppointments;
    private List<AssessorProfileAppointmentForm> appointments;

    private Boolean hasFinancialInterests;
    private String financialInterests;

    private Boolean hasFamilyAffiliations;
    private List<AssessorProfileFamilyAffiliationForm> familyAffiliations;

    private Boolean hasFamilyFinancialInterests;
    private String familyInterests;

    private Boolean isAccurateAccount;

    public AssessorProfileDeclarationForm() {
    }

    public AssessorProfileDeclarationForm(String principalEmployer, String role, String professionalAffiliations, Boolean hasAppointments, List<AssessorProfileAppointmentForm> appointments, Boolean hasFinancialInterests, String financialInterests, Boolean hasFamilyAffiliations, List<AssessorProfileFamilyAffiliationForm> familyAffiliations, Boolean hasFamilyFinancialInterests, String familyInterests, Boolean isAccurateAccount) {
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
        this.familyInterests = familyInterests;
        this.isAccurateAccount = isAccurateAccount;
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

    public String getFamilyInterests() {
        return familyInterests;
    }

    public void setFamilyInterests(String familyInterests) {
        this.familyInterests = familyInterests;
    }

    public Boolean getAccurateAccount() {
        return isAccurateAccount;
    }

    public void setAccurateAccount(Boolean accurateAccount) {
        isAccurateAccount = accurateAccount;
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
                .append(familyInterests, that.familyInterests)
                .append(isAccurateAccount, that.isAccurateAccount)
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
                .append(familyInterests)
                .append(isAccurateAccount)
                .toHashCode();
    }
}
