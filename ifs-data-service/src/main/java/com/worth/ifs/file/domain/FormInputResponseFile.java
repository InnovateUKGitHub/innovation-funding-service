package com.worth.ifs.file.domain;

import javax.persistence.Entity;

/**
 * Represents a File Upload that is linked to a specific FormInputResponse.  An example of this would be
 * an uploaded Appendix document to accompany a question response in an Application Form.
 */
@Entity
public class FormInputResponseFile extends BaseFile {

    private Long formInputResponseId;

    public Long getFormInputResponseId() {
        return formInputResponseId;
    }
}
