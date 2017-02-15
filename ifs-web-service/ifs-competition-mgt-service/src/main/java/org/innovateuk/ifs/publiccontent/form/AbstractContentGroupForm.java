package org.innovateuk.ifs.publiccontent.form;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract fields for public content form.
 */
public abstract class AbstractContentGroupForm extends AbstractPublicContentForm {
    @Valid
    private List<ContentGroupForm> contentGroups = new ArrayList<>();

    private Long removeFile;

    private Integer uploadFile;

    public List<ContentGroupForm> getContentGroups() {
        return contentGroups;
    }

    public void setContentGroups(List<ContentGroupForm> contentGroups) {
        this.contentGroups = contentGroups;
    }

    public Long getRemoveFile() {
        return removeFile;
    }

    public void setRemoveFile(Long removeFile) {
        this.removeFile = removeFile;
    }

    public Integer getUploadFile() {
        return uploadFile;
    }

    public void setUploadFile(Integer uploadFile) {
        this.uploadFile = uploadFile;
    }
}
