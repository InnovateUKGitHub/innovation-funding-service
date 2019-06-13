package org.innovateuk.ifs.project.grantofferletter.form;

import org.innovateuk.ifs.controller.BaseBindingResultTarget;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

/**
 * Empty form to handle validation errors
 */
public class GrantOfferLetterLetterForm extends BaseBindingResultTarget {

    private MultipartFile annex;

    private MultipartFile grantOfferLetter;

    @NotNull(message = "{validation.project.grant.offer.letter.confirmation}")
    @AssertTrue(message = "{validation.project.grant.offer.letter.confirmation}")
    private Boolean confirmation;

    public Boolean getConfirmation() {
        return confirmation;
    }

    public void setConfirmation(Boolean confirmation) {
        this.confirmation = confirmation;
    }

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
