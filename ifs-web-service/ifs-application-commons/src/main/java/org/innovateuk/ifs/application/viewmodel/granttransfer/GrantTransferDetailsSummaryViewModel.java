package org.innovateuk.ifs.application.viewmodel.granttransfer;

import java.math.BigDecimal;
import java.time.LocalDate;

public class GrantTransferDetailsSummaryViewModel {

    private final String grantAgreementNumber;

    private final  String participantId;

    private final  String projectName;

    private final  LocalDate startDate;

    private final  LocalDate endDate;

    private final  BigDecimal fundingContribution;

    private final  Boolean projectCoordinator;

    private final  String actionType;

    private GrantTransferDetailsSummaryViewModel() {
        this(null, null, null, null, null, null, null, null);
    }

    public GrantTransferDetailsSummaryViewModel(String grantAgreementNumber, String participantId, String projectName, LocalDate startDate, LocalDate endDate, BigDecimal fundingContribution, Boolean projectCoordinator, String actionType) {
        this.grantAgreementNumber = grantAgreementNumber;
        this.participantId = participantId;
        this.projectName = projectName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.fundingContribution = fundingContribution;
        this.projectCoordinator = projectCoordinator;
        this.actionType = actionType;
    }

    public String getGrantAgreementNumber() {
        return grantAgreementNumber;
    }

    public String getParticipantId() {
        return participantId;
    }

    public String getProjectName() {
        return projectName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public BigDecimal getFundingContribution() {
        return fundingContribution;
    }

    public Boolean getProjectCoordinator() {
        return projectCoordinator;
    }

    public String getActionType() {
        return actionType;
    }

    public static GrantTransferDetailsSummaryViewModel empty() {
        return new GrantTransferDetailsSummaryViewModel();
    }
}
