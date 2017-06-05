package org.innovateuk.ifs.project.queries.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.commons.error.exception.ForbiddenActionException;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.finance.ProjectFinanceService;
import org.innovateuk.ifs.project.financecheck.FinanceCheckService;
import org.innovateuk.ifs.project.queries.form.FinanceChecksQueriesAddQueryForm;
import org.innovateuk.ifs.project.queries.form.FinanceChecksQueriesFormConstraints;
import org.innovateuk.ifs.project.queries.viewmodel.FinanceChecksQueriesAddQueryViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.util.CookieUtil;
import org.innovateuk.ifs.util.JsonUtil;
import org.innovateuk.threads.attachment.resource.AttachmentResource;
import org.innovateuk.threads.resource.FinanceChecksSectionType;
import org.innovateuk.threads.resource.PostResource;
import org.innovateuk.threads.resource.QueryResource;
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

import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;
import static org.innovateuk.ifs.controller.FileUploadControllerUtils.getMultipartFileBytes;
import static org.innovateuk.ifs.file.controller.FileDownloadControllerUtils.getFileResponseEntity;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;

/**
 * This Controller handles finance check queries activity for the finance team members
 */
@Controller
@RequestMapping(FinanceChecksQueriesAddQueryController.FINANCE_CHECKS_QUERIES_NEW_QUERY_BASE_URL)
public class FinanceChecksQueriesAddQueryController {

    static final String FINANCE_CHECKS_QUERIES_NEW_QUERY_BASE_URL = "/project/{projectId}/finance-check/organisation/{organisationId}/query/new-query";
    private static final String FINANCE_CHECKS_QUERIES_LIST = "/project/{projectId}/finance-check/organisation/{organisationId}/query";
    private static final String ATTACHMENT_COOKIE = "finance_checks_queries_new_query_attachments";
    private static final String FORM_COOKIE = "finance_checks_queries_new_query_form";
    private static final String FORM_ATTR = "form";
    private static final String UNKNOWN_FIELD = "Unknown";
    private static final String NEW_QUERY_VIEW = "project/financecheck/new-query";
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


    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_QUERIES_SECTION_ADD_QUERY')")
    @GetMapping
    public String viewNew(@PathVariable final Long projectId,
                          @PathVariable final Long organisationId,
                          @RequestParam(value = "query_section", required = false) final String querySection,
                          Model model,
                          UserResource loggedInUser,
                          HttpServletRequest request,
                          HttpServletResponse response) {
        projectService.getPartnerOrganisation(projectId, organisationId);
        List<Long> attachments = loadAttachmentsFromCookie(request, projectId, organisationId);
        FinanceChecksQueriesAddQueryViewModel viewModel = populateQueriesViewModel(projectId, organisationId, querySection, attachments);
        model.addAttribute("model", viewModel);
        model.addAttribute(FORM_ATTR, loadForm(request, projectId, organisationId).orElse(new FinanceChecksQueriesAddQueryForm()));
        return NEW_QUERY_VIEW;
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_QUERIES_SECTION_ADD_QUERY')")
    @PostMapping
    public String saveQuery(@PathVariable final Long projectId,
                            @PathVariable final Long organisationId,
                            @RequestParam(value = "query_section", required = false) final String querySection,
                            @Valid @ModelAttribute(FORM_ATTR) FinanceChecksQueriesAddQueryForm form,
                            @SuppressWarnings("unused") BindingResult bindingResult,
                            ValidationHandler validationHandler,
                            Model model,
                            UserResource loggedInUser,
                            HttpServletRequest request,
                            HttpServletResponse response) {
        Supplier<String> failureView = () -> {
            List<Long> attachments = loadAttachmentsFromCookie(request, projectId, organisationId);
            FinanceChecksQueriesAddQueryViewModel viewModel = populateQueriesViewModel(projectId, organisationId, querySection, attachments);
            model.addAttribute("model", viewModel);
            model.addAttribute(FORM_ATTR, form);
            return NEW_QUERY_VIEW;
        };

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            FinanceChecksSectionType section = null;
            for (FinanceChecksSectionType value : FinanceChecksSectionType.values()) {
                if (value.name().toUpperCase().equals(form.getSection().toUpperCase())) {
                    section = value;
                }
            }
            ValidationMessages validationMessages = new ValidationMessages(bindingResult);

            ProjectFinanceResource projectFinance = projectFinanceService.getProjectFinance(projectId, organisationId);

            List<AttachmentResource> attachmentResources = new ArrayList<>();
            List<Long> attachments = loadAttachmentsFromCookie(request, projectId, organisationId);
            attachments.forEach(attachment -> financeCheckService.getAttachment(attachment).ifSuccessful(fileEntry -> attachmentResources.add(fileEntry)));

            PostResource post = new PostResource(null, loggedInUser, form.getQuery(), attachmentResources, ZonedDateTime.now());

            List<PostResource> posts = new ArrayList<>();
            posts.add(post);
            QueryResource query = new QueryResource(null, projectFinance.getId(), posts, section, form.getQueryTitle(), true, ZonedDateTime.now());
            ServiceResult<Long> result = financeCheckService.saveQuery(query);
            validationHandler.addAnyErrors(result);
            return validationHandler.addAnyErrors(validationMessages, fieldErrorsToFieldErrors(), asGlobalErrors()).
                    failNowOrSucceedWith(failureView, () -> {
                        deleteCookies(response, projectId, organisationId);
                        return redirectTo(queriesListView(projectId, organisationId, querySection));
                    });
        });
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_QUERIES_SECTION_ADD_QUERY')")
    @PostMapping(params = "uploadAttachment")
    public String uploadAttachment(Model model,
                                   @PathVariable final Long projectId,
                                   @PathVariable final Long organisationId,
                                   @RequestParam(value = "query_section", required = false) String querySection,
                                   @ModelAttribute(FORM_ATTR) FinanceChecksQueriesAddQueryForm form,
                                   @SuppressWarnings("unused") BindingResult bindingResult,
                                   ValidationHandler validationHandler,
                                   UserResource loggedInUser,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {
        List<Long> attachments = loadAttachmentsFromCookie(request, projectId, organisationId);
        Supplier<String> onSuccess = () -> redirectTo(rootView(projectId, organisationId, querySection));
        Supplier<String> onError = () -> {
            FinanceChecksQueriesAddQueryViewModel viewModel = populateQueriesViewModel(projectId, organisationId, querySection, attachments);
            model.addAttribute("model", viewModel);
            model.addAttribute("form", form);
            return NEW_QUERY_VIEW;
        };

        return validationHandler.performActionOrBindErrorsToField("attachment", onError, onSuccess, () -> {
            MultipartFile file = form.getAttachment();

            ServiceResult<AttachmentResource> result = financeCheckService.uploadFile(projectId, file.getContentType(),
                    file.getSize(), file.getOriginalFilename(), getMultipartFileBytes(file));
            result.ifSuccessful(uploadedAttachment -> {
                attachments.add(uploadedAttachment.id);
                saveAttachmentsToCookie(response, attachments, projectId, organisationId);
                saveFormToCookie(response, projectId, organisationId, form);
            });

            model.addAttribute("model", populateQueriesViewModel(projectId, organisationId, querySection, attachments));
            return result;
        });
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_QUERIES_SECTION_ADD_QUERY')")
    @GetMapping("/attachment/{attachmentId}")
    public @ResponseBody
    ResponseEntity<ByteArrayResource> downloadAttachment(@PathVariable Long projectId,
                                                         @PathVariable Long organisationId,
                                                         @PathVariable Long attachmentId,
                                                         UserResource loggedInUser,
                                                         HttpServletRequest request) {
        projectService.getPartnerOrganisation(projectId, organisationId);
        List<Long> attachments = loadAttachmentsFromCookie(request, projectId, organisationId);

        if (attachments.contains(attachmentId)) {
            return getFileResponseEntity(financeCheckService.downloadFile(attachmentId), financeCheckService.getAttachmentInfo(attachmentId));
        } else {
            throw new ForbiddenActionException();
        }
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_QUERIES_SECTION_ADD_QUERY')")
    @PostMapping(params = "removeAttachment")
    public String removeAttachment(@PathVariable Long projectId,
                                   @PathVariable Long organisationId,
                                   @RequestParam(value = "query_section", required = false) final String querySection,
                                   @RequestParam(value = "removeAttachment") final Long attachmentId,
                                   @ModelAttribute(FORM_ATTR) FinanceChecksQueriesAddQueryForm form,
                                   @SuppressWarnings("unused") BindingResult bindingResult,
                                   ValidationHandler validationHandler,
                                   UserResource loggedInUser,
                                   HttpServletRequest request,
                                   HttpServletResponse response,
                                   Model model) {
        List<Long> attachments = loadAttachmentsFromCookie(request, projectId, organisationId);
        if (attachments.contains(attachmentId)) {
            financeCheckService.deleteFile(attachmentId).andOnSuccess(() ->
                    attachments.remove(attachments.indexOf(attachmentId)));
        }
        saveAttachmentsToCookie(response, attachments, projectId, organisationId);
        saveFormToCookie(response, projectId, organisationId, form);

        return redirectTo(rootView(projectId, organisationId, querySection));
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_QUERIES_SECTION_ADD_QUERY')")
    @GetMapping("/cancel")
    public String cancelNewForm(@PathVariable Long projectId,
                                @PathVariable Long organisationId,
                                @RequestParam(value = "query_section", required = false) String querySection,
                                UserResource loggedInUser,
                                HttpServletRequest request,
                                HttpServletResponse response) {
        projectService.getPartnerOrganisation(projectId, organisationId);
        loadAttachmentsFromCookie(request, projectId, organisationId).forEach(financeCheckService::deleteFile);
        deleteCookies(response, projectId, organisationId);
        return redirectTo(queriesListView(projectId, organisationId, querySection));
    }

    private FinanceChecksQueriesAddQueryViewModel populateQueriesViewModel(Long projectId, Long organisationId, String querySection, List<Long> attachmentFileIds) {
        ProjectResource project = projectService.getById(projectId);
        OrganisationResource organisation = organisationService.getOrganisationById(organisationId);
        OrganisationResource leadOrganisation = projectService.getLeadOrganisation(projectId);
        boolean leadPartnerOrganisation = leadOrganisation.getId().equals(organisation.getId());
        Optional<ProjectUserResource> financeContact = getFinanceContact(projectId, organisationId);

        Map<Long, String> attachmentLinks = new HashMap<>();
        attachmentFileIds.forEach(id -> financeCheckService.getAttachment(id).ifSuccessful(file -> attachmentLinks.put(id, file.name)));

        return new FinanceChecksQueriesAddQueryViewModel(
                organisation.getName(),
                leadPartnerOrganisation,
                financeContact.map(ProjectUserResource::getUserName).orElse(UNKNOWN_FIELD),
                financeContact.map(ProjectUserResource::getEmail).orElse(UNKNOWN_FIELD),
                financeContact.map(ProjectUserResource::getPhoneNumber).orElse(UNKNOWN_FIELD),
                querySection == null ? UNKNOWN_FIELD : querySection,
                project.getId(),
                project.getName(),
                attachmentLinks,
                FinanceChecksQueriesFormConstraints.MAX_QUERY_WORDS,
                FinanceChecksQueriesFormConstraints.MAX_QUERY_CHARACTERS,
                FinanceChecksQueriesFormConstraints.MAX_TITLE_CHARACTERS,
                organisationId,
                FINANCE_CHECKS_QUERIES_NEW_QUERY_BASE_URL
        );
    }

    private Optional<ProjectUserResource> getFinanceContact(Long projectId, Long organisationId) {
        List<ProjectUserResource> projectUsers = projectService.getProjectUsersForProject(projectId);
        return simpleFindFirst(projectUsers, pr -> pr.isFinanceContact() && organisationId.equals(pr.getOrganisation()));
    }

    private String getCookieName(Long projectId, Long organisationId) {
        return ATTACHMENT_COOKIE + "_" + projectId + "_" + organisationId;
    }

    private String getFormCookieName(Long projectId, Long organisationId) {
        return FORM_COOKIE + "_" + projectId + "_" + organisationId;
    }

    private void saveAttachmentsToCookie(HttpServletResponse response, List<Long> attachmentFileIds, Long projectId, Long organisationId) {
        cookieUtil.saveToCookie(response, getCookieName(projectId, organisationId), JsonUtil.getSerializedObject(attachmentFileIds));
    }

    private void saveFormToCookie(HttpServletResponse response, Long projectId, Long organisationId, FinanceChecksQueriesAddQueryForm form) {
        cookieUtil.saveToCookie(response, getFormCookieName(projectId, organisationId), JsonUtil.getSerializedObject(form));
    }

    private List<Long> loadAttachmentsFromCookie(HttpServletRequest request, Long projectId, Long organisationId) {
        return cookieUtil.getCookieAsList(request, getCookieName(projectId, organisationId), new TypeReference<List<Long>>() {
        });
    }

    private Optional<FinanceChecksQueriesAddQueryForm> loadForm(HttpServletRequest request, Long projectId, Long organisationId) {
        return cookieUtil.getCookieAs(request, getFormCookieName(projectId, organisationId),
                new TypeReference<FinanceChecksQueriesAddQueryForm>() {
                });
    }

    private String rootView(final Long projectId, final Long organisationId, String querySection) {
        return addQuerySection(String.format(FINANCE_CHECKS_QUERIES_NEW_QUERY_BASE_URL, projectId, organisationId), querySection);
    }

    private String queriesListView(Long projectId, Long organisationId, String querySection) {
        return addQuerySection(String.format(FINANCE_CHECKS_QUERIES_LIST, projectId, organisationId), querySection);
    }

    private String addQuerySection(String url, String querySection) {
        return url + (querySection != null ? "?query_section=" + querySection : "");
    }

    private String redirectTo(final String path) {
        return "redirect:" + path;
    }

    private void deleteCookies(HttpServletResponse response, Long projectId, Long organisationId) {
        cookieUtil.removeCookie(response, getCookieName(projectId, organisationId));
        cookieUtil.removeCookie(response, getFormCookieName(projectId, organisationId));
    }
}
