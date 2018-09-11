package org.innovateuk.ifs.eugrant.funding.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;

public class EuFundingForm {

    @NotBlank(message = "{validation.fundingForm.grant.aggreement.number}")
    private String grantAgreementNumber;

    @NotNull
    @Pattern(regexp="[\\d]{6}", message = "{validation.fundingForm.participant.identification.code}")
    private String participantId;

    @NotBlank(message = "{validation.fundingForm.project.name}")
    private String projectName;

    @NotNull(message = "{validation.fundingForm.date.month}")
    @Range(min = 1, max = 12, message = "{validation.fundingForm.date.month}")
    private Integer startDateMonth;

    @NotNull(message = "{validation.fundingForm.date.year}")
    @Range(max = 9999, message = "{validation.fundingForm.date.year}")
    private Integer startDateYear;

    @NotNull(message = "{validation.fundingForm.date.month}")
    @Range(min = 1, max = 12, message = "{validation.fundingForm.date.month}")
    private Integer endDateMonth;

    @NotNull(message = "{validation.fundingForm.date.year}")
    @Range(min = 2000, max = 9999, message = "{validation.fundingForm.date.year}")
    private Integer endDateYear;

    @NotNull(message = "{validation.fundingForm.funding.contribution}")
    private BigDecimal fundingContribution;

    @NotNull(message = "{validation.fundingForm.projectCoordinator}")
    private Boolean projectCoordinator;

    @NotNull(message = "{validation.fundingForm.action.type}")
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

    public Integer getStartDateMonth() {
        return startDateMonth;
    }

    public void setStartDateMonth(Integer startDateMonth) {
        this.startDateMonth = startDateMonth;
    }

    public Integer getStartDateYear() {
        return startDateYear;
    }

    public void setStartDateYear(Integer startDateYear) {
        this.startDateYear = startDateYear;
    }

    public Integer getEndDateMonth() {
        return endDateMonth;
    }

    public void setEndDateMonth(Integer endDateMonth) {
        this.endDateMonth = endDateMonth;
    }

    public Integer getEndDateYear() {
        return endDateYear;
    }

    public void setEndDateYear(Integer endDateYear) {
        this.endDateYear = endDateYear;
    }

    public Boolean getProjectCoordinator() {
        return projectCoordinator;
    }

    public void setProjectCoordinator(Boolean projectCoordinator) {
        this.projectCoordinator = projectCoordinator;
    }

    public BigDecimal getFundingContribution() {
        return fundingContribution;
    }

    public void setFundingContribution(BigDecimal fundingContribution) {
        this.fundingContribution = fundingContribution;
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
