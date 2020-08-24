package org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form;

import org.innovateuk.ifs.finance.resource.cost.Overhead;
import org.innovateuk.ifs.finance.resource.cost.OverheadRateType;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.groups.Default;
import java.math.BigDecimal;

import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowItem.*;
import static org.innovateuk.ifs.finance.resource.cost.OverheadRateType.TOTAL;

public class OverheadForm {

    private Long costId;

    private OverheadRateType rateType;

    @Min(value = 0, groups = Default.class, message = VALUE_MUST_BE_HIGHER_MESSAGE)
    @Digits(integer = MAX_DIGITS_INT, fraction = 0, message = MAX_DIGITS_MESSAGE)
    private Integer totalSpreadsheet;
    private String filename;
    private BigDecimal total;

    private MultipartFile file;

    public OverheadForm() {
    }

    public OverheadForm(Overhead overhead, String filename) {
        this.rateType = overhead.getRateType();
        if (TOTAL.equals(rateType)) {
            this.totalSpreadsheet = overhead.getRate();
        } else {
            this.totalSpreadsheet = 0;
        }
        this.filename = filename;
        this.total = overhead.getTotal();
        this.costId = overhead.getId();
    }

    public OverheadRateType getRateType() {
        return rateType;
    }

    public void setRateType(OverheadRateType rateType) {
        this.rateType = rateType;
    }

    public Integer getTotalSpreadsheet() {
        return totalSpreadsheet;
    }

    public void setTotalSpreadsheet(Integer totalSpreadsheet) {
        this.totalSpreadsheet = totalSpreadsheet;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Long getCostId() {
        return costId;
    }

    public void setCostId(Long costId) {
        this.costId = costId;
    }

    public BigDecimal calculateTotal(BigDecimal labourTotal) {
        if (rateType == null) {
            return BigDecimal.ZERO;
        }
        switch(rateType) {
            case NONE:
                return BigDecimal.ZERO;
            case DEFAULT_PERCENTAGE:
                return labourTotal.multiply(new BigDecimal(rateType.getRate()));
            case TOTAL:
            case HORIZON_2020_TOTAL:
                return ofNullable(getTotalSpreadsheet()).map(BigDecimal::new).orElse(BigDecimal.ZERO);
        }
        return BigDecimal.ZERO;
    }
}
