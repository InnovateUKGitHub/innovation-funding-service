package org.innovateuk.ifs.application.forms.sections.yourfeccosts.form;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import org.innovateuk.ifs.commons.validation.constraints.FutureLocalDate;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.time.DateTimeException;
import java.time.LocalDate;

import static org.springframework.util.StringUtils.hasText;

/**
 * Form used to capture project fEC Model information
 */
@NoArgsConstructor
@Setter
@Getter
public class YourFECModelForm {

    @NotNull(message = "{validation.finance.fecmodel.fecModelEnabled.blank}")
    private Boolean fecModelEnabled;
    private Long fecFileEntryId;
    private String fecCertificateFileName;
    private MultipartFile fecCertificateFile;
    private boolean displayBanner;

    private String fecCertExpiryYear;
    private String fecCertExpiryMonth;
    private String fecCertExpiryDay;

    public void setFecCertExpiryDate(LocalDate fecCertExpiryDate) {
        if(fecCertExpiryDate != null) {
            fecCertExpiryYear = String.valueOf(fecCertExpiryDate.getYear());
            fecCertExpiryMonth = String.valueOf(fecCertExpiryDate.getMonthValue());
            fecCertExpiryDay = String.valueOf(fecCertExpiryDate.getDayOfMonth());
        }
    }

    public LocalDate getFecCertExpiryDate() {
        try {
            return (hasText(fecCertExpiryYear) && hasText(fecCertExpiryMonth) && hasText(fecCertExpiryDay)) ?
                    LocalDate.of(Integer.valueOf(fecCertExpiryYear), Integer.valueOf(fecCertExpiryMonth), Integer.valueOf(fecCertExpiryDay)) : null;
        } catch (DateTimeException e) {
            return null;
        }
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
                .append(fecCertExpiryYear, that.fecCertExpiryYear)
                .append(fecCertExpiryMonth, that.fecCertExpiryMonth)
                .append(fecCertExpiryDay, that.fecCertExpiryDay)
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
                .append(fecCertExpiryYear)
                .append(fecCertExpiryMonth)
                .append(fecCertExpiryDay)
                .toHashCode();
    }
}
