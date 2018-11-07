package org.innovateuk.ifs.application.forms.yourprojectcosts.form;

import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.finance.resource.cost.Overhead;
import org.innovateuk.ifs.finance.resource.cost.OverheadRateType;
import org.springframework.web.multipart.MultipartFile;

public class OverheadForm {

    private OverheadRateType rateType;
    private Integer totalSpreadsheet;
    private String filename;
    private MultipartFile overheadfile;

    public OverheadForm() {
    }

    public OverheadForm(Overhead overhead) {
        this.rateType = overhead.getRateType();
        this.totalSpreadsheet = overhead.getRate();
        this.filename = overhead.getCalculationFile().map(FileEntryResource::getName).orElse("");
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

    public MultipartFile getOverheadfile() {
        return overheadfile;
    }

    public void setOverheadfile(MultipartFile overheadfile) {
        this.overheadfile = overheadfile;
    }
}
