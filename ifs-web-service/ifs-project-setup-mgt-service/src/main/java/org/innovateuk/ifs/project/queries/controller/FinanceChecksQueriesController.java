package org.innovateuk.ifs.project.queries.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.commons.error.exception.ObjectNotFoundException;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.constant.ProjectActivityStates;
import org.innovateuk.ifs.project.finance.ProjectFinanceService;
import org.innovateuk.ifs.project.financecheck.FinanceCheckService;
import org.innovateuk.ifs.project.queries.form.FinanceChecksQueriesAddResponseForm;
import org.innovateuk.ifs.project.queries.form.FinanceChecksQueriesFormConstraints;
import org.innovateuk.ifs.project.queries.viewmodel.FinanceChecksQueriesViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.thread.viewmodel.ThreadPostViewModel;
import org.innovateuk.ifs.thread.viewmodel.ThreadViewModel;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.innovateuk.ifs.user.service.UserService;
import org.innovateuk.ifs.util.CookieUtil;
import org.innovateuk.ifs.util.JsonUtil;
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
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.QUERIES_CANNOT_BE_SENT_AS_FINANCE_CONTACT_NOT_SUBMITTED;
import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;
import static org.innovateuk.ifs.controller.FileUploadControllerUtils.getMultipartFileBytes;
import static org.innovateuk.ifs.file.controller.FileDownloadControllerUtils.getFileResponseEntity;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;

/**
 * This Controller handles finance check queries activity for the finance team members
 */
@Controller
@RequestMapping(FinanceChecksQueriesController.FINANCE_CHECKS_QUERIES_BASE_URL)
public class FinanceChecksQueriesController {

    static final String FINANCE_CHECKS_QUERIES_BASE_URL = "/project/{projectId}/finance-check/organisation/{organisationId}/query";
    private static final String NEW_RESPONSE_URL = "/project/{projectId}/finance-check/organisation/{organisationId}/query/{queryId}/new-response";
    private static final String ATTACHMENT_COOKIE = "finance_checks_queries_new_response_attachments";
    private static final String FORM_COOKIE = "finance_checks_queries_new_response_form";
    private static final String QUERIES_VIEW = "project/financecheck/queries";
    private static final String UNKNOWN_FIELD = "Unknown";
    private static final String FORM_ATTR = "form";
    @Autowired
    private OrganisationService organisationService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private UserService userService;
    @Autowired
    private CookieUtil cookieUtil;
    @Autowired
    private ProjectFinanceService projectFinanceService;
    @Autowired
    private FinanceCheckService financeCheckService;

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_QUERIES_SECTION')")
    @GetMapping
    public String showPage(@PathVariable Long projectId,
                           @PathVariable Long organisationId,
                           @RequestParam(value = "query_section", required = false) String querySection,
                           Model model) {
        checkFinanceContactIsSubmitted(projectId, organisationId);
        FinanceChecksQueriesViewModel viewModel = populateQueriesViewModel(projectId, organisationId, null, querySection, null);
        model.addAttribute("model", viewModel);
        return QUERIES_VIEW;
    }

    private void checkFinanceContactIsSubmitted(Long projectId, Long organisationId) {
        ProjectTeamStatusResource projectTeamStatusResource = projectService.getProjectTeamStatus(projectId, Optional.empty());
        boolean financeContactSubmitted = projectTeamStatusResource.getPartnerStatusForOrganisation(organisationId).map(partnerStatus -> partnerStatus.getFinanceContactStatus().equals(ProjectActivityStates.COMPLETE)).orElse(false);
        if(!financeContactSubmitted) {
            throw new ObjectNotFoundException(QUERIES_CANNOT_BE_SENT_AS_FINANCE_CONTACT_NOT_SUBMITTED.getErrorKey(), Collections.emptyList());
        }
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_QUERIES_SECTION')")
    @GetMapping("/attachment/{attachmentId}")
    public
    @ResponseBody
    ResponseEntity<ByteArrayResource> downloadAttachment(@PathVariable Long projectId,
                                                         @PathVariable Long organisationId,
                                                         @PathVariable Long attachmentId,
                                                         @ModelAttribute(name = "loggedInUser", binding = false) UserResource loggedInUser) {
        checkFinanceContactIsSubmitted(projectId, organisationId);

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

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_QUERIES_SECTION')")
    @GetMapping("/{queryId}/new-response")
    public String viewNewResponse(@PathVariable Long projectId,
                                  @PathVariable Long organisationId,
                                  @PathVariable Long queryId,
                                  @RequestParam(value = "query_section", required = false) String querySection,
                                  Model model,
                                  @ModelAttribute(name = "loggedInUser", binding = false) UserResource loggedInUser,
                                  HttpServletRequest request) {
        checkFinanceContactIsSubmitted(projectId, organisationId);
        List<Long> attachments = loadAttachmentsFromCookie(request, projectId, organisationId, queryId);
        model.addAttribute("model", populateQueriesViewModel(projectId, organisationId, queryId, querySection, attachments));
        model.addAttribute(FORM_ATTR, loadForm(request, projectId, organisationId, queryId).orElse(new FinanceChecksQueriesAddResponseForm()));
        return QUERIES_VIEW;
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_QUERIES_SECTION')")
    @PostMapping("/{queryId}/new-response")
    public String saveResponse(Model model,
                               @PathVariable("projectId") final Long projectId,
                               @PathVariable final Long organisationId,
                               @PathVariable final Long queryId,
                               @RequestParam(value = "query_section", required = false) String querySection,
                               @Valid @ModelAttribute(FORM_ATTR) final FinanceChecksQueriesAddResponseForm form,
                               @SuppressWarnings("unused") BindingResult bindingResult,
                               ValidationHandler validationHandler,
                               @ModelAttribute(name = "loggedInUser", binding = false) UserResource loggedInUser,
                               HttpServletRequest request,
                               HttpServletResponse response) {
        checkFinanceContactIsSubmitted(projectId, organisationId);
        Supplier<String> failureView = () -> {
            List<Long> attachments = loadAttachmentsFromCookie(request, projectId, organisationId, queryId);
            FinanceChecksQueriesViewModel viewModel = populateQueriesViewModel(projectId, organisationId, queryId, querySection, attachments);
            model.addAttribute("model", viewModel);
            model.addAttribute(FORM_ATTR, form);
            return QUERIES_VIEW;
        };

        Supplier<String> saveFailureView = () -> {
            FinanceChecksQueriesViewModel viewModel = populateQueriesViewModel(projectId, organisationId, null, querySection, null);
            model.addAttribute("model", viewModel);
            model.addAttribute("nonFormErrors", validationHandler.getAllErrors());
            model.addAttribute(FORM_ATTR, null);
            return QUERIES_VIEW;
        };

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            ValidationMessages validationMessages = new ValidationMessages(bindingResult);

            return validationHandler.addAnyErrors(validationMessages, fieldErrorsToFieldErrors(), asGlobalErrors()).
                    failNowOrSucceedWith(failureView, () -> {

                        List<AttachmentResource> attachmentResources = new ArrayList<>();
                        List<Long> attachments = loadAttachmentsFromCookie(request, projectId, organisationId, queryId);
                        attachments.forEach(attachment -> financeCheckService.getAttachment(attachment)
                                .ifSuccessful(attachmentResources::add));
                        PostResource post = new PostResource(null, loggedInUser, form.getResponse(), attachmentResources, ZonedDateTime.now());

                        ValidationMessages errors = new ValidationMessages();
                        ServiceResult<Void> saveResult = financeCheckService.saveQueryPost(post, queryId);
                        if (saveResult.isFailure()) {
                            errors.addError(fieldError("saveError", null, "validation.notesandqueries.query.response.save.failed"));
                            validationHandler.addAnyErrors(errors);
                            attachments.forEach(attachment -> financeCheckService.deleteFile(attachment));
                            deleteCookies(response, projectId, organisationId, queryId);
                        }
                        return validationHandler.failNowOrSucceedWith(saveFailureView, () -> {
                            deleteCookies(response, projectId, organisationId, queryId);
                            return redirectToQueryPage(projectId, organisationId, querySection);
                        });
                    });
        });
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_QUERIES_SECTION')")
    @PostMapping(value = "/{queryId}/new-response", params = "uploadAttachment")
    public String saveNewResponseAttachment(Model model,
                                            @PathVariable("projectId") final Long projectId,
                                            @PathVariable Long organisationId,
                                            @PathVariable Long queryId,
                                            @RequestParam(value = "query_section", required = false) String querySection,
                                            @ModelAttribute(FORM_ATTR) FinanceChecksQueriesAddResponseForm form,
                                            @SuppressWarnings("unused") BindingResult bindingResult,
                                            ValidationHandler validationHandler,
                                            @ModelAttribute(name = "loggedInUser", binding = false) UserResource loggedInUser,
                                            HttpServletRequest request,
                                            HttpServletResponse response) {
        checkFinanceContactIsSubmitted(projectId, organisationId);
        List<Long> attachments = loadAttachmentsFromCookie(request, projectId, organisationId, queryId);
        Supplier<String> onSuccess = () -> redirectTo(rootView(projectId, organisationId, queryId, querySection));
        Supplier<String> onError = () -> {
            FinanceChecksQueriesViewModel viewModel = populateQueriesViewModel(projectId, organisationId, queryId, querySection, attachments);
            model.addAttribute("model", viewModel);
            model.addAttribute("form", form);
            return QUERIES_VIEW;
        };

        return validationHandler.performActionOrBindErrorsToField("attachment", onError, onSuccess, () -> {
            MultipartFile file = form.getAttachment();
            ServiceResult<AttachmentResource> result = financeCheckService.uploadFile(projectId, file.getContentType(),
                    file.getSize(), file.getOriginalFilename(), getMultipartFileBytes(file));
            result.ifSuccessful(uploadedAttachment -> {
                attachments.add(result.getSuccessObject().id);
                saveAttachmentsToCookie(response, attachments, projectId, organisationId, queryId);
                saveFormToCookie(response, projectId, organisationId, queryId, form);
            });

            model.addAttribute("model", populateQueriesViewModel(projectId, organisationId, queryId, querySection, attachments));
            return result;
        });
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_QUERIES_SECTION')")
    @GetMapping("/{queryId}/new-response/attachment/{attachmentId}")
    public
    @ResponseBody
    ResponseEntity<ByteArrayResource> downloadResponseAttachment(@PathVariable Long projectId,
                                                                 @PathVariable Long organisationId,
                                                                 @PathVariable Long queryId,
                                                                 @PathVariable Long attachmentId,
                                                                 @ModelAttribute(name = "loggedInUser", binding = false) UserResource loggedInUser,
                                                                 HttpServletRequest request) {
        checkFinanceContactIsSubmitted(projectId, organisationId);
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

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_QUERIES_SECTION')")
    @PostMapping(value = "/{queryId}/new-response", params = "removeAttachment")
    public String removeAttachment(@PathVariable Long projectId,
                                   @PathVariable Long organisationId,
                                   @PathVariable Long queryId,
                                   @RequestParam(value = "query_section", required = false) final String querySection,
                                   @RequestParam(value = "removeAttachment") final Long attachmentId,
                                   @ModelAttribute(FORM_ATTR) FinanceChecksQueriesAddResponseForm form,
                                   @SuppressWarnings("unused") BindingResult bindingResult,
                                   ValidationHandler validationHandler,
                                   @ModelAttribute(name = "loggedInUser", binding = false) UserResource loggedInUser,
                                   HttpServletRequest request,
                                   HttpServletResponse response,
                                   Model model) {
        checkFinanceContactIsSubmitted(projectId, organisationId);
        List<Long> attachments = loadAttachmentsFromCookie(request, projectId, organisationId, queryId);
        if (attachments.contains(attachmentId)) {
            financeCheckService.deleteFile(attachmentId).andOnSuccess(() ->
                    attachments.remove(attachments.indexOf(attachmentId)));
        }
        saveAttachmentsToCookie(response, attachments, projectId, organisationId, queryId);
        saveFormToCookie(response, projectId, organisationId, queryId, form);

        return redirectTo(rootView(projectId, organisationId, queryId, querySection));
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_QUERIES_SECTION')")
    @GetMapping("/{queryId}/new-response/cancel")
    public String cancelNewForm(@PathVariable Long projectId,
                                @PathVariable Long organisationId,
                                @PathVariable Long queryId,
                                @RequestParam(value = "query_section", required = false) String querySection,
                                Model model,
                                @ModelAttribute(name = "loggedInUser", binding = false) UserResource loggedInUser,
                                HttpServletRequest request,
                                HttpServletResponse response) {
        checkFinanceContactIsSubmitted(projectId, organisationId);
        loadAttachmentsFromCookie(request, projectId, organisationId, queryId).forEach(financeCheckService::deleteFile);
        deleteCookies(response, projectId, organisationId, queryId);
        return redirectToQueryPage(projectId, organisationId, querySection);
    }

    private List<ThreadViewModel> loadQueryModel(Long projectId, Long organisationId) {

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
                    UserResource user = userService.findById(p.author.getId());
                    OrganisationResource organisation = organisationService.getOrganisationForUser(p.author.getId());
                    ThreadPostViewModel post = new ThreadPostViewModel(p.id, p.author, p.body, p.attachments, p.createdOn);
                    post.setUsername(user.getName() + " - " + organisation.getName() + (user.hasRole(UserRoleType.PROJECT_FINANCE) ? " (Finance team)" : ""));
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

    private FinanceChecksQueriesViewModel populateQueriesViewModel(Long projectId, Long organisationId, Long queryId, String querySection, List<Long> attachments) {
        ProjectResource project = projectService.getById(projectId);
        OrganisationResource organisation = organisationService.getOrganisationById(organisationId);
        OrganisationResource leadOrganisation = projectService.getLeadOrganisation(projectId);
        boolean leadPartnerOrganisation = leadOrganisation.getId().equals(organisation.getId());
        Optional<ProjectUserResource> financeContact = getFinanceContact(projectId, organisationId);
        Map<Long, String> attachmentLinks = new HashMap<>();
        if (attachments != null) {
            attachments.forEach(id -> financeCheckService.getAttachment(id).ifSuccessful(attachment -> attachmentLinks.put(id, attachment.name)));
        }

        return new FinanceChecksQueriesViewModel(
                organisation.getName(),
                leadPartnerOrganisation,
                financeContact.isPresent() ? financeContact.get().getUserName() : UNKNOWN_FIELD,
                financeContact.isPresent() ? financeContact.get().getEmail() : UNKNOWN_FIELD,
                financeContact.isPresent() ? financeContact.get().getPhoneNumber() : UNKNOWN_FIELD,
                querySection == null ? UNKNOWN_FIELD : querySection,
                project.getId(),
                project.getName(),
                loadQueryModel(projectId, organisationId),
                organisationId,
                FINANCE_CHECKS_QUERIES_BASE_URL,
                attachmentLinks,
                FinanceChecksQueriesFormConstraints.MAX_QUERY_WORDS,
                FinanceChecksQueriesFormConstraints.MAX_QUERY_CHARACTERS,
                queryId
        );
    }

    private Optional<ProjectUserResource> getFinanceContact(Long projectId, Long organisationId) {
        List<ProjectUserResource> projectUsers = projectService.getProjectUsersForProject(projectId);
        return simpleFindFirst(projectUsers, pr -> pr.isFinanceContact() && organisationId.equals(pr.getOrganisation()));
    }

    private String redirectToQueryPage(Long projectId, Long organisationId, String querySection) {
        return addQuerySection("redirect:/project/" + projectId + "/finance-check/organisation/" + organisationId + "/query", querySection);
    }

    private String addQuerySection(String url, String querySection) {
        return url + (querySection != null ? "?query_section=" + querySection : "");
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

    private void saveAttachmentsToCookie(HttpServletResponse response, List<Long> attachmentFileIds, Long projectId,
                                         Long organisationId, Long queryId) {
        String jsonState = JsonUtil.getSerializedObject(attachmentFileIds);
        cookieUtil.saveToCookie(response, getCookieName(projectId, organisationId, queryId), jsonState);
    }

    private List<Long> loadAttachmentsFromCookie(HttpServletRequest request, Long projectId, Long organisationId, Long queryId) {
        return cookieUtil.getCookieAsList(request, getCookieName(projectId, organisationId, queryId),
                new TypeReference<List<Long>>() {});
    }


    private String getFormCookieName(Long projectId, Long organisationId, Long queryId) {
        return FORM_COOKIE + "_" + projectId + "_" + organisationId + "_" + queryId;
    }

    private void saveFormToCookie(HttpServletResponse response, Long projectId,
                                  Long organisationId, Long queryId,
                                  FinanceChecksQueriesAddResponseForm form) {
        cookieUtil.saveToCookie(response, getFormCookieName(projectId, organisationId, queryId),
                JsonUtil.getSerializedObject(form));
    }

    private Optional<FinanceChecksQueriesAddResponseForm> loadForm(HttpServletRequest request, Long projectId,
                                                                   Long organisationId, Long queryId) {
        return cookieUtil.getCookieAs(request, getFormCookieName(projectId, organisationId, queryId),
                new TypeReference<FinanceChecksQueriesAddResponseForm>() {});
    }

    private String rootView(final Long projectId, final Long organisationId, Long queryId, String querySection) {
        return addQuerySection(String.format(NEW_RESPONSE_URL, projectId, organisationId, queryId), querySection);
    }

    private String redirectTo(final String path) {
        return "redirect:" + path;
    }

    private void deleteCookies(HttpServletResponse response, Long projectId, Long organisationId, Long queryId) {
        cookieUtil.removeCookie(response, getCookieName(projectId, organisationId, queryId));
        cookieUtil.removeCookie(response, getFormCookieName(projectId,
                organisationId, queryId));
    }
}
