package org.innovateuk.ifs.application.forms.questions.grantagreement.form;

import org.springframework.web.multipart.MultipartFile;

public class GrantAgreementForm {

    private MultipartFile grantAgreement;

    public MultipartFile getGrantAgreement() {
        return grantAgreement;
    }

    public void setGrantAgreement(MultipartFile grantAgreement) {
        this.grantAgreement = grantAgreement;
    }
}
