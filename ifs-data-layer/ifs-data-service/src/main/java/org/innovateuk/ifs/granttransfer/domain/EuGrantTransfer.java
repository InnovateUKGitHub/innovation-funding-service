package org.innovateuk.ifs.granttransfer.domain;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.file.domain.FileEntry;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class EuGrantTransfer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="application_id", referencedColumnName="id")
    private Application application;

    private String grantAgreementNumber;

    private String participantId;

    private LocalDate projectStartDate;

    private LocalDate projectEndDate;

    private BigDecimal fundingContribution;

    private boolean projectCoordinator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eu_action_type_id")
    private EuActionType actionType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="grant_agreement_id", referencedColumnName="id")
    private FileEntry grantAgreement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="calculation_spreadsheet_id", referencedColumnName="id")
    private FileEntry calculationSpreadsheet;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public EuActionType getActionType() {
        return actionType;
    }

    public void setActionType(EuActionType actionType) {
        this.actionType = actionType;
    }

    public FileEntry getGrantAgreement() {
        return grantAgreement;
    }

    public void setGrantAgreement(FileEntry grantAgreement) {
        this.grantAgreement = grantAgreement;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public FileEntry getCalculationSpreadsheet() {
        return calculationSpreadsheet;
    }

    public void setCalculationSpreadsheet(FileEntry calculationSpreadsheet) {
        this.calculationSpreadsheet = calculationSpreadsheet;
    }
}
