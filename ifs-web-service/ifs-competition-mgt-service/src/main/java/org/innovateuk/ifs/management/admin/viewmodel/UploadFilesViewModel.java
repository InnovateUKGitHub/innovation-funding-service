package org.innovateuk.ifs.management.admin.viewmodel;

import org.innovateuk.ifs.fileupload.resource.FileUploadType;

public class UploadFilesViewModel {

    private FileUploadType fileUploadType;

    public UploadFilesViewModel(FileUploadType fileUploadType)
    {
        this.fileUploadType = fileUploadType;
    }
}
