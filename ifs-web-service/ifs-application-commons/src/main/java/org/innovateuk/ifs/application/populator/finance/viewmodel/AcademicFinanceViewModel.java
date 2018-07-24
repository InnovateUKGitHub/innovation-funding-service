package org.innovateuk.ifs.application.populator.finance.viewmodel;

import org.innovateuk.ifs.application.populator.finance.form.AcademicFinance;

/**
 * Base viewmodel for academic finances
 */
public class AcademicFinanceViewModel extends BaseFinanceViewModel {
    private String title;
    private Long applicationFinanceId;
    private AcademicFinance academicFinance;
    private String filename;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getApplicationFinanceId() {
        return applicationFinanceId;
    }

    public void setApplicationFinanceId(Long applicationFinanceId) {
        this.applicationFinanceId = applicationFinanceId;
    }

    public AcademicFinance getAcademicFinance() {
        return academicFinance;
    }

    public void setAcademicFinance(AcademicFinance academicFinance) {
        this.academicFinance = academicFinance;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
