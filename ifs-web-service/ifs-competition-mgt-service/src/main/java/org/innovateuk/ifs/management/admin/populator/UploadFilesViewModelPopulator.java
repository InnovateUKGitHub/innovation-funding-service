package org.innovateuk.ifs.management.admin.populator;

import org.innovateuk.ifs.fileupload.resource.FileUploadType;
import org.innovateuk.ifs.management.admin.viewmodel.UploadFilesViewModel;
import org.springframework.stereotype.Component;

@Component
public class UploadFilesViewModelPopulator {

    public UploadFilesViewModel populate(FileUploadType fileUploadType) {
        return new UploadFilesViewModel(fileUploadType);
    }
}
