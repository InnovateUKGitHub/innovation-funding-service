package org.innovateuk.ifs.competition.viewmodel.publiccontent;

import org.innovateuk.ifs.competition.publiccontent.resource.ContentGroupResource;
import org.innovateuk.ifs.file.resource.FileEntryResource;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public abstract class AbstractPublicContentGroupViewModel extends AbstractPublicSectionContentViewModel {

    private Map<Long, FileEntryResource> fileEntries;

    public Map<Long, FileEntryResource> getFileEntries() {
        return fileEntries;
    }

    public List<ContentGroupResource> contentGroups;

    public void setFileEntries(Map<Long, FileEntryResource> fileEntries) {
        this.fileEntries = fileEntries;
    }

    public List<ContentGroupResource> getContentGroups() {
        return contentGroups;
    }

    public void setContentGroups(List<ContentGroupResource> contentGroups) {
        this.contentGroups = contentGroups;
    }

    public List<ContentGroupResource> getContentGroupsOrdered() {
        List<ContentGroupResource> contentGroupsOrdered = contentGroups;
        contentGroupsOrdered.sort(Comparator.comparing(ContentGroupResource::getPriority));
        return contentGroupsOrdered;
    }

    public boolean hasAttachment(Long contentGroupId) {
        return fileEntries != null
                && fileEntries.get(contentGroupId) != null
                && fileEntries.get(contentGroupId).getId() != null;
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
