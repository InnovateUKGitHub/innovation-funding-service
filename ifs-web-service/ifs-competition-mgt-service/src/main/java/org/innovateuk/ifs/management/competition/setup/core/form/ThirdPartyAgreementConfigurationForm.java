package org.innovateuk.ifs.management.competition.setup.core.form;

import javax.validation.constraints.NotBlank;

public class ThirdPartyAgreementConfigurationForm extends TermsAndConditionsForm {

    @NotBlank(message="{validation.thirdParty.configurationform.agreementTitle.required}")
    private String thirdPartyAgreementTitle;

    public String getThirdPartyAgreementTitle() {
        return thirdPartyAgreementTitle;
    }

    public void setThirdPartyAgreementTitle(String thirdPartyAgreementTitle) {
        this.thirdPartyAgreementTitle = thirdPartyAgreementTitle;
    }
}
