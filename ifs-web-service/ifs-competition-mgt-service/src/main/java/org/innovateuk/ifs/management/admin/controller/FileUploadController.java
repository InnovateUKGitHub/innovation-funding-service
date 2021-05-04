package org.innovateuk.ifs.management.admin.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.fileupload.resource.FileUploadType;
import org.innovateuk.ifs.management.admin.form.UploadFilesForm;
import org.innovateuk.ifs.management.admin.populator.UploadFilesViewModelPopulator;
import org.innovateuk.ifs.management.admin.viewmodel.UploadFilesViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for handling requests related to uploading files by the IFS Administrator
 */
@Controller
@RequestMapping("/admin")
@SecuredBySpring(value = "Controller", description = "Only IFS Administrators can upload files", securedType = FileUploadController.class)
@PreAuthorize("hasAuthority('ifs_administrator')")
public class FileUploadController {

    private static final String FORM_ATTR_NAME = "form";
    private static final String MODEL_ATTR_NAME = "model";

    @Autowired
    private UploadFilesViewModelPopulator uploadFilesViewModelPopulator;

    @GetMapping("/upload-files")
    public String UploadFiles(@RequestParam(value = "type") FileUploadType fileUploadType, Model model) {
        UploadFilesForm form = new UploadFilesForm();

        UploadFilesViewModel viewModel = uploadFilesViewModelPopulator.populate(fileUploadType);

        model.addAttribute(FORM_ATTR_NAME, form);
        model.addAttribute(MODEL_ATTR_NAME, viewModel);

        return "admin/upload-files";
    }
}
