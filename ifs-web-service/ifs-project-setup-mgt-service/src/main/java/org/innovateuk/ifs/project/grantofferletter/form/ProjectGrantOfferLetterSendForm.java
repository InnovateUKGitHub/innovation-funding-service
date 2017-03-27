package org.innovateuk.ifs.project.grantofferletter.form;

import org.innovateuk.ifs.controller.BaseBindingResultTarget;
import org.springframework.web.multipart.MultipartFile;

/**
 * Empty form to handle validation errors
 */
public class ProjectGrantOfferLetterSendForm extends BaseBindingResultTarget {

    private MultipartFile annex;

    private MultipartFile grantOfferLetter;

    public MultipartFile getAnnex() {
        return annex;
    }

    public void setAnnex(MultipartFile annex) {
        this.annex = annex;
    }

    public MultipartFile getGrantOfferLetter() {
        return grantOfferLetter;
    }

    public void setGrantOfferLetter(MultipartFile grantOfferLetter) {
        this.grantOfferLetter = grantOfferLetter;
    }
}
