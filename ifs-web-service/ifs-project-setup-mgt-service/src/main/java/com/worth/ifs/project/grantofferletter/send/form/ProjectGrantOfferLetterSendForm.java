package com.worth.ifs.project.grantofferletter.send.form;

import com.worth.ifs.controller.BaseBindingResultTarget;
import org.springframework.web.multipart.MultipartFile;

/**
 * Empty form to handle validation errors
 */
public class ProjectGrantOfferLetterSendForm extends BaseBindingResultTarget {

    private MultipartFile annex;

    public MultipartFile getAnnex() {
        return annex;
    }

    public void setAnnex(MultipartFile annex) {
        this.annex = annex;
    }
}
