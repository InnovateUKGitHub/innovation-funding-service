package org.innovateuk.ifs.project.documents.form;

import org.innovateuk.ifs.controller.BaseBindingResultTarget;
import org.springframework.web.multipart.MultipartFile;

/**
 * Form backing the Other Documents page
 */
public class DocumentsForm extends BaseBindingResultTarget {

    private MultipartFile uploadDocument;

    public MultipartFile getUploadDocument() {
        return uploadDocument;
    }

    public void setUploadDocument(MultipartFile uploadDocument) {
        this.uploadDocument = uploadDocument;
    }
}
