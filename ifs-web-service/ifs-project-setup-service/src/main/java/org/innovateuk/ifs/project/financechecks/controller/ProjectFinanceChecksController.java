package org.innovateuk.ifs.project.financechecks.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.finance.viewmodel.ProjectFinanceChangesViewModel;
import org.innovateuk.ifs.application.forms.academiccosts.form.AcademicCostForm;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.commons.security.UserAuthenticationService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.finance.ProjectFinanceService;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.financecheck.FinanceCheckService;
import org.innovateuk.ifs.financecheck.eligibility.form.FinanceChecksEligibilityForm;
import org.innovateuk.ifs.financecheck.eligibility.viewmodel.FinanceChecksEligibilityViewModel;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.eligibility.populator.ProjectAcademicCostFormPopulator;
import org.innovateuk.ifs.project.eligibility.populator.ProjectFinanceChangesViewModelPopulator;
import org.innovateuk.ifs.project.finance.resource.EligibilityRagStatus;
import org.innovateuk.ifs.project.finance.resource.EligibilityResource;
import org.innovateuk.ifs.project.finance.resource.EligibilityState;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckEligibilityResource;
import org.innovateuk.ifs.project.financechecks.form.FinanceChecksQueryConstraints;
import org.innovateuk.ifs.project.financechecks.form.FinanceChecksQueryResponseForm;
import org.innovateuk.ifs.project.financechecks.populator.FinanceChecksEligibilityProjectCostsFormPopulator;
import org.innovateuk.ifs.project.financechecks.viewmodel.FinanceChecksProjectCostsViewModel;
import org.innovateuk.ifs.project.financechecks.viewmodel.ProjectFinanceChecksViewModel;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.project.resource.ProjectPartnerStatusResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.status.StatusService;
import org.innovateuk.ifs.thread.viewmodel.ThreadState;
import org.innovateuk.ifs.thread.viewmodel.ThreadViewModel;
import org.innovateuk.ifs.thread.viewmodel.ThreadViewModelPopulator;
import org.innovateuk.ifs.threads.attachment.resource.AttachmentResource;
import org.innovateuk.ifs.threads.resource.PostResource;
import org.innovateuk.ifs.threads.resource.QueryResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.innovateuk.ifs.util.EncryptedCookieService;
import org.innovateuk.ifs.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Supplier;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.groupingBy;
import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;
import static org.innovateuk.ifs.controller.FileUploadControllerUtils.getMultipartFileBytes;
import static org.innovateuk.ifs.file.controller.FileDownloadControllerUtils.getFileResponseEntity;
import static org.innovateuk.ifs.project.constant.ProjectActivityStates.COMPLETE;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleToMap;

/**
 * This controller will handle requests related to finance checks for external users
 */
@Controller
@RequestMapping(ProjectFinanceChecksController.PROJECT_FINANCE_CHECKS_BASE_URL)
public class ProjectFinanceChecksController {

    private static final Log LOG = LogFactory.getLog(ProjectFinanceChecksController.class);

    static final String PROJECT_FINANCE_CHECKS_BASE_URL = "/project/{projectId}/finance-checks";

    private static final String ATTACHMENT_COOKIE = "query_new_response_attachments";
    private static final String FORM_ATTR = "form";

    @Autowired
    private ProjectService projectService;

    @Autowired
    private StatusService statusService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ProjectFinanceService projectFinanceService;

    @Autowired
    private FinanceCheckService financeCheckService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private UserAuthenticationService userAuthenticationService;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private EncryptedCookieService cookieUtil;

    @Autowired
    private UserRestService userRestService;

    @Autowired
    private ApplicantRestService applicantRestService;

    @Autowired
    private ThreadViewModelPopulator threadViewModelPopulator;

    @Autowired
    private FinanceChecksEligibilityProjectCostsFormPopulator formPopulator;

    @Autowired
    private ProjectFinanceChangesViewModelPopulator projectFinanceChangesViewModelPopulator;

    @Autowired
    private ProjectAcademicCostFormPopulator projectAcademicCostFormPopulator;

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_FINANCE_CHECKS_SECTION_EXTERNAL')")
    @GetMapping
    public String viewFinanceChecks(Model model, @PathVariable final long projectId,
                                    UserResource loggedInUser) {

        long organisationId = projectService.getOrganisationIdFromUser(projectId, loggedInUser);
        ProjectOrganisationCompositeId projectComposite = new ProjectOrganisationCompositeId(projectId, organisationId);
        model.addAttribute("model", buildFinanceChecksLandingPage(projectComposite, null, null));

        return "project/finance-checks";
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_FINANCE_CHECKS_SECTION_EXTERNAL')")
    @GetMapping("/{queryId}/new-response")
    public String viewNewResponse(@PathVariable long projectId,
                                  @PathVariable Long queryId,
                                  Model model,
                                  HttpServletRequest request,
                                  HttpServletResponse response,
                                  UserResource loggedInUser) {

        Long organisationId = projectService.getOrganisationIdFromUser(projectId, loggedInUser);
        ProjectOrganisationCompositeId projectComposite = new ProjectOrganisationCompositeId(projectId, organisationId);
        List<Long> attachments = loadAttachmentsFromCookie(request, projectId, organisationId, queryId);
        attachments.forEach(financeCheckService::deleteFile);
        saveAttachmentsToCookie(response, new ArrayList<>(), projectId, organisationId, queryId);
        model.addAttribute("model", buildFinanceChecksLandingPage(projectComposite, attachments, queryId));
        model.addAttribute(FORM_ATTR, new FinanceChecksQueryResponseForm());
        return "project/finance-checks";
    }


    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_FINANCE_CHECKS_SECTION_EXTERNAL')")
    @PostMapping("/{queryId}/new-response")
    public String saveResponse(Model model,
                               @PathVariable final long projectId,
                               @PathVariable final Long queryId,
                               @Valid @ModelAttribute(FORM_ATTR) final FinanceChecksQueryResponseForm form,
                               @SuppressWarnings("unused") BindingResult bindingResult,
                               ValidationHandler validationHandler,
                               UserResource loggedInUser,
                               HttpServletRequest request,
                               HttpServletResponse response) {

        Long organisationId = projectService.getOrganisationIdFromUser(projectId, loggedInUser);
        ProjectOrganisationCompositeId projectComposite = new ProjectOrganisationCompositeId(projectId, organisationId);

        Supplier<String> failureView = () -> {
            List<Long> attachments = loadAttachmentsFromCookie(request, projectId, organisationId, queryId);
            ProjectFinanceChecksViewModel viewModel = buildFinanceChecksLandingPage(projectComposite, attachments, queryId);
            model.addAttribute("model", viewModel);
            model.addAttribute(FORM_ATTR, form);
            return "project/finance-checks";
        };

        Supplier<String> saveFailureView = () -> {
            ProjectFinanceChecksViewModel viewModel = buildFinanceChecksLandingPage(projectComposite, null, null);
            model.addAttribute("model", viewModel);
            model.addAttribute("nonFormErrors", validationHandler.getAllErrors());
            model.addAttribute(FORM_ATTR, null);
            return "project/finance-checks";
        };

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            ValidationMessages validationMessages = new ValidationMessages(bindingResult);

            return validationHandler.addAnyErrors(validationMessages, fieldErrorsToFieldErrors(), asGlobalErrors()).
                    failNowOrSucceedWith(failureView, () -> {

                        List<AttachmentResource> attachmentResources = new ArrayList<>();
                        List<Long> attachments = loadAttachmentsFromCookie(request, projectId, organisationId, queryId);
                        attachments.forEach(attachment -> financeCheckService.getAttachment(attachment).ifSuccessful(fileEntry -> attachmentResources.add(fileEntry)));
                        PostResource post = new PostResource(null, loggedInUser, form.getResponse(), attachmentResources, ZonedDateTime.now());

                        ValidationMessages errors = new ValidationMessages();
                        financeCheckService.saveQueryPost(post, queryId).andOnFailure(saveResult -> {
                            errors.addError(fieldError("saveError", null, "validation.notesandqueries.query.response.save.failed"));
                            validationHandler.addAnyErrors(errors);
                            attachments.forEach(attachment -> financeCheckService.deleteFile(attachment));
                            cookieUtil.removeCookie(response, getCookieName(projectId, organisationId, queryId));
                        });
                        return validationHandler.failNowOrSucceedWith(saveFailureView, () -> {
                            cookieUtil.removeCookie(response, getCookieName(projectId, organisationId, queryId));
                            return redirectToQueries(projectId);
                        });
                    });
        });
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_FINANCE_CHECKS_SECTION_EXTERNAL')")
    @PostMapping(value = "/{queryId}/new-response", params = "uploadAttachment")
    public String saveNewResponseAttachment(Model model,
                                            @PathVariable("projectId") final long projectId,
                                            @PathVariable Long queryId,
                                            @ModelAttribute(FORM_ATTR) FinanceChecksQueryResponseForm form,
                                            @SuppressWarnings("unused") BindingResult bindingResult,
                                            ValidationHandler validationHandler,
                                            HttpServletRequest request,
                                            HttpServletResponse response,
                                            UserResource loggedInUser) {
        Long organisationId = projectService.getOrganisationIdFromUser(projectId, loggedInUser);
        ProjectOrganisationCompositeId projectComposite = new ProjectOrganisationCompositeId(projectId, organisationId);

        List<Long> attachments = loadAttachmentsFromCookie(request, projectId, organisationId, queryId);
        Supplier<String> view = () -> {
            ProjectFinanceChecksViewModel viewModel = buildFinanceChecksLandingPage(projectComposite, attachments, queryId);
            model.addAttribute("model", viewModel);
            model.addAttribute("form", form);
            return "project/finance-checks";
        };

        return validationHandler.performActionOrBindErrorsToField("attachment", view, view, () -> {
            MultipartFile file = form.getAttachment();
            ServiceResult<AttachmentResource> result = financeCheckService.uploadFile(projectId, file.getContentType(),
                    file.getSize(), file.getOriginalFilename(), getMultipartFileBytes(file));
            if (result.isSuccess()) {
                attachments.add(result.getSuccess().id);
                saveAttachmentsToCookie(response, attachments, projectId, organisationId, queryId);
            }
            ProjectFinanceChecksViewModel viewModel = buildFinanceChecksLandingPage(projectComposite, attachments, queryId);
            model.addAttribute("model", viewModel);
            return result;
        });
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_FINANCE_CHECKS_SECTION_EXTERNAL')")
    @GetMapping("/{queryId}/new-response/attachment/{attachmentId}")
    public
    @ResponseBody
    ResponseEntity<ByteArrayResource> downloadResponseAttachment(@PathVariable long projectId,
                                                                 @PathVariable Long queryId,
                                                                 @PathVariable long attachmentId,
                                                                 UserResource loggedInUser,
                                                                 HttpServletRequest request) {
        Long organisationId = projectService.getOrganisationIdFromUser(projectId, loggedInUser);
        List<Long> attachments = loadAttachmentsFromCookie(request, projectId, organisationId, queryId);

        if (attachments.contains(attachmentId)) {
            return getFileResponseEntity(financeCheckService.downloadFile(attachmentId), financeCheckService.getAttachmentInfo(attachmentId));
        } else {
            throw new ObjectNotFoundException();
        }
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_FINANCE_CHECKS_SECTION_EXTERNAL')")
    @GetMapping("/attachment/{attachmentId}")
    public @ResponseBody ResponseEntity<ByteArrayResource> downloadAttachment(@PathVariable Long projectId,  @PathVariable long attachmentId) {
        return getFileResponseEntity(financeCheckService.downloadFile(attachmentId), financeCheckService.getAttachmentInfo(attachmentId));
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_FINANCE_CHECKS_SECTION_EXTERNAL')")
    @PostMapping(value = "/{queryId}/new-response", params = "removeAttachment")
    public String removeAttachment(@PathVariable long projectId,
                                   @PathVariable Long queryId,
                                   @RequestParam(value = "removeAttachment") final Long attachmentId,
                                   @ModelAttribute(FORM_ATTR) FinanceChecksQueryResponseForm form,
                                   @SuppressWarnings("unused") BindingResult bindingResult,
                                   UserResource loggedInUser,
                                   HttpServletRequest request,
                                   HttpServletResponse response,
                                   Model model) {
        Long organisationId = projectService.getOrganisationIdFromUser(projectId, loggedInUser);

        ProjectOrganisationCompositeId projectComposite = new ProjectOrganisationCompositeId(projectId, organisationId);

        List<Long> attachments = loadAttachmentsFromCookie(request, projectId, organisationId, queryId);
        if (attachments.contains(attachmentId)) {
            attachments.remove(attachmentId);
            financeCheckService.deleteFile(attachmentId);
        }
        saveAttachmentsToCookie(response, attachments, projectId, organisationId, queryId);

        ProjectFinanceChecksViewModel viewModel = buildFinanceChecksLandingPage(projectComposite, attachments, queryId);
        model.addAttribute("model", viewModel);
        model.addAttribute("form", form);
        return "project/finance-checks";
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_FINANCE_CHECKS_SECTION_EXTERNAL')")
    @GetMapping("/{queryId}/new-response/cancel")
    public String cancelNewForm(@PathVariable long projectId,
                                @PathVariable Long queryId,
                                HttpServletRequest request,
                                HttpServletResponse response,
                                UserResource loggedInUser) {
        Long organisationId = projectService.getOrganisationIdFromUser(projectId, loggedInUser);

        List<Long> attachments = loadAttachmentsFromCookie(request, projectId, organisationId, queryId);
        attachments.forEach((id -> financeCheckService.deleteFile(id)));

        cookieUtil.removeCookie(response, getCookieName(projectId, organisationId, queryId));

        return redirectToQueries(projectId);
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_FINANCE_CHECKS_SECTION_EXTERNAL')")
    @GetMapping("/eligibility")
    public String viewExternalEligibilityPage(@PathVariable final long projectId,
                                              Model model,
                                              UserResource loggedInUser) {
        ProjectResource project = projectService.getById(projectId);
        Long organisationId = projectService.getOrganisationIdFromUser(projectId, loggedInUser);
        ApplicationResource application = applicationService.getById(project.getApplication());
        OrganisationResource organisation = organisationRestService.getOrganisationById(organisationId).getSuccess();
        OrganisationResource leadOrganisation = projectService.getLeadOrganisation(projectId);
        boolean isLeadPartnerOrganisation = leadOrganisation.getId().equals(organisation.getId());

        return doViewEligibility(application, project, isLeadPartnerOrganisation, organisation, model);
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_FINANCE_CHECKS_SECTION_EXTERNAL')")
    @GetMapping("/eligibility/changes")
    public String viewExternalEligibilityChanges(@PathVariable final long projectId,
                                                 Model model,
                                                 UserResource loggedInUser) {
        Long organisationId = projectService.getOrganisationIdFromUser(projectId, loggedInUser);
        ProjectResource project = projectService.getById(projectId);
        OrganisationResource organisation = organisationRestService.getOrganisationById(organisationId).getSuccess();
        return doViewEligibilityChanges(project, organisation, loggedInUser.getId(), model);
    }

    private ProjectFinanceChecksViewModel buildFinanceChecksLandingPage(final ProjectOrganisationCompositeId compositeId, List<Long> attachments, Long queryId) {

        Long projectId = compositeId.getProjectId();
        Long organisationId = compositeId.getOrganisationId();
        ProjectResource projectResource = projectService.getById(projectId);
        CompetitionResource competition = competitionRestService.getCompetitionById(projectResource.getCompetition()).getSuccess();
        OrganisationResource organisationResource = organisationRestService.getOrganisationById(organisationId).getSuccess();

        Map<Long, String> attachmentLinks =
                simpleToMap(attachments, identity(), id -> financeCheckService.getAttachmentInfo(id).getName());

        boolean approved = isApproved(compositeId);

        Map<ThreadState, List<ThreadViewModel>> groupedQueries = getGroupedQueries(projectId, organisationId);

        List<ThreadViewModel> closedQueryThreads = groupedQueries.get(ThreadState.CLOSED) == null ? emptyList() : groupedQueries.get(ThreadState.CLOSED);
        List<ThreadViewModel> lastPostByExternalUserQueryThreads = groupedQueries.get(ThreadState.LAST_POST_BY_EXTERNAL_USER) == null ? emptyList() : groupedQueries.get(ThreadState.LAST_POST_BY_EXTERNAL_USER);
        List<ThreadViewModel> lastPostByInternalUserQueryThreads = groupedQueries.get(ThreadState.LAST_POST_BY_INTERNAL_USER) == null ? emptyList() : groupedQueries.get(ThreadState.LAST_POST_BY_INTERNAL_USER);

        return new ProjectFinanceChecksViewModel(projectResource,
                organisationResource,
                lastPostByInternalUserQueryThreads,
                lastPostByExternalUserQueryThreads,
                closedQueryThreads,
                approved,
                attachmentLinks,
                FinanceChecksQueryConstraints.MAX_QUERY_WORDS,
                FinanceChecksQueryConstraints.MAX_QUERY_CHARACTERS,
                queryId,
                PROJECT_FINANCE_CHECKS_BASE_URL, competition.applicantShouldUseJesFinances(organisationResource.getOrganisationTypeEnum()),
                competition.isLoan());
    }

    private boolean isApproved(final ProjectOrganisationCompositeId compositeId) {
        Optional<ProjectPartnerStatusResource> organisationStatus = statusService.getProjectTeamStatus(compositeId.getProjectId(), Optional.empty()).getPartnerStatusForOrganisation(compositeId.getOrganisationId());
        return COMPLETE.equals(organisationStatus.map(ProjectPartnerStatusResource::getFinanceChecksStatus).orElse(null));
    }

    private Map<ThreadState, List<ThreadViewModel>> getGroupedQueries(Long projectId, Long organisationId) {

        ProjectFinanceResource projectFinance = projectFinanceService.getProjectFinance(projectId, organisationId);

        ServiceResult<List<QueryResource>> queriesResult = financeCheckService.getQueries(projectFinance.getId());

        return queriesResult.handleSuccessOrFailure(

                failure -> emptyMap(),
                success -> {
                    List<ThreadViewModel> queryThreads =
                            threadViewModelPopulator.threadViewModelListFromQueries(projectId, organisationId, queriesResult.getSuccess(),
                                    threadViewModelPopulator.anonymousProjectFinanceOrNamedExternalUser(organisationId));

                    return queryThreads
                            .stream()
                            .collect(groupingBy(ThreadViewModel::getState));
                });
    }

    private String redirectToQueries(Long projectId) {
        return "redirect:/project/" + projectId + "/finance-checks";
    }

    private String getCookieName(Long projectId, Long organisationId, Long queryId) {
        return ATTACHMENT_COOKIE + "_" + projectId + "_" + organisationId + "_" + queryId;
    }

    private void saveAttachmentsToCookie(HttpServletResponse response, List<Long> attachmentFileIds, Long projectId, Long organisationId, Long queryId) {
        String jsonState = JsonUtil.getSerializedObject(attachmentFileIds);
        cookieUtil.saveToCookie(response, getCookieName(projectId, organisationId, queryId), jsonState);
    }

    private List<Long> loadAttachmentsFromCookie(HttpServletRequest request, Long projectId, Long organisationId, Long queryId) {

        List<Long> attachments = new LinkedList<>();
        String json = cookieUtil.getCookieValue(request, getCookieName(projectId, organisationId, queryId));

        if (json != null && !"".equals(json)) {
            TypeReference<List<Long>> listType = new TypeReference<List<Long>>() {
            };
            ObjectMapper mapper = new ObjectMapper();
            try {
                attachments = mapper.readValue(json, listType);
                return attachments;
            } catch (IOException e) {
                //ignored
                LOG.trace(e);
            }
        }
        return attachments;
    }

    private String doViewEligibility(ApplicationResource application, ProjectResource project, boolean isLeadPartnerOrganisation, OrganisationResource organisation, Model model) {

        EligibilityResource eligibility = projectFinanceService.getEligibility(project.getId(), organisation.getId());

        FinanceChecksEligibilityForm eligibilityForm = getEligibilityForm(eligibility);

        FinanceCheckEligibilityResource eligibilityOverview = financeCheckService.getFinanceCheckEligibilityDetails(project.getId(), organisation.getId());

        boolean eligibilityApproved = eligibility.getEligibility() == EligibilityState.APPROVED;

        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();

        List<ProjectFinanceResource> projectFinances = projectFinanceService.getProjectFinances(project.getId());

        boolean isUsingJesFinances = competition.applicantShouldUseJesFinances(organisation.getOrganisationTypeEnum());
        if (!isUsingJesFinances) {
            model.addAttribute("model", new FinanceChecksProjectCostsViewModel(application.getId(), competition.getFinanceRowTypes(), competition.getId(), competition.getName()));
            model.addAttribute("form", formPopulator.populateForm(project.getId(), organisation.getId()));
        } else {
            model.addAttribute("academicCostForm", projectAcademicCostFormPopulator.populate(new AcademicCostForm(), project.getId(), organisation.getId()));
        }

        model.addAttribute("summaryModel", new FinanceChecksEligibilityViewModel(project, competition, eligibilityOverview,
                organisation.getName(),
                isLeadPartnerOrganisation,
                organisation.getId(),
                eligibilityApproved,
                eligibility.getEligibilityRagStatus(),
                eligibility.getEligibilityApprovalUserFirstName(),
                eligibility.getEligibilityApprovalUserLastName(),
                eligibility.getEligibilityApprovalDate(),
                true,
                isUsingJesFinances,
                false,
                projectFinances));

        model.addAttribute("eligibilityForm", eligibilityForm);

        return "project/financecheck/eligibility";
    }

    private FinanceChecksEligibilityForm getEligibilityForm(EligibilityResource eligibility) {

        boolean confirmEligibilityChecked = eligibility.getEligibilityRagStatus() != EligibilityRagStatus.UNSET;

        return new FinanceChecksEligibilityForm(eligibility.getEligibilityRagStatus(), confirmEligibilityChecked);
    }

    private String doViewEligibilityChanges(ProjectResource project, OrganisationResource organisation, Long userId, Model model) {
        ProjectFinanceChangesViewModel projectFinanceChangesViewModel = projectFinanceChangesViewModelPopulator.getProjectFinanceChangesViewModel(false, project, organisation, userId);
        model.addAttribute("model", projectFinanceChangesViewModel);
        return "project/financecheck/eligibility-changes";
    }

    protected void setThreadViewModelPopulator(ThreadViewModelPopulator threadViewModelPopulator) {
        this.threadViewModelPopulator = threadViewModelPopulator;
    }
}