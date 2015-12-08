package com.worth.ifs.file.resource;

/**
 * A Resource representing a FormInputResponseFile
 */
public class FormInputResponseFileResource extends BaseFileResource {

    private Long formInputResponseId;

    public FormInputResponseFileResource() {
        super(null);
    }

    public FormInputResponseFileResource(Long id, Long formInputResponseId) {
        super(id);
        this.formInputResponseId = formInputResponseId;
    }

    public Long getFormInputResponseId() {
        return formInputResponseId;
    }
}
