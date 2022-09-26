package org.innovateuk.ifs.application.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.innovateuk.ifs.commons.error.HumanByteUtil;
import org.innovateuk.ifs.file.resource.FileEntryResource;

public class EoiEvidenceReadOnlyViewModel extends FileEntryResource {

    private final Long applicationId;
    private final boolean expressionOfInterestApplication;
    private final boolean leadOrganisationMember;
    private final String title;

    public EoiEvidenceReadOnlyViewModel(Long applicationId, boolean expressionOfInterestApplication, boolean leadOrganisationMember,
                                        String title, FileEntryResource fileEntryResource) {
        super(fileEntryResource.getId(), fileEntryResource.getName(), fileEntryResource.getMediaType(), fileEntryResource.getFilesizeBytes());
        this.applicationId = applicationId;
        this.expressionOfInterestApplication = expressionOfInterestApplication;
        this.leadOrganisationMember = leadOrganisationMember;
        this.title = title;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public boolean isExpressionOfInterestApplication() {
        return expressionOfInterestApplication;
    }

    public boolean isLeadOrganisationMember() {
        return leadOrganisationMember;
    }

    public String getTitle() {
        return title;
    }

    @JsonIgnore
    public String getHumanReadableFileSize() {
        return HumanByteUtil.byteCountToHuman(getFilesizeBytes());
    }

    @JsonIgnore
    public boolean shouldDisplayEoiEvidenceUpload() {
        return isExpressionOfInterestApplication() && isLeadOrganisationMember();
    }
}
