package com.worth.ifs.assessment.form;

public class AssessorRegistrationDeclarationForm {

    private String principalEmployer;
    private String role;
    private String professionalAffiliations;

    private Boolean hasAppointments;
    private Boolean hasFinancialInterests;

    private Boolean hasFamilyAffiliations;
    private Boolean hasFamilyFinancialInterests;
    private Boolean isAccurateAccount;

    public AssessorRegistrationDeclarationForm() {
    }

    public AssessorRegistrationDeclarationForm(String principalEmployer, String role, String professionalAffiliations, Boolean hasAppointments, Boolean hasFinancialInterests, Boolean hasFamilyAffiliations, Boolean hasFamilyFinancialInterests, Boolean isAccurateAccount) {
        this.principalEmployer = principalEmployer;
        this.role = role;
        this.professionalAffiliations = professionalAffiliations;
        this.hasAppointments = hasAppointments;
        this.hasFinancialInterests = hasFinancialInterests;
        this.hasFamilyAffiliations = hasFamilyAffiliations;
        this.hasFamilyFinancialInterests = hasFamilyFinancialInterests;
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

    public Boolean getHasFinancialInterests() {
        return hasFinancialInterests;
    }

    public void setHasFinancialInterests(Boolean hasFinancialInterests) {
        this.hasFinancialInterests = hasFinancialInterests;
    }

    public Boolean getHasFamilyAffiliations() {
        return hasFamilyAffiliations;
    }

    public void setHasFamilyAffiliations(Boolean hasFamilyAffiliations) {
        this.hasFamilyAffiliations = hasFamilyAffiliations;
    }

    public Boolean getHasFamilyFinancialInterests() {
        return hasFamilyFinancialInterests;
    }

    public void setHasFamilyFinancialInterests(Boolean hasFamilyFinancialInterests) {
        this.hasFamilyFinancialInterests = hasFamilyFinancialInterests;
    }

    public Boolean getAccurateAccount() {
        return isAccurateAccount;
    }

    public void setAccurateAccount(Boolean accurateAccount) {
        isAccurateAccount = accurateAccount;
    }
}
