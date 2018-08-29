package org.innovateuk.ifs.eugrant.domain;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * The funding details for an {@link EuOrganisation} benefiting from an {@link EuGrant}.
 */
@Entity
public class EuFunding {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String grantAgreementNumber;
    private String particpantId; // 6 digit number

    private String projectName;
    private LocalDate projectStartDate;
    private LocalDate projectEndDate;

    private BigDecimal fundingContribution;

    private boolean projectCoordinator;

    @ManyToOne
    private EuActionType actionType;

    EuFunding() {
    }

    public EuFunding(EuActionType actionType,
                     String grantAgreementNumber,
                     String particpantId,
                     String projectName,
                     LocalDate projectStartDate,
                     LocalDate projectEndDate,
                     BigDecimal fundingContribution,
                     boolean projectCoordinator) {
        this.actionType = actionType;
        this.particpantId = particpantId;
        this.projectName = projectName;
        this.projectStartDate = projectStartDate;
        this.projectEndDate = projectEndDate;
        this.grantAgreementNumber = grantAgreementNumber;
        this.fundingContribution = fundingContribution;
        this.projectCoordinator = projectCoordinator;
    }

    public Long getId() {
        return id;
    }

    public String getGrantAgreementNumber() {
        return grantAgreementNumber;
    }

    public void setGrantAgreementNumber(String grantAgreementNumber) {
        this.grantAgreementNumber = grantAgreementNumber;
    }

    public String getParticpantId() {
        return particpantId;
    }

    public void setParticpantId(String particpantId) {
        this.particpantId = particpantId;
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

    public EuActionType getActionType() {
        return actionType;
    }

    public void setActionType(EuActionType actionType) {
        this.actionType = actionType;
    }
}