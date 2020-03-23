package org.innovateuk.ifs.assessment.profile.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import javax.validation.constraints.NotBlank;
import org.innovateuk.ifs.commons.validation.constraints.FieldRequiredIf;
import org.innovateuk.ifs.commons.validation.constraints.WordCount;
import org.innovateuk.ifs.controller.BindingResultTarget;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * Form field model for the Assessor Profile Declaration of Interest page
 */
@FieldRequiredIf(required = "appointments", argument = "hasAppointments", predicate = true, message = "{validation.assessorprofiledeclarationform.appointments.required}")
@FieldRequiredIf(required = "financialInterests", argument = "hasFinancialInterests", predicate = true, message = "{validation.assessorprofiledeclarationform.financialInterests.required}")
@FieldRequiredIf(required = "familyAffiliations", argument = "hasFamilyAffiliations", predicate = true, message = "{validation.assessorprofiledeclarationform.familyAffiliations.required}")
@FieldRequiredIf(required = "familyFinancialInterests", argument = "hasFamilyFinancialInterests", predicate = true, message = "{validation.assessorprofiledeclarationform.familyFinancialInterests.required}")
public class AssessorProfileDeclarationForm implements BindingResultTarget {

    @NotBlank(message = "{validation.assessorprofiledeclarationform.principalEmployer.required}")
    @Size(max = 255, message = "{validation.field.too.many.characters}")
    private String principalEmployer;

    @NotBlank(message = "{validation.assessorprofiledeclarationform.role.required}")
    @Size(max = 255, message = "{validation.field.too.many.characters}")
    private String role;

    @Size(max = 5000, message = "{validation.field.too.many.characters}")
    @WordCount(max = 200, message = "{validation.field.max.word.count}")
    private String professionalAffiliations;

    @NotNull(message = "{validation.assessorprofiledeclarationform.hasAppointments.required}")
    private Boolean hasAppointments;
    @Valid
    private List<AssessorProfileAppointmentForm> appointments = new ArrayList<>();

    @NotNull(message = "{validation.assessorprofiledeclarationform.hasFinancialInterests.required}")
    private Boolean hasFinancialInterests;
    @Size(max = 5000, message = "{validation.field.too.many.characters}")
    @WordCount(max = 100, message = "{validation.field.max.word.count}")
    private String financialInterests;

    @NotNull(message = "{validation.assessorprofiledeclarationform.hasFamilyAffiliations.required}")
    private Boolean hasFamilyAffiliations;
    @Valid
    private List<AssessorProfileFamilyAffiliationForm> familyAffiliations = new ArrayList<>();

    @NotNull(message = "{validation.assessorprofiledeclarationform.hasFamilyFinancialInterests.required}")
    private Boolean hasFamilyFinancialInterests;
    @Size(max = 5000, message = "{validation.field.too.many.characters}")
    @WordCount(max = 100, message = "{validation.field.max.word.count}")
    private String familyFinancialInterests;

    @NotNull(message = "{validation.assessorprofiledeclarationform.accurateAccount.required}")
    @AssertTrue(message = "{validation.assessorprofiledeclarationform.accurateAccount.required}")
    private Boolean accurateAccount;

    private BindingResult bindingResult;
    private List<ObjectError> objectErrors;

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
    public BindingResult getBindingResult() {
        return bindingResult;
    }

    @Override
    public void setBindingResult(BindingResult bindingResult) {
        this.bindingResult = bindingResult;
    }

    @Override
    public List<ObjectError> getObjectErrors() {
        return objectErrors;
    }

    @Override
    public void setObjectErrors(List<ObjectError> objectErrors) {
        this.objectErrors = objectErrors;
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
                .append(bindingResult, that.bindingResult)
                .append(objectErrors, that.objectErrors)
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
                .append(bindingResult)
                .append(objectErrors)
                .toHashCode();
    }
}
