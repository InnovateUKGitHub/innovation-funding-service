package org.innovateuk.ifs.project.notes.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.finance.ProjectFinanceService;
import org.innovateuk.ifs.project.financecheck.FinanceCheckService;
import org.innovateuk.ifs.project.notes.form.FinanceChecksNotesAddNoteForm;
import org.innovateuk.ifs.project.notes.form.FinanceChecksNotesFormConstraints;
import org.innovateuk.ifs.project.notes.viewmodel.FinanceChecksNotesAddNoteViewModel;
import org.innovateuk.ifs.project.queries.viewmodel.FinanceChecksQueriesAddQueryViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.util.CookieUtil;
import org.innovateuk.ifs.util.JsonUtil;
import org.innovateuk.threads.attachment.resource.AttachmentResource;
import org.innovateuk.threads.resource.NoteResource;
import org.innovateuk.threads.resource.PostResource;
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

import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;
import static org.innovateuk.ifs.controller.FileUploadControllerUtils.getMultipartFileBytes;
import static org.innovateuk.ifs.file.controller.FileDownloadControllerUtils.getFileResponseEntity;

/**
 * This Controller handles finance check notes activity for the finance team members
 */
@Controller
@RequestMapping(FinanceChecksNotesAddNoteController.FINANCE_CHECKS_NOTES_NEW_NOTE_BASE_URL)
public class FinanceChecksNotesAddNoteController {
    static final String FINANCE_CHECKS_NOTES_NEW_NOTE_BASE_URL = "/project/{projectId}/finance-check/organisation/{organisationId}/note/new-note";
    private static final String ATTACHMENT_COOKIE = "finance_checks_notes_new_note_attachments";
    private static final String FORM_COOKIE = "finance_checks_notes_new_note_form";
    private static final String FORM_ATTR = "form";

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private CookieUtil cookieUtil;

    @Autowired
    private ProjectFinanceService projectFinanceService;

    @Autowired
    private FinanceCheckService financeCheckService;

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_NOTES_SECTION')")
    @GetMapping
    public String viewNewNote(@PathVariable final Long projectId,
                              @PathVariable final Long organisationId,
                              Model model,
                              @ModelAttribute("loggedInUser") UserResource loggedInUser,
                              HttpServletRequest request,
                              HttpServletResponse response) {

        List<Long> attachments = loadAttachmentsFromCookie(request, projectId, organisationId);
        model.addAttribute("model", populateNoteViewModel(projectId, organisationId, attachments));
        model.addAttribute(FORM_ATTR, loadForm(request, projectId, organisationId).orElse(new FinanceChecksNotesAddNoteForm()));
        return "project/financecheck/new-note";
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_NOTES_SECTION')")
    @PostMapping
    public String saveNote(@PathVariable final Long projectId,
                           @PathVariable final Long organisationId,
                           @Valid @ModelAttribute(FORM_ATTR) FinanceChecksNotesAddNoteForm form,
                           @SuppressWarnings("unused") BindingResult bindingResult,
                           ValidationHandler validationHandler,
                           Model model,
                           @ModelAttribute("loggedInUser") UserResource loggedInUser,
                           HttpServletRequest request,
                           HttpServletResponse response) {
        Supplier<String> failureView = () -> {
            List<Long> attachments = loadAttachmentsFromCookie(request, projectId, organisationId);
            FinanceChecksNotesAddNoteViewModel viewModel = populateNoteViewModel(projectId, organisationId, attachments);
            model.addAttribute("model", viewModel);
            model.addAttribute(FORM_ATTR, form);
            return "project/financecheck/new-note";
        };

        return validationHandler.failNowOrSucceedWith(failureView, () -> {

            ValidationMessages validationMessages = new ValidationMessages(bindingResult);

            ProjectFinanceResource projectFinance = projectFinanceService.getProjectFinance(projectId, organisationId);

            List<AttachmentResource> attachmentResources = new ArrayList<>();
            List<Long> attachments = loadAttachmentsFromCookie(request, projectId, organisationId);
            attachments.forEach(attachment -> {
                ServiceResult<AttachmentResource> fileEntry = financeCheckService.getAttachment(attachment);
                if (fileEntry.isSuccess()) {
                    attachmentResources.add(fileEntry.getSuccessObject());
                }
            });

            PostResource post = new PostResource(null, loggedInUser, form.getNote(), attachmentResources, ZonedDateTime.now());

            List<PostResource> posts = new ArrayList<>();
            posts.add(post);
            NoteResource note = new NoteResource(null, projectFinance.getId(), posts, form.getNoteTitle(), ZonedDateTime.now());
            ServiceResult<Long> result = financeCheckService.saveNote(note);
            validationHandler.addAnyErrors(result);
            return validationHandler.addAnyErrors(validationMessages, fieldErrorsToFieldErrors(), asGlobalErrors()).
                    failNowOrSucceedWith(failureView, () -> {
                        deleteCookies(response, projectId, organisationId);
                        return redirectToNotePage(projectId, organisationId);
                    });
        });
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_NOTES_SECTION')")
    @PostMapping(params = "uploadAttachment")
    public String saveNewNoteAttachment(Model model,
                                        @PathVariable final Long projectId,
                                        @PathVariable final Long organisationId,
                                        @ModelAttribute(FORM_ATTR) FinanceChecksNotesAddNoteForm form,
                                        @SuppressWarnings("unused") BindingResult bindingResult,
                                        ValidationHandler validationHandler,
                                        @ModelAttribute("loggedInUser") UserResource loggedInUser,
                                        HttpServletRequest request,
                                        HttpServletResponse response) {
        List<Long> attachments = loadAttachmentsFromCookie(request, projectId, organisationId);
        Supplier<String> view = () -> redirectTo(rootView(projectId, organisationId));
        Supplier<String> errorView = () -> {
            model.addAttribute("model", populateNoteViewModel(projectId, organisationId, attachments));
            model.addAttribute("form", form);
            return "project/financecheck/new-query";
        };
        
        return validationHandler.performActionOrBindErrorsToField("attachment", errorView, view, () -> {
            MultipartFile file = form.getAttachment();

            ServiceResult<AttachmentResource> result = financeCheckService.uploadFile(projectId, file.getContentType(), file.getSize(), file.getOriginalFilename(), getMultipartFileBytes(file));
            result.ifSuccessful(uploadedAttachment -> {
                attachments.add(uploadedAttachment.id);
                saveAttachmentsToCookie(response, attachments, projectId, organisationId);
                saveFormToCookie(response, projectId, organisationId, form);
            });

            model.addAttribute("model", populateNoteViewModel(projectId, organisationId, attachments));
            return result;
        });
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_NOTES_SECTION')")
    @GetMapping("/attachment/{attachmentId}")
    public
    @ResponseBody
    ResponseEntity<ByteArrayResource> downloadAttachment(@PathVariable Long projectId,
                                                         @PathVariable Long organisationId,
                                                         @PathVariable Long attachmentId,
                                                         @ModelAttribute("loggedInUser") UserResource loggedInUser,
                                                         HttpServletRequest request) {
        List<Long> attachments = loadAttachmentsFromCookie(request, projectId, organisationId);
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
    @PostMapping(params = "removeAttachment")
    public String removeAttachment(@PathVariable Long projectId,
                                   @PathVariable Long organisationId,
                                   @RequestParam(value = "removeAttachment") final Long attachmentId,
                                   @ModelAttribute(FORM_ATTR) FinanceChecksNotesAddNoteForm form,
                                   @SuppressWarnings("unused") BindingResult bindingResult,
                                   ValidationHandler validationHandler,
                                   @ModelAttribute("loggedInUser") UserResource loggedInUser,
                                   HttpServletRequest request,
                                   HttpServletResponse response,
                                   Model model) {
        List<Long> attachments = loadAttachmentsFromCookie(request, projectId, organisationId);
        if (attachments.contains(attachmentId)) {
            financeCheckService.deleteFile(attachmentId)
                    .andOnSuccess(() -> attachments.remove(attachments.indexOf(attachmentId)));
        }
        saveAttachmentsToCookie(response, attachments, projectId, organisationId);
        saveFormToCookie(response, projectId, organisationId, form);

        return redirectTo(rootView(projectId, organisationId));
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_NOTES_SECTION')")
    @GetMapping("/cancel")
    public String cancelNewForm(@PathVariable Long projectId,
                                @PathVariable Long organisationId,
                                @ModelAttribute("loggedInUser") UserResource loggedInUser,
                                HttpServletRequest request,
                                HttpServletResponse response) {
        List<Long> attachments = loadAttachmentsFromCookie(request, projectId, organisationId);
        attachments.forEach(financeCheckService::deleteFile);
        deleteCookies(response, projectId, organisationId);
        return redirectToNotePage(projectId, organisationId);
    }

    private FinanceChecksNotesAddNoteViewModel populateNoteViewModel(Long projectId, Long organisationId, List<Long> attachmentFileIds) {

        ProjectResource project = projectService.getById(projectId);

        OrganisationResource organisation = organisationService.getOrganisationById(organisationId);
        OrganisationResource leadOrganisation = projectService.getLeadOrganisation(projectId);
        boolean leadPartnerOrganisation = leadOrganisation.getId().equals(organisation.getId());

        Map<Long, String> attachmentLinks = new HashMap<>();
        attachmentFileIds.forEach(id -> financeCheckService.getAttachment(id)
                .ifSuccessful(foundAttachment -> attachmentLinks.put(id, foundAttachment.name)));

        return new FinanceChecksNotesAddNoteViewModel(
                organisation.getName(),
                leadPartnerOrganisation,
                project.getId(),
                project.getName(),
                attachmentLinks,
                FinanceChecksNotesFormConstraints.MAX_NOTE_WORDS,
                FinanceChecksNotesFormConstraints.MAX_NOTE_CHARACTERS,
                FinanceChecksNotesFormConstraints.MAX_TITLE_CHARACTERS,
                organisationId,
                FINANCE_CHECKS_NOTES_NEW_NOTE_BASE_URL
        );
    }

    private String redirectToNotePage(Long projectId, Long organisationId) {
        return "redirect:/project/" + projectId + "/finance-check/organisation/" + organisationId + "/note";
    }

    private ResponseEntity<ByteArrayResource> returnFileIfFoundOrThrowNotFoundException(Optional<ByteArrayResource> content, Optional<FileEntryResource> fileDetails) {
        if (content.isPresent() && fileDetails.isPresent()) {
            return getFileResponseEntity(content.get(), fileDetails.get());
        } else {
            return new ResponseEntity<>(null, null, HttpStatus.NO_CONTENT);
        }
    }

    private String getCookieName(Long projectId, Long organisationId) {
        return ATTACHMENT_COOKIE + "_" + projectId + "_" + organisationId;
    }

    private void saveAttachmentsToCookie(HttpServletResponse response, List<Long> attachmentFileIds, Long projectId, Long organisationId) {
        cookieUtil.saveToCookie(response, getCookieName(projectId, organisationId), JsonUtil.getSerializedObject(attachmentFileIds));
    }

    private List<Long> loadAttachmentsFromCookie(HttpServletRequest request, Long projectId, Long organisationId) {
        return cookieUtil.getCookieAsList(request, getCookieName(projectId, organisationId), new TypeReference<List<Long>>() {
        });
    }

    private void deleteCookies(HttpServletResponse response, Long projectId, Long organisationId) {
        cookieUtil.removeCookie(response, getCookieName(projectId, organisationId));
        cookieUtil.removeCookie(response, getFormCookieName(projectId, organisationId));
    }

    private String getFormCookieName(Long projectId, Long organisationId) {
        return FORM_COOKIE + "_" + projectId + "_" + organisationId;
    }

    private String redirectTo(final String path) {
        return "redirect:" + path;
    }

    private Optional<FinanceChecksNotesAddNoteForm> loadForm(HttpServletRequest request, Long projectId, Long organisationId) {
        return cookieUtil.getCookieAs(request, getFormCookieName(projectId, organisationId),
                new TypeReference<FinanceChecksNotesAddNoteForm>() {
                });
    }

    private String rootView(final Long projectId, final Long organisationId) {
        return String.format(FINANCE_CHECKS_NOTES_NEW_NOTE_BASE_URL, projectId, organisationId);
    }

    private void saveFormToCookie(HttpServletResponse response, Long projectId, Long organisationId, FinanceChecksNotesAddNoteForm form) {
        cookieUtil.saveToCookie(response, getFormCookieName(projectId, organisationId), JsonUtil.getSerializedObject(form));
    }
}
