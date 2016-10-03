package com.worth.ifs.project.grantofferletter.form;

import com.worth.ifs.controller.BaseBindingResultTarget;
import org.springframework.web.multipart.MultipartFile;

/**
 * Form backing the Grant offer letter page
 **/
public class ProjectGrantOfferLetterForm extends BaseBindingResultTarget {

    private MultipartFile grantOfferLetter;
    private MultipartFile signedGrantOfferLetter;
    private MultipartFile additionalContract;


    public MultipartFile getGrantOfferLetter() {
        return grantOfferLetter;
    }

    public void setGrantOfferLetter(MultipartFile grantOfferLetter) {
        this.grantOfferLetter = grantOfferLetter;
    }

    public MultipartFile getAdditionalContract() {
        return additionalContract;
    }

    public void setAdditionalContract(MultipartFile additionalContract) {
        this.additionalContract = additionalContract;
    }

    public MultipartFile getSignedGrantOfferLetter() {
        return signedGrantOfferLetter;
    }

    public void setSignedGrantOfferLetter(MultipartFile signedGrantOfferLetter) {
        this.signedGrantOfferLetter = signedGrantOfferLetter;
    }
}
