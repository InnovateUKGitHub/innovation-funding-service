package org.innovateuk.ifs.management.admin.viewmodel;

public class UploadFilesViewModel {

    private final boolean uploadSuccess;

    public UploadFilesViewModel(boolean uploadSuccess) {
        this.uploadSuccess = uploadSuccess;
    }

    public boolean isUploadSuccess() {
        return uploadSuccess;
    }
}
