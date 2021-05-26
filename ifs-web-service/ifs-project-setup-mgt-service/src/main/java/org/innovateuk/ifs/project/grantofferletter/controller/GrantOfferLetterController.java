package org.innovateuk.ifs.project.grantofferletter.controller;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.commons.service.FailingOrSucceedingResult;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.controller.CaseInsensitiveConverter;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.file.controller.viewmodel.FileDetailsViewModel;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.grantofferletter.GrantOfferLetterService;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.grantofferletter.form.GrantOfferLetterApprovalForm;
import org.innovateuk.ifs.project.grantofferletter.form.GrantOfferLetterLetterForm;
import org.innovateuk.ifs.project.grantofferletter.populator.GrantOfferLetterTemplatePopulator;
import org.innovateuk.ifs.project.grantofferletter.populator.KtpGrantOfferLetterTemplatePopulator;
import org.innovateuk.ifs.project.grantofferletter.populator.ProcurementGrantOfferLetterTemplatePopulator;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterApprovalResource;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterStateResource;
import org.innovateuk.ifs.project.grantofferletter.template.resource.GolTemplateResource;
import org.innovateuk.ifs.project.grantofferletter.viewmodel.GrantOfferLetterModel;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.Authority;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.Optional;
import java.util.function.Supplier;

import static org.innovateuk.ifs.controller.FileUploadControllerUtils.getMultipartFileBytes;
import static org.innovateuk.ifs.file.controller.FileDownloadControllerUtils.getFileResponseEntity;
import static org.innovateuk.ifs.project.grantofferletter.template.resource.GolTemplateResource.DEFAULT_GOL_TEMPLATE;

/**
 * This Controller handles Grant Offer Letter activity for the Internal Competition team members
 */
@Controller
@RequestMapping("/project/{projectId}/grant-offer-letter")
public class GrantOfferLetterController {
    @Autowired
    private ProjectService projectService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private GrantOfferLetterService grantOfferLetterService;

    @Autowired
    private GrantOfferLetterTemplatePopulator grantOfferLetterTemplatePopulator;

    @Autowired
    private KtpGrantOfferLetterTemplatePopulator ktpGrantOfferLetterTemplatePopulator;

    @Autowired
    private ProcurementGrantOfferLetterTemplatePopulator procurementGrantOfferLetterTemplatePopulator;

    private static final String FORM_ATTR = "form";
    private static final String APPROVAL_FORM_ATTR = "approvalForm";

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(ApprovalType.class, new CaseInsensitiveConverter<>(ApprovalType.class));
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_GRANT_OFFER_LETTER_SEND_SECTION')")
    @GetMapping("/send")
    public String viewGrantOfferLetterSend(@P("projectId") @PathVariable Long projectId, Model model, UserResource loggedInUser) {
        GrantOfferLetterLetterForm form = new GrantOfferLetterLetterForm();
        return doViewGrantOfferLetterSend(projectId, model, form, loggedInUser);
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_GRANT_OFFER_LETTER_SEND_SECTION')")
    @PostMapping("/send")
    public String sendGrantOfferLetter(@P("projectId") @PathVariable Long projectId,
                                       Model model,
                                       @ModelAttribute(FORM_ATTR) @Valid GrantOfferLetterLetterForm form,
                                       @SuppressWarnings("unused") BindingResult bindingResult,
                                       ValidationHandler validationHandler,
                                       UserResource loggedInUser) {
        Supplier<String> failureView = () -> doViewGrantOfferLetterSend(projectId, model, form, loggedInUser);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {

            ServiceResult<Void> generateResult = grantOfferLetterService.sendGrantOfferLetter(projectId);

            return validationHandler.addAnyErrors(generateResult).failNowOrSucceedWith(failureView, () ->
                    redirectToGrantOfferLetterPage(projectId));
        });

    }

    private String doViewGrantOfferLetterSend(Long projectId, Model model, GrantOfferLetterLetterForm form, UserResource loggedInUser) {
        GrantOfferLetterModel viewModel = populateGrantOfferLetterSendViewModel(projectId, loggedInUser);
        GrantOfferLetterApprovalForm approvalForm = new GrantOfferLetterApprovalForm();

        model.addAttribute("model", viewModel);
        model.addAttribute("form", form);
        model.addAttribute(APPROVAL_FORM_ATTR, approvalForm);

        return "project/grant-offer-letter-send";
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_GRANT_OFFER_LETTER_SEND_SECTION')")
    @PostMapping(value = "/grant-offer-letter", params = "uploadGrantOfferLetterClicked")
    public String uploadGrantOfferLetterFile(@P("projectId") @PathVariable("projectId") final Long projectId,
                                             @ModelAttribute(FORM_ATTR) GrantOfferLetterLetterForm form,
                                             @SuppressWarnings("unused") BindingResult bindingResult,
                                             ValidationHandler validationHandler,
                                             Model model,
                                             UserResource loggedInUser) {
        return performActionOrBindErrorsToField(projectId, validationHandler, model, loggedInUser, "grantOfferLetter", form, () -> {

            MultipartFile file = form.getGrantOfferLetter();

            return grantOfferLetterService.addGrantOfferLetter(projectId, file.getContentType(), file.getSize(),
                    file.getOriginalFilename(), getMultipartFileBytes(file));
        });
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_GRANT_OFFER_LETTER_SEND_SECTION')")
    @PostMapping(value = "/grant-offer-letter", params = "removeGrantOfferLetterClicked")
    public String removeGrantOfferLetterFile(@P("projectId") @PathVariable("projectId") final Long projectId) {

        grantOfferLetterService.removeGrantOfferLetter(projectId);

        return redirectToGrantOfferLetterPage(projectId);
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'RESET_GRANT_OFFER_LETTER')")
    @PostMapping("/reset")
    public String resetGrantOfferLetterSection(@PathVariable("projectId") final Long projectId) {

        grantOfferLetterService.resetGrantOfferLetter(projectId);

        return redirectToGrantOfferLetterPage(projectId);
    }
    
    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_GRANT_OFFER_LETTER_SEND_SECTION')")
    @PostMapping(value = "/upload-annex", params = "removeAdditionalContractFileClicked")
    public String removeAdditionalContractFile(@P("projectId") @PathVariable("projectId") final Long projectId) {
        grantOfferLetterService.removeAdditionalContractFile(projectId);
        return redirectToGrantOfferLetterPage(projectId);
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_GRANT_OFFER_LETTER_SEND_SECTION')")
    @PostMapping("/signed")
    public String signedGrantOfferLetterApproval(
            @P("projectId") @PathVariable("projectId") final Long projectId,
            @ModelAttribute(APPROVAL_FORM_ATTR) GrantOfferLetterApprovalForm approvalForm) {

        if (validateApprovalOrRejection(approvalForm)) {
            grantOfferLetterService.approveOrRejectSignedGrantOfferLetter(projectId,
                    new GrantOfferLetterApprovalResource(approvalForm.getApprovalType(), approvalForm.getRejectionReason()))
                    .toPostResponse();
        }

        return redirectToGrantOfferLetterPage(projectId);
    }

    private boolean validateApprovalOrRejection(GrantOfferLetterApprovalForm approvalForm) {
        if (ApprovalType.REJECTED.equals(approvalForm.getApprovalType())) {
            if (StringUtils.isNotBlank(approvalForm.getRejectionReason())) {
                return true;
            }
        } else if (ApprovalType.APPROVED.equals(approvalForm.getApprovalType())) {
            return true;
        }

        return false;
    }

    private String performActionOrBindErrorsToField(Long projectId, ValidationHandler validationHandler, Model model, UserResource loggedInUser, String fieldName, GrantOfferLetterLetterForm form, Supplier<FailingOrSucceedingResult<?, ?>> actionFn) {

        Supplier<String> successView = () -> redirectToGrantOfferLetterPage(projectId);
        Supplier<String> failureView = () -> doViewGrantOfferLetterSend(projectId, model, form, loggedInUser);

        return validationHandler.performActionOrBindErrorsToField(fieldName, failureView, successView, actionFn);
    }

    private String redirectToGrantOfferLetterPage(Long projectId) {
        return "redirect:/project/" + projectId + "/grant-offer-letter/send";
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_GRANT_OFFER_LETTER_SEND_SECTION')")
    @GetMapping("/additional-contract")
    public
    @ResponseBody
    ResponseEntity<ByteArrayResource> downloadAdditionalContractFile(
            @P("projectId") @PathVariable("projectId") final Long projectId) {

        final Optional<ByteArrayResource> content = grantOfferLetterService.getAdditionalContractFile(projectId);
        final Optional<FileEntryResource> fileDetails = grantOfferLetterService.getAdditionalContractFileDetails(projectId);

        return returnFileIfFoundOrThrowNotFoundException(content, fileDetails);
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_GRANT_OFFER_LETTER_SEND_SECTION')")
    @GetMapping("/grant-offer-letter")
    public
    @ResponseBody
    ResponseEntity<ByteArrayResource> downloadGeneratedGrantOfferLetterFile(
            @P("projectId") @PathVariable("projectId") final Long projectId) {

        final Optional<ByteArrayResource> content = grantOfferLetterService.getGrantOfferFile(projectId);
        final Optional<FileEntryResource> fileDetails = grantOfferLetterService.getGrantOfferFileDetails(projectId);

        return returnFileIfFoundOrThrowNotFoundException(content, fileDetails);
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_GRANT_OFFER_LETTER_SEND_SECTION')")
    @GetMapping("/signed-grant-offer-letter")
    public
    @ResponseBody
    ResponseEntity<ByteArrayResource> downloadSignedGrantOfferLetterFile(
            @P("projectId") @PathVariable("projectId") final Long projectId) {

        final Optional<ByteArrayResource> content = grantOfferLetterService.getSignedGrantOfferLetterFile(projectId);
        final Optional<FileEntryResource> fileDetails = grantOfferLetterService.getSignedGrantOfferLetterFileDetails(projectId);

        return returnFileIfFoundOrThrowNotFoundException(content, fileDetails);
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_GRANT_OFFER_LETTER_SEND_SECTION')")
    @GetMapping("/signed-additional-contract")
    public
    @ResponseBody
    ResponseEntity<ByteArrayResource> downloadSignedAdditionalContractFile(
            @PathVariable final long projectId) {

        final Optional<ByteArrayResource> content = grantOfferLetterService.getSignedAdditionalContractFile(projectId);
        final Optional<FileEntryResource> fileDetails = grantOfferLetterService.getSignedAdditionalContractFileDetails(projectId);

        return returnFileIfFoundOrThrowNotFoundException(content, fileDetails);
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_GRANT_OFFER_LETTER_SEND_SECTION')")
    @PostMapping(params = "uploadAnnexClicked", value = "/upload-annex")
    public String uploadAnnexFile(
            @P("projectId") @PathVariable("projectId") final Long projectId,
            @ModelAttribute(FORM_ATTR) GrantOfferLetterLetterForm form,
            @SuppressWarnings("unused") BindingResult bindingResult,
            ValidationHandler validationHandler,
            Model model,
            UserResource loggedInUser) {

        return performActionOrBindErrorsToField(projectId, validationHandler, model, loggedInUser,"annex", form, () -> {

            MultipartFile file = form.getAnnex();

            return grantOfferLetterService.addAdditionalContractFile(projectId, file.getContentType(), file.getSize(),
                    file.getOriginalFilename(), getMultipartFileBytes(file));
        });
    }

    private GrantOfferLetterModel populateGrantOfferLetterSendViewModel(Long projectId, UserResource loggedInUser) {
        ProjectResource project = projectService.getById(projectId);
        ApplicationResource application = applicationService.getById(project.getApplication());
        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();

        Optional<FileEntryResource> grantOfferFileDetails = grantOfferLetterService.getGrantOfferFileDetails(projectId);

        Optional<FileEntryResource> additionalContractFile = grantOfferLetterService.getAdditionalContractFileDetails(projectId);

        Optional<FileEntryResource> signedGrantOfferLetterFile = grantOfferLetterService.getSignedGrantOfferLetterFileDetails(projectId);

        Optional<FileEntryResource> signedAdditionalContractFile = grantOfferLetterService.getSignedAdditionalContractFileDetails(projectId);

        GrantOfferLetterStateResource golState = grantOfferLetterService.getGrantOfferLetterState(projectId).getSuccess();

        return new GrantOfferLetterModel(
                competition.isProcurement() ? "Contract" : "Grant offer letter",
                competition.isProcurement() ? "Contract" : "Letter",
                competition.getId(),
                competition.isProcurement(),
                competition.isH2020(),
                competition.isKtp(),
                grantOfferFileDetails.map(FileDetailsViewModel::new).orElse(null),
                additionalContractFile.map(FileDetailsViewModel::new).orElse(null),
                projectId,
                project.getName(),
                application.getId(),
                grantOfferFileDetails.isPresent(),
                additionalContractFile.isPresent(),
                signedGrantOfferLetterFile.map(FileDetailsViewModel::new).orElse(null),
                signedAdditionalContractFile.map(FileDetailsViewModel::new).orElse(null),
                golState,
                project.getGrantOfferLetterRejectionReason(),
                project.getProjectState(),
                project.isUseDocusignForGrantOfferLetter(),
                loggedInUser.hasAuthority(Authority.SUPER_ADMIN_USER));
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_GRANT_OFFER_LETTER_SEND_SECTION')")
    @GetMapping("/template")
    public String viewGrantOfferLetterTemplatePage(@PathVariable("projectId") long projectId,
                                                   Model model) {
        ProjectResource project = projectService.getById(projectId);
        CompetitionResource competition = competitionRestService.getCompetitionById(project.getCompetition()).getSuccess();
        GolTemplateResource template = competition.getGolTemplate();
        if (template.getName().equals(DEFAULT_GOL_TEMPLATE)) {
            model.addAttribute("model", grantOfferLetterTemplatePopulator.populate(project, competition));
        } else if (template.getName().equals(FundingType.KTP.getGolType())) {
            model.addAttribute("model", ktpGrantOfferLetterTemplatePopulator.populate(project));
        } else if (template.getName().equals(FundingType.PROCUREMENT.getGolType())) {
            model.addAttribute("model", procurementGrantOfferLetterTemplatePopulator.populate(project, competition));
        }
        return "project/" + template.getTemplate();
    }

    private ResponseEntity<ByteArrayResource> returnFileIfFoundOrThrowNotFoundException(Optional<ByteArrayResource> content, Optional<FileEntryResource> fileDetails) {
        if (content.isPresent() && fileDetails.isPresent()) {
            return getFileResponseEntity(content.get(), fileDetails.get());
        } else {
            return new ResponseEntity<>(null, null, HttpStatus.NO_CONTENT);
        }
    }

}
