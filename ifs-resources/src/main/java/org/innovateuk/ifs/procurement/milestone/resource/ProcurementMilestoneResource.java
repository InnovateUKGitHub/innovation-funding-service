package org.innovateuk.ifs.procurement.milestone.resource;

import java.math.BigInteger;

public abstract class ProcurementMilestoneResource {

    private Long id;
    private Integer month;
    private String description;
    private String taskOrActivity;
    private String deliverable;
    private String successCriteria;
    private BigInteger payment;
    private long organisationId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTaskOrActivity() {
        return taskOrActivity;
    }

    public void setTaskOrActivity(String taskOrActivity) {
        this.taskOrActivity = taskOrActivity;
    }

    public String getDeliverable() {
        return deliverable;
    }

    public void setDeliverable(String deliverable) {
        this.deliverable = deliverable;
    }

    public String getSuccessCriteria() {
        return successCriteria;
    }

    public void setSuccessCriteria(String successCriteria) {
        this.successCriteria = successCriteria;
    }

    public BigInteger getPayment() {
        return payment;
    }

    public void setPayment(BigInteger payment) {
        this.payment = payment;
    }

    public long getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(long organisationId) {
        this.organisationId = organisationId;
    }
}
