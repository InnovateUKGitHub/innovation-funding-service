package org.innovateuk.ifs.project.documents.form;

import org.innovateuk.ifs.controller.BaseBindingResultTarget;
import org.springframework.web.multipart.MultipartFile;

/**
 * Form backing the Other Documents page
 */
public class DocumentForm extends BaseBindingResultTarget {

    private MultipartFile document;

    public MultipartFile getDocument() {
        return document;
    }

    public void setDocument(MultipartFile document) {
        this.document = document;
    }
}
