package org.innovateuk.ifs.management.admin.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileUploadRestService;
import org.innovateuk.ifs.management.admin.form.UploadFilesForm;
import org.innovateuk.ifs.management.admin.viewmodel.UploadFilesViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Controller for handling requests related to uploading files
 */
@Controller
@RequestMapping("/admin")
@SecuredBySpring(value = "Controller", description = "Only system maintainer can upload files", securedType = FileUploadController.class)
@PreAuthorize("hasAuthority('system_maintainer')")
public class FileUploadController {

    private static final String FORM_ATTR_NAME = "form";
    private static final String MODEL_ATTR_NAME = "model";
    protected static final String TEMPLATE_PATH = "admin";
    private static final String DISPLAY_UPLOAD_RESULTS = "display-upload-results";
    private static final String UPLOAD_FILES = "upload-files";
    @Autowired
    private FileUploadRestService fileUploadRestService;

    @GetMapping("/upload-files")
    public String viewUploadPage(@ModelAttribute("form") UploadFilesForm form, Model model) {
        UploadFilesViewModel viewModel = new UploadFilesViewModel(false);

        return doViewUploadPage(model, form, viewModel);
    }

    private String doViewUploadPage(Model model, UploadFilesForm form, UploadFilesViewModel viewModel) {
        model.addAttribute(FORM_ATTR_NAME, form);
        model.addAttribute(MODEL_ATTR_NAME, viewModel);

        return TEMPLATE_PATH + "/" + UPLOAD_FILES;
    }

    @PostMapping(path = "/upload-files", params = {"upload_file"})
    @SecuredBySpring(value = "UPLOAD_FILE", description = "System maintainer can upload files")
    public String uploadExternalSystemFiles(@ModelAttribute("form") UploadFilesForm form,
                                            BindingResult bindingResult,
                                            Model model) throws IOException {
        MultipartFile file = form.getFile();

        RestResult<FileEntryResource> result = fileUploadRestService.uploadFile("AssessmentOnly", file.getContentType(),
                file.getSize(), file.getOriginalFilename(), file.getBytes());

        if(result.isFailure()) {
            result.getErrors().forEach(error ->
                    bindingResult.rejectValue("file", error.getErrorKey(), error.getArguments().toArray(), "")
            );
        } else {
            form.setFileName(result.getSuccess().getName());
        }

        UploadFilesViewModel viewModel = new UploadFilesViewModel(true);

        return doViewUploadPage(model, form, viewModel);
    }
 }
