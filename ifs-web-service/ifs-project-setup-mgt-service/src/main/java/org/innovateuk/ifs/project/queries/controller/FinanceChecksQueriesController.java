package org.innovateuk.ifs.project.queries.controller;

import org.apache.commons.lang3.StringEscapeUtils;
import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.notesandqueries.resource.post.PostAttachmentResource;
import org.innovateuk.ifs.notesandqueries.resource.post.PostResource;
import org.innovateuk.ifs.notesandqueries.resource.thread.SectionTypeEnum;
import org.innovateuk.ifs.notesandqueries.resource.thread.ThreadResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.queries.form.FinanceChecksQueriesForm;
import org.innovateuk.ifs.project.queries.form.FinanceChecksQueriesPostForm;
import org.innovateuk.ifs.project.queries.viewmodel.FinanceChecksQueriesAttachmentResourceViewModel;
import org.innovateuk.ifs.project.queries.viewmodel.FinanceChecksQueriesPostViewModel;
import org.innovateuk.ifs.project.queries.viewmodel.FinanceChecksQueriesQueryViewModel;
import org.innovateuk.ifs.project.queries.viewmodel.FinanceChecksQueriesViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.file.controller.FileDownloadControllerUtils.getFileResponseEntity;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * This Controller handles finance check queries activity for the finance team members
 */
@Controller
@RequestMapping("/project/{projectId}/finance-check/organisation/{organisationId}/query")
@SessionAttributes({FinanceChecksQueriesController.NEW_POST_ATTACHMENTS,
                    FinanceChecksQueriesController.SAVED_QUERY_FORM,
                    FinanceChecksQueriesController.SAVED_POST_FORM,
                    FinanceChecksQueriesController.SAVED_MODEL,
                    FinanceChecksQueriesController.NEW_POST_FLAG,
                    FinanceChecksQueriesController.NEW_QUERY_FLAG,
                    FinanceChecksQueriesController.NEW_POST_QUERY_ID,
                    FinanceChecksQueriesController.SAVED_MODEL_FLAG,
                    FinanceChecksQueriesController.FORM_ERRORS})
public class FinanceChecksQueriesController {

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    private static final String FORM_ATTR = "form";
    private static final String UNKNOWN_FIELD = "Unknown";
    static final String NEW_POST_ATTACHMENTS = "postAttachments";
    static final String SAVED_QUERY_FORM = "savedQueryForm";
    static final String SAVED_POST_FORM = "savedPostForm";
    static final String SAVED_MODEL = "savedModel";
    static final String SAVED_MODEL_FLAG = "savedModelFlag";
    static final String NEW_QUERY_FLAG = "newQuery";
    static final String NEW_POST_FLAG = "newPost";
    static final String NEW_POST_QUERY_ID = "newPostQueryId";
    static final String FORM_ERRORS = "formErrors";

    @ModelAttribute(NEW_POST_ATTACHMENTS)
    private Map<Long, String> getEmptyAttachments() {
        return new HashMap<>();
    }

    @ModelAttribute(SAVED_QUERY_FORM)
    private FinanceChecksQueriesForm getSavedQueryForm() { return new FinanceChecksQueriesForm(); }

    @ModelAttribute(SAVED_POST_FORM)
    private FinanceChecksQueriesPostForm getSavedPostForm() { return new FinanceChecksQueriesPostForm(); }

    @ModelAttribute(SAVED_MODEL)
    private FinanceChecksQueriesViewModel getSavedModel() { return new FinanceChecksQueriesViewModel("", false, "", "", "", false, "", 0L, "", new HashMap<>(), 0, 0, 0, new ArrayList<>(), false, 0L, 0L); }

    @ModelAttribute(SAVED_MODEL_FLAG)
    private Boolean getModelFlag() { return Boolean.FALSE; }

    @ModelAttribute(NEW_QUERY_FLAG)
    private Boolean getNewQueryFlag() { return Boolean.FALSE; }

    @ModelAttribute(NEW_POST_FLAG)
    private Boolean getNewPostFlag() { return Boolean.FALSE; }

    @ModelAttribute(NEW_POST_QUERY_ID)
    private Long getNewPostQueryId() { return 0L; }

    @ModelAttribute(FORM_ERRORS)
    private BindingResult getFormErrors() { return createEmptyBindingResult(); }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_QUERIES_SECTION')")
    @RequestMapping(method = GET)
    public String showPage(@PathVariable Long projectId,
                           @PathVariable Long organisationId,
                           @RequestParam(value = "query_section", required = false) String querySection,
                           Model model,
                           @ModelAttribute(NEW_POST_ATTACHMENTS) Map<Long, String>attachments,
                           @ModelAttribute(NEW_POST_FLAG) Boolean postFlag,
                           @ModelAttribute(NEW_QUERY_FLAG) Boolean queryFlag,
                           @ModelAttribute(SAVED_QUERY_FORM) FinanceChecksQueriesForm queryForm,
                           @ModelAttribute(SAVED_POST_FORM) FinanceChecksQueriesPostForm postForm,
                           @ModelAttribute(SAVED_MODEL) FinanceChecksQueriesViewModel viewModel,
                           @ModelAttribute(SAVED_MODEL_FLAG) Boolean savedModel,
                           @ModelAttribute(FORM_ERRORS) BindingResult errors,
                           ValidationHandler validationHandler) {

        if (!queryFlag) {
            model.addAttribute(SAVED_QUERY_FORM, new FinanceChecksQueriesForm());
        } else {
            model.addAttribute(FORM_ATTR, queryForm);
        }
        if (!postFlag) {
            model.addAttribute(SAVED_POST_FORM, new FinanceChecksQueriesPostForm());
        } else {
            model.addAttribute(FORM_ATTR, postForm);
        }
        if (!savedModel) {
            viewModel = populateQueriesViewModel(projectId, organisationId, queryFlag, querySection, attachments, postFlag, null);
        }

        model.addAttribute("org.springframework.validation.BindingResult."+FORM_ATTR, errors);
        model.addAttribute("model", viewModel);

        return "project/financecheck/queries";
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_QUERIES_SECTION')")
    @RequestMapping(value="/new-query", method = GET)
    public String viewNewQuery(@PathVariable Long projectId,
                               @PathVariable Long organisationId,
                               @RequestParam(value = "query_section", required = false) String querySection,
                               Model model,
                               @ModelAttribute("loggedInUser") UserResource loggedInUser,
                               @ModelAttribute(NEW_POST_ATTACHMENTS) Map<Long, String> attachments) {

        FinanceChecksQueriesViewModel viewModel = populateQueriesViewModel(projectId, organisationId, true, querySection, attachments, false, null);
        model.addAttribute(SAVED_MODEL, viewModel);
        model.addAttribute(NEW_QUERY_FLAG, Boolean.TRUE);
        model.addAttribute(NEW_POST_FLAG, Boolean.FALSE);
        model.addAttribute(SAVED_MODEL_FLAG, Boolean.TRUE);
        model.addAttribute(FORM_ERRORS, createEmptyBindingResult());
        return redirectToQueryPage(projectId, organisationId, querySection);
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_QUERIES_SECTION')")
    @RequestMapping(value="/save-new-query", method = POST, params = "uploadAttachment")
    public String saveNewQueryAttachment(Model model,
                                         @PathVariable("projectId") final Long projectId,
                                         @PathVariable Long organisationId,
                                         @RequestParam(value = "query_section", required = false) String querySection,
                                         @ModelAttribute(NEW_POST_ATTACHMENTS) Map<Long, String> attachments,
                                         @ModelAttribute(FORM_ATTR) FinanceChecksQueriesForm form,
                                         @SuppressWarnings("unused") BindingResult bindingResult,
                                         ValidationHandler validationHandler) {
        MultipartFile file = form.getAttachment();
        boolean success = true;
        if(file == null) {
            success = false;
            validationHandler.addAnyErrors(ServiceResult.serviceFailure(CommonFailureKeys.FINANCE_CHECKS_POST_ATTACH_NOT_UPLOADED));
        }
        // TODO store file, get file ID
        if (success) {
            attachments.put(Long.valueOf(attachments.size()), StringEscapeUtils.escapeHtml4(file.getOriginalFilename()));
        }

        FinanceChecksQueriesViewModel viewModel = populateQueriesViewModel(projectId, organisationId, true, querySection, attachments, false, null);
        model.addAttribute(SAVED_MODEL, viewModel);
        model.addAttribute(NEW_QUERY_FLAG, Boolean.TRUE);
        model.addAttribute(SAVED_MODEL_FLAG, Boolean.TRUE);
        model.addAttribute(SAVED_QUERY_FORM, form);
        model.addAttribute(FORM_ERRORS, bindingResult);

        return redirectToQueryPage(projectId, organisationId, querySection);
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_QUERIES_SECTION')")
    @RequestMapping(value = "/attachment/{attachmentId}", method = GET)
    public
    @ResponseBody
    ResponseEntity<ByteArrayResource> downloadAttachment(@PathVariable Long projectId,
                                                         @PathVariable Long organisationId,
                                                         @PathVariable Long attachmentId,
                                                         @ModelAttribute("loggedInUser") UserResource loggedInUser,
                                                         @ModelAttribute(NEW_POST_ATTACHMENTS) Map<Long, String> attachments) {
        Optional<ByteArrayResource> content = Optional.empty();
        Optional<FileEntryResource> fileDetails = Optional.empty();

        if (attachments.containsKey(attachmentId)) {
            // TODO get file from service
        }
        return returnFileIfFoundOrThrowNotFoundException(content, fileDetails);
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_QUERIES_SECTION')")
    @RequestMapping(value="/save-new-query", method = POST)
    public String saveQuery(@PathVariable("projectId") final Long projectId,
                            @PathVariable Long organisationId,
                            @RequestParam(value = "query_section", required = false) String querySection,
                            @ModelAttribute(NEW_POST_ATTACHMENTS) Map<Long, String> attachments,
                            @Valid @ModelAttribute(FORM_ATTR) FinanceChecksQueriesForm form,
                            @SuppressWarnings("unused") BindingResult bindingResult,
                            ValidationHandler validationHandler,
                            Model model)
    {
        //TODO save query
        boolean success = true;
        // get file entry IDs from session data
        //attachments.forEach();
        //validationHandler.addAnyErrors(result, toField(FORM_ATTR))
        success &= bindingResult.getAllErrors().size()==0;
        if (!success) {
            FinanceChecksQueriesViewModel viewModel = populateQueriesViewModel(projectId, organisationId, true, querySection, attachments, false, null);
            model.addAttribute(SAVED_MODEL, viewModel);
            model.addAttribute(NEW_QUERY_FLAG, Boolean.TRUE);
            model.addAttribute(SAVED_MODEL_FLAG, Boolean.TRUE);
            model.addAttribute(SAVED_QUERY_FORM, form);
        } else {
            FinanceChecksQueriesViewModel viewModel = populateQueriesViewModel(projectId, organisationId, false, querySection, attachments, false, null);
            model.addAttribute(SAVED_MODEL, viewModel);
            model.addAttribute(NEW_QUERY_FLAG, Boolean.FALSE);
            model.addAttribute(SAVED_MODEL_FLAG, Boolean.TRUE);
        }
        model.addAttribute(FORM_ERRORS, bindingResult);
        return redirectToQueryPage(projectId, organisationId, querySection);
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_QUERIES_SECTION')")
    @RequestMapping(value="/cancel", method = GET)
    public String cancelNewForm(@PathVariable Long projectId,
                                @PathVariable Long organisationId,
                                @RequestParam(value = "query_section", required = false) String querySection,
                                Model model,
                                @ModelAttribute("loggedInUser") UserResource loggedInUser) {
        // TODO delete attachments
        model.addAttribute(NEW_QUERY_FLAG, Boolean.FALSE);
        model.addAttribute(NEW_POST_FLAG, Boolean.FALSE);
        model.addAttribute(SAVED_MODEL_FLAG ,Boolean.FALSE);
        model.addAttribute(NEW_POST_ATTACHMENTS, new HashMap<>());
        model.addAttribute(FORM_ERRORS, createEmptyBindingResult());
        return redirectToQueryPage(projectId, organisationId, querySection);
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_QUERIES_SECTION')")
    @RequestMapping(value="/{queryId}/new-response", method = GET)
    public String viewNewResponse(@PathVariable Long projectId,
                                  @PathVariable Long organisationId,
                                  @PathVariable Long queryId,
                                  @RequestParam(value = "query_section", required = false) String querySection,
                                  Model model,
                                  @ModelAttribute("loggedInUser") UserResource loggedInUser,
                                  @ModelAttribute(NEW_POST_ATTACHMENTS) Map<Long, String> attachments) {

        FinanceChecksQueriesViewModel viewModel = populateQueriesViewModel(projectId, organisationId, false, querySection, attachments, true, queryId);
        model.addAttribute(SAVED_MODEL, viewModel);
        model.addAttribute(NEW_POST_FLAG, Boolean.TRUE);
        model.addAttribute(NEW_QUERY_FLAG, Boolean.FALSE);
        model.addAttribute(SAVED_MODEL_FLAG, Boolean.TRUE);
        model.addAttribute(FORM_ERRORS, createEmptyBindingResult());
        return redirectToQueryPage(projectId, organisationId, querySection);
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_QUERIES_SECTION')")
    @RequestMapping(value="/{queryId}/save-new-response", method = POST, params = "uploadAttachment")
    public String saveNewResponseAttachment(Model model,
                                            @PathVariable("projectId") final Long projectId,
                                            @PathVariable Long organisationId,
                                            @PathVariable Long queryId,
                                            @RequestParam(value = "query_section", required = false) String querySection,
                                            @ModelAttribute(NEW_POST_ATTACHMENTS) Map<Long, String> attachments,
                                            @ModelAttribute(FORM_ATTR) FinanceChecksQueriesPostForm form,
                                            @SuppressWarnings("unused") BindingResult bindingResult,
                                            ValidationHandler validationHandler) {
        MultipartFile file = form.getAttachment();
        boolean success = true;
        if(file == null) {
            success = false;
            validationHandler.addAnyErrors(ServiceResult.serviceFailure(CommonFailureKeys.FINANCE_CHECKS_POST_ATTACH_NOT_UPLOADED));
        }
        // TODO save file
        if (success) {
            // save file entry ID in model
            attachments.put(Long.valueOf(attachments.size()), StringEscapeUtils.escapeHtml4(file.getOriginalFilename()));
        }

        FinanceChecksQueriesViewModel viewModel = populateQueriesViewModel(projectId, organisationId, false, querySection, attachments, true, queryId);
        model.addAttribute(SAVED_MODEL, viewModel);
        model.addAttribute(NEW_POST_FLAG, Boolean.TRUE);
        model.addAttribute(NEW_QUERY_FLAG, Boolean.FALSE);
        model.addAttribute(SAVED_MODEL_FLAG, Boolean.TRUE);
        model.addAttribute(SAVED_POST_FORM, form);
        model.addAttribute(FORM_ERRORS, bindingResult);
        return redirectToQueryPage(projectId, organisationId, querySection);
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_QUERIES_SECTION')")
    @RequestMapping(value="/{queryId}/save-new-response", method = POST)
    public String saveResponse(Model model,
                               @PathVariable("projectId") final Long projectId,
                               @PathVariable Long organisationId,
                               @PathVariable Long queryId,
                               @RequestParam(value = "query_section", required = false) String querySection,
                               @ModelAttribute(NEW_POST_ATTACHMENTS) Map<Long, String> attachments,
                               @Valid @ModelAttribute(FORM_ATTR) FinanceChecksQueriesPostForm form,
                               @SuppressWarnings("unused") BindingResult bindingResult,
                               ValidationHandler validationHandler)
    {
        boolean success = true;
        //TODO save Response
        // get file entry IDs from session data
        //attachments.forEach();
        //validationHandler.addAnyErrors(result, toField(FORM_ATTR))
        success &= bindingResult.getAllErrors().size()==0;
        if (!success) {
            FinanceChecksQueriesViewModel viewModel = populateQueriesViewModel(projectId, organisationId, false, querySection, attachments, true, queryId);
            model.addAttribute(SAVED_MODEL, viewModel);
            model.addAttribute(NEW_POST_FLAG, Boolean.TRUE);
            model.addAttribute(NEW_QUERY_FLAG, Boolean.FALSE);
            model.addAttribute(SAVED_MODEL_FLAG, Boolean.TRUE);
            model.addAttribute(SAVED_POST_FORM, form);
        } else {
            FinanceChecksQueriesViewModel viewModel = populateQueriesViewModel(projectId, organisationId, false, querySection, attachments, false, null);
            model.addAttribute(SAVED_MODEL, viewModel);
            model.addAttribute(NEW_QUERY_FLAG, Boolean.FALSE);
            model.addAttribute(SAVED_MODEL_FLAG, Boolean.TRUE);
        }
        model.addAttribute(FORM_ERRORS, bindingResult);
        return redirectToQueryPage(projectId, organisationId, querySection);
    }

    private List<FinanceChecksQueriesQueryViewModel> loadQueryModel(Long projectId, Long organisationId) {
        // Dummy test data
        ThreadResource thread = new ThreadResource();
        PostResource firstPost = new PostResource();
        PostResource firstResponse = new PostResource();
        thread.setCreatedOn(LocalDateTime.now());
        thread.setAwaitingResponse(false);
        thread.setOrganisationId(22L);
        thread.setProjectId(3L);
        thread.setTitle("Query title");
        thread.setSectionType(SectionTypeEnum.ELIGIBILITY);
        thread.setId(1L);
        firstPost.setCreatedOn(LocalDateTime.now().plusMinutes(10L));
        firstPost.setUserId(18L);
        firstPost.setPostBody("Question");
        firstResponse.setCreatedOn(LocalDateTime.now().plusMinutes(20L));
        firstResponse.setUserId(55L);
        firstResponse.setPostBody("Response");
        firstResponse.setAttachments(new LinkedList<>());
        PostAttachmentResource att1 = new PostAttachmentResource();
        att1.setFileEntryId(23L);
        firstPost.setAttachments(Arrays.asList(att1));
        thread.setPosts(Arrays.asList(firstPost, firstResponse));

        ThreadResource thread2 = new ThreadResource();
        PostResource firstPost2 = new PostResource();
        thread2.setCreatedOn(LocalDateTime.now());
        thread2.setAwaitingResponse(true);
        thread2.setProjectId(3L);
        thread2.setTitle("Query2 title");
        thread2.setSectionType(SectionTypeEnum.ELIGIBILITY);
        thread2.setId(3L);
        firstPost2.setCreatedOn(LocalDateTime.now().plusMinutes(10L));
        firstPost2.setUserId(18L);
        firstPost2.setPostBody("Question2");
        firstPost2.setAttachments(new LinkedList<>());
        thread2.setPosts (Arrays.asList(firstPost2));
        List<ThreadResource> queries = Arrays.asList(thread, thread2);

        // TODO read data from service

        // order queries by most recent at top
        List<ThreadResource> sortedQueries = queries.stream().
                flatMap(t -> t.getPosts().stream()
                        .map(p -> new AbstractMap.SimpleImmutableEntry<>(t, p)))
                .sorted((e1, e2) -> e1.getValue().getCreatedOn().compareTo(e2.getValue().getCreatedOn()))
                .map(m -> m.getKey())
                .distinct()
                .collect(Collectors.toList());

        List<FinanceChecksQueriesQueryViewModel> queryModel = new LinkedList<>();
        Long attachmentIndex = 0L;
        for (ThreadResource t : sortedQueries) {
            List<FinanceChecksQueriesPostViewModel> posts = new LinkedList<>();
            for (PostResource p : t.getPosts()) {
                List<FinanceChecksQueriesAttachmentResourceViewModel> attachments = new LinkedList<>();
                for (PostAttachmentResource a : p.getAttachments()) {
                    FinanceChecksQueriesAttachmentResourceViewModel attachment = new FinanceChecksQueriesAttachmentResourceViewModel();
                    // TODO get file details from service
                    attachment.setFileEntryId(a.getFileEntryId());
                    attachment.setPostId(a.getPostId());
                    attachment.setFilename("file" + attachmentIndex.toString());
                    attachment.setLocalFileId(attachmentIndex);
                    attachments.add(attachment);
                    attachmentIndex++;
                    //FileEntryResource f;
                    //f.getName();
                }
                UserResource user = userService.findById(p.getUserId());
                OrganisationResource organisation = organisationService.getOrganisationForUser(p.getUserId());
                FinanceChecksQueriesPostViewModel post = new FinanceChecksQueriesPostViewModel();
                post.setViewModelAttachments(attachments);
                post.setUsername(user.getName() + " - " + organisation.getName() + (user.hasRole(UserRoleType.PROJECT_FINANCE)?  " (Finance team)" : ""));
                post.setCreatedOn(p.getCreatedOn());
                post.setPostBody(p.getPostBody());
                posts.add(post);
            }
            FinanceChecksQueriesQueryViewModel detail = new FinanceChecksQueriesQueryViewModel();
            detail.setViewModelPosts(posts);
            detail.setSectionType(t.getSectionType());
            detail.setCreatedOn(t.getCreatedOn());
            detail.setAwaitingResponse(t.isAwaitingResponse());
            detail.setTitle(t.getTitle());
            detail.setId(t.getId());
            queryModel.add(detail);
        }
        return queryModel;
    }

    private FinanceChecksQueriesViewModel populateQueriesViewModel(Long projectId, Long organisationId, boolean showNewQuery, String querySection, Map<Long, String> attachmentLinks, boolean showNewPost, Long newPostQueryId) {

        ProjectResource project = projectService.getById(projectId);

        OrganisationResource organisation = organisationService.getOrganisationById(organisationId);
        OrganisationResource leadOrganisation = projectService.getLeadOrganisation(projectId);
        boolean leadPartnerOrganisation = leadOrganisation.getId().equals(organisation.getId());

        Optional<ProjectUserResource> financeContact = getFinanceContact(projectId, organisationId);


        return new FinanceChecksQueriesViewModel(
                organisation.getName(),
                leadPartnerOrganisation,
                financeContact.isPresent() ? financeContact.get().getUserName() : UNKNOWN_FIELD,
                financeContact.isPresent() ? financeContact.get().getEmail() : UNKNOWN_FIELD,
                financeContact.isPresent() ? financeContact.get().getPhoneNumber() : UNKNOWN_FIELD,
                showNewQuery,
                querySection == null ? UNKNOWN_FIELD : querySection,
                project.getId(),
                project.getName(),
                attachmentLinks,
                FinanceChecksQueriesForm.MAX_QUERY_WORDS,
                FinanceChecksQueriesForm.MAX_QUERY_CHARACTERS,
                FinanceChecksQueriesForm.MAX_TITLE_CHARACTERS,
                loadQueryModel(projectId, organisationId),
                showNewPost,
                newPostQueryId,
                organisationId
        );
    }

    private Optional<ProjectUserResource> getFinanceContact(Long projectId, Long organisationId){
        List<ProjectUserResource> projectUsers = projectService.getProjectUsersForProject(projectId);
        return simpleFindFirst(projectUsers, pr -> pr.isFinanceContact() && organisationId.equals(pr.getOrganisation()));
    }

    private String redirectToQueryPage(Long projectId, Long organisationId, String querySection) {
        return "redirect:/project/" + projectId + "/finance-check/organisation/" + organisationId + "/query?query_section=" + querySection;
    }

    private ResponseEntity<ByteArrayResource> returnFileIfFoundOrThrowNotFoundException(Optional<ByteArrayResource> content, Optional<FileEntryResource> fileDetails) {
        if (content.isPresent() && fileDetails.isPresent()) {
            return getFileResponseEntity(content.get(), fileDetails.get());
        } else {
            return new ResponseEntity<>(null, null, HttpStatus.NO_CONTENT);
        }
    }

    private BindingResult createEmptyBindingResult() {
        return new MapBindingResult(new HashMap<>(), "");
    }
}
