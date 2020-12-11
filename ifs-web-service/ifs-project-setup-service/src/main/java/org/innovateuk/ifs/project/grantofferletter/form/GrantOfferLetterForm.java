package org.innovateuk.ifs.project.grantofferletter.form;

import org.innovateuk.ifs.controller.BaseBindingResultTarget;
import org.springframework.web.multipart.MultipartFile;

/**
 * Form backing the Grant offer letter page
 **/
public class GrantOfferLetterForm extends BaseBindingResultTarget {

    private MultipartFile signedAdditionalContract;
    private MultipartFile signedGrantOfferLetter;

    public MultipartFile getSignedAdditionalContract() {
        return signedAdditionalContract;
    }

    public void setSignedAdditionalContract(MultipartFile signedAdditionalContract) {
        this.signedAdditionalContract = signedAdditionalContract;
    }

    public MultipartFile getSignedGrantOfferLetter() {
        return signedGrantOfferLetter;
    }

    public void setSignedGrantOfferLetter(MultipartFile signedGrantOfferLetter) {
        this.signedGrantOfferLetter = signedGrantOfferLetter;
    }
}
