package org.innovateuk.ifs.application.forms.academiccosts.form;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

import static org.innovateuk.ifs.finance.resource.cost.FinanceRowItem.NOT_BLANK_MESSAGE;

public class AcademicCostForm {

    @NotBlank(message = NOT_BLANK_MESSAGE)
    private String tsbReference;

    private BigDecimal incurredStaff;
    private BigDecimal incurredTravel;
    private BigDecimal incurredOtherCosts;

    private BigDecimal allocatedInvestigators;
    private BigDecimal allocatedEstateCosts;
    private BigDecimal allocatedOtherCosts;

    private BigDecimal indirectCosts;

    private BigDecimal exceptionsStaff;
    private BigDecimal exceptionsOtherCosts;

    private MultipartFile jesFile;
    private String filename;

    public String getTsbReference() {
        return tsbReference;
    }

    public void setTsbReference(String tsbReference) {
        this.tsbReference = tsbReference;
    }

    public BigDecimal getIncurredStaff() {
        return incurredStaff;
    }

    public void setIncurredStaff(BigDecimal incurredStaff) {
        this.incurredStaff = incurredStaff;
    }

    public BigDecimal getIncurredTravel() {
        return incurredTravel;
    }

    public void setIncurredTravel(BigDecimal incurredTravel) {
        this.incurredTravel = incurredTravel;
    }

    public BigDecimal getIncurredOtherCosts() {
        return incurredOtherCosts;
    }

    public void setIncurredOtherCosts(BigDecimal incurredOtherCosts) {
        this.incurredOtherCosts = incurredOtherCosts;
    }

    public BigDecimal getAllocatedInvestigators() {
        return allocatedInvestigators;
    }

    public void setAllocatedInvestigators(BigDecimal allocatedInvestigators) {
        this.allocatedInvestigators = allocatedInvestigators;
    }

    public BigDecimal getAllocatedEstateCosts() {
        return allocatedEstateCosts;
    }

    public void setAllocatedEstateCosts(BigDecimal allocatedEstateCosts) {
        this.allocatedEstateCosts = allocatedEstateCosts;
    }

    public BigDecimal getAllocatedOtherCosts() {
        return allocatedOtherCosts;
    }

    public void setAllocatedOtherCosts(BigDecimal allocatedOtherCosts) {
        this.allocatedOtherCosts = allocatedOtherCosts;
    }

    public BigDecimal getIndirectCosts() {
        return indirectCosts;
    }

    public void setIndirectCosts(BigDecimal indirectCosts) {
        this.indirectCosts = indirectCosts;
    }

    public BigDecimal getExceptionsStaff() {
        return exceptionsStaff;
    }

    public void setExceptionsStaff(BigDecimal exceptionsStaff) {
        this.exceptionsStaff = exceptionsStaff;
    }

    public BigDecimal getExceptionsOtherCosts() {
        return exceptionsOtherCosts;
    }

    public void setExceptionsOtherCosts(BigDecimal exceptionsOtherCosts) {
        this.exceptionsOtherCosts = exceptionsOtherCosts;
    }

    public MultipartFile getJesFile() {
        return jesFile;
    }

    public void setJesFile(MultipartFile jesFile) {
        this.jesFile = jesFile;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    /* View logic */
    public BigDecimal getTotalIncurred() {
        return incurredStaff.add(incurredTravel).add(incurredOtherCosts);
    }

    public BigDecimal getTotalAllocated() {
        return allocatedInvestigators.add(allocatedEstateCosts).add(allocatedOtherCosts);
    }

    public BigDecimal getTotalExceptions() {
        return exceptionsStaff.add(exceptionsOtherCosts);
    }

    public BigDecimal getTotal() {
        return getTotalIncurred().add(getTotalAllocated()).add(indirectCosts).add(getTotalExceptions());
    }

}
