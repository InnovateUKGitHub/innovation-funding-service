package org.innovateuk.ifs.management.competition.setup.core.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

public class TermsAndConditionsForm extends CompetitionSetupForm {

    @NotNull(message = "validation.termsandconditionsform.field.required")
    private Long termsAndConditionsId;

    private MultipartFile termsAndConditionsDoc;

    public Long getTermsAndConditionsId() {
        return termsAndConditionsId;
    }

    public void setTermsAndConditionsId(Long termsAndConditionsId) {
        this.termsAndConditionsId = termsAndConditionsId;
    }

    public MultipartFile getTermsAndConditionsDoc() {
        return termsAndConditionsDoc;
    }

    public void setTermsAndConditionsDoc(MultipartFile termsAndConditionsDoc) {
        this.termsAndConditionsDoc = termsAndConditionsDoc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        TermsAndConditionsForm that = (TermsAndConditionsForm) o;

        return new EqualsBuilder()
                .append(termsAndConditionsId, that.termsAndConditionsId)
                .append(termsAndConditionsDoc, that.termsAndConditionsDoc)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(termsAndConditionsId)
                .append(termsAndConditionsDoc)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "TermsAndConditionsForm{" +
                "termsAndConditionsId=" + termsAndConditionsId +
                ", termsAndConditionsDoc=" + termsAndConditionsDoc +
                '}';
    }
}