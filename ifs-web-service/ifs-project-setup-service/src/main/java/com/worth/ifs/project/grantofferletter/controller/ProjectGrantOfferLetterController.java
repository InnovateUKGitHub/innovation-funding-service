package com.worth.ifs.project.grantofferletter.controller;

import com.worth.ifs.commons.error.exception.ObjectNotFoundException;
import com.worth.ifs.commons.service.FailingOrSucceedingResult;
import com.worth.ifs.controller.ValidationHandler;
import com.worth.ifs.file.controller.viewmodel.FileDetailsViewModel;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.project.ProjectService;
import com.worth.ifs.project.grantofferletter.form.ProjectGrantOfferLetterForm;
import com.worth.ifs.project.grantofferletter.viewmodel.ProjectGrantOfferLetterViewModel;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.resource.ProjectUserResource;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static com.worth.ifs.controller.FileUploadControllerUtils.getMultipartFileBytes;
import static com.worth.ifs.file.controller.FileDownloadControllerUtils.getFileResponseEntity;
import static com.worth.ifs.user.resource.UserRoleType.PROJECT_MANAGER;
import static com.worth.ifs.util.CollectionFunctions.simpleFindFirst;
import static java.util.Collections.singletonList;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

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
    @RequestMapping(method = GET)
    public String viewGrantOfferLetterPage(@PathVariable("projectId") Long projectId, Model model,
                                           @ModelAttribute("loggedInUser") UserResource loggedInUser) {
        ProjectGrantOfferLetterForm form = new ProjectGrantOfferLetterForm();

        return createGrantOfferLetterPage(projectId, model, loggedInUser, form);
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_GRANT_OFFER_LETTER_SECTION')")
    @RequestMapping(value="/confirmation", method = GET)
    public String confirmation(@PathVariable("projectId") Long projectId, Model model) {
        model.addAttribute("projectId", projectId);
        return BASE_DIR + "/grant-offer-letter-confirmation";
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_GRANT_OFFER_LETTER_SECTION')")
    @RequestMapping(params = "confirmSubmit", method = POST)
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
    @RequestMapping(params = "uploadSignedGrantOfferLetterClicked", method = POST)
    public String uploadGrantOfferLetterFile(
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

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_GRANT_OFFER_LETTER_SECTION')")
    @RequestMapping(params = "uploadGeneratedOfferLetterClicked", method = POST)
    public String uploadGeneratedGrantOfferLetterFile(
            @PathVariable("projectId") final Long projectId,
            @ModelAttribute(FORM_ATTR) ProjectGrantOfferLetterForm form,
            @SuppressWarnings("unused") BindingResult bindingResult,
            ValidationHandler validationHandler,
            Model model,
            @ModelAttribute("loggedInUser") UserResource loggedInUser) {

        return performActionOrBindErrorsToField(projectId, validationHandler, model, loggedInUser, "grantOfferLetter", form, () -> {

            MultipartFile grantOfferLetter = form.getGrantOfferLetter();

            return projectService.addGeneratedGrantOfferLetter(projectId, grantOfferLetter.getContentType(), grantOfferLetter.getSize(),
                    grantOfferLetter.getOriginalFilename(), getMultipartFileBytes(grantOfferLetter));
        });
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_GRANT_OFFER_LETTER_SECTION')")
    @RequestMapping(params = "uploadAdditionalContractClicked", method = POST)
    public String uploadAdditionalContractFile(
            @PathVariable("projectId") final Long projectId,
            @ModelAttribute(FORM_ATTR) ProjectGrantOfferLetterForm form,
            @SuppressWarnings("unused") BindingResult bindingResult,
            ValidationHandler validationHandler,
            Model model,
            @ModelAttribute("loggedInUser") UserResource loggedInUser) {

        return performActionOrBindErrorsToField(projectId, validationHandler, model, loggedInUser, "additionalContractFile", form, () -> {

            MultipartFile additionalContract = form.getAdditionalContract();

            return projectService.addGeneratedGrantOfferLetter(projectId, additionalContract.getContentType(), additionalContract.getSize(),
                    additionalContract.getOriginalFilename(), getMultipartFileBytes(additionalContract));
        });
    }


    @PreAuthorize("hasPermission(#projectId, 'ACCESS_GRANT_OFFER_LETTER_SECTION')")
    @RequestMapping(value = "/grant-offer-letter", method = GET)
    public
    @ResponseBody
    ResponseEntity<ByteArrayResource> downloadGeneratedGrantOfferLetterFile(
            @PathVariable("projectId") final Long projectId) {

        final Optional<ByteArrayResource> content = projectService.getGeneratedGrantOfferFile(projectId);
        final Optional<FileEntryResource> fileDetails = projectService.getGeneratedGrantOfferFileDetails(projectId);

        return returnFileIfFoundOrThrowNotFoundException(projectId, content, fileDetails);
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_GRANT_OFFER_LETTER_SECTION')")
    @RequestMapping(value = "/signed-grant-offer-letter", method = GET)
    public
    @ResponseBody
    ResponseEntity<ByteArrayResource> downloadGrantOfferLetterFile(
            @PathVariable("projectId") final Long projectId) {

        final Optional<ByteArrayResource> content = projectService.getSignedGrantOfferLetterFile(projectId);
        final Optional<FileEntryResource> fileDetails = projectService.getSignedGrantOfferLetterFileDetails(projectId);

        return returnFileIfFoundOrThrowNotFoundException(projectId, content, fileDetails);
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_GRANT_OFFER_LETTER_SECTION')")
    @RequestMapping(value = "/additional-contract", method = GET)
    public
    @ResponseBody
    ResponseEntity<ByteArrayResource> downloadAdditionalContrcatFile(
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

        Optional<FileEntryResource> grantOfferFileDetails = projectService.getGeneratedGrantOfferFileDetails(projectId);

        Optional<FileEntryResource> additionalContractFile = projectService.getAdditionalContractFileDetails(projectId);

        boolean isProjectManager = getProjectManager(projectId)
                .map(projectManager -> loggedInUser.getId().equals(projectManager.getUser())).orElse(false);

        return new ProjectGrantOfferLetterViewModel(projectId, project.getName(),
                leadPartner, grantOfferFileDetails.map(FileDetailsViewModel::new).orElse(null),
                signedGrantOfferLetterFile.map(FileDetailsViewModel::new).orElse(null),
                additionalContractFile.map(FileDetailsViewModel::new).orElse(null),
                project.getOfferSubmittedDate(), project.isOfferRejected() != null && project.isOfferRejected(),

                // TODO - Not sure why 'accepted' was always false earlier - If you agree this was a mistake, I'll delete this comment. Else I'll set it back to false.
                project.isOfferRejected() != null && !project.isOfferRejected(), isProjectManager);
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
