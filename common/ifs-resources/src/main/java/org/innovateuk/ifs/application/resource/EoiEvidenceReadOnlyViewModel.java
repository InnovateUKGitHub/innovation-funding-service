package org.innovateuk.ifs.application.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.innovateuk.ifs.commons.error.HumanByteUtil;
import org.innovateuk.ifs.file.resource.FileEntryResource;

public class EoiEvidenceReadOnlyViewModel extends FileEntryResource {

    private Long applicationId;
    private boolean leadOrganisationMember
    private String title;

    public EoiEvidenceReadOnlyViewModel(Long applicationId, boolean leadOrganisationMember, String title, FileEntryResource fileEntryResource) {
        super(fileEntryResource.getId(), fileEntryResource.getName(), fileEntryResource.getMediaType(), fileEntryResource.getFilesizeBytes());
        this.applicationId = applicationId;
        this.leadOrganisationMember = leadOrganisationMember;
        this.title = title;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public String getTitle() {
        return title;
    }

    public boolean isLeadOrganisationMember() {
        return leadOrganisationMember;
    }

    @JsonIgnore
    public String getHumanReadableFileSize() {
        return HumanByteUtil.byteCountToHuman(getFilesizeBytes());
    }
}
