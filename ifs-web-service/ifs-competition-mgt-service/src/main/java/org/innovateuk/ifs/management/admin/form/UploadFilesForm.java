package org.innovateuk.ifs.management.admin.form;

import org.springframework.web.multipart.MultipartFile;

public class UploadFilesForm {

    private String fileName;
    private MultipartFile file;

    public UploadFilesForm() {
        // for spring form binding
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}
