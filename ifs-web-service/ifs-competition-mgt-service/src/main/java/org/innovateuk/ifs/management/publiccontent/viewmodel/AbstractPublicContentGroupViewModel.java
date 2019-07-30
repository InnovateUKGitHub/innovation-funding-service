package org.innovateuk.ifs.management.publiccontent.viewmodel;

import org.innovateuk.ifs.file.resource.FileEntryResource;

import java.util.Map;

public abstract class AbstractPublicContentGroupViewModel extends AbstractPublicContentViewModel {

    private Map<Long, FileEntryResource> fileEntries;

    public Map<Long, FileEntryResource> getFileEntries() {
        return fileEntries;
    }

    public void setFileEntries(Map<Long, FileEntryResource> fileEntries) {
        this.fileEntries = fileEntries;
    }

    public boolean hasAttachment(Long contentGroupId) {
        return fileEntries.get(contentGroupId) != null && fileEntries.get(contentGroupId).getId() != null;
    }

    public Long id(Long contentGroupId) {
        if (hasAttachment(contentGroupId)) {
            return fileEntries.get(contentGroupId).getId();
        }
        return null;
    }

    public String fileName(Long contentGroupId) {
        return fileEntries.get(contentGroupId).getName();
    }
}
