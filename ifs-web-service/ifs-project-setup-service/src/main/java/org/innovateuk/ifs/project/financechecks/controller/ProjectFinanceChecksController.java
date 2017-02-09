package org.innovateuk.ifs.project.financechecks.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.finance.ProjectFinanceService;
import org.innovateuk.ifs.project.financecheck.FinanceCheckService;
import org.innovateuk.ifs.project.financechecks.form.FinanceChecksQueryConstraints;
import org.innovateuk.ifs.project.financechecks.form.FinanceChecksQueryResponseForm;
import org.innovateuk.ifs.project.financechecks.viewmodel.ProjectFinanceChecksViewModel;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.project.resource.ProjectPartnerStatusResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.thread.viewmodel.ThreadPostViewModel;
import org.innovateuk.ifs.thread.viewmodel.ThreadViewModel;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserService;
import org.innovateuk.ifs.util.CookieUtil;
import org.innovateuk.ifs.util.JsonUtil;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;
import static org.innovateuk.ifs.controller.FileUploadControllerUtils.getMultipartFileBytes;
import static org.innovateuk.ifs.file.controller.FileDownloadControllerUtils.getFileResponseEntity;
import static org.innovateuk.ifs.project.constant.ProjectActivityStates.COMPLETE;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * This controller will handle requests related to finance checks
 */
@Controller
@RequestMapping(ProjectFinanceChecksController.PROJECT_FINANCE_CHECKS_BASE_URL)
public class ProjectFinanceChecksController {

    public static final String PROJECT_FINANCE_CHECKS_BASE_URL = "/project/{projectId}/partner-organisation/{organisationId}/finance-checks";

    private static final String ATTACHMENT_COOKIE = "query_new_response_attachments";
    private static final String FORM_ATTR = "form";

    @Autowired
    ProjectService projectService;

    @Autowired
    UserService userService;

    @Autowired
    ProjectFinanceService projectFinanceService;

    @Autowired
    FinanceCheckService financeCheckService;

    @Autowired
    OrganisationService organisationService;

    @Autowired
    private CookieUtil cookieUtil;

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_SECTION_EXTERNAL')")
    @RequestMapping(method = GET)
    public String viewFinanceChecks(Model model,
                                    @PathVariable("projectId") final Long projectId,
                                    @PathVariable("organisationId") final Long organisationId) {

        ProjectOrganisationCompositeId projectComposite = new ProjectOrganisationCompositeId(projectId, organisationId);

        model.addAttribute("model", buildFinanceChecksLandingPage(projectComposite, null, null));

        return "project/finance-checks";
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_SECTION_EXTERNAL')")
    @RequestMapping(value = "/{queryId}/new-response", method = GET)
    public String viewNewResponse(@PathVariable Long projectId,
                                  @PathVariable Long organisationId,
                                  @PathVariable Long queryId,
                                  Model model,
                                  HttpServletRequest request,
                                  HttpServletResponse response) {
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
    @RequestMapping(value = "/{queryId}/new-response", method = POST)
    public String saveResponse(Model model,
                               @PathVariable("projectId") final Long projectId,
                               @PathVariable final Long organisationId,
                               @PathVariable final Long queryId,
                               @Valid @ModelAttribute(FORM_ATTR) final FinanceChecksQueryResponseForm form,
                               @SuppressWarnings("unused") BindingResult bindingResult,
                               ValidationHandler validationHandler,
                               @ModelAttribute("loggedInUser") UserResource loggedInUser,
                               HttpServletRequest request,
                               HttpServletResponse response)
    {
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

                        List<FileEntryResource> attachmentResources = new ArrayList<>();
                        List<Long> attachments = loadAttachmentsFromCookie(request, projectId, organisationId, queryId);
                        attachments.forEach(attachment -> {
                            ServiceResult<FileEntryResource> fileEntry = financeCheckService.getFileInfo(attachment);
                            if (fileEntry.isSuccess()) {
                                attachmentResources.add(fileEntry.getSuccessObject());
                            }
                        });
                        PostResource post = new PostResource(null, loggedInUser, form.getResponse(), attachmentResources, LocalDateTime.now());

                        ValidationMessages errors = new ValidationMessages();
                        ServiceResult<Void> saveResult = financeCheckService.savePost(post, queryId);
                        if (saveResult.isFailure()) {
                            errors.addError(fieldError("saveError", null, "validation.notesandqueries.query.response.save.failed"));
                            validationHandler.addAnyErrors(errors);
                            attachments.forEach(attachment -> financeCheckService.deleteFile(attachment));
                            cookieUtil.removeCookie(response, getCookieName(projectId, organisationId, queryId));
                        }
                        return validationHandler.failNowOrSucceedWith( saveFailureView, () -> {
                            cookieUtil.removeCookie(response, getCookieName(projectId, organisationId, queryId));
                            return redirectToQueries(projectId, organisationId);
                        });
                    });
        });
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_SECTION_EXTERNAL')")
    @RequestMapping(value = "/{queryId}/new-response", method = POST, params = "uploadAttachment")
    public String saveNewResponseAttachment(Model model,
                                            @PathVariable("projectId") final Long projectId,
                                            @PathVariable Long organisationId,
                                            @PathVariable Long queryId,
                                            @ModelAttribute(FORM_ATTR) FinanceChecksQueryResponseForm form,
                                            @SuppressWarnings("unused") BindingResult bindingResult,
                                            ValidationHandler validationHandler,
                                            HttpServletRequest request,
                                            HttpServletResponse response) {
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
            ServiceResult<FileEntryResource> result = financeCheckService.uploadFile(file.getContentType(), file.getSize(), file.getOriginalFilename(), getMultipartFileBytes(file));
            if(result.isSuccess()) {
                attachments.add(result.getSuccessObject().getId());
                saveAttachmentsToCookie(response, attachments, projectId, organisationId, queryId);
            }
            ProjectFinanceChecksViewModel viewModel = buildFinanceChecksLandingPage(projectComposite, attachments, queryId);
            model.addAttribute("model", viewModel);
            return ServiceResult.serviceSuccess();
        });
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_SECTION_EXTERNAL')")
    @RequestMapping(value = "/{queryId}/new-response/attachment/{attachmentId}", method = GET)
    public
    @ResponseBody
    ResponseEntity<ByteArrayResource> downloadResponseAttachment(@PathVariable Long projectId,
                                                                 @PathVariable Long organisationId,
                                                                 @PathVariable Long queryId,
                                                                 @PathVariable Long attachmentId,
                                                                 @ModelAttribute("loggedInUser") UserResource loggedInUser,
                                                                 HttpServletRequest request) {
        List<Long> attachments = loadAttachmentsFromCookie(request, projectId, organisationId, queryId);
        Optional<ByteArrayResource> content = Optional.empty();
        Optional<FileEntryResource> fileDetails = Optional.empty();

        if (attachments.contains(attachmentId)) {
            ServiceResult<Optional<ByteArrayResource>> fileContent = financeCheckService.downloadFile(attachmentId);
            if (fileContent.isSuccess()) {
                content = fileContent.getSuccessObject();
            }
            ServiceResult<FileEntryResource> fileInfo = financeCheckService.getFileInfo(attachmentId);
            if (fileInfo.isSuccess()) {
                fileDetails = Optional.of(fileInfo.getSuccessObject());
            }
        }
        return returnFileIfFoundOrThrowNotFoundException(content, fileDetails);
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_SECTION_EXTERNAL')")
    @RequestMapping(value = "/{queryId}/new-response", params = "removeAttachment", method = POST)
    public String removeAttachment(@PathVariable Long projectId,
                                   @PathVariable Long organisationId,
                                   @PathVariable Long queryId,
                                   @RequestParam(value = "removeAttachment") final Long attachmentId,
                                   @ModelAttribute(FORM_ATTR) FinanceChecksQueryResponseForm form,
                                   @SuppressWarnings("unused") BindingResult bindingResult,
                                   ValidationHandler validationHandler,
                                   @ModelAttribute("loggedInUser") UserResource loggedInUser,
                                   HttpServletRequest request,
                                   HttpServletResponse response,
                                   Model model) {
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
        return "project/finance-check";
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_QUERIES_SECTION')")
    @RequestMapping(value="/{queryId}/new-response/cancel", method = GET)
    public String cancelNewForm(@PathVariable Long projectId,
                                @PathVariable Long organisationId,
                                @PathVariable Long queryId,
                                HttpServletRequest request,
                                HttpServletResponse response) {
        List<Long> attachments = loadAttachmentsFromCookie(request, projectId, organisationId, queryId);
        attachments.forEach(( id -> financeCheckService.deleteFile(id)));

        cookieUtil.removeCookie(response, getCookieName(projectId, organisationId, queryId));

        return redirectToQueries(projectId, organisationId);
    }


    private ProjectFinanceChecksViewModel buildFinanceChecksLandingPage(final ProjectOrganisationCompositeId compositeId, List<Long> attachments, Long queryId) {
        ProjectResource projectResource = projectService.getById(compositeId.getProjectId());
        OrganisationResource organisationResource = organisationService.getOrganisationById(compositeId.getOrganisationId());

        Map<Long, String> attachmentLinks = new HashMap<>();
        if(attachments != null) {
            attachments.forEach(id -> {
                ServiceResult<FileEntryResource> file = financeCheckService.getFileInfo(id);
                if (file.isSuccess()) {
                    attachmentLinks.put(id, file.getSuccessObject().getName());
                }
            });
        }

        boolean approved = isApproved(compositeId);

        return new ProjectFinanceChecksViewModel(projectResource,
                organisationResource,
                loadQueryModel(compositeId.getProjectId(),compositeId.getOrganisationId()),
                approved,
                attachmentLinks,
                FinanceChecksQueryConstraints.MAX_QUERY_WORDS,
                FinanceChecksQueryConstraints.MAX_QUERY_CHARACTERS,
                queryId,
                PROJECT_FINANCE_CHECKS_BASE_URL);
    }

    private boolean isApproved(final ProjectOrganisationCompositeId compositeId) {
        Optional<ProjectPartnerStatusResource> organisationStatus = projectService.getProjectTeamStatus(compositeId.getProjectId(), Optional.empty()).getPartnerStatusForOrganisation(compositeId.getOrganisationId());
        return COMPLETE.equals(organisationStatus.map(ProjectPartnerStatusResource::getFinanceChecksStatus).orElse(null));
    }

    private List<ThreadViewModel> loadQueryModel(Long projectId, Long organisationId) {

        List<ThreadViewModel> queryModel = new LinkedList<>();

        ProjectFinanceResource projectFinance = projectFinanceService.getProjectFinance(projectId, organisationId);
        ServiceResult<List<QueryResource>> queries = financeCheckService.loadQueries(projectFinance.getId());
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
                    List<Long> projectUserIds = projectService.getProjectUsersForProject(projectId).stream().map(u -> u.getUser()).distinct().collect(Collectors.toList());
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

    private String redirectToQueries(Long projectId, Long organisationId) {
        return "redirect:/project/" + projectId + "/partner-organisation/" + organisationId + "/finance-checks";
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

}
