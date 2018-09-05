package org.innovateuk.ifs.eugrant;

import java.math.BigDecimal;
import java.time.LocalDate;

public class EuFundingResource {

    private String grantAgreementNumber;

    private String participantId;

    private String projectName;

    private LocalDate projectStartDate;

    private LocalDate projectEndDate;

    private BigDecimal fundingContribution;

    private boolean projectCoordinator;

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
}
