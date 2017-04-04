package org.innovateuk.ifs.project.notes.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.project.finance.ProjectFinanceService;
import org.innovateuk.ifs.project.financecheck.FinanceCheckService;
import org.innovateuk.ifs.project.notes.form.FinanceChecksNotesAddCommentForm;
import org.innovateuk.ifs.project.notes.form.FinanceChecksNotesFormConstraints;
import org.innovateuk.ifs.project.notes.viewmodel.FinanceChecksNotesViewModel;
import org.innovateuk.threads.attachment.resource.AttachmentResource;
import org.innovateuk.threads.resource.NoteResource;
import org.innovateuk.threads.resource.PostResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.thread.viewmodel.ThreadPostViewModel;
import org.innovateuk.ifs.thread.viewmodel.ThreadViewModel;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.innovateuk.ifs.user.service.UserService;
import org.innovateuk.ifs.util.CookieUtil;
import org.innovateuk.ifs.util.JsonUtil;
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

import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;
import static org.innovateuk.ifs.controller.FileUploadControllerUtils.getMultipartFileBytes;
import static org.innovateuk.ifs.file.controller.FileDownloadControllerUtils.getFileResponseEntity;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * This Controller handles finance check notes activity for the finance team members
 */
@Controller
@RequestMapping(FinanceChecksNotesController.FINANCE_CHECKS_NOTES_BASE_URL)
public class FinanceChecksNotesController {

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

    public static final String FINANCE_CHECKS_NOTES_BASE_URL = "/project/{projectId}/finance-check/organisation/{organisationId}/note";

    private static final String ATTACHMENT_COOKIE = "finance_checks_notes_new_comment_attachments";
    private static final String FORM_ATTR = "form";


    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_NOTES_SECTION')")
    @RequestMapping(method = GET)
    public String showPage(@PathVariable Long projectId,
                           @PathVariable Long organisationId,
                           Model model) {
        FinanceChecksNotesViewModel viewModel = populateNotesViewModel(projectId, organisationId, null, null);
        model.addAttribute("model", viewModel);
        return "project/financecheck/notes";
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_NOTES_SECTION')")
    @RequestMapping(value = "/attachment/{attachmentId}", method = GET)
    public
    @ResponseBody
    ResponseEntity<ByteArrayResource> downloadAttachment(@PathVariable Long projectId,
                                                         @PathVariable Long organisationId,
                                                         @PathVariable Long attachmentId,
                                                         @ModelAttribute("loggedInUser") UserResource loggedInUser,
                                                         HttpServletRequest request) {
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

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_NOTES_SECTION')")
    @RequestMapping(value = "/{noteId}/new-comment", method = GET)
    public String viewNewComment(@PathVariable Long projectId,
                                  @PathVariable Long organisationId,
                                  @PathVariable Long noteId,
                                  Model model,
                                  @ModelAttribute("loggedInUser") UserResource loggedInUser,
                                  HttpServletRequest request,
                                  HttpServletResponse response) {

        List<Long> attachments = loadAttachmentsFromCookie(request, projectId, organisationId, noteId);
        attachments.forEach(id -> financeCheckService.deleteFile(id));
        saveAttachmentsToCookie(response, new ArrayList<>(), projectId, organisationId, noteId);

        FinanceChecksNotesViewModel viewModel = populateNotesViewModel(projectId, organisationId, noteId, attachments);
        model.addAttribute("model", viewModel);
        FinanceChecksNotesAddCommentForm form = new FinanceChecksNotesAddCommentForm();
        model.addAttribute(FORM_ATTR, form);
        return "project/financecheck/notes";
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_NOTES_SECTION')")
    @RequestMapping(value = "/{noteId}/new-comment", method = POST)
    public String saveComment(Model model,
                               @PathVariable("projectId") final Long projectId,
                               @PathVariable final Long organisationId,
                               @PathVariable final Long noteId,
                               @Valid @ModelAttribute(FORM_ATTR) final FinanceChecksNotesAddCommentForm form,
                               @SuppressWarnings("unused") BindingResult bindingResult,
                               ValidationHandler validationHandler,
                               @ModelAttribute("loggedInUser") UserResource loggedInUser,
                               HttpServletRequest request,
                               HttpServletResponse response)
    {
        Supplier<String> failureView = () -> {
            List<Long> attachments = loadAttachmentsFromCookie(request, projectId, organisationId, noteId);
            FinanceChecksNotesViewModel viewModel = populateNotesViewModel(projectId, organisationId, noteId, attachments);
            model.addAttribute("model", viewModel);
            model.addAttribute(FORM_ATTR, form);
            return "project/financecheck/notes";
        };

        Supplier<String> saveFailureView = () -> {
            FinanceChecksNotesViewModel viewModel = populateNotesViewModel(projectId, organisationId, null, null);
            model.addAttribute("model", viewModel);
            model.addAttribute("nonFormErrors", validationHandler.getAllErrors());
            model.addAttribute(FORM_ATTR, null);
            return "project/financecheck/notes";
        };

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            ValidationMessages validationMessages = new ValidationMessages(bindingResult);

            return validationHandler.addAnyErrors(validationMessages, fieldErrorsToFieldErrors(), asGlobalErrors()).
                    failNowOrSucceedWith(failureView, () -> {

                        List<AttachmentResource> attachmentResources = new ArrayList<>();
                        List<Long> attachments = loadAttachmentsFromCookie(request, projectId, organisationId, noteId);
                        attachments.forEach(attachment -> {
                            ServiceResult<AttachmentResource> fileEntry = financeCheckService.getAttachment(attachment);
                            if (fileEntry.isSuccess()) {
                                attachmentResources.add(fileEntry.getSuccessObject());
                            }
                        });
                        PostResource post = new PostResource(null, loggedInUser, form.getComment(), attachmentResources, ZonedDateTime.now());

                        ServiceResult<Void> saveResult = financeCheckService.saveNotePost(post, noteId);
                        validationHandler.addAnyErrors(saveResult);
                        return validationHandler.failNowOrSucceedWith( saveFailureView, () -> {
                            cookieUtil.removeCookie(response, getCookieName(projectId, organisationId, noteId));
                            return redirectToQueryPage(projectId, organisationId);
                        });
                    });
        });
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_NOTES_SECTION')")
    @RequestMapping(value = "/{noteId}/new-comment", method = POST, params = "uploadAttachment")
    public String saveNewCommentAttachment(Model model,
                                            @PathVariable("projectId") final Long projectId,
                                            @PathVariable Long organisationId,
                                            @PathVariable Long noteId,
                                            @ModelAttribute(FORM_ATTR) FinanceChecksNotesAddCommentForm form,
                                            @SuppressWarnings("unused") BindingResult bindingResult,
                                            ValidationHandler validationHandler,
                                            @ModelAttribute("loggedInUser") UserResource loggedInUser,
                                            HttpServletRequest request,
                                            HttpServletResponse response) {
        List<Long> attachments = loadAttachmentsFromCookie(request, projectId, organisationId, noteId);
        Supplier<String> view = () -> {
            FinanceChecksNotesViewModel viewModel = populateNotesViewModel(projectId, organisationId, noteId, attachments);
            model.addAttribute("model", viewModel);
            model.addAttribute("form", form);
            return "project/financecheck/notes";
        };

        return validationHandler.performActionOrBindErrorsToField("attachment", view, view, () -> {
            MultipartFile file = form.getAttachment();
            ServiceResult<AttachmentResource> result = financeCheckService.uploadFile(projectId, file.getContentType(), file.getSize(), file.getOriginalFilename(), getMultipartFileBytes(file));
            result.ifSuccessful( uploadedAttachment -> {
                attachments.add(uploadedAttachment.id);
                saveAttachmentsToCookie(response, attachments, projectId, organisationId, noteId);
            });

            FinanceChecksNotesViewModel viewModel = populateNotesViewModel(projectId, organisationId, noteId, attachments);
            model.addAttribute("model", viewModel);
            return result;
        });
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_NOTES_SECTION')")
    @RequestMapping(value = "/{noteId}/new-comment/attachment/{attachmentId}", method = GET)
    public
    @ResponseBody
    ResponseEntity<ByteArrayResource> downloadResponseAttachment(@PathVariable Long projectId,
                                                                 @PathVariable Long organisationId,
                                                                 @PathVariable Long noteId,
                                                                 @PathVariable Long attachmentId,
                                                                 @ModelAttribute("loggedInUser") UserResource loggedInUser,
                                                                 HttpServletRequest request) {
        List<Long> attachments = loadAttachmentsFromCookie(request, projectId, organisationId, noteId);
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

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_NOTES_SECTION')")
    @RequestMapping(value = "/{noteId}/new-comment", params = "removeAttachment", method = POST)
    public String removeAttachment(@PathVariable Long projectId,
                                   @PathVariable Long organisationId,
                                   @PathVariable Long noteId,
                                   @RequestParam(value = "removeAttachment") final Long attachmentId,
                                   @ModelAttribute(FORM_ATTR) FinanceChecksNotesAddCommentForm form,
                                   @SuppressWarnings("unused") BindingResult bindingResult,
                                   ValidationHandler validationHandler,
                                   @ModelAttribute("loggedInUser") UserResource loggedInUser,
                                   HttpServletRequest request,
                                   HttpServletResponse response,
                                   Model model) {
        List<Long> attachments = loadAttachmentsFromCookie(request, projectId, organisationId, noteId);
        if (attachments.contains(attachmentId)) {
            attachments.remove(attachments.indexOf(attachmentId));
            financeCheckService.deleteFile(attachmentId);
        }
        saveAttachmentsToCookie(response, attachments, projectId, organisationId, noteId);

        FinanceChecksNotesViewModel viewModel = populateNotesViewModel(projectId, organisationId, noteId, attachments);
        model.addAttribute("model", viewModel);
        model.addAttribute("form", form);
        return "project/financecheck/notes";
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_NOTES_SECTION')")
    @RequestMapping(value="/{noteId}/new-comment/cancel", method = GET)
    public String cancelNewForm(@PathVariable Long projectId,
                                @PathVariable Long organisationId,
                                @PathVariable Long noteId,
                                Model model,
                                @ModelAttribute("loggedInUser") UserResource loggedInUser,
                                HttpServletRequest request,
                                HttpServletResponse response) {
        List<Long> attachments = loadAttachmentsFromCookie(request, projectId, organisationId, noteId);
        attachments.forEach(( id -> financeCheckService.deleteFile(id)));

        cookieUtil.removeCookie(response, getCookieName(projectId, organisationId, noteId));

        return redirectToQueryPage(projectId, organisationId);
    }

    private List<ThreadViewModel> loadNoteModel(Long projectId, Long organisationId) {

        List<ThreadViewModel> noteModel = new LinkedList<>();

        ProjectFinanceResource projectFinance = projectFinanceService.getProjectFinance(projectId, organisationId);
        ServiceResult<List<NoteResource>> notes = financeCheckService.loadNotes(projectFinance.getId());
        if (notes.isSuccess()) {
            // order notes by most recent comment
            List<NoteResource> sortedQueries = notes.getSuccessObject().stream().
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
                    OrganisationResource organisation = organisationService.getOrganisationForUser(p.author.getId());
                    ThreadPostViewModel post = new ThreadPostViewModel(p.id, p.author, p.body, p.attachments, p.createdOn);
                    post.setUsername(user.getName() + " - " + organisation.getName() + (user.hasRole(UserRoleType.PROJECT_FINANCE) ? " (Finance team)" : ""));
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
        }
        return noteModel;
    }

    private FinanceChecksNotesViewModel populateNotesViewModel(Long projectId, Long organisationId, Long noteId, List<Long> attachments) {

        ProjectResource project = projectService.getById(projectId);

        OrganisationResource organisation = organisationService.getOrganisationById(organisationId);
        OrganisationResource leadOrganisation = projectService.getLeadOrganisation(projectId);
        boolean leadPartnerOrganisation = leadOrganisation.getId().equals(organisation.getId());

        Map<Long, String> attachmentLinks = new HashMap<>();
        if(attachments != null) {
            attachments.forEach(id -> {
                financeCheckService.getAttachment(id).ifSuccessful(foundAttachment -> attachmentLinks.put(id, foundAttachment.name));
            });
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

    private String redirectToQueryPage(Long projectId, Long organisationId) {
        return "redirect:/project/" + projectId + "/finance-check/organisation/" + organisationId + "/note";
    }

    private ResponseEntity<ByteArrayResource> returnFileIfFoundOrThrowNotFoundException(Optional<ByteArrayResource> content, Optional<FileEntryResource> fileDetails) {
        if (content.isPresent() && fileDetails.isPresent()) {
            return getFileResponseEntity(content.get(), fileDetails.get());
        } else {
            return new ResponseEntity<>(null, null, HttpStatus.NO_CONTENT);
        }
    }

    private String getCookieName(Long projectId, Long organisationId, Long noteId) {
        return ATTACHMENT_COOKIE + "_" + projectId + "_" + organisationId + "_" + noteId;
    }

    private void saveAttachmentsToCookie(HttpServletResponse response, List<Long> attachmentFileIds, Long projectId, Long organisationId, Long noteId) {
        String jsonState = JsonUtil.getSerializedObject(attachmentFileIds);
        cookieUtil.saveToCookie(response, getCookieName(projectId, organisationId, noteId), jsonState);
    }

    private List<Long> loadAttachmentsFromCookie(HttpServletRequest request, Long projectId, Long organisationId, Long noteId) {

        List<Long> attachments = new LinkedList<>();
        String json = cookieUtil.getCookieValue(request, getCookieName(projectId, organisationId, noteId));

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
