package org.innovateuk.ifs.application.forms.yourprojectcosts.form;

import org.innovateuk.ifs.commons.validation.constraints.FieldRequiredIf;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.finance.resource.cost.Overhead;
import org.innovateuk.ifs.finance.resource.cost.OverheadRateType;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.groups.Default;
import java.math.BigDecimal;

import static org.innovateuk.ifs.finance.resource.cost.FinanceRowItem.*;
import static org.innovateuk.ifs.finance.resource.cost.OverheadRateType.TOTAL;

@FieldRequiredIf(required = "totalSpreadsheet", argument = "useSpreadsheetOption", predicate = true, message = NOT_BLANK_MESSAGE)
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

    public OverheadForm(Overhead overhead) {
        this.rateType = overhead.getRateType();
        this.totalSpreadsheet = overhead.getRate();
        this.filename = overhead.getCalculationFile().map(FileEntryResource::getName).orElse(null);
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

    public boolean isUseSpreadsheetOption() {
        return TOTAL.equals(rateType);
    }
}
