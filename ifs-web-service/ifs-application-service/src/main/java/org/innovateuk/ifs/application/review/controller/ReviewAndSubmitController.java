package org.innovateuk.ifs.application.review.controller;

import org.innovateuk.ifs.application.forms.form.ApplicationReopenForm;
import org.innovateuk.ifs.application.forms.form.ApplicationSubmitForm;
import org.innovateuk.ifs.application.forms.form.EoiEvidenceForm;
import org.innovateuk.ifs.application.resource.ApplicationEoiEvidenceResponseResource;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.review.populator.ReviewAndSubmitViewModelPopulator;
import org.innovateuk.ifs.application.review.populator.TrackViewModelPopulator;
import org.innovateuk.ifs.application.review.viewmodel.ReviewAndSubmitViewModel;
import org.innovateuk.ifs.application.service.ApplicationEoiEvidenceResponseRestService;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.QuestionStatusRestService;
import org.innovateuk.ifs.async.annotations.AsyncMethod;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.resource.CovidType;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.horizon.service.HorizonWorkProgrammeRestService;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.ProcessRoleRestService;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static java.lang.Boolean.TRUE;
import static java.lang.String.format;
import static org.innovateuk.ifs.application.resource.ApplicationState.SUBMITTED;
import static org.innovateuk.ifs.controller.FileUploadControllerUtils.getMultipartFileBytes;
import static org.innovateuk.ifs.file.controller.FileDownloadControllerUtils.getFileResponseEntity;

@Controller
@RequestMapping("/application")
public class ReviewAndSubmitController {
    public static final String FORM_ATTR_NAME = "form";

    @Autowired
    private ReviewAndSubmitViewModelPopulator reviewAndSubmitViewModelPopulator;
    @Autowired
    private ApplicationRestService applicationRestService;
    @Autowired
    private CompetitionRestService competitionRestService;
    @Autowired
    private UserService userService;
    @Autowired
    private CookieFlashMessageFilter cookieFlashMessageFilter;
    @Autowired
    private QuestionStatusRestService questionStatusRestService;
    @Autowired
    private ProcessRoleRestService processRoleRestService;
    @Autowired
    private HorizonWorkProgrammeRestService horizonWorkProgrammeRestService;
    @Autowired
    private TrackViewModelPopulator trackViewModelPopulator;
    @Autowired
    private ApplicationEoiEvidenceResponseRestService applicationEoiEvidenceResponseRestService;

    @SecuredBySpring(value = "READ", description = "Applicants can review and submit their applications")
    @PreAuthorize("hasAnyAuthority('applicant')")
    @GetMapping("/{applicationId}/review-and-submit")
    @AsyncMethod
    public String reviewAndSubmit(@ModelAttribute(value = FORM_ATTR_NAME, binding = false) ApplicationSubmitForm form,
                                  BindingResult bindingResult,
                                  @PathVariable long applicationId,
                                  Model model,
                                  UserResource user) {

        ReviewAndSubmitViewModel viewModel = reviewAndSubmitViewModelPopulator.populate(applicationId, user);
        model.addAttribute("model", viewModel);
        handleSubsidyBasis(viewModel, bindingResult);


        return "application/review-and-submit";
    }

    @SecuredBySpring(value = "APPLICATION_SUBMIT", description = "Applicants can submit their applications.")
    @PreAuthorize("hasAuthority('applicant')")
    @PostMapping("/{applicationId}/review-and-submit")
    public String submitApplication(@PathVariable long applicationId,
                                    @ModelAttribute(FORM_ATTR_NAME) ApplicationSubmitForm form,
                                    BindingResult bindingResult,
                                    UserResource user,
                                    HttpServletResponse response) {

        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();

        if (!ableToSubmitApplication(user, application)) {
            cookieFlashMessageFilter.setFlashMessage(response, "cannotSubmit");
            return format("redirect:/application/%d", applicationId);
        }

        return format("redirect:/application/%d/confirm-submit?termsAgreed=true", applicationId);
    }

    @SecuredBySpring(value = "APPLICATION_REVIEW_AND_SUBMIT_RETURN_AND_EDIT",
            description = "Applicants can return to edit questions from the review and submit page")
    @PreAuthorize("hasAuthority('applicant')")
    @PostMapping(value = "/{applicationId}/review-and-submit", params = "edit")
    public String editQuestion(@PathVariable long applicationId,
                               @RequestParam("edit") long questionId) {
        return redirectToQuestion(applicationId, questionId);
    }

    @SecuredBySpring(value = "APPLICATION_REVIEW_AND_SUBMIT_MARK_AS_COMPLETE",
            description = "Applicants can mark questions as complete from the review and submit page")
    @PreAuthorize("hasAuthority('applicant')")
    @PostMapping(value = "/{applicationId}/review-and-submit", params = "complete")
    public String completeQuestion(@PathVariable long applicationId,
                                   @RequestParam("complete") long questionId,
                                   UserResource user) {
        ProcessRoleResource processRole = processRoleRestService.findProcessRole(user.getId(), applicationId).getSuccess();
        List<ValidationMessages> messages = questionStatusRestService.markAsComplete(questionId, applicationId, processRole.getId()).getSuccess();
        long competitionId = applicationRestService.getApplicationById(applicationId).getSuccess().getCompetition();
        boolean hecpCompetition = competitionRestService.getCompetitionById(competitionId).getSuccess().isHorizonEuropeGuarantee();
        boolean workProgrammeQuestionIncomplete = hecpCompetition && horizonWorkProgrammeRestService.findSelected(applicationId).getSuccess().isEmpty();

        if (messages.isEmpty() && !workProgrammeQuestionIncomplete) {
            return redirectToReview(applicationId);
        } else {
            return handleMarkAsCompleteFailure(applicationId, questionId, processRole);
        }
    }

    @SecuredBySpring(value = "APPLICATION_REVIEW_AND_SUBMIT_ASSIGN",
            description = "Applicants can assign questions from the review and submit page")
    @PreAuthorize("hasAuthority('applicant')")
    @PostMapping(value = "/{applicationId}/review-and-submit", params = "assign")
    public String assignQuestionToLead(@PathVariable long applicationId,
                                       @RequestParam("assign") long questionId,
                                       UserResource user) {

        ProcessRoleResource assignTo = userService.getLeadApplicantProcessRole(applicationId);
        ProcessRoleResource assignFrom = processRoleRestService.findProcessRole(user.getId(), applicationId).getSuccess();
        questionStatusRestService.assign(questionId, applicationId, assignTo.getId(), assignFrom.getId()).getSuccess();
        return redirectToReview(applicationId);
    }

    @SecuredBySpring(value = "APPLICATION_REVIEW_AND_SUBMIT_MARK_AS_INCOMPLETE",
            description = "Applicants can mark questions as incomplete from the review and submit page")
    @PreAuthorize("hasAuthority('applicant')")
    @PostMapping(value = "/{applicationId}/review-and-submit", params = "incomplete")
    public String incompleteQuestion(@PathVariable long applicationId,
                                     @RequestParam("incomplete") long questionId,
                                     UserResource user) {
        ProcessRoleResource processRole = processRoleRestService.findProcessRole(user.getId(), applicationId).getSuccess();
        questionStatusRestService.markAsInComplete(questionId, applicationId, processRole.getId());
        return redirectToQuestion(applicationId, questionId);
    }

    private String redirectToQuestion(long applicationId, long questionId) {
        return format("redirect:/application/%d/form/question/%d", applicationId, questionId);
    }

    private String redirectToReview(long applicationId) {
        return format("redirect:/application/%d/review-and-submit", applicationId);
    }


    @SecuredBySpring(value = "APPLICANT_CONFIRM_SUBMIT", description = "Applicants can confirm they wish to submit their applications")
    @PreAuthorize("hasAuthority('applicant')")
    @GetMapping("/{applicationId}/confirm-submit")
    public String applicationConfirmSubmit(@PathVariable long applicationId,
                                           @ModelAttribute("termsAgreed") Boolean termsAgreed,
                                           @ModelAttribute(FORM_ATTR_NAME) ApplicationSubmitForm form,
                                           Model model) {
        if (!TRUE.equals(termsAgreed)) {
            return format("redirect:/application/%d/summary", applicationId);
        }
        model.addAttribute("termsAgreed", termsAgreed);
        model.addAttribute("applicationId", applicationId);
        return "application-confirm-submit";
    }

    @SecuredBySpring(value = "APPLICANT_CONFIRM_SUBMIT", description = "Applicants can confirm they wish to submit their applications")
    @PreAuthorize("hasAuthority('applicant')")
    @PostMapping("/{applicationId}/confirm-submit")
    public String applicationSubmit(Model model,
                                    @ModelAttribute(FORM_ATTR_NAME) ApplicationSubmitForm form,
                                    @SuppressWarnings("UnusedParameters") BindingResult bindingResult,
                                    ValidationHandler validationHandler,
                                    @PathVariable("applicationId") long applicationId,
                                    UserResource user,
                                    HttpServletResponse response) {

        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();

        if (!ableToSubmitApplication(user, application)) {
            cookieFlashMessageFilter.setFlashMessage(response, "cannotSubmit");
            return  format("redirect:/application/%d", applicationId);
        }

        RestResult<Void> updateResult = applicationRestService.updateApplicationState(applicationId, SUBMITTED);

        Supplier<String> failureView = () -> applicationConfirmSubmit(applicationId, true, form, model);

        return validationHandler.addAnyErrors(updateResult)
                .failNowOrSucceedWith(failureView, () -> format("redirect:/application/%d/track", applicationId));
    }

    @SecuredBySpring(value = "APPLICANT_REOPEN", description = "Applicants can confirm they wish to reopen their applications")
    @PreAuthorize("hasAuthority('applicant')")
    @GetMapping("/{applicationId}/confirm-reopen")
    public String applicationConfirmReopen(@PathVariable long applicationId,
                                           @ModelAttribute(FORM_ATTR_NAME) ApplicationReopenForm form,
                                           Model model,
                                           UserResource userResource) {

        ApplicationResource applicationResource = applicationRestService.getApplicationById(applicationId).getSuccess();
        CompetitionResource competitionResource = competitionRestService.getCompetitionById(applicationResource.getCompetition()).getSuccess();

        if (!canReopenApplication(applicationResource, userResource, competitionResource)) {
            return "redirect:/application/" + applicationId + "/track";
        }

        model.addAttribute("applicationId", applicationResource.getId());
        model.addAttribute("applicationName", applicationResource.getName());
        model.addAttribute("competitionName", applicationResource.getCompetitionName());

        return "application-confirm-reopen";
    }

    private boolean canReopenApplication(ApplicationResource application, UserResource user, CompetitionResource competitionResource) {
        if (application.isEnabledForExpressionOfInterest()) {
            return false;
        }

        return !competitionResource.isAlwaysOpen()
                && CompetitionStatus.OPEN.equals(application.getCompetitionStatus())
                && application.canBeReopened()
                && userService.isLeadApplicant(user.getId(), application);
    }

    @SecuredBySpring(value = "APPLICANT_REOPEN", description = "Applicants can reopen their applications")
    @PreAuthorize("hasAuthority('applicant')")
    @PostMapping("/{applicationId}/confirm-reopen")
    public String applicationReopen(Model model,
                                    @ModelAttribute(FORM_ATTR_NAME) ApplicationReopenForm form,
                                    @SuppressWarnings("UnusedParameters") BindingResult bindingResult,
                                    ValidationHandler validationHandler,
                                    @PathVariable("applicationId") long applicationId) {

        RestResult<Void> updateResult = applicationRestService.reopenApplication(applicationId);

        Supplier<String> failureView = () -> applicationReopen(model, form, bindingResult, validationHandler, applicationId);
        Supplier<String> successView = () -> format("redirect:/application/%d", applicationId);

        return validationHandler.addAnyErrors(updateResult)
                .failNowOrSucceedWith(failureView, successView);
    }

    @PreAuthorize("hasAuthority('applicant')")
    @PostMapping(value="/{applicationId}/track", params = "upload-eoi-evidence")
    @SecuredBySpring(value = "UPLOAD_EOI_EVIDENCE", description = "Lead applicant can upload their eoi evidence")
    public String uploadDocument(@PathVariable("applicationId") long applicationId,
                                 @ModelAttribute("form") EoiEvidenceForm form,
                                 @SuppressWarnings("unused") BindingResult bindingResult,
                                 ValidationHandler validationHandler,
                                 Model model,
                                 UserResource loggedInUser) {
        long organisationId = processRoleRestService.findProcessRole(loggedInUser.getId(), applicationId).getSuccess().getOrganisationId();
        MultipartFile file = form.getEoiEvidenceFile();
        Supplier<String> view = () -> applicationTrack(model, applicationId, form, loggedInUser);

        return validationHandler.performFileUpload("eoiEvidenceFile", view, () ->
                applicationEoiEvidenceResponseRestService.uploadEoiEvidence(applicationId, organisationId, loggedInUser.getId(), file.getContentType(), file.getSize(), file.getOriginalFilename(), getMultipartFileBytes(file)));
    }

    @PostMapping(value="/{applicationId}/track", params = "remove-eoi-evidence")
    @PreAuthorize("hasAuthority('applicant')")
    @SecuredBySpring(value = "REMOVE_EOI_EVIDENCE", description = "Lead applicant can remove their eoi evidence")
    public String removeEoiEvidenceResponse(@PathVariable("applicationId") long applicationId,
                                            @ModelAttribute("form") EoiEvidenceForm form,
                                            Model model,
                                            UserResource loggedInUser) {

        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();
        applicationEoiEvidenceResponseRestService.remove(application.getApplicationEoiEvidenceResponseResource(), loggedInUser).getSuccess();

        return "redirect:/application/" + applicationId + "/track";
    }

    @PostMapping(value="/{applicationId}/track", params = "submit-eoi-evidence")
    @PreAuthorize("hasAuthority('applicant')")
    @SecuredBySpring(value = "SUBMIT_EOI_EVIDENCE", description = "Lead applicant can submit eoi evidence")
    public String submitEoiEvidenceResponse(@PathVariable("applicationId") long applicationId,
                                            @ModelAttribute("form") EoiEvidenceForm form,
                                            @SuppressWarnings("unused") BindingResult bindingResult,
                                            ValidationHandler validationHandler,
                                            Model model,
                                            UserResource loggedInUser) {
        Optional<ApplicationEoiEvidenceResponseResource> applicationEoiEvidenceResponseResource = applicationEoiEvidenceResponseRestService.findOneByApplicationId(applicationId).getSuccess();
        boolean noEoiEvidenceToSubmit = (applicationEoiEvidenceResponseResource.isEmpty())|| (applicationEoiEvidenceResponseResource.get().getFileEntryId() == null);
        if (noEoiEvidenceToSubmit) {
            bindingResult.rejectValue("eoiEvidenceFile", "validation.file.required");
            return applicationTrack(model, applicationId, form, loggedInUser);
        }
        applicationEoiEvidenceResponseRestService.submitEoiEvidence(applicationEoiEvidenceResponseResource.get(), loggedInUser);
        return String.format("redirect:/application/%s/track", applicationId);
    }

    @SecuredBySpring(value = "DOWNLOAD_EOI_EVIDENCE", description = "Lead applicant can download eoi evidence")
    @PreAuthorize("hasAuthority('applicant')")
    @GetMapping("/{applicationId}/view-eoi-evidence")
    public @ResponseBody ResponseEntity<ByteArrayResource> downloadEOIEvidenceFile(
            @PathVariable("applicationId") final Long applicationId) {

        final ByteArrayResource resource = applicationEoiEvidenceResponseRestService.getEvidenceByApplication(applicationId).getSuccess();
        final FileEntryResource fileDetails = applicationEoiEvidenceResponseRestService.getEvidenceDetailsByApplication(applicationId).getSuccess();

       return getFileResponseEntity(resource, fileDetails);
    }


    @SecuredBySpring(value = "APPLICANT_TRACK", description = "Applicants and kta can track their application after submitting.")
    @PreAuthorize("hasAnyAuthority('applicant', 'knowledge_transfer_adviser')")
    @GetMapping("/{applicationId}/track")
    public String applicationTrack(Model model,
                                   @PathVariable long applicationId,
                                   @ModelAttribute("form") EoiEvidenceForm form,
                                   UserResource user) {
        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();
        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();

        if (!application.isSubmitted()) {
            return "redirect:/application/" + applicationId;
        }

        model.addAttribute("model", trackViewModelPopulator.populate(applicationId,canReopenApplication(application, user, competition), user));
        return getTrackingPage(competition, application);
    }

    private String getTrackingPage(CompetitionResource competition, ApplicationResource application) {
        if (CovidType.ADDITIONAL_FUNDING.equals(competition.getCovidType())) {
            return "covid-additional-funding-application-track";
        } else if (competition.isHorizonEuropeGuarantee()) {
            if (isExpressionOfInterestApplication(application)) {
                return "horizon-europe-guarantee-eoi-application-track";
            }
            return "horizon-europe-guarantee-application-track";
        } else if (competition.isAlwaysOpen()) {
            return "always-open-track";
        } else if (competition.isH2020()) {
            return "h2020-grant-transfer-track";
        } else if (competition.isLoan()) {
            return "loan-application-track";
        } else if (competition.isThirdPartyOfgem()) {
            return "third-party-ofgem-application-track";
        } else {
            return "application-track";
        }
    }

    private boolean ableToSubmitApplication(UserResource user, ApplicationResource application) {
        return userService.isLeadApplicant(user.getId(), application) && application.isSubmittable();
    }

    private boolean isExpressionOfInterestApplication(ApplicationResource application) {
        return application.isEnabledForExpressionOfInterest();
    }

    private String handleMarkAsCompleteFailure(long applicationId, long questionId, ProcessRoleResource processRole) {
        questionStatusRestService.markAsInComplete(questionId, applicationId, processRole.getId());
        return "redirect:/application/" + applicationId + "/form/question/edit/" + questionId + "?show-errors=true";
    }

    private void handleSubsidyBasis(ReviewAndSubmitViewModel viewModel, BindingResult bindingResult) {
        if(viewModel.isUserIsLeadApplicant() && viewModel.isWaitingForPartnerSubsidyBasisOnly()) {
            bindingResult.addError(new ObjectError("subsidyBasis", new String[] {"validation.subsidy.basis.partner.incomplete"}, null, null));
        }
    }
}
