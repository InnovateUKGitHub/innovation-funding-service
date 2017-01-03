package org.innovateuk.ifs.assessment.viewmodel.profile;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.assessment.controller.profile.AssessorProfileContractController.ContractAnnexParameter;

/**
 * Holder of model attributes for the Assessor Profile Terms of Contract Annex view.
 */
public class AssessorProfileContractAnnexViewModel {

    private ContractAnnexParameter annex;
    private String text;

    public AssessorProfileContractAnnexViewModel(ContractAnnexParameter annex, String text) {
        this.annex = annex;
        this.text = text;
    }

    public ContractAnnexParameter getAnnex() {
        return annex;
    }

    public void setAnnex(ContractAnnexParameter annex) {
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

        AssessorProfileContractAnnexViewModel that = (AssessorProfileContractAnnexViewModel) o;

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
