package org.innovateuk.ifs.publiccontent.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final AbstractContentGroupForm that = (AbstractContentGroupForm) o;

        return new EqualsBuilder()
                .append(contentGroups, that.contentGroups)
                .append(removeFile, that.removeFile)
                .append(uploadFile, that.uploadFile)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(contentGroups)
                .append(removeFile)
                .append(uploadFile)
                .toHashCode();
    }
}
