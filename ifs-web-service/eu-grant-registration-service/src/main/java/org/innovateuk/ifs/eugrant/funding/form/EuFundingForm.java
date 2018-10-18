package org.innovateuk.ifs.eugrant.funding.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;
import org.innovateuk.ifs.commons.validation.constraints.FutureLocalDate;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class EuFundingForm {

    @NotBlank(message = "{validation.fundingForm.grant.agreement.number}")
    @Pattern(regexp="[\\d]{6}", message = "{validation.fundingForm.grant.agreement.format.invalid}")
    private String grantAgreementNumber;

    @NotNull(message = "{validation.fundingForm.participant.identification.code}")
    @Pattern(regexp="[\\d]{9}", message = "{validation.fundingForm.participant.identification.code.format.invalid}")
    private String participantId;

    @NotBlank(message = "{validation.fundingForm.project.name}")
    private String projectName;

    @Range(min = 1, max = 12, message = "{validation.fundingForm.date.month}")
    private Integer startDateMonth;

    @Range(min = 1000, max = 9999, message = "{validation.fundingForm.date.year}")
    private Integer startDateYear;

    @Range(min = 1, max = 12, message = "{validation.fundingForm.date.month}")
    private Integer endDateMonth;

    @Range(min = 1000, max = 9999, message = "{validation.fundingForm.date.year}")
    private Integer endDateYear;

    @Digits(integer = 10, fraction = 0, message = "{validation.fundingForm.funding.format.invalid}")
    @Range(min = 0, message = "{validation.fundingForm.funding.positive}")
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

    @NotNull(message = "{validation.standard.date.format}")
    public LocalDate getStartDate() {
        if (startDateYear == null || startDateMonth == null) {
            return null;
        }

        try {
            return getLocalDate(startDateMonth, startDateYear);
        } catch (DateTimeException e) {
            return null;
        }
    }

    @NotNull(message = "{validation.standard.date.format}")
    @FutureLocalDate(message = "{validation.standard.date.future}")
    public LocalDate getEndDate() {
        if (endDateYear == null || endDateMonth == null) {
            return null;
        }

        try {
            return getLocalDate(endDateMonth, endDateYear);
        } catch (DateTimeException e) {
            return null;
        }
    }

    public LocalDate getLocalDate(int month, int year) {

        String date = String.valueOf(year) + "-" + String.format("%02d", month) + "-01";

        return LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
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
                .append(endDateMonth)
                .append(endDateYear)
                .append(fundingContribution)
                .append(projectCoordinator)
                .append(actionType)
                .toHashCode();
    }
}
