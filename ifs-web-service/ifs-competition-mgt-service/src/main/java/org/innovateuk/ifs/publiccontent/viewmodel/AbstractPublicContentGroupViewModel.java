package org.innovateuk.ifs.publiccontent.viewmodel;

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
}
