package org.innovateuk.ifs.management.admin.controller;

import org.innovateuk.ifs.async.annotations.AsyncMethod;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileUploadRestService;
import org.innovateuk.ifs.management.admin.form.UploadFilesForm;
import org.innovateuk.ifs.management.admin.populator.UploadFilesViewModelPopulator;
import org.innovateuk.ifs.management.admin.viewmodel.UploadFilesViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

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
    private FileUploadRestService fileUploadRestService;

    @Autowired
    private UploadFilesViewModelPopulator uploadFilesViewModelPopulator;

    @GetMapping("/upload-files")
    public String UploadFiles(Model model) {
        UploadFilesForm form = new UploadFilesForm();

        UploadFilesViewModel viewModel = uploadFilesViewModelPopulator.populate();

        model.addAttribute(FORM_ATTR_NAME, form);
        model.addAttribute(MODEL_ATTR_NAME, viewModel);

        return "admin/upload-files";
    }

    @PostMapping(path = "/upload-files", params = {"upload_file"})
    @SecuredBySpring(value = "UPLOAD_FILE", description = "Ifs admin can upload the file")
    public String uploadExternalSystemFiles(Model model,
                                           UserResource user,
                                           @ModelAttribute("form") UploadFilesForm form,
                                           BindingResult bindingResult) throws IOException {
        MultipartFile file = form.getFile();
//        RestResult<FileEntryResource> result = fileUploadRestService.addFile(form.getType().name(), file.getContentType(),
//                file.getSize(), file.getOriginalFilename(), file.getBytes());
        RestResult<FileEntryResource> result = fileUploadRestService.addFile("AssessmentOnly", file.getContentType(),
                file.getSize(), file.getOriginalFilename(), file.getBytes());
        if(result.isFailure()) {
            result.getErrors().forEach(error ->
                    bindingResult.rejectValue("file", error.getErrorKey(), error.getArguments().toArray(), "")
            );
        } else {
            form.setFileName(result.getSuccess().getName());
        }

        model.addAttribute("model", uploadFilesViewModelPopulator.populate());
        return "admin/upload-files";
    }

    @PostMapping(path = "/upload-files", params = {"process_files"})
    @SecuredBySpring(value = "UPLOAD_FILE", description = "Ifs admin can upload the file")
    public String parseUploadedFiles(Model model,
                                     UserResource user,
                                     @ModelAttribute("form") UploadFilesForm form,
                                     BindingResult bindingResult) throws IOException {

        RestResult<List<FileEntryResource>>  uploadedFileentriesResult = fileUploadRestService.getAllUploadedFileEntryResources();
        if (uploadedFileentriesResult.isSuccess()) {
            List<FileEntryResource> uploadedFileentries = uploadedFileentriesResult.getSuccess();
            //TODO need to think about how to schedule each file parsing
            FileEntryResource fileEntryResource = uploadedFileentries.get(0);
            Long fileEntryId = fileEntryResource.getId();
            RestResult<ByteArrayResource> fileAndContents = fileUploadRestService.getFileAndContents(fileEntryId);
            if (fileAndContents.isSuccess()) {
                ByteArrayResource byteArrayResource = fileAndContents.getSuccess();
                File competitionFile = byteArrayResource.getFile();
                fileUploadRestService.parseAndSaveFileContents(competitionFile);
            }
        }
         return "admin/upload-files";
    }
    @GetMapping(path = "/upload-files/get-file/", params = {"fileEntryId"})
    @SecuredBySpring(value = "GET_FILE", description = "Ifs admin can do parse and get the file")
    public void getFileAndContents(Model model,
                                     UserResource user,
                                     @ModelAttribute("form") UploadFilesForm form,
                                     @PathVariable("fileEntryId") final Long fileEntryId,
                                     BindingResult bindingResult) throws IOException {

        Optional<ByteArrayResource> fileAndContents = fileUploadRestService.getFileAndContents(fileEntryId).getOptionalSuccessObject();
    }

    @PostMapping(params = "remove_file")
    @AsyncMethod
    @SecuredBySpring(value = "REMOVE_FILE", description = "Ifs admin can remove the file")
    public String removeFECCertificateFile(Model model,
                                           UserResource user,
                                           @ModelAttribute("form") UploadFilesForm form) {
        fileUploadRestService.removeFile(null);
        return "redirect:/admin/upload-files";
    }
}
