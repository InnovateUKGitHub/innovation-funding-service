package com.worth.ifs.application.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.worth.ifs.file.resource.FileEntryResource;
import org.apache.commons.io.FileUtils;

/**
 * This is used for displaying a list of appendix files at the bottom of comp admin read only application view
 * See INFUND-2283 and prototype page 1968-admin-readonly-application
 */
public class AppendixResource extends FileEntryResource {
    private Long applicationId;
    private String title;
    private Long formInputId;

    public AppendixResource(Long applicationId, Long formInputId, String title, FileEntryResource fileEntryResource) {
        super(fileEntryResource.getId(), fileEntryResource.getName(), fileEntryResource.getMediaType(), fileEntryResource.getFilesizeBytes());
        this.applicationId = applicationId;
        this.title = title;
        this.formInputId = formInputId;
    }

    public String getTitle() {
        return title;
    }

    public Long getFormInputId(){
        return formInputId;
    }

    public void setFormInputId(Long formInputId){
        this.formInputId = formInputId;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    @JsonIgnore
    public String getDownloadURL() {
        return applicationId + "/forminput/" + formInputId + "/download";
    }

    @JsonIgnore
    public String getHumanReadableFileSize() {
        return FileUtils.byteCountToDisplaySize(getFilesizeBytes());
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
