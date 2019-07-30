package org.innovateuk.ifs.management.competition.setup.core.form;

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
}