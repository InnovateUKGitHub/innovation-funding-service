package com.worth.ifs.project.grantofferletter.send.controller;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.CompetitionSummaryResource;
import com.worth.ifs.application.service.ApplicationService;
import com.worth.ifs.application.service.ApplicationSummaryService;
import com.worth.ifs.commons.error.exception.FileAwaitingVirusScanException;
import com.worth.ifs.commons.error.exception.FileQuarantinedException;
import com.worth.ifs.commons.service.FailingOrSucceedingResult;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.controller.ValidationHandler;
import com.worth.ifs.file.controller.viewmodel.FileDetailsViewModel;
import com.worth.ifs.project.grantofferletter.send.form.ProjectGrantOfferLetterSendForm;
import com.worth.ifs.project.grantofferletter.send.viewmodel.ProjectGrantOfferLetterSendViewModel;
import com.worth.ifs.project.ProjectService;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
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

import java.util.function.Supplier;
import java.util.Optional;

import static com.worth.ifs.controller.FileUploadControllerUtils.getMultipartFileBytes;
import static com.worth.ifs.file.controller.FileDownloadControllerUtils.getFileResponseEntity;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
/**
 * This Controller handles Grant Offer Letter activity for the Internal Competition team members
 */
@Controller
@RequestMapping("/project/{projectId}/grant-offer-letter")
public class ProjectGrantOfferLetterSendController {
    @Autowired
    private ProjectService projectService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ApplicationSummaryService applicationSummaryService;

    private static final String FORM_ATTR = "form";

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_GRANT_OFFER_LETTER_SEND_SECTION')")
    @RequestMapping(value = "/send", method = GET)
    public String viewGrantOfferLetterSend(@PathVariable Long projectId, Model model, @ModelAttribute("loggedInUser") UserResource loggedInUser) {
        ProjectGrantOfferLetterSendForm form = new ProjectGrantOfferLetterSendForm();
        return doViewGrantOfferLetterSend(projectId, model, form);
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_GRANT_OFFER_LETTER_SEND_SECTION')")
    @RequestMapping(value = "/send", method = POST)
    public String sendGrantOfferLetter(@PathVariable Long projectId,
                                       @ModelAttribute(FORM_ATTR) ProjectGrantOfferLetterSendForm form,
                                       Model model,
                                       @SuppressWarnings("unused") BindingResult bindingResult,
                                       ValidationHandler validationHandler) {
        Supplier<String> failureView = () -> doViewGrantOfferLetterSend(projectId, model, form);
        ServiceResult<Void> generateResult = projectService.sendGrantOfferLetter(projectId);

        return validationHandler.addAnyErrors(generateResult).failNowOrSucceedWith(failureView, () -> {return doViewGrantOfferLetterSend(projectId, model, form);}
        );
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_GRANT_OFFER_LETTER_SEND_SECTION')")
    @RequestMapping(value = "/receivedByPost", method = POST)
    public String grantOfferLetterReceivedByPost(@PathVariable Long projectId,
                                                 @ModelAttribute ProjectGrantOfferLetterSendForm form,
                                                 Model model,
                                                 @SuppressWarnings("unused") BindingResult bindingResult,
                                                 ValidationHandler validationHandler) {
        // TODO - set to ready to approve???
        return doViewGrantOfferLetterSend(projectId, model, form);
    }

    private String doViewGrantOfferLetterSend(Long projectId, Model model, ProjectGrantOfferLetterSendForm form) {
        ProjectGrantOfferLetterSendViewModel viewModel = populateGrantOfferLetterSendViewModel(projectId);

        model.addAttribute("model", viewModel);
        model.addAttribute("form", form);

        return "project/grant-offer-letter-send";
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_GRANT_OFFER_LETTER_SEND_SECTION')")
    @RequestMapping(params = "uploadAnnexClicked", value = "/upload-annex", method = POST)
    public String uploadAnnexFile(
            @PathVariable("projectId") final Long projectId,
            @ModelAttribute(FORM_ATTR) ProjectGrantOfferLetterSendForm form,
            @SuppressWarnings("unused") BindingResult bindingResult,
            ValidationHandler validationHandler,
            Model model,
            @ModelAttribute("loggedInUser") UserResource loggedInUser) {

        /*Supplier<String> failureView = () -> doViewGrantOfferLetterSend(projectId, model, form);
        MultipartFile file = form.getAnnex();
        ServiceResult<FileEntryResource> generateResult = projectService.addAdditionalContractFile(projectId, file.getContentType(), file.getSize(),
                file.getOriginalFilename(), getMultipartFileBytes(file));
        return validationHandler.addAnyErrors(generateResult).failNowOrSucceedWith(failureView, () -> { return doViewGrantOfferLetterSend(projectId, model, form);}
        );*/
        return performActionOrBindErrorsToField(projectId, validationHandler, model, "annex", form, () -> {

            MultipartFile file = form.getAnnex();

            return projectService.addAdditionalContractFile(projectId, file.getContentType(), file.getSize(),
                    file.getOriginalFilename(), getMultipartFileBytes(file));
        });
    }

    private String performActionOrBindErrorsToField(Long projectId, ValidationHandler validationHandler, Model model, String fieldName, ProjectGrantOfferLetterSendForm form, Supplier<FailingOrSucceedingResult<?, ?>> actionFn) {

        Supplier<String> successView = () -> redirectToGrantOfferLetterPage(projectId);
        Supplier<String> failureView = () -> doViewGrantOfferLetterSend(projectId, model, form);

        return validationHandler.performActionOrBindErrorsToField(fieldName, failureView, successView, actionFn);
    }

    private String redirectToGrantOfferLetterPage(Long projectId) {
        return "redirect:/project/" + projectId + "/grant-offer-letter/send";
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_GRANT_OFFER_LETTER_SEND_SECTION')")
    @RequestMapping(value = "/additional-contract", method = GET)
    public
    @ResponseBody
    ResponseEntity<ByteArrayResource> downloadAdditionalContractFile(
            @PathVariable("projectId") final Long projectId) {

        final Optional<ByteArrayResource> content = projectService.getAdditionalContractFile(projectId);
        final Optional<FileEntryResource> fileDetails = projectService.getAdditionalContractFileDetails(projectId);

        return returnFileIfFoundOrThrowNotFoundException(content, fileDetails);
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_GRANT_OFFER_LETTER_SEND_SECTION')")
    @RequestMapping(value = "/grant-offer-letter", method = GET)
    public
    @ResponseBody
    ResponseEntity<ByteArrayResource> downloadGeneratedGrantOfferLetterFile(
            @PathVariable("projectId") final Long projectId) {

        final Optional<ByteArrayResource> content = projectService.getGeneratedGrantOfferFile(projectId);
        final Optional<FileEntryResource> fileDetails = projectService.getGeneratedGrantOfferFileDetails(projectId);

        return returnFileIfFoundOrThrowNotFoundException(content, fileDetails);
    }

    private ProjectGrantOfferLetterSendViewModel populateGrantOfferLetterSendViewModel(Long projectId) {
        ProjectResource project = projectService.getById(projectId);
        ApplicationResource application = applicationService.getById(project.getApplication());
        CompetitionSummaryResource competitionSummary = applicationSummaryService.getCompetitionSummaryByCompetitionId(application.getCompetition());

        Optional<FileEntryResource> grantOfferFileDetails;
        try {
            grantOfferFileDetails = projectService.getGeneratedGrantOfferFileDetails(projectId);
        } catch (FileAwaitingVirusScanException e1) {
            grantOfferFileDetails = null;
        } catch (FileQuarantinedException e2) {
            grantOfferFileDetails = null;
        }

        Optional<FileEntryResource> additionalContractFile;
        try {
            additionalContractFile = projectService.getAdditionalContractFileDetails(projectId);
        } catch (FileAwaitingVirusScanException e1) {
            additionalContractFile = null;
        } catch (FileQuarantinedException e2) {
            additionalContractFile = null;
        }

        Boolean sendOfferLetterAllowed = projectService.isGrantOfferLetterAlreadySent(projectId).getSuccessObject();

        return new ProjectGrantOfferLetterSendViewModel(competitionSummary,
                                                        grantOfferFileDetails != null ? grantOfferFileDetails.map(FileDetailsViewModel::new).orElse(null) : null,
                                                        additionalContractFile != null ? additionalContractFile.map(FileDetailsViewModel::new).orElse(null) : null,
                                                        sendOfferLetterAllowed,
                                                        projectId,
                                                        project.getName(),
                                                        application.getId(),
                                                        grantOfferFileDetails != null ? grantOfferFileDetails.isPresent() : Boolean.FALSE,
                                                        additionalContractFile != null ? additionalContractFile.isPresent() : Boolean.FALSE);
    }

    private ResponseEntity<ByteArrayResource> returnFileIfFoundOrThrowNotFoundException(Optional<ByteArrayResource> content, Optional<FileEntryResource> fileDetails) {
        if (content.isPresent() && fileDetails.isPresent()) {
            return getFileResponseEntity(content.get(), fileDetails.get());
        } else {
            return new ResponseEntity<>(null, null, HttpStatus.NO_CONTENT);
        }
    }


}
