package org.innovateuk.ifs.management.competition.setup.core.form;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.Objects;

public class TermsAndConditionsForm extends CompetitionSetupForm {

    @NotNull(message = "{validation.termsandconditionsform.field.required}")
    private Long termsAndConditionsId;

    private MultipartFile termsAndConditionsDoc;
    private MultipartFile thirdPartyTermsAndConditionsDoc;

    private String thirdPartyTermsAndConditionsLabel;

    private String thirdPartyTermsAndConditionsText;

    private String projectCostGuidanceLink;

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

    public String getThirdPartyTermsAndConditionsLabel() {
        return thirdPartyTermsAndConditionsLabel;
    }

    public void setThirdPartyTermsAndConditionsLabel(String thirdPartyTermsAndConditionsLabel) {
        this.thirdPartyTermsAndConditionsLabel = thirdPartyTermsAndConditionsLabel;
    }

    public String getThirdPartyTermsAndConditionsText() {
        return thirdPartyTermsAndConditionsText;
    }

    public void setThirdPartyTermsAndConditionsText(String thirdPartyTermsAndConditionsText) {
        this.thirdPartyTermsAndConditionsText = thirdPartyTermsAndConditionsText;
    }

    public String getProjectCostGuidanceLink() {
        return projectCostGuidanceLink;
    }

    public void setProjectCostGuidanceLink(String projectCostGuidanceLink) {
        this.projectCostGuidanceLink = projectCostGuidanceLink;
    }

    public MultipartFile getThirdPartyTermsAndConditionsDoc() {
        return thirdPartyTermsAndConditionsDoc;
    }

    public void setThirdPartyTermsAndConditionsDoc(MultipartFile thirdPartyTermsAndConditionsDoc) {
        this.thirdPartyTermsAndConditionsDoc = thirdPartyTermsAndConditionsDoc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TermsAndConditionsForm that = (TermsAndConditionsForm) o;
        return Objects.equals(termsAndConditionsId, that.termsAndConditionsId) &&
                Objects.equals(termsAndConditionsDoc, that.termsAndConditionsDoc) &&
                Objects.equals(thirdPartyTermsAndConditionsLabel, that.thirdPartyTermsAndConditionsLabel) &&
                Objects.equals(thirdPartyTermsAndConditionsText, that.thirdPartyTermsAndConditionsText) &&
                Objects.equals(projectCostGuidanceLink, that.projectCostGuidanceLink);
    }

    @Override
    public int hashCode() {
        return Objects.hash(termsAndConditionsId, termsAndConditionsDoc, thirdPartyTermsAndConditionsLabel, thirdPartyTermsAndConditionsText, projectCostGuidanceLink);
    }

    @Override
    public String toString() {
        return "TermsAndConditionsForm{" +
                "termsAndConditionsId=" + termsAndConditionsId +
                ", termsAndConditionsDoc=" + termsAndConditionsDoc +
                ", thirdPartyTermsAndConditionsLabel='" + thirdPartyTermsAndConditionsLabel + '\'' +
                ", thirdPartyTermsAndConditionsText='" + thirdPartyTermsAndConditionsText + '\'' +
                ", projectCostGuidanceLink='" + projectCostGuidanceLink + '\'' +
                '}';
    }
}