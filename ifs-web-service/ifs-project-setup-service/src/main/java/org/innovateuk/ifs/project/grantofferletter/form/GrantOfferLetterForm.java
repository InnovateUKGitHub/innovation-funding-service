package org.innovateuk.ifs.project.grantofferletter.form;

import org.innovateuk.ifs.controller.BaseBindingResultTarget;
import org.springframework.web.multipart.MultipartFile;

/**
 * Form backing the Grant offer letter page
 **/
public class GrantOfferLetterForm extends BaseBindingResultTarget {

    private MultipartFile signedGrantOfferLetter;

    public MultipartFile getSignedGrantOfferLetter() {
        return signedGrantOfferLetter;
    }

    public void setSignedGrantOfferLetter(MultipartFile signedGrantOfferLetter) {
        this.signedGrantOfferLetter = signedGrantOfferLetter;
    }
}
