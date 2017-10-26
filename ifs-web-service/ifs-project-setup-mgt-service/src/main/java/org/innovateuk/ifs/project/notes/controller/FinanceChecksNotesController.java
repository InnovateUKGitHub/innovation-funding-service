package org.innovateuk.ifs.project.notes.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.commons.error.exception.ObjectNotFoundException;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.finance.ProjectFinanceService;
import org.innovateuk.ifs.project.financecheck.FinanceCheckService;
import org.innovateuk.ifs.project.notes.form.FinanceChecksNotesAddCommentForm;
import org.innovateuk.ifs.project.notes.form.FinanceChecksNotesFormConstraints;
import org.innovateuk.ifs.project.notes.viewmodel.FinanceChecksNotesViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.thread.viewmodel.ThreadPostViewModel;
import org.innovateuk.ifs.thread.viewmodel.ThreadViewModel;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.innovateuk.ifs.user.service.UserService;
import org.innovateuk.ifs.util.CookieUtil;
import org.innovateuk.ifs.util.JsonUtil;
import org.innovateuk.threads.attachment.resource.AttachmentResource;
import org.innovateuk.threads.resource.NoteResource;
import org.innovateuk.threads.resource.PostResource;
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
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;
import static org.innovateuk.ifs.controller.FileUploadControllerUtils.getMultipartFileBytes;
import static org.innovateuk.ifs.file.controller.FileDownloadControllerUtils.getFileResponseEntity;

/**
 * This Controller handles finance check notes activity for the finance team members
 */
@Controller
@RequestMapping(FinanceChecksNotesController.FINANCE_CHECKS_NOTES_BASE_URL)
public class FinanceChecksNotesController {
    static final String FINANCE_CHECKS_NOTES_BASE_URL = "/project/{projectId}/finance-check/organisation/{organisationId}/note";
    private static final String FINANCE_CHECKS_NOTES_COMMENT_BASE_URL = FINANCE_CHECKS_NOTES_BASE_URL + "/{noteId}/new-comment";
    private static final String ATTACHMENT_COOKIE = "finance_checks_notes_new_comment_attachments";
    private static final String FORM_COOKIE = "finance_checks_notes_new_comment_form";
    private static final String ORIGIN_GET_COOKIE = "finance_checks_notes_new_comment_origin";
    private static final String FORM_ATTR = "form";
    private static final String NOTES_VIEW = "project/financecheck/notes";
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

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_NOTES_SECTION')")
    @GetMapping
    public String showPage(@PathVariable Long projectId,
                           @PathVariable Long organisationId,
                           Model model) {
        projectService.getPartnerOrganisation(projectId, organisationId);
        FinanceChecksNotesViewModel viewModel = populateNoteViewModel(projectId, organisationId, null, null);
        model.addAttribute("model", viewModel);
        return NOTES_VIEW;
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_NOTES_SECTION')")
    @GetMapping(value = "/attachment/{attachmentId}")
    public
    @ResponseBody
    ResponseEntity<ByteArrayResource> downloadAttachment(@PathVariable Long projectId,
                                                         @PathVariable Long organisationId,
                                                         @PathVariable Long attachmentId,
                                                         UserResource loggedInUser,
                                                         HttpServletRequest request) {

        projectService.getPartnerOrganisation(projectId, organisationId);
        return getFileResponseEntity(financeCheckService.downloadFile(attachmentId), financeCheckService.getAttachmentInfo(attachmentId));
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_NOTES_SECTION')")
    @GetMapping("/{noteId}/new-comment")
    public String viewNewComment(@PathVariable Long projectId,
                                 @PathVariable Long organisationId,
                                 @PathVariable Long noteId,
                                 Model model,
                                 UserResource loggedInUser,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {

        projectService.getPartnerOrganisation(projectId, organisationId);
        saveOriginCookie(response, projectId, organisationId, noteId, loggedInUser.getId());
        List<Long> attachments = loadAttachmentsFromCookie(request, projectId, organisationId, noteId);
        populateNoteViewModel(projectId, organisationId, noteId, model, attachments);
        model.addAttribute(FORM_ATTR, loadForm(request, projectId, organisationId, noteId).orElse(new FinanceChecksNotesAddCommentForm()));
        return NOTES_VIEW;
    }

    private void populateNoteViewModel(Long projectId, Long organisationId, Long noteId, Model model, List<Long> attachments) {
        FinanceChecksNotesViewModel financeChecksNotesViewModel = populateNoteViewModel(projectId, organisationId, noteId, attachments);
        validateNoteId(financeChecksNotesViewModel, noteId);
        model.addAttribute("model", financeChecksNotesViewModel);
    }

    private void validateNoteId(FinanceChecksNotesViewModel financeChecksNotesViewModel, Long noteId) {
        if (financeChecksNotesViewModel.getNotes().stream().noneMatch(threadViewModel -> threadViewModel.getId().equals(noteId))) {
            throw new ObjectNotFoundException();
        }
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_NOTES_SECTION')")
    @PostMapping(value = "/{noteId}/new-comment")
    public String saveComment(Model model,
                              @PathVariable("projectId") final Long projectId,
                              @PathVariable final Long organisationId,
                              @PathVariable final Long noteId,
                              @Valid @ModelAttribute(FORM_ATTR) final FinanceChecksNotesAddCommentForm form,
                              @SuppressWarnings("unused") BindingResult bindingResult,
                              ValidationHandler validationHandler,
                              UserResource loggedInUser,
                              HttpServletRequest request,
                              HttpServletResponse response) {
        if (postParametersMatchOrigin(request, projectId, organisationId, noteId, loggedInUser.getId())) {
            Supplier<String> failureView = () -> {
                List<Long> attachments = loadAttachmentsFromCookie(request, projectId, organisationId, noteId);
                FinanceChecksNotesViewModel viewModel = populateNoteViewModel(projectId, organisationId, noteId, attachments);
                model.addAttribute("model", viewModel);
                model.addAttribute(FORM_ATTR, form);
                return NOTES_VIEW;
            };

            Supplier<String> saveFailureView = () -> {
                FinanceChecksNotesViewModel viewModel = populateNoteViewModel(projectId, organisationId, null, null);
                model.addAttribute("model", viewModel);
                model.addAttribute("nonFormErrors", validationHandler.getAllErrors());
                model.addAttribute(FORM_ATTR, null);
                return NOTES_VIEW;
            };

            return validationHandler.failNowOrSucceedWith(failureView, () -> {
                ValidationMessages validationMessages = new ValidationMessages(bindingResult);

                return validationHandler.addAnyErrors(validationMessages, fieldErrorsToFieldErrors(), asGlobalErrors()).
                        failNowOrSucceedWith(failureView, () -> {

                            List<AttachmentResource> attachmentResources = new ArrayList<>();
                            List<Long> attachments = loadAttachmentsFromCookie(request, projectId, organisationId, noteId);
                            attachments.forEach(attachment -> financeCheckService.getAttachment(attachment).ifSuccessful(fileEntry -> attachmentResources.add(fileEntry)));
                            PostResource post = new PostResource(null, loggedInUser, form.getComment(), attachmentResources, ZonedDateTime.now());

                            ServiceResult<Void> saveResult = financeCheckService.saveNotePost(post, noteId);
                            validationHandler.addAnyErrors(saveResult);
                            return validationHandler.failNowOrSucceedWith(saveFailureView, () -> {
                                deleteCookies(response, projectId, organisationId, noteId);
                                return redirectTo(rootView(projectId, organisationId));
                            });
                        });
            });
        } else {
            throw new ObjectNotFoundException();
        }
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_NOTES_SECTION')")
    @PostMapping(value = "/{noteId}/new-comment", params = "uploadAttachment")
    public String saveNewCommentAttachment(Model model,
                                           @PathVariable("projectId") final Long projectId,
                                           @PathVariable Long organisationId,
                                           @PathVariable Long noteId,
                                           @ModelAttribute(FORM_ATTR) FinanceChecksNotesAddCommentForm form,
                                           @SuppressWarnings("unused") BindingResult bindingResult,
                                           ValidationHandler validationHandler,
                                           UserResource loggedInUser,
                                           HttpServletRequest request,
                                           HttpServletResponse response) {
        if (postParametersMatchOrigin(request, projectId, organisationId, noteId, loggedInUser.getId())) {
            List<Long> attachments = loadAttachmentsFromCookie(request, projectId, organisationId, noteId);
            Supplier<String> onSuccess = () -> redirectTo(formView(projectId, organisationId, noteId));
            Supplier<String> onError = () -> {
                model.addAttribute("model", populateNoteViewModel(projectId, organisationId, noteId, attachments));
                model.addAttribute("form", form);
                return NOTES_VIEW;
            };

            return validationHandler.performActionOrBindErrorsToField("attachment", onError, onSuccess, () -> {
                MultipartFile file = form.getAttachment();
                ServiceResult<AttachmentResource> result = financeCheckService.uploadFile(projectId, file.getContentType(), file.getSize(), file.getOriginalFilename(), getMultipartFileBytes(file));
                result.ifSuccessful(uploadedAttachment -> {
                    attachments.add(uploadedAttachment.id);
                    saveAttachmentsToCookie(response, attachments, projectId, organisationId, noteId);
                    saveFormToCookie(response, projectId, organisationId, noteId, form);
                });

                FinanceChecksNotesViewModel viewModel = populateNoteViewModel(projectId, organisationId, noteId, attachments);
                model.addAttribute("model", viewModel);
                return result;
            });
        } else {
            throw new ObjectNotFoundException();
        }
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_NOTES_SECTION')")
    @GetMapping("/{noteId}/new-comment/attachment/{attachmentId}")
    public @ResponseBody
    ResponseEntity<ByteArrayResource> downloadResponseAttachment(@PathVariable Long projectId,
                                                                 @PathVariable Long organisationId,
                                                                 @PathVariable Long noteId,
                                                                 @PathVariable Long attachmentId,
                                                                 UserResource loggedInUser,
                                                                 HttpServletRequest request) {
        projectService.getPartnerOrganisation(projectId, organisationId);
        List<Long> attachments = loadAttachmentsFromCookie(request, projectId, organisationId, noteId);
        if (attachments.contains(attachmentId)) {
            return getFileResponseEntity(financeCheckService.downloadFile(attachmentId), financeCheckService.getAttachmentInfo(attachmentId));
        } else {
            throw new ObjectNotFoundException("Cannot find comment attachment " + attachmentId + " for organisation " + organisationId + " and project " + projectId, emptyList());
        }
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_NOTES_SECTION')")
    @PostMapping(value = "/{noteId}/new-comment", params = "removeAttachment")
    public String removeAttachment(@PathVariable Long projectId,
                                   @PathVariable Long organisationId,
                                   @PathVariable Long noteId,
                                   @RequestParam(value = "removeAttachment") final Long attachmentId,
                                   @ModelAttribute(FORM_ATTR) FinanceChecksNotesAddCommentForm form,
                                   @SuppressWarnings("unused") BindingResult bindingResult,
                                   ValidationHandler validationHandler,
                                   UserResource loggedInUser,
                                   HttpServletRequest request,
                                   HttpServletResponse response,
                                   Model model) {
        if (postParametersMatchOrigin(request, projectId, organisationId, noteId, loggedInUser.getId())) {
            List<Long> attachments = loadAttachmentsFromCookie(request, projectId, organisationId, noteId);
            if (attachments.contains(attachmentId)) {
                financeCheckService.deleteFile(attachmentId)
                        .andOnSuccess(() -> attachments.remove(attachments.indexOf(attachmentId)));
            }
            saveAttachmentsToCookie(response, attachments, projectId, organisationId, noteId);
            saveFormToCookie(response, projectId, organisationId, noteId, form);

            return redirectTo(formView(projectId, organisationId, noteId));
        } else {
            throw new ObjectNotFoundException();
        }
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_NOTES_SECTION')")
    @GetMapping("/{noteId}/new-comment/cancel")
    public String cancelNewForm(@PathVariable Long projectId,
                                @PathVariable Long organisationId,
                                @PathVariable Long noteId,
                                Model model,
                                UserResource loggedInUser,
                                HttpServletRequest request,
                                HttpServletResponse response) {
        projectService.getPartnerOrganisation(projectId, organisationId);
        List<Long> attachments = loadAttachmentsFromCookie(request, projectId, organisationId, noteId);
        attachments.forEach((id -> financeCheckService.deleteFile(id)));
        deleteCookies(response, projectId, organisationId, noteId);
        return redirectTo(rootView(projectId, organisationId));
    }

    private List<ThreadViewModel> loadNoteModel(Long projectId, Long organisationId) {

        List<ThreadViewModel> noteModel = new LinkedList<>();

        ProjectFinanceResource projectFinance = projectFinanceService.getProjectFinance(projectId, organisationId);
        financeCheckService.loadNotes(projectFinance.getId()).ifSuccessful(notes -> {
            // order notes by most recent comment
            List<NoteResource> sortedQueries = notes.stream().
                    flatMap(t -> t.posts.stream()
                            .map(p -> new AbstractMap.SimpleImmutableEntry<>(t, p)))
                    .sorted((e1, e2) -> e2.getValue().createdOn.compareTo(e1.getValue().createdOn))
                    .map(m -> m.getKey())
                    .distinct()
                    .collect(Collectors.toList());

            for (NoteResource note : sortedQueries) {
                List<ThreadPostViewModel> posts = new LinkedList<>();
                for (PostResource p : note.posts) {
                    UserResource user = userService.findById(p.author.getId());
                    ThreadPostViewModel post = new ThreadPostViewModel(p.id, p.author, p.body, p.attachments, p.createdOn);
                    post.setUsername(user.getName() + " - Innovate UK" + (user.hasRole(UserRoleType.PROJECT_FINANCE) ? " (Finance team)" : ""));
                    posts.add(post);
                }
                ThreadViewModel detail = new ThreadViewModel();
                detail.setViewModelPosts(posts);
                detail.setCreatedOn(note.createdOn);
                detail.setTitle(note.title);
                detail.setId(note.id);
                detail.setProjectId(projectId);
                detail.setOrganisationId(organisationId);
                noteModel.add(detail);
            }
        });
        return noteModel;
    }

    private FinanceChecksNotesViewModel populateNoteViewModel(Long projectId, Long organisationId, Long noteId, List<Long> attachments) {

        ProjectResource project = projectService.getById(projectId);

        OrganisationResource organisation = organisationService.getOrganisationById(organisationId);
        OrganisationResource leadOrganisation = projectService.getLeadOrganisation(projectId);
        boolean leadPartnerOrganisation = leadOrganisation.getId().equals(organisation.getId());

        Map<Long, String> attachmentLinks = new HashMap<>();
        if (attachments != null) {
            attachments.forEach(id -> financeCheckService.getAttachment(id).ifSuccessful(foundAttachment -> attachmentLinks.put(id, foundAttachment.name)));
        }

        return new FinanceChecksNotesViewModel(
                organisation.getName(),
                leadPartnerOrganisation,
                project.getId(),
                project.getName(),
                loadNoteModel(projectId, organisationId),
                organisationId,
                FINANCE_CHECKS_NOTES_BASE_URL,
                attachmentLinks,
                FinanceChecksNotesFormConstraints.MAX_NOTE_WORDS,
                FinanceChecksNotesFormConstraints.MAX_NOTE_CHARACTERS,
                noteId
        );
    }

    private String rootView(Long projectId, Long organisationId) {
        return String.format(FINANCE_CHECKS_NOTES_BASE_URL, projectId, organisationId);
    }

    private String getCookieName(Long projectId, Long organisationId, Long noteId) {
        return ATTACHMENT_COOKIE + "_" + projectId + "_" + organisationId + "_" + noteId;
    }

    private String getFormCookieName(Long projectId, Long organisationId, Long noteId) {
        return FORM_COOKIE + "_" + projectId + "_" + organisationId + "_" + noteId;
    }

    private void saveAttachmentsToCookie(HttpServletResponse response, List<Long> attachmentFileIds, Long projectId, Long organisationId, Long noteId) {
        String jsonState = JsonUtil.getSerializedObject(attachmentFileIds);
        cookieUtil.saveToCookie(response, getCookieName(projectId, organisationId, noteId), jsonState);
    }

    private void saveFormToCookie(HttpServletResponse response, Long projectId, Long organisationId, Long noteId,
                                  FinanceChecksNotesAddCommentForm form) {
        cookieUtil.saveToCookie(response, getFormCookieName(projectId, organisationId, noteId), JsonUtil.getSerializedObject(form));
    }

    private List<Long> loadAttachmentsFromCookie(HttpServletRequest request, Long projectId, Long organisationId, Long noteId) {
        return cookieUtil.getCookieAsList(request, getCookieName(projectId, organisationId, noteId), new TypeReference<List<Long>>() {
        });
    }

    private Optional<FinanceChecksNotesAddCommentForm> loadForm(HttpServletRequest request, Long projectId, Long organisationId, Long noteId) {
        return cookieUtil.getCookieAs(request, getFormCookieName(projectId, organisationId, noteId),
                new TypeReference<FinanceChecksNotesAddCommentForm>() {});
    }

    private void saveOriginCookie(HttpServletResponse response, Long projectId, Long organisationId, Long noteId, Long userId) {
        String jsonState = JsonUtil.getSerializedObject(Arrays.asList(projectId, organisationId, noteId, userId));
        cookieUtil.saveToCookie(response, ORIGIN_GET_COOKIE, jsonState);
    }

    private boolean postParametersMatchOrigin(HttpServletRequest request, Long projectId, Long organisationId, Long noteId, Long userId){
        List<Long> getParams = cookieUtil.getCookieAsList(request, ORIGIN_GET_COOKIE, new TypeReference<List<Long>>() {
        });
        return getParams.size() == 4 && getParams.get(0) == projectId && getParams.get(1) == organisationId && getParams.get(2) == noteId && getParams.get(3) == userId;
    }

    private void deleteCookies(HttpServletResponse response, Long projectId, Long organisationId, Long noteId) {
        cookieUtil.removeCookie(response, getCookieName(projectId, organisationId, noteId));
        cookieUtil.removeCookie(response, getFormCookieName(projectId, organisationId, noteId));
        cookieUtil.removeCookie(response, ORIGIN_GET_COOKIE);
    }

    private String redirectTo(final String path) {
        return "redirect:" + path;
    }

    private String formView(final Long projectId, final Long organisationId, Long noteId) {
        return String.format(FINANCE_CHECKS_NOTES_COMMENT_BASE_URL, projectId, organisationId, noteId);
    }
}
