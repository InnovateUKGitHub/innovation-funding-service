package org.innovateuk.ifs.granttransfer.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;

public class EuGrantTransferResource {

    private String grantAgreementNumber;

    private String participantId;

    private String projectName;

    private LocalDate projectStartDate;

    private LocalDate projectEndDate;

    private BigDecimal fundingContribution;

    private Boolean projectCoordinator;

    private EuActionTypeResource actionType;

    public EuGrantTransferResource() {
    }

    public EuGrantTransferResource(String grantAgreementNumber,
                             String participantId,
                             String projectName,
                             LocalDate projectStartDate,
                             LocalDate projectEndDate,
                             BigDecimal fundingContribution,
                             Boolean projectCoordinator,
                             EuActionTypeResource actionType) {
        this.grantAgreementNumber = grantAgreementNumber;
        this.participantId = participantId;
        this.projectName = projectName;
        this.projectStartDate = projectStartDate;
        this.projectEndDate = projectEndDate;
        this.fundingContribution = fundingContribution;
        this.projectCoordinator = projectCoordinator;
        this.actionType = actionType;
    }

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

    public Boolean getProjectCoordinator() {
        return projectCoordinator;
    }

    public void setProjectCoordinator(Boolean projectCoordinator) {
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

        EuGrantTransferResource that = (EuGrantTransferResource) o;

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
