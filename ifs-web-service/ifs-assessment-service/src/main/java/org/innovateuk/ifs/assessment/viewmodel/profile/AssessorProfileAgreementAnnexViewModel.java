package org.innovateuk.ifs.assessment.viewmodel.profile;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.assessment.controller.profile.AssessorProfileAgreementController.AgreementAnnexParameter;

/**
 * Holder of model attributes for the Assessor Profile Agreement Annex view.
 */
public class AssessorProfileAgreementAnnexViewModel {

    private AgreementAnnexParameter annex;
    private String text;

    public AssessorProfileAgreementAnnexViewModel(AgreementAnnexParameter annex, String text) {
        this.annex = annex;
        this.text = text;
    }

    public AgreementAnnexParameter getAnnex() {
        return annex;
    }

    public void setAnnex(AgreementAnnexParameter annex) {
        this.annex = annex;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AssessorProfileAgreementAnnexViewModel that = (AssessorProfileAgreementAnnexViewModel) o;

        return new EqualsBuilder()
                .append(annex, that.annex)
                .append(text, that.text)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(annex)
                .append(text)
                .toHashCode();
    }
}
