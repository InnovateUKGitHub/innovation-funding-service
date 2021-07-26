package org.innovateuk.ifs.management.competition.setup.core.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class TermsAndConditionsForm extends CompetitionSetupForm {

    @NotNull(message = "{validation.termsandconditionsform.field.required}")
    private Long termsAndConditionsId;

    private MultipartFile termsAndConditionsDoc;

    @NotBlank(message="{validation.thirdParty.agreementTitle.required}")
    private String thirdPartyAgreementTitle;

    @NotBlank(message="{validation.thirdParty.agreementContent.required}")
    private String thirdPartyAgreementContent;

    @NotBlank(message="{validation.thirdParty.projectCostGuidanceURL.required}")
    private String projectCostGuidanceURL;

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

    public String getThirdPartyAgreementTitle() {
        return thirdPartyAgreementTitle;
    }

    public void setThirdPartyAgreementTitle(String thirdPartyAgreementTitle) {
        this.thirdPartyAgreementTitle = thirdPartyAgreementTitle;
    }

    public String getThirdPartyAgreementContent() {
        return thirdPartyAgreementContent;
    }

    public void setThirdPartyAgreementContent(String thirdPartyAgreementContent) {
        this.thirdPartyAgreementContent = thirdPartyAgreementContent;
    }

    public String getProjectCostGuidanceURL() {
        return projectCostGuidanceURL;
    }

    public void setProjectCostGuidanceURL(String projectCostGuidanceURL) {
        this.projectCostGuidanceURL = projectCostGuidanceURL;
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