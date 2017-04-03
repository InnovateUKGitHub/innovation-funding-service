package org.innovateuk.ifs.project.grantofferletter.controller;

import org.innovateuk.ifs.commons.error.exception.ObjectNotFoundException;
import org.innovateuk.ifs.commons.service.FailingOrSucceedingResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.file.controller.viewmodel.FileDetailsViewModel;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.grantofferletter.form.ProjectGrantOfferLetterForm;
import org.innovateuk.ifs.project.grantofferletter.viewmodel.ProjectGrantOfferLetterViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import static java.util.function.Function.identity;
import java.util.function.Supplier;

import static org.innovateuk.ifs.controller.FileUploadControllerUtils.getMultipartFileBytes;
import static org.innovateuk.ifs.file.controller.FileDownloadControllerUtils.getFileResponseEntity;
import static org.innovateuk.ifs.user.resource.UserRoleType.PROJECT_MANAGER;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;
import static java.util.Collections.singletonList;
/**
 * Controller for the grant offer letter
 **/
@Controller
@RequestMapping("/project/{projectId}/offer")
public class ProjectGrantOfferLetterController {

    private static final String FORM_ATTR = "form";
    public static final String BASE_DIR = "project";
    public static final String TEMPLATE_NAME = "grant-offer-letter";

    @Autowired
    private ProjectService projectService;

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_GRANT_OFFER_LETTER_SECTION')")
    @GetMapping
    public String viewGrantOfferLetterPage(@PathVariable("projectId") Long projectId, Model model,
                                           @ModelAttribute("loggedInUser") UserResource loggedInUser) {
        ProjectGrantOfferLetterForm form = new ProjectGrantOfferLetterForm();

        return createGrantOfferLetterPage(projectId, model, loggedInUser, form);
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_GRANT_OFFER_LETTER_SECTION')")
    @GetMapping("/confirmation")
    public String confirmation(@PathVariable("projectId") Long projectId, Model model) {
        model.addAttribute("projectId", projectId);
        return BASE_DIR + "/grant-offer-letter-confirmation";
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_GRANT_OFFER_LETTER_SECTION')")
    @PostMapping(params = "confirmSubmit")
    public String submit(@PathVariable("projectId") Long projectId,
                         @ModelAttribute(FORM_ATTR) ProjectGrantOfferLetterForm form,
                         @SuppressWarnings("unused") BindingResult bindingResult,
                         ValidationHandler validationHandler,
                         Model model,
                         @ModelAttribute("loggedInUser") UserResource loggedInUser) {


        return validationHandler.performActionOrBindErrorsToField("",
                () -> createGrantOfferLetterPage(projectId, model, loggedInUser, form),
                () -> "redirect:/project/" + projectId,
                () -> projectService.submitGrantOfferLetter(projectId));
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_GRANT_OFFER_LETTER_SECTION')")
    @PostMapping(params = "uploadSignedGrantOfferLetterClicked")
    public String uploadSignedGrantOfferLetterFile(
            @PathVariable("projectId") final Long projectId,
            @ModelAttribute(FORM_ATTR) ProjectGrantOfferLetterForm form,
            @SuppressWarnings("unused") BindingResult bindingResult,
            ValidationHandler validationHandler,
            Model model,
            @ModelAttribute("loggedInUser") UserResource loggedInUser) {
        return performActionOrBindErrorsToField(projectId, validationHandler, model, loggedInUser, "signedGrantOfferLetter", form, () -> {

            MultipartFile signedGrantOfferLetter = form.getSignedGrantOfferLetter();

            return projectService.addSignedGrantOfferLetter(projectId, signedGrantOfferLetter.getContentType(), signedGrantOfferLetter.getSize(),
                    signedGrantOfferLetter.getOriginalFilename(), getMultipartFileBytes(signedGrantOfferLetter));
        });
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_SIGNED_GRANT_OFFER_LETTER')")
    @PostMapping(params = "removeSignedGrantOfferLetterClicked")
    public String deleteSignedGrantOfferLetterFile(
            @PathVariable("projectId") final Long projectId,
            @ModelAttribute(FORM_ATTR) ProjectGrantOfferLetterForm form,
            @SuppressWarnings("unused") BindingResult bindingResult,
            ValidationHandler validationHandler,
            Model model,
            @ModelAttribute("loggedInUser") UserResource loggedInUser) {
        return performActionOrBindErrorsToField(projectId, validationHandler, model, loggedInUser, "signedGrantOfferLetter", form, () -> {
            return projectService.removeSignedGrantOfferLetter(projectId);
        });
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_GRANT_OFFER_LETTER_SECTION')")
    @GetMapping("/grant-offer-letter")
    public
    @ResponseBody
    ResponseEntity<ByteArrayResource> downloadGeneratedGrantOfferLetterFile(
            @PathVariable("projectId") final Long projectId) {

        final Optional<ByteArrayResource> content = projectService.getGrantOfferFile(projectId);
        final Optional<FileEntryResource> fileDetails = projectService.getGrantOfferFileDetails(projectId);

        return returnFileIfFoundOrThrowNotFoundException(projectId, content, fileDetails);
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_SIGNED_GRANT_OFFER_LETTER')")
    @GetMapping("/signed-grant-offer-letter")
    public
    @ResponseBody
    ResponseEntity<ByteArrayResource> downloadGrantOfferLetterFile(
            @PathVariable("projectId") final Long projectId) {
        final Optional<ByteArrayResource> content = projectService.getSignedGrantOfferLetterFile(projectId);
        final Optional<FileEntryResource> fileDetails = projectService.getSignedGrantOfferLetterFileDetails(projectId);
        return returnFileIfFoundOrThrowNotFoundException(projectId, content, fileDetails);
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_GRANT_OFFER_LETTER_SECTION')")
    @GetMapping("/additional-contract")
    public
    @ResponseBody
    ResponseEntity<ByteArrayResource> downloadAdditionalContractFile(
            @PathVariable("projectId") final Long projectId) {

        final Optional<ByteArrayResource> content = projectService.getAdditionalContractFile(projectId);
        final Optional<FileEntryResource> fileDetails = projectService.getAdditionalContractFileDetails(projectId);

        return returnFileIfFoundOrThrowNotFoundException(projectId, content, fileDetails);
    }


    private String createGrantOfferLetterPage(Long projectId, Model model, UserResource loggedInUser, ProjectGrantOfferLetterForm form) {
        ProjectGrantOfferLetterViewModel viewModel = populateGrantOfferLetterViewModel(projectId, loggedInUser);
        model.addAttribute("model", viewModel);
        model.addAttribute("form", form);
        return BASE_DIR + "/" + TEMPLATE_NAME;
    }

    private ProjectGrantOfferLetterViewModel populateGrantOfferLetterViewModel(Long projectId, UserResource loggedInUser) {
        ProjectResource project = projectService.getById(projectId);
        boolean leadPartner = projectService.isUserLeadPartner(projectId, loggedInUser.getId());

        Optional<FileEntryResource> signedGrantOfferLetterFile = projectService.getSignedGrantOfferLetterFileDetails(projectId);

        Optional<FileEntryResource> grantOfferFileDetails = projectService.getGrantOfferFileDetails(projectId);

        Optional<FileEntryResource> additionalContractFile = projectService.getAdditionalContractFileDetails(projectId);

        Boolean grantOfferLetterApproved = projectService.isSignedGrantOfferLetterApproved(projectId).getSuccessObject();

        boolean isProjectManager = projectService.isProjectManager(loggedInUser.getId(), projectId);

        boolean isGrantOfferLetterSent = projectService.isGrantOfferLetterAlreadySent(projectId).getOptionalSuccessObject().map(identity()).orElse(false);

        return new ProjectGrantOfferLetterViewModel(projectId, project.getName(),
                leadPartner,
                grantOfferFileDetails.isPresent() ? grantOfferFileDetails.map(FileDetailsViewModel::new).orElse(null) : null,
                signedGrantOfferLetterFile.isPresent() ? signedGrantOfferLetterFile.map(FileDetailsViewModel::new).orElse(null) : null,
                additionalContractFile.isPresent() ? additionalContractFile.map(FileDetailsViewModel::new).orElse(null) : null,
                project.getOfferSubmittedDate(),
                grantOfferLetterApproved,
                isProjectManager,
		isGrantOfferLetterSent);
    }

    private String performActionOrBindErrorsToField(Long projectId, ValidationHandler validationHandler, Model model, UserResource loggedInUser, String fieldName, ProjectGrantOfferLetterForm form, Supplier<FailingOrSucceedingResult<?, ?>> actionFn) {

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
            throw new ObjectNotFoundException("Could not find Collaboration Agreement for project " + projectId, singletonList(projectId));
        }
    }


    private Optional<ProjectUserResource> getProjectManager(Long projectId) {
        List<ProjectUserResource> projectUsers = projectService.getProjectUsersForProject(projectId);
        return simpleFindFirst(projectUsers, pu -> PROJECT_MANAGER.getName().equals(pu.getRoleName()));
    }
}
