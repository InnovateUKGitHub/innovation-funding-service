package org.innovateuk.ifs.application.forms.sections.procurement.milestones.form;

import org.innovateuk.ifs.commons.validation.constraints.WordCount;
import org.innovateuk.ifs.procurement.milestone.resource.ApplicationProcurementMilestoneResource;
import org.innovateuk.ifs.procurement.milestone.resource.ProcurementMilestoneResource;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import static java.util.Optional.ofNullable;

public class ProcurementMilestoneForm {

    private Long id;
    @NotNull(message = "{validation.procurement.milestones.month}")
    private Integer month;
    @Size(max = 255, message = "{validation.field.too.many.characters}")
    private String description;
    @NotBlank(message = "{validation.procurement.milestones.taskOrActivity}")
    @WordCount(max = 200, message = "{validation.field.max.word.count}")
    private String taskOrActivity;
    @WordCount(max = 200, message = "{validation.field.max.word.count}")
    private String deliverable;
    @WordCount(max = 200, message = "{validation.field.max.word.count}")
    private String successCriteria;
    @NotNull(message = "{validation.procurement.milestones.payment}")
    private BigInteger payment;

    public ProcurementMilestoneForm() {}

    public ProcurementMilestoneForm(int index) {
        this.description = descriptionIfNull(index);
        this.month = monthIfNull(index);
    }

    public <R extends ProcurementMilestoneResource> ProcurementMilestoneForm(R resource, int index) {
        this.id = resource.getId();
        this.month = ofNullable(resource.getMonth()).orElse(monthIfNull(index));
        this.description = ofNullable(resource.getDescription()).orElse(descriptionIfNull(index));
        this.taskOrActivity = resource.getTaskOrActivity();
        this.deliverable = resource.getDeliverable();
        this.successCriteria = resource.getSuccessCriteria();
        this.payment = resource.getPayment();
    }

    private String descriptionIfNull(int index) {
        return "Milestone " + (index + 1);
    }
    private int monthIfNull(int index) {
        return index + 1;
    }

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
        return payment == null ? BigInteger.ZERO : payment;
    }

    public void setPayment(BigInteger payment) {
        this.payment = payment;
    }

    public BigDecimal getPercentageOfFundingAmount(BigInteger totalCosts) {
        if (totalCosts == null || payment == null || totalCosts.equals(BigInteger.ZERO)) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(payment)
                .multiply(new BigDecimal("100"))
                .divide(new BigDecimal(totalCosts), 2, RoundingMode.HALF_UP);
    }

    public void copyToResource(ApplicationProcurementMilestoneResource resource) {
        resource.setId(id);
        resource.setMonth(month);
        resource.setDescription(description);
        resource.setDeliverable(deliverable);
        resource.setTaskOrActivity(taskOrActivity);
        resource.setSuccessCriteria(successCriteria);
        resource.setPayment(payment);
    }
}
