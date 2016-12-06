package com.worth.ifs.project.grantofferletter.controller;

import com.worth.ifs.commons.error.exception.ObjectNotFoundException;
import com.worth.ifs.commons.service.FailingOrSucceedingResult;
import com.worth.ifs.controller.ValidationHandler;
import com.worth.ifs.file.controller.viewmodel.FileDetailsViewModel;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.project.ProjectService;
import com.worth.ifs.project.grantofferletter.form.ProjectInternalGOLForm;
import com.worth.ifs.project.grantofferletter.viewmodel.ProjectInternalGOLViewModel;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Supplier;

import static com.worth.ifs.controller.FileUploadControllerUtils.getMultipartFileBytes;
import static com.worth.ifs.file.controller.FileDownloadControllerUtils.getFileResponseEntity;
import static java.util.Collections.singletonList;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Controller for the grant offer letter
 **/
@Controller
@RequestMapping("/project/{projectId}/offer")
public class ProjectInternalGOLController {

    public static final String HEADER_CONTENT_DISPOSITION = "Content-disposition";
    public static final String CONTENT_TYPE = "application/pdf";
    private static final String ATTACHMENT_HEADER = "attachment;filename=";

    private static final String FORM_ATTR = "form";
    public static final String BASE_DIR = "project";
    public static final String TEMPLATE_NAME = "grant-offer-letter";

    @Autowired
    private ProjectService projectService;

    @RequestMapping(method = GET)
    public String viewGrantOfferLetterPage(@PathVariable("projectId") Long projectId, Model model,
                                           @ModelAttribute("loggedInUser") UserResource loggedInUser) {
        ProjectInternalGOLForm form = new ProjectInternalGOLForm();

        return createGrantOfferLetterPage(projectId, model, loggedInUser, form);
    }

    @RequestMapping(params = "uploadAnnexFileClicked", method = POST)
    public String uploadAnnexFile(
            @PathVariable("projectId") final Long projectId,
            @ModelAttribute(FORM_ATTR) ProjectInternalGOLForm form,
            @SuppressWarnings("unused") BindingResult bindingResult,
            ValidationHandler validationHandler,
            Model model,
            @ModelAttribute("loggedInUser") UserResource loggedInUser) {

        return performActionOrBindErrorsToField(projectId, validationHandler, model, loggedInUser, "additionalContractFile", form, () -> {

            MultipartFile additionalContract = form.getAdditionalContract();

            return projectService.addAdditionalContractFile(projectId, additionalContract.getContentType(), additionalContract.getSize(),
                    additionalContract.getOriginalFilename(), getMultipartFileBytes(additionalContract));
        });
    }

    @RequestMapping(value = "/grant-offer-letter", method = GET)
    @ResponseBody
    public ResponseEntity<ByteArrayResource> downloadGeneratedGrantOfferLetterFile(
            @PathVariable("projectId") final Long projectId) {

        final Optional<ByteArrayResource> content = projectService.getGeneratedGrantOfferFile(projectId);
        final Optional<FileEntryResource> fileDetails = projectService.getGeneratedGrantOfferFileDetails(projectId);

        return returnFileIfFoundOrThrowNotFoundException(projectId, content, fileDetails);
    }

    //TODO Endpoint to be removed as GOL will be generated when Finance Checks or Other documents are approved.
    //TODO - INFUND-5998
    @RequestMapping(value = "/generate", method = GET)
    public ResponseEntity<FileEntryResource> generatedGrantOfferLetterFile(
            @PathVariable("projectId") final Long projectId,
            @ModelAttribute("loggedInUser") UserResource loggedInUser) throws IOException {

        return new ResponseEntity<>(projectService.addGeneratedGrantOfferLetter(projectId, CONTENT_TYPE, 26845, "grant-offer-letter", new byte[26845]).getSuccessObject(), HttpStatus.OK);
    }

    private String getHeaderAttachment(String fileName) {
        return new StringBuffer().append(ATTACHMENT_HEADER).append(fileName).toString();
    }


    @RequestMapping(value = "/annex-file", method = GET)
    public
    @ResponseBody
    ResponseEntity<ByteArrayResource> downloadAnnexFile(
            @PathVariable("projectId") final Long projectId) {

        final Optional<ByteArrayResource> content = projectService.getAdditionalContractFile(projectId);
        final Optional<FileEntryResource> fileDetails = projectService.getAdditionalContractFileDetails(projectId);

        return returnFileIfFoundOrThrowNotFoundException(projectId, content, fileDetails);
    }


    private String createGrantOfferLetterPage(Long projectId, Model model, UserResource loggedInUser, ProjectInternalGOLForm form) {
        ProjectInternalGOLViewModel viewModel = populateGrantOfferLetterViewModel(projectId, loggedInUser);
        model.addAttribute("model", viewModel);
        model.addAttribute("form", form);
        return BASE_DIR + "/" + TEMPLATE_NAME;
    }

    private ProjectInternalGOLViewModel populateGrantOfferLetterViewModel(Long projectId, UserResource loggedInUser) {
        ProjectResource project = projectService.getById(projectId);

        Optional<FileEntryResource> grantOfferFileDetails = projectService.getGeneratedGrantOfferFileDetails(projectId);

        Optional<FileEntryResource> additionalContractFile = projectService.getAdditionalContractFileDetails(projectId);

        return new ProjectInternalGOLViewModel(projectId, project.getName(),
                grantOfferFileDetails.map(FileDetailsViewModel::new).orElse(null),
                additionalContractFile.map(FileDetailsViewModel::new).orElse(null));
    }

    private String performActionOrBindErrorsToField(Long projectId, ValidationHandler validationHandler, Model model, UserResource loggedInUser, String fieldName, ProjectInternalGOLForm form, Supplier<FailingOrSucceedingResult<?, ?>> actionFn) {

        Supplier<String> successView = () -> redirectToGrantOfferLetterPage(projectId);
        Supplier<String> failureView = () -> createGrantOfferLetterPage(projectId, model, loggedInUser, form);

        return validationHandler.performActionOrBindErrorsToField(fieldName, failureView, successView, actionFn);
    }

    private String redirectToGrantOfferLetterPage(Long projectId) {
        return "redirect:/project/" + projectId + "/offer";
    }

    private ResponseEntity<ByteArrayResource> returnFileIfFoundOrThrowNotFoundException(Long projectId, Optional<ByteArrayResource> content, Optional<FileEntryResource> fileDetails) {
        if (content.isPresent() && fileDetails.isPresent()) {
            return getFileResponseEntity(content.get(), fileDetails.get());
        } else {
            throw new ObjectNotFoundException("Could not find grant offer letter for project " + projectId, singletonList(projectId));
        }
    }


}
