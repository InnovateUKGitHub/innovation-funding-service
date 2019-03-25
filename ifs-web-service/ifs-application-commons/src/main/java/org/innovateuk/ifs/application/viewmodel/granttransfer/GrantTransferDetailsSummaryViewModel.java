package org.innovateuk.ifs.application.viewmodel.granttransfer;

import org.innovateuk.ifs.application.viewmodel.AbstractLeadOnlyViewModel;
import org.innovateuk.ifs.granttransfer.resource.EuActionTypeResource;

import java.math.BigDecimal;
import java.time.LocalDate;

public class GrantTransferDetailsSummaryViewModel extends AbstractLeadOnlyViewModel {

    private final String grantAgreementNumber;

    private final String participantId;

    private final String projectName;

    private final LocalDate startDate;

    private final LocalDate endDate;

    private final BigDecimal fundingContribution;

    private final Boolean projectCoordinator;

    private final EuActionTypeResource actionType;

    public GrantTransferDetailsSummaryViewModel(Long questionId, Long applicationId, boolean closed, boolean complete, boolean canMarkAsComplete, boolean allReadOnly) {
        this(questionId, applicationId, closed, complete, canMarkAsComplete, allReadOnly, null, null, null, null, null, null, null, null);
    }

    public GrantTransferDetailsSummaryViewModel(Long questionId, Long applicationId, boolean closed, boolean complete, boolean canMarkAsComplete, boolean allReadOnly, String grantAgreementNumber, String participantId, String projectName, LocalDate startDate, LocalDate endDate, BigDecimal fundingContribution, Boolean projectCoordinator, EuActionTypeResource actionType) {
        super(questionId, applicationId, closed, complete, canMarkAsComplete, allReadOnly);
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

    public EuActionTypeResource getActionType() {
        return actionType;
    }

    @Override
    public boolean isSummary() {
        return true;
    }
}
