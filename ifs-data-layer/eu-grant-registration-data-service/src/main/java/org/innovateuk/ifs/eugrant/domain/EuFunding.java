package org.innovateuk.ifs.eugrant.domain;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class EuFunding {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String callName;
    private String callId;
    private String topicId;
    private String projectName;

    private LocalDate projectStartDate;
    private LocalDate projectEndDate;

    private String grantAgreementNumber;
    private BigDecimal fundingContribution;

    private boolean projectCoordinator;

    @ManyToOne
    private EuActionType actionType;

    EuFunding() {
    }

    public EuFunding(EuActionType actionType,
                     String callName,
                     String callId,
                     String topicId,
                     String projectName,
                     LocalDate projectStartDate,
                     LocalDate projectEndDate,
                     String grantAgreementNumber,
                     BigDecimal fundingContribution,
                     boolean projectCoordinator) {
        this.actionType = actionType;
        this.callName = callName;
        this.callId = callId;
        this.topicId = topicId;
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

    public String getCallName() {
        return callName;
    }

    public void setCallName(String callName) {
        this.callName = callName;
    }

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
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

    public String getGrantAgreementNumber() {
        return grantAgreementNumber;
    }

    public void setGrantAgreementNumber(String grantAgreementNumber) {
        this.grantAgreementNumber = grantAgreementNumber;
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