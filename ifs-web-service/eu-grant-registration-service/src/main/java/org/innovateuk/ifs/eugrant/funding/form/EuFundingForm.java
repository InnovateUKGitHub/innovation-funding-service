package org.innovateuk.ifs.eugrant.funding.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;

public class EuFundingForm {

    @NotBlank
    private String grantAgreementNumber;

    @NotNull
    @Pattern(regexp="[\\d]{6}")
    private String participantId;

    @NotBlank
    private String projectName;

    @Range(min = 1, max = 12, message = "{validation.fundingForm.date.month}")
    private int startDateMonth;

    @Range(min = 2000, max = 9999, message = "{validation.fundingForm.date.year}")
    private int startDateYear;

    @Range(min = 1, max = 12, message = "{validation.fundingForm.date.month}")
    private int endDateMonth;

    @Range(min = 2000, max = 9999, message = "{validation.fundingForm.date.year}")
    private int endDateYear;

    @NotNull
    private BigDecimal fundingContribution;

    private boolean projectCoordinator;

    private Long actionType;

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

    public int getStartDateMonth() {
        return startDateMonth;
    }

    public void setStartDateMonth(int startDateMonth) {
        this.startDateMonth = startDateMonth;
    }

    public int getStartDateYear() {
        return startDateYear;
    }

    public void setStartDateYear(int startDateYear) {
        this.startDateYear = startDateYear;
    }

    public int getEndDateMonth() {
        return endDateMonth;
    }

    public void setEndDateMonth(int endDateMonth) {
        this.endDateMonth = endDateMonth;
    }

    public int getEndDateYear() {
        return endDateYear;
    }

    public void setEndDateYear(int endDateYear) {
        this.endDateYear = endDateYear;
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

    public Long getActionType() {
        return actionType;
    }

    public void setActionType(Long actionType) {
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
                .append(startDateMonth, that.startDateMonth)
                .append(startDateYear, that.startDateYear)
                .append(endDateMonth, that.endDateMonth)
                .append(endDateYear, that.endDateYear)
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
                .append(startDateMonth)
                .append(startDateYear)
                .append(startDateMonth)
                .append(endDateMonth)
                .append(endDateYear)
                .append(fundingContribution)
                .append(projectCoordinator)
                .append(actionType)
                .toHashCode();
    }
}
