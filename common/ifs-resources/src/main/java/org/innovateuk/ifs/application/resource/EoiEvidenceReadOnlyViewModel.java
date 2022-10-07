package org.innovateuk.ifs.application.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.innovateuk.ifs.commons.error.HumanByteUtil;
import org.innovateuk.ifs.file.resource.FileEntryResource;

@Builder
@AllArgsConstructor
public class EoiEvidenceReadOnlyViewModel extends FileEntryResource {

    private final Long applicationId;
    private final boolean expressionOfInterestApplication;
    private final String title;

    public EoiEvidenceReadOnlyViewModel(Long applicationId, boolean expressionOfInterestApplication, String title, FileEntryResource fileEntryResource) {
        super(fileEntryResource.getId(), fileEntryResource.getName(), fileEntryResource.getMediaType(), fileEntryResource.getFilesizeBytes());
        this.applicationId = applicationId;
        this.expressionOfInterestApplication = expressionOfInterestApplication;
        this.title = title;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public boolean isExpressionOfInterestApplication() {
        return expressionOfInterestApplication;
    }

    public String getTitle() {
        return title;
    }

    @JsonIgnore
    public String getHumanReadableFileSize() {
        return HumanByteUtil.byteCountToHuman(getFilesizeBytes());
    }
}
