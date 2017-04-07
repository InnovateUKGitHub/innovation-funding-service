package org.innovateuk.ifs.project.financechecks.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.innovateuk.ifs.application.finance.view.DefaultProjectFinanceModelManager;
import org.innovateuk.ifs.application.finance.view.FinanceHandler;
import org.innovateuk.ifs.application.finance.viewmodel.ProjectFinanceChangesViewModel;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.populator.ApplicationModelPopulator;
import org.innovateuk.ifs.application.populator.OpenProjectFinanceSectionModelPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.application.viewmodel.BaseSectionViewModel;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.commons.security.UserAuthenticationService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.finance.ProjectFinanceService;
import org.innovateuk.ifs.project.finance.resource.Eligibility;
import org.innovateuk.ifs.project.finance.resource.EligibilityRagStatus;
import org.innovateuk.ifs.project.finance.resource.EligibilityResource;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckEligibilityResource;
import org.innovateuk.ifs.project.financecheck.FinanceCheckService;
import org.innovateuk.ifs.project.financecheck.eligibility.form.FinanceChecksEligibilityForm;
import org.innovateuk.ifs.project.financecheck.eligibility.viewmodel.FinanceChecksEligibilityViewModel;
import org.innovateuk.ifs.project.financechecks.form.FinanceChecksQueryConstraints;
import org.innovateuk.ifs.project.financechecks.form.FinanceChecksQueryResponseForm;
import org.innovateuk.ifs.project.financechecks.viewmodel.ProjectFinanceChecksViewModel;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.project.resource.ProjectPartnerStatusResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.project.util.FinanceUtil;
import org.innovateuk.ifs.thread.viewmodel.ThreadPostViewModel;
import org.innovateuk.ifs.thread.viewmodel.ThreadViewModel;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserService;
import org.innovateuk.ifs.util.CookieUtil;
import org.innovateuk.ifs.util.JsonUtil;
import org.innovateuk.ifs.utils.UserOrganisationUtil;
import org.innovateuk.threads.attachment.resource.AttachmentResource;
import org.innovateuk.threads.resource.PostResource;
import org.innovateuk.threads.resource.QueryResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
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
import java.util.stream.Collectors;

import static org.innovateuk.ifs.application.resource.SectionType.PROJECT_COST_FINANCES;
import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;
import static org.innovateuk.ifs.controller.FileUploadControllerUtils.getMultipartFileBytes;
import static org.innovateuk.ifs.file.controller.FileDownloadControllerUtils.getFileResponseEntity;
import static org.innovateuk.ifs.project.constant.ProjectActivityStates.COMPLETE;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;

/**
 * This controller will handle requests related to finance checks
 */
@Controller
@RequestMapping(ProjectFinanceChecksController.PROJECT_FINANCE_CHECKS_BASE_URL)
public class ProjectFinanceChecksController {

    static final String PROJECT_FINANCE_CHECKS_BASE_URL = "/project/{projectId}/finance-checks";

    private static final String ATTACHMENT_COOKIE = "query_new_response_attachments";
    private static final String FORM_ATTR = "form";

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectFinanceService projectFinanceService;

    @Autowired
    private FinanceCheckService financeCheckService;

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private UserAuthenticationService userAuthenticationService;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private ApplicationModelPopulator applicationModelPopulator;

    @Autowired
    private OpenProjectFinanceSectionModelPopulator openFinanceSectionModel;

    @Autowired
    private CookieUtil cookieUtil;

    @Autowired
    private FinanceHandler financeHandler;

    @Autowired
    private FinanceUtil financeUtil;

    @Autowired
    private
    UserOrganisationUtil userOrganisationUtil;

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_SECTION_EXTERNAL')")
    @GetMapping
    public String viewFinanceChecks(Model model,
                                    @PathVariable("projectId") final Long projectId,
                                    @ModelAttribute("loggedInUser") UserResource loggedInUser) {

        Long organisationId = userOrganisationUtil.getOrganisationIdFromUser(projectId, loggedInUser);

        ProjectOrganisationCompositeId projectComposite = new ProjectOrganisationCompositeId(projectId, organisationId);

        model.addAttribute("model", buildFinanceChecksLandingPage(projectComposite, null, null));

        return "project/finance-checks";
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_SECTION_EXTERNAL')")
    @GetMapping("/{queryId}/new-response")
    public String viewNewResponse(@PathVariable Long projectId,
                                  @PathVariable Long queryId,
                                  Model model,
                                  HttpServletRequest request,
                                  HttpServletResponse response,
                                  @ModelAttribute("loggedInUser") UserResource loggedInUser) {

        Long organisationId = userOrganisationUtil.getOrganisationIdFromUser(projectId, loggedInUser);

        ProjectOrganisationCompositeId projectComposite = new ProjectOrganisationCompositeId(projectId, organisationId);

        List<Long> attachments = loadAttachmentsFromCookie(request, projectId, organisationId, queryId);
        attachments.forEach(id -> financeCheckService.deleteFile(id));
        saveAttachmentsToCookie(response, new ArrayList<>(), projectId, organisationId, queryId);

        ProjectFinanceChecksViewModel viewModel = buildFinanceChecksLandingPage(projectComposite, attachments, queryId);
        model.addAttribute("model", viewModel);
        FinanceChecksQueryResponseForm form = new FinanceChecksQueryResponseForm();
        model.addAttribute(FORM_ATTR, form);
        return "project/finance-checks";
    }


    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_SECTION_EXTERNAL')")
    @PostMapping("/{queryId}/new-response")
    public String saveResponse(Model model,
                               @PathVariable("projectId") final Long projectId,
                               @PathVariable final Long queryId,
                               @Valid @ModelAttribute(FORM_ATTR) final FinanceChecksQueryResponseForm form,
                               @SuppressWarnings("unused") BindingResult bindingResult,
                               ValidationHandler validationHandler,
                               @ModelAttribute("loggedInUser") UserResource loggedInUser,
                               HttpServletRequest request,
                               HttpServletResponse response) {

        Long organisationId = userOrganisationUtil.getOrganisationIdFromUser(projectId, loggedInUser);

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
                        attachments.forEach(attachment -> {
                            ServiceResult<AttachmentResource> fileEntry = financeCheckService.getAttachment(attachment);
                            if (fileEntry.isSuccess()) {
                                attachmentResources.add(fileEntry.getSuccessObject());
                            }
                        });
                        PostResource post = new PostResource(null, loggedInUser, form.getResponse(), attachmentResources, ZonedDateTime.now());

                        ValidationMessages errors = new ValidationMessages();
                        ServiceResult<Void> saveResult = financeCheckService.saveQueryPost(post, queryId);
                        if (saveResult.isFailure()) {
                            errors.addError(fieldError("saveError", null, "validation.notesandqueries.query.response.save.failed"));
                            validationHandler.addAnyErrors(errors);
                            attachments.forEach(attachment -> financeCheckService.deleteFile(attachment));
                            cookieUtil.removeCookie(response, getCookieName(projectId, organisationId, queryId));
                        }
                        return validationHandler.failNowOrSucceedWith( saveFailureView, () -> {
                            cookieUtil.removeCookie(response, getCookieName(projectId, organisationId, queryId));
                            return redirectToQueries(projectId);
                        });
                    });
        });
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_SECTION_EXTERNAL')")
    @PostMapping(value = "/{queryId}/new-response", params = "uploadAttachment")
    public String saveNewResponseAttachment(Model model,
                                            @PathVariable("projectId") final Long projectId,
                                            @PathVariable Long queryId,
                                            @ModelAttribute(FORM_ATTR) FinanceChecksQueryResponseForm form,
                                            @SuppressWarnings("unused") BindingResult bindingResult,
                                            ValidationHandler validationHandler,
                                            HttpServletRequest request,
                                            HttpServletResponse response,
                                            @ModelAttribute("loggedInUser") UserResource loggedInUser) {

        Long organisationId = userOrganisationUtil.getOrganisationIdFromUser(projectId, loggedInUser);

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
            if(result.isSuccess()) {
                attachments.add(result.getSuccessObject().id);
                saveAttachmentsToCookie(response, attachments, projectId, organisationId, queryId);
            }
            ProjectFinanceChecksViewModel viewModel = buildFinanceChecksLandingPage(projectComposite, attachments, queryId);
            model.addAttribute("model", viewModel);
            return result;
        });
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_SECTION_EXTERNAL')")
    @GetMapping("/{queryId}/new-response/attachment/{attachmentId}")
    public
    @ResponseBody
    ResponseEntity<ByteArrayResource> downloadResponseAttachment(@PathVariable Long projectId,
                                                                 @PathVariable Long queryId,
                                                                 @PathVariable Long attachmentId,
                                                                 @ModelAttribute("loggedInUser") UserResource loggedInUser,
                                                                 HttpServletRequest request) {
        Long organisationId = userOrganisationUtil.getOrganisationIdFromUser(projectId, loggedInUser);
        List<Long> attachments = loadAttachmentsFromCookie(request, projectId, organisationId, queryId);
        Optional<ByteArrayResource> content = Optional.empty();
        Optional<FileEntryResource> fileDetails = Optional.empty();

        if (attachments.contains(attachmentId)) {
            ServiceResult<Optional<ByteArrayResource>> fileContent = financeCheckService.downloadFile(attachmentId);
            if (fileContent.isSuccess()) {
                content = fileContent.getSuccessObject();
            }
            ServiceResult<FileEntryResource> fileInfo = financeCheckService.getAttachmentInfo(attachmentId);
            if (fileInfo.isSuccess()) {
                fileDetails = Optional.of(fileInfo.getSuccessObject());
            }
        }
        return returnFileIfFoundOrThrowNotFoundException(content, fileDetails);
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_SECTION_EXTERNAL')")
    @GetMapping("/attachment/{attachmentId}")
    public
    @ResponseBody
    ResponseEntity<ByteArrayResource> downloadAttachment(@PathVariable Long projectId,
                                                         @PathVariable Long attachmentId) {
        Optional<ByteArrayResource> content = Optional.empty();
        Optional<FileEntryResource> fileDetails = Optional.empty();

        ServiceResult<Optional<ByteArrayResource>> fileContent = financeCheckService.downloadFile(attachmentId);
        if (fileContent.isSuccess()) {
            content = fileContent.getSuccessObject();
        }
        ServiceResult<FileEntryResource> fileInfo = financeCheckService.getAttachmentInfo(attachmentId);
        if (fileInfo.isSuccess()) {
            fileDetails = Optional.of(fileInfo.getSuccessObject());
        }

        return returnFileIfFoundOrThrowNotFoundException(content, fileDetails);
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_SECTION_EXTERNAL')")
    @PostMapping(value = "/{queryId}/new-response", params = "removeAttachment")
    public String removeAttachment(@PathVariable Long projectId,
                                   @PathVariable Long queryId,
                                   @RequestParam(value = "removeAttachment") final Long attachmentId,
                                   @ModelAttribute(FORM_ATTR) FinanceChecksQueryResponseForm form,
                                   @SuppressWarnings("unused") BindingResult bindingResult,
                                   @ModelAttribute("loggedInUser") UserResource loggedInUser,
                                   HttpServletRequest request,
                                   HttpServletResponse response,
                                   Model model) {
        Long organisationId = userOrganisationUtil.getOrganisationIdFromUser(projectId, loggedInUser);

        ProjectOrganisationCompositeId projectComposite = new ProjectOrganisationCompositeId(projectId, organisationId);

        List<Long> attachments = loadAttachmentsFromCookie(request, projectId, organisationId, queryId);
        if (attachments.contains(attachmentId)) {
            attachments.remove(attachments.indexOf(attachmentId));
            financeCheckService.deleteFile(attachmentId);
        }
        saveAttachmentsToCookie(response, attachments, projectId, organisationId, queryId);

        ProjectFinanceChecksViewModel viewModel = buildFinanceChecksLandingPage(projectComposite, attachments, queryId);
        model.addAttribute("model", viewModel);
        model.addAttribute("form", form);
        return "project/finance-checks";
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_SECTION_EXTERNAL')")
    @GetMapping("/{queryId}/new-response/cancel")
    public String cancelNewForm(@PathVariable Long projectId,
                                @PathVariable Long queryId,
                                HttpServletRequest request,
                                HttpServletResponse response,
                                @ModelAttribute("loggedInUser") UserResource loggedInUser) {
        Long organisationId = userOrganisationUtil.getOrganisationIdFromUser(projectId, loggedInUser);

        List<Long> attachments = loadAttachmentsFromCookie(request, projectId, organisationId, queryId);
        attachments.forEach(( id -> financeCheckService.deleteFile(id)));

        cookieUtil.removeCookie(response, getCookieName(projectId, organisationId, queryId));

        return redirectToQueries(projectId);
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_SECTION_EXTERNAL')")
    @GetMapping("/eligibility")
    public String viewExternalEligibilityPage(@PathVariable("projectId") final Long projectId, @ModelAttribute(FORM_ATTR) ApplicationForm form, BindingResult bindingResult, Model model, HttpServletRequest request, @ModelAttribute("loggedInUser") UserResource loggedInUser){
        ProjectResource project = projectService.getById(projectId);
        Long organisationId = userOrganisationUtil.getOrganisationIdFromUser(projectId, loggedInUser);
        ApplicationResource application = applicationService.getById(project.getApplication());
        OrganisationResource organisation = organisationService.getOrganisationById(organisationId);
        OrganisationResource leadOrganisation = projectService.getLeadOrganisation(projectId);
        boolean isLeadPartnerOrganisation = leadOrganisation.getId().equals(organisation.getId());
        UserResource user = userAuthenticationService.getAuthenticatedUser(request);
        List<SectionResource> allSections = sectionService.getAllByCompetitionId(application.getCompetition());
        CompetitionResource competition = competitionService.getById(application.getCompetition());

        return doViewEligibility(competition, application, project, allSections, user, isLeadPartnerOrganisation, organisation, model, null, form, bindingResult);
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_SECTION_EXTERNAL')")
    @GetMapping("/eligibility/changes")
    public String viewExternalEligibilityChanges(@PathVariable("projectId") final Long projectId, Model model, @ModelAttribute("loggedInUser") UserResource loggedInUser){
        Long organisationId = userOrganisationUtil.getOrganisationIdFromUser(projectId, loggedInUser);
        ProjectResource project = projectService.getById(projectId);
        OrganisationResource organisation = organisationService.getOrganisationById(organisationId);
        return doViewEligibilityChanges(project, organisation, loggedInUser.getId(), model);
    }

    private ProjectFinanceChecksViewModel buildFinanceChecksLandingPage(final ProjectOrganisationCompositeId compositeId, List<Long> attachments, Long queryId) {
        ProjectResource projectResource = projectService.getById(compositeId.getProjectId());
        OrganisationResource organisationResource = organisationService.getOrganisationById(compositeId.getOrganisationId());

        Map<Long, String> attachmentLinks = new HashMap<>();
        if(attachments != null) {
            attachments.forEach(id -> {
                ServiceResult<FileEntryResource> file = financeCheckService.getAttachmentInfo(id);
                if (file.isSuccess()) {
                    attachmentLinks.put(id, file.getSuccessObject().getName());
                }
            });
        }

        boolean approved = isApproved(compositeId);

        return new ProjectFinanceChecksViewModel(projectResource,
                organisationResource,
                getQueriesAndPopulateViewModel(compositeId.getProjectId(),compositeId.getOrganisationId()),
                approved,
                attachmentLinks,
                FinanceChecksQueryConstraints.MAX_QUERY_WORDS,
                FinanceChecksQueryConstraints.MAX_QUERY_CHARACTERS,
                queryId,
                PROJECT_FINANCE_CHECKS_BASE_URL, financeUtil.isUsingJesFinances(organisationResource.getOrganisationType()));
    }

    private boolean isApproved(final ProjectOrganisationCompositeId compositeId) {
        Optional<ProjectPartnerStatusResource> organisationStatus = projectService.getProjectTeamStatus(compositeId.getProjectId(), Optional.empty()).getPartnerStatusForOrganisation(compositeId.getOrganisationId());
        return COMPLETE.equals(organisationStatus.map(ProjectPartnerStatusResource::getFinanceChecksStatus).orElse(null));
    }

    private List<ThreadViewModel> getQueriesAndPopulateViewModel(Long projectId, Long organisationId) {

        List<ThreadViewModel> queryModel = new LinkedList<>();

        ProjectFinanceResource projectFinance = projectFinanceService.getProjectFinance(projectId, organisationId);
        ServiceResult<List<QueryResource>> queries = financeCheckService.getQueries(projectFinance.getId());
        if (queries.isSuccess()) {
            // order queries by most recent post
            List<QueryResource> sortedQueries = queries.getSuccessObject().stream().
                    flatMap(t -> t.posts.stream()
                            .map(p -> new AbstractMap.SimpleImmutableEntry<>(t, p)))
                    .sorted((e1, e2) -> e2.getValue().createdOn.compareTo(e1.getValue().createdOn))
                    .map(m -> m.getKey())
                    .distinct()
                    .collect(Collectors.toList());

            for (QueryResource query : sortedQueries) {
                List<ThreadPostViewModel> posts = new LinkedList<>();
                for (PostResource p : query.posts) {
                    ThreadPostViewModel post = new ThreadPostViewModel(p.id, p.author, p.body, p.attachments, p.createdOn);
                    List<Long> projectUserIds = projectService.getProjectUsersForProject(projectId).stream().map(ProjectUserResource::getUser).distinct().collect(Collectors.toList());
                    if(projectUserIds.contains(p.author.getId())) {
                        UserResource user = userService.findById(p.author.getId());
                        post.setUsername(user.getName() + " - " + organisationService.getOrganisationForUser(user.getId()).getName());

                    } else {
                        post.setUsername("Innovate UK - Finance team");
                    }
                    posts.add(post);
                }
                ThreadViewModel detail = new ThreadViewModel();
                detail.setViewModelPosts(posts);
                detail.setSectionType(query.section);
                detail.setCreatedOn(query.createdOn);
                detail.setAwaitingResponse(query.awaitingResponse);
                detail.setTitle(query.title);
                detail.setId(query.id);
                detail.setProjectId(projectId);
                detail.setOrganisationId(organisationId);
                queryModel.add(detail);
            }
        }
        return queryModel;
    }

    private String redirectToQueries(Long projectId) {
        return "redirect:/project/" + projectId + "/finance-checks";
    }

    private ResponseEntity<ByteArrayResource> returnFileIfFoundOrThrowNotFoundException(Optional<ByteArrayResource> content, Optional<FileEntryResource> fileDetails) {
        if (content.isPresent() && fileDetails.isPresent()) {
            return getFileResponseEntity(content.get(), fileDetails.get());
        } else {
            return new ResponseEntity<>(null, null, HttpStatus.NO_CONTENT);
        }
    }

    private String getCookieName(Long projectId, Long organisationId, Long queryId) {
        return ATTACHMENT_COOKIE + "_" + projectId + "_" + organisationId + "_" + queryId;
    }

    private void saveAttachmentsToCookie(HttpServletResponse response, List<Long> attachmentFileIds, Long projectId, Long organisationId, Long queryId) {
        String jsonState = JsonUtil.getSerializedObject(attachmentFileIds);
        cookieUtil.saveToCookie(response, getCookieName(projectId,organisationId, queryId), jsonState);
    }

    private List<Long> loadAttachmentsFromCookie(HttpServletRequest request, Long projectId, Long organisationId, Long queryId) {

        List<Long> attachments = new LinkedList<>();
        String json = cookieUtil.getCookieValue(request, getCookieName(projectId, organisationId, queryId));

        if (json != null && !"".equals(json)) {
            TypeReference<List<Long>> listType = new TypeReference<List<Long>>() {};
            ObjectMapper mapper = new ObjectMapper();
            try {
                attachments = mapper.readValue(json, listType);
                return attachments;
            } catch (IOException e) {
                //ignored
            }
        }
        return attachments;
    }

    private String doViewEligibility(CompetitionResource competition, ApplicationResource application, ProjectResource project, List<SectionResource> allSections, UserResource user, boolean isLeadPartnerOrganisation, OrganisationResource organisation, Model model, FinanceChecksEligibilityForm eligibilityForm, ApplicationForm form, BindingResult bindingResult) {

        populateProjectFinanceDetails(competition, application, project, organisation.getId(), allSections, user, form, bindingResult, model);

        EligibilityResource eligibility = projectFinanceService.getEligibility(project.getId(), organisation.getId());

        if (eligibilityForm == null) {
            eligibilityForm = getEligibilityForm(eligibility);
        }

        FinanceCheckEligibilityResource eligibilityOverview = financeCheckService.getFinanceCheckEligibilityDetails(project.getId(), organisation.getId());

        boolean eligibilityApproved = eligibility.getEligibility() == Eligibility.APPROVED;

        model.addAttribute("summaryModel", new FinanceChecksEligibilityViewModel(eligibilityOverview, organisation.getName(), project.getName(),
                application.getId(), isLeadPartnerOrganisation, project.getId(), organisation.getId(),
                eligibilityApproved, eligibility.getEligibilityRagStatus(), eligibility.getEligibilityApprovalUserFirstName(),
                eligibility.getEligibilityApprovalUserLastName(), eligibility.getEligibilityApprovalDate(), true, false, null));

        model.addAttribute("eligibilityForm", eligibilityForm);
        model.addAttribute("form", form);

        return "project/financecheck/eligibility";
    }

    private void populateProjectFinanceDetails(CompetitionResource competition, ApplicationResource application, ProjectResource project, Long organisationId, List<SectionResource> allSections, UserResource user, ApplicationForm form, BindingResult bindingResult, Model model){

        SectionResource section = simpleFilter(allSections, s -> s.getType().equals(PROJECT_COST_FINANCES)).get(0);

        addApplicationAndSectionsInternalWithOrgDetails(application, competition, user.getId(), Optional.ofNullable(section), Optional.empty(), model, form);

        BaseSectionViewModel openFinanceSectionViewModel = openFinanceSectionModel.populateModel(form, model, application, section, user, bindingResult, allSections, organisationId);

        model.addAttribute("model", openFinanceSectionViewModel);

        model.addAttribute("project", project);
    }

    private void addApplicationAndSectionsInternalWithOrgDetails(final ApplicationResource application, final CompetitionResource competition, final Long userId, Optional<SectionResource> section, Optional<Long> currentQuestionId, final Model model, final ApplicationForm form) {
        applicationModelPopulator.addApplicationAndSections(application, competition, userId, section, currentQuestionId, model, form);
    }

    private FinanceChecksEligibilityForm getEligibilityForm(EligibilityResource eligibility) {

        boolean confirmEligibilityChecked = eligibility.getEligibilityRagStatus() != EligibilityRagStatus.UNSET;

        return new FinanceChecksEligibilityForm(eligibility.getEligibilityRagStatus(), confirmEligibilityChecked);
    }

    private String doViewEligibilityChanges(ProjectResource project, OrganisationResource organisation, Long userId, Model model) {
        ProjectFinanceChangesViewModel projectFinanceChangesViewModel = ((DefaultProjectFinanceModelManager)financeHandler.getProjectFinanceModelManager(organisation.getOrganisationType())).getProjectFinanceChangesViewModel(false, project, organisation, userId);
        model.addAttribute("model", projectFinanceChangesViewModel);
        return "project/financecheck/eligibility-changes";
    }
}
