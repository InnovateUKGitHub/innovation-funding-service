package org.innovateuk.ifs.eugrant.funding.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.NotBlank;
import org.innovateuk.ifs.eugrant.EuActionTypeResource;
import org.innovateuk.ifs.eugrant.contact.form.EuContactForm;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.time.LocalDate;

public class EuFundingForm {

    @NotBlank
    private String grantAgreementNumber;

    @NotNull
    @Pattern(regexp="[\\d]{6}")
    private String participantId;

    @NotBlank
    private String projectName;

    @NotNull
    private LocalDate projectStartDate;
    @NotNull
    private LocalDate projectEndDate;

    @NotBlank
    private BigDecimal fundingContribution;

    private boolean projectCoordinator;

    private EuActionTypeResource actionType;


    public String getGrantAgreementNumber() {
        return grantAgreementNumber;
    }

    public void setGrantAgreementNumber(String grantAgreementNumber) {
        this.grantAgreementNumber = grantAgreementNumber;
    }

    public String getParticipantId() {
        return participantId;
    }

    public void setParticipantId(String participantId) {
        this.participantId = participantId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public LocalDate getProjectStartDate() {
        return projectStartDate;
    }

    public void setProjectStartDate(LocalDate projectStartDate) {
        this.projectStartDate = projectStartDate;
    }

    public LocalDate getProjectEndDate() {
        return projectEndDate;
    }

    public void setProjectEndDate(LocalDate projectEndDate) {
        this.projectEndDate = projectEndDate;
    }

    public BigDecimal getFundingContribution() {
        return fundingContribution;
    }

    public void setFundingContribution(BigDecimal fundingContribution) {
        this.fundingContribution = fundingContribution;
    }

    public boolean isProjectCoordinator() {
        return projectCoordinator;
    }

    public void setProjectCoordinator(boolean projectCoordinator) {
        this.projectCoordinator = projectCoordinator;
    }

    public EuActionTypeResource getActionType() {
        return actionType;
    }

    public void setActionType(EuActionTypeResource actionType) {
        this.actionType = actionType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        EuFundingForm that = (EuFundingForm) o;

        return new EqualsBuilder()
                .append(grantAgreementNumber, that.grantAgreementNumber)
                .append(participantId, that.participantId)
                .append(projectName, that.projectName)
                .append(projectStartDate, that.projectStartDate)
                .append(projectEndDate, that.projectEndDate)
                .append(fundingContribution, that.fundingContribution)
                .append(projectCoordinator, that.projectCoordinator)
                .append(actionType, that.actionType)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(grantAgreementNumber)
                .append(participantId)
                .append(projectName)
                .append(projectStartDate)
                .append(projectEndDate)
                .append(fundingContribution)
                .append(projectCoordinator)
                .append(actionType)
                .toHashCode();
    }
}
