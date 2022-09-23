package org.innovateuk.ifs.application.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.innovateuk.ifs.commons.error.HumanByteUtil;
import org.innovateuk.ifs.file.resource.FileEntryResource;

public class EoiEvidenceReadOnlyViewModel extends FileEntryResource {

    private Long applicationId;
    private String title;


    public EoiEvidenceReadOnlyViewModel(Long applicationId, String title, FileEntryResource fileEntryResource) {
        super(fileEntryResource.getId(), fileEntryResource.getName(), fileEntryResource.getMediaType(), fileEntryResource.getFilesizeBytes());
        this.applicationId = applicationId;
        this.title = title;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public String getTitle() {
        return title;
    }

    @JsonIgnore
    public String getHumanReadableFileSize() {
        return HumanByteUtil.byteCountToHuman(getFilesizeBytes());
    }
}
