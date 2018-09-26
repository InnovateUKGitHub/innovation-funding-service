package org.innovateuk.ifs.eugrant.domain;

import org.hibernate.validator.constraints.NotBlank;
import org.innovateuk.ifs.euactiontype.domain.EuActionType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
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

    @NotBlank
    private String grantAgreementNumber;

    @NotNull
    @Pattern(regexp="[\\d]{9}")
    private String participantId;

    @NotBlank
    private String projectName;

    @NotNull
    private LocalDate projectStartDate;

    @NotNull
    private LocalDate projectEndDate;

    @NotNull
    private BigDecimal fundingContribution;

    private boolean projectCoordinator;

    @ManyToOne
    @JoinColumn(name = "eu_action_type_id")
    private EuActionType actionType;

    public Long getId() {
        return id;
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