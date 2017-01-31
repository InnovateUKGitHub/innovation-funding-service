package org.innovateuk.ifs.publiccontent.form;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

/**
 * Abstract fields for public content form.
 */
public abstract class AbstractContentGroupForm extends AbstractPublicContentForm {

    private Integer uploadFile;
    private Integer removeFile;
    @NotEmpty
    @Valid
    private List<ContentGroupForm> contentGroups;
    private MultipartFile attachment;

    public List<ContentGroupForm> getContentGroups() {
        return contentGroups;
    }

    public void setContentGroups(List<ContentGroupForm> contentGroups) {
        this.contentGroups = contentGroups;
    }

    public Integer getUploadFile() {
        return uploadFile;
    }

    public void setUploadFile(Integer uploadFile) {
        this.uploadFile = uploadFile;
    }

    public Integer getRemoveFile() {
        return removeFile;
    }

    public void setRemoveFile(Integer removeFile) {
        this.removeFile = removeFile;
    }

    public MultipartFile getAttachment() {
        return attachment;
    }

    public void setAttachment(MultipartFile attachment) {
        this.attachment = attachment;
    }
}
