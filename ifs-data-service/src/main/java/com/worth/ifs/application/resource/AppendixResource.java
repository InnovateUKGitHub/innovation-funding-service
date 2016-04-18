package com.worth.ifs.application.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.io.FileUtils;

public class AppendixResource {
    private Long applicationId;
    private String title;
    private Long formInputId;
    private String name;
    private Long fileSizeInBytes;

    public AppendixResource(Long applicationId, Long formInputId, String title, String name, Long fileSizeInBytes) {
        this.title = title;
        this.formInputId = formInputId;
        this.name = name;
        this.fileSizeInBytes = fileSizeInBytes;
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

    public String getName() {
        return name;
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
        return FileUtils.byteCountToDisplaySize(fileSizeInBytes);
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getFileSizeInBytes() {
        return fileSizeInBytes;
    }

    public void setFileSizeInBytes(Long fileSizeInBytes) {
        this.fileSizeInBytes = fileSizeInBytes;
    }
}
