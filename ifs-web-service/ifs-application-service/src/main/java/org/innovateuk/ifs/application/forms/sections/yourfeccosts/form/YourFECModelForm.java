package org.innovateuk.ifs.application.forms.sections.yourfeccosts.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

/**
 * Form used to capture project fEC Model information
 */
public class YourFECModelForm {

    @NotNull(message = "{validation.finance.fecmodel.fecModelEnabled.blank}")
    private Boolean fecModelEnabled;
    private Long fecFileEntryId;
    private String fecCertificateFileName;
    private MultipartFile fecCertificateFile;
    private boolean displayBanner;

    public YourFECModelForm() {
    }

    public Boolean getFecModelEnabled() {
        return fecModelEnabled;
    }

    public void setFecModelEnabled(Boolean fecModelEnabled) {
        this.fecModelEnabled = fecModelEnabled;
    }

    public Long getFecFileEntryId() {
        return fecFileEntryId;
    }

    public void setFecFileEntryId(Long fecFileEntryId) {
        this.fecFileEntryId = fecFileEntryId;
    }

    public String getFecCertificateFileName() {
        return fecCertificateFileName;
    }

    public void setFecCertificateFileName(String fecCertificateFileName) {
        this.fecCertificateFileName = fecCertificateFileName;
    }

    public MultipartFile getFecCertificateFile() {
        return fecCertificateFile;
    }

    public void setFecCertificateFile(MultipartFile fecCertificateFile) {
        this.fecCertificateFile = fecCertificateFile;
    }

    public boolean isDisplayBanner() {
        return displayBanner;
    }

    public void setDisplayBanner(boolean displayBanner) {
        this.displayBanner = displayBanner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        YourFECModelForm that = (YourFECModelForm) o;
        return new EqualsBuilder()
                .append(fecModelEnabled, that.fecModelEnabled)
                .append(fecFileEntryId, that.fecFileEntryId)
                .append(fecCertificateFileName, that.fecCertificateFileName)
                .append(fecCertificateFile, that.fecCertificateFile)
                .append(displayBanner, that.displayBanner)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(fecModelEnabled)
                .append(fecFileEntryId)
                .append(fecCertificateFileName)
                .append(fecCertificateFile)
                .append(displayBanner)
                .toHashCode();
    }
}
