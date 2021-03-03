package org.innovateuk.ifs.procurement.milestone.domain;


import org.innovateuk.ifs.commons.util.AuditableEntity;

import javax.persistence.*;
import java.math.BigInteger;

@MappedSuperclass
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public class ProcurementMilestone extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Integer month;

    @Column
    private String description;

    @Column
    private String taskOrActivity;

    @Column
    private String deliverable;

    @Column
    private String successCriteria;

    @Column
    private BigInteger payment;

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
}
