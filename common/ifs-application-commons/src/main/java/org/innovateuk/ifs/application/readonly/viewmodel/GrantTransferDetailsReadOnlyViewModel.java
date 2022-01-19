package org.innovateuk.ifs.application.readonly.viewmodel;

import org.innovateuk.ifs.application.readonly.ApplicationReadOnlyData;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.granttransfer.resource.EuActionTypeResource;

import java.math.BigDecimal;
import java.time.LocalDate;

public class GrantTransferDetailsReadOnlyViewModel extends AbstractQuestionReadOnlyViewModel {

    private final String grantAgreementNumber;

    private final String participantId;

    private final String projectName;

    private final LocalDate startDate;

    private final LocalDate endDate;

    private final BigDecimal fundingContribution;

    private final Boolean projectCoordinator;

    private final EuActionTypeResource actionType;

    public GrantTransferDetailsReadOnlyViewModel(ApplicationReadOnlyData data, QuestionResource question) {
        this(data, question, null, null, null, null, null, null, null, null);
    }

    public GrantTransferDetailsReadOnlyViewModel(ApplicationReadOnlyData data, QuestionResource question, String grantAgreementNumber, String participantId, String projectName, LocalDate startDate, LocalDate endDate, BigDecimal fundingContribution, Boolean projectCoordinator, EuActionTypeResource actionType) {
        super(data, question);
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
    public String getFragment() {
        return "grant-transfer-details";
    }

    @Override
    public boolean shouldDisplayMarkAsComplete() {
        return false;
    }
}
