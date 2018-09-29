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

    private LocalDate projectStartDate;

    private LocalDate projectEndDate;

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
        if (projectStartDate == null) {
            return null;
        }

        try {
            return getLocalDate(projectStartDate.getMonthValue(), projectStartDate.getYear());
        } catch (DateTimeException e) {
            return null;
        }
    }

    @NotNull(message = "{validation.standard.date.format}")
    @FutureLocalDate(message = "{validation.standard.date.future}")
    public LocalDate getEndDate() {
        if (projectEndDate == null) {
            return null;
        }

        try {
            return getLocalDate(projectEndDate.getMonthValue(), projectEndDate.getYear());
        } catch (DateTimeException e) {
            return null;
        }
    }

    private LocalDate getLocalDate(int projectMonth, int projectYear) {

        String date = String.valueOf(projectYear) + "-" + String.format("%02d",projectMonth)+ "-01";

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
