package com.worth.ifs.project.grantofferletter.send.controller;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.CompetitionSummaryResource;
import com.worth.ifs.application.service.ApplicationService;
import com.worth.ifs.application.service.ApplicationSummaryService;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.controller.ValidationHandler;
import com.worth.ifs.file.controller.viewmodel.FileDetailsViewModel;
import com.worth.ifs.project.grantofferletter.send.form.ProjectGrantOfferLetterSendForm;
import com.worth.ifs.project.grantofferletter.send.viewmodel.ProjectGrantOfferLetterSendViewModel;
import com.worth.ifs.project.ProjectService;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import java.util.function.Supplier;
import java.util.Optional;

import static com.worth.ifs.controller.FileUploadControllerUtils.getMultipartFileBytes;
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

    @Autowired
    private CompetitionService competitionService;

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_GRANT_OFFER_LETTER_SEND_SECTION')")
    @RequestMapping(value = "/send", method = GET)
    public String viewGrantOfferLetterSend(@PathVariable Long projectId, Model model, @ModelAttribute("loggedInUser") UserResource loggedInUser) {
        return doViewGrantOfferLetterSend(projectId, model);
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_GRANT_OFFER_LETTER_SEND_SECTION')")
    @RequestMapping(value = "/send", method = POST)
    public String sendGrantOfferLetter(@PathVariable Long projectId,
                                       @ModelAttribute ProjectGrantOfferLetterSendForm form,
                                       @ModelAttribute("loggedInUser") UserResource loggedInUser,
                                       Model model,
                                       @SuppressWarnings("unused") BindingResult bindingResult,
                                       ValidationHandler validationHandler) {
        Supplier<String> failureView = () -> doViewGrantOfferLetterSend(projectId, model);
        ServiceResult<Void> generateResult = projectService.sendGrantOfferLetter(projectId, loggedInUser.getId());

        return validationHandler.addAnyErrors(generateResult).failNowOrSucceedWith(failureView, () ->
                redirectToCompetitionSummaryPage(projectId)
        );
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_GRANT_OFFER_LETTER_SEND_SECTION')")
    @RequestMapping(value = "/receivedByPost", method = POST)
    public String grantOfferLetterReceivedByPost(@PathVariable Long projectId,
                                                 @ModelAttribute ProjectGrantOfferLetterSendForm form,
                                                 @ModelAttribute("loggedInUser") UserResource loggedInUser,
                                                 Model model,
                                                 @SuppressWarnings("unused") BindingResult bindingResult,
                                                 ValidationHandler validationHandler) {
        // TODO - DRS set to ready to approve???
        return redirectToCompetitionSummaryPage(projectId);
    }

    private String doViewGrantOfferLetterSend(Long projectId, Model model) {
        ProjectGrantOfferLetterSendViewModel viewModel = populateGrantOfferLetterSendViewModel(projectId);

        model.addAttribute("model", viewModel);

        return "project/grant-offer-letter-send";
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_OTHER_DOCUMENTS_SECTION')")
    @RequestMapping(params = "uploadAnnexClicked", method = POST)
    public String uploadAnnexFile(
            @PathVariable("projectId") final Long projectId,
            @ModelAttribute(FORM_ATTR) ProjectGrantOfferLetterSendForm form,
            @SuppressWarnings("unused") BindingResult bindingResult,
            ValidationHandler validationHandler,
            Model model,
            @ModelAttribute("loggedInUser") UserResource loggedInUser) {

        return performActionOrBindErrorsToField(projectId, validationHandler, model, loggedInUser, "collaborationAgreement", form, () -> {

            MultipartFile file = form.getCollaborationAgreement();

            return projectService.addCollaborationAgreementDocument(projectId, file.getContentType(), file.getSize(),
                    file.getOriginalFilename(), getMultipartFileBytes(file));
        });
    }

    private ProjectGrantOfferLetterSendViewModel populateGrantOfferLetterSendViewModel(Long projectId) {
        ProjectResource project = projectService.getById(projectId);
        ApplicationResource application = applicationService.getById(project.getApplication());
        CompetitionSummaryResource competitionSummary = applicationSummaryService.getCompetitionSummaryByCompetitionId(application.getCompetition());

        Optional<FileEntryResource> grantOfferFileDetails = projectService.getGeneratedGrantOfferFileDetails(projectId);

        Optional<FileEntryResource> additionalContractFile = projectService.getAdditionalContractFileDetails(projectId);

        Boolean sendOfferLetterAllowed = projectService.isSendGrantOfferLetterAllowed(projectId).getSuccessObject();

        return new ProjectGrantOfferLetterSendViewModel(competitionSummary,
                                                        grantOfferFileDetails.map(FileDetailsViewModel::new).orElse(null),
                                                        additionalContractFile.map(FileDetailsViewModel::new).orElse(null),
                                                        sendOfferLetterAllowed,
                                                        projectId,
                                                        project.getName(),
                                                        application.getId());
    }

    private String redirectToCompetitionSummaryPage(Long projectId) {
        ProjectResource project = projectService.getById(projectId);
        ApplicationResource application = applicationService.getById(project.getApplication());
        CompetitionResource competition = competitionService.getById(application.getCompetition());

        return "redirect:/competition/" + competition.getId() + "/status";
    }

}
