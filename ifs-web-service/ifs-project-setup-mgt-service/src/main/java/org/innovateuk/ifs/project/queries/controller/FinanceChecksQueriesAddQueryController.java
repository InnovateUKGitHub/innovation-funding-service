package org.innovateuk.ifs.project.queries.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.threads.attachment.resource.AttachmentResource;
import org.innovateuk.threads.resource.FinanceChecksSectionType;
import org.innovateuk.threads.resource.PostResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.finance.ProjectFinanceService;
import org.innovateuk.ifs.project.financecheck.FinanceCheckService;
import org.innovateuk.ifs.project.queries.form.FinanceChecksQueriesFormConstraints;
import org.innovateuk.ifs.project.queries.form.FinanceChecksQueriesAddQueryForm;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.project.queries.viewmodel.FinanceChecksQueriesAddQueryViewModel;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.util.CookieUtil;
import org.innovateuk.ifs.util.JsonUtil;
import org.innovateuk.threads.resource.QueryResource;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Supplier;

import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;
import static org.innovateuk.ifs.controller.FileUploadControllerUtils.getMultipartFileBytes;
import static org.innovateuk.ifs.file.controller.FileDownloadControllerUtils.getFileResponseEntity;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * This Controller handles finance check queries activity for the finance team members
 */
@Controller
@RequestMapping(FinanceChecksQueriesAddQueryController.FINANCE_CHECKS_QUERIES_NEW_QUERY_BASE_URL)
public class FinanceChecksQueriesAddQueryController {

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

    public static final String FINANCE_CHECKS_QUERIES_NEW_QUERY_BASE_URL = "/project/{projectId}/finance-check/organisation/{organisationId}/query/new-query";

    private static final String ATTACHMENT_COOKIE = "finance_checks_queries_new_query_attachments";
    private static final String FORM_ATTR = "form";
    private static final String UNKNOWN_FIELD = "Unknown";


    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_QUERIES_SECTION')")
    @RequestMapping(method = GET)
    public String viewNewQuery(@PathVariable final Long projectId,
                               @PathVariable final Long organisationId,
                               @RequestParam(value = "query_section", required = false) final String querySection,
                               Model model,
                               @ModelAttribute("loggedInUser") UserResource loggedInUser,
                               HttpServletRequest request,
                               HttpServletResponse response) {

        List<Long> attachments = loadAttachmentsFromCookie(request, projectId, organisationId);
        attachments.forEach(id -> financeCheckService.deleteFile(id));
        saveAttachmentsToCookie(response, new ArrayList<>(), projectId, organisationId);

        FinanceChecksQueriesAddQueryViewModel viewModel = populateQueriesViewModel(projectId, organisationId, querySection, attachments);
        model.addAttribute("model", viewModel);
        FinanceChecksQueriesAddQueryForm form = new FinanceChecksQueriesAddQueryForm();
        model.addAttribute(FORM_ATTR, form);
        return "project/financecheck/new-query";
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_QUERIES_SECTION')")
    @RequestMapping(method = POST)
    public String saveQuery(@PathVariable final Long projectId,
                            @PathVariable final Long organisationId,
                            @RequestParam(value = "query_section", required = false) final String querySection,
                            @Valid @ModelAttribute(FORM_ATTR) FinanceChecksQueriesAddQueryForm form,
                            @SuppressWarnings("unused") BindingResult bindingResult,
                            ValidationHandler validationHandler,
                            Model model,
                            @ModelAttribute("loggedInUser") UserResource loggedInUser,
                            HttpServletRequest request,
                            HttpServletResponse response)
    {
        Supplier<String> failureView = () -> {
            List<Long> attachments = loadAttachmentsFromCookie(request, projectId, organisationId);
            FinanceChecksQueriesAddQueryViewModel viewModel = populateQueriesViewModel(projectId, organisationId, querySection, attachments);
            model.addAttribute("model", viewModel);
            model.addAttribute(FORM_ATTR, form);
            return "project/financecheck/new-query";
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
            attachments.forEach(attachment -> {
                ServiceResult<AttachmentResource> fileEntry = financeCheckService.getAttachment(attachment);
                if (fileEntry.isSuccess()) {
                    attachmentResources.add(fileEntry.getSuccessObject());
                }
            });

            PostResource post = new PostResource(null, loggedInUser, form.getQuery(), attachmentResources, ZonedDateTime.now());

            List<PostResource> posts = new ArrayList<>();
            posts.add(post);
            QueryResource query = new QueryResource(null, projectFinance.getId(), posts, section, form.getQueryTitle(), true, ZonedDateTime.now());
            ServiceResult<Long> result = financeCheckService.saveQuery(query);
            validationHandler.addAnyErrors(result);
            return validationHandler.addAnyErrors(validationMessages, fieldErrorsToFieldErrors(), asGlobalErrors()).
                    failNowOrSucceedWith(failureView, () -> {
                        cookieUtil.removeCookie(response, getCookieName(projectId, organisationId));
                        return redirectToQueryPage(projectId, organisationId, querySection);
                    });
        });
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_QUERIES_SECTION')")
    @RequestMapping(method = POST, params = "uploadAttachment")
    public String saveNewQueryAttachment(Model model,
                                         @PathVariable final Long projectId,
                                         @PathVariable final Long organisationId,
                                         @RequestParam(value = "query_section", required = false) String querySection,
                                         @ModelAttribute(FORM_ATTR) FinanceChecksQueriesAddQueryForm form,
                                         @SuppressWarnings("unused") BindingResult bindingResult,
                                         ValidationHandler validationHandler,
                                         @ModelAttribute("loggedInUser") UserResource loggedInUser,
                                         HttpServletRequest request,
                                         HttpServletResponse response) {
        List<Long> attachments = loadAttachmentsFromCookie(request, projectId, organisationId);
        Supplier<String> view = () -> {
            FinanceChecksQueriesAddQueryViewModel viewModel = populateQueriesViewModel(projectId, organisationId, querySection, attachments);
            model.addAttribute("model", viewModel);
            model.addAttribute("form", form);
            return "project/financecheck/new-query";
        };

        return validationHandler.performActionOrBindErrorsToField("attachment", view, view, () -> {
            MultipartFile file = form.getAttachment();

            ServiceResult<AttachmentResource> result = financeCheckService.uploadFile(projectId, file.getContentType(), file.getSize(), file.getOriginalFilename(), getMultipartFileBytes(file));
            if(result.isSuccess()) {
                attachments.add(result.getSuccessObject().id);
                saveAttachmentsToCookie(response, attachments, projectId, organisationId);
            }

            FinanceChecksQueriesAddQueryViewModel viewModel = populateQueriesViewModel(projectId, organisationId, querySection, attachments);
            model.addAttribute("model", viewModel);
            return result;
        });
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_QUERIES_SECTION')")
    @RequestMapping(value = "/attachment/{attachmentId}", method = GET)
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

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_QUERIES_SECTION')")
    @RequestMapping(method = POST, params="removeAttachment")
    public String removeAttachment(@PathVariable Long projectId,
                                   @PathVariable Long organisationId,
                                   @RequestParam(value = "query_section", required = false) final String querySection,
                                   @RequestParam(value = "removeAttachment") final Long attachmentId,
                                   @ModelAttribute(FORM_ATTR) FinanceChecksQueriesAddQueryForm form,
                                   @SuppressWarnings("unused") BindingResult bindingResult,
                                   ValidationHandler validationHandler,
                                   @ModelAttribute("loggedInUser") UserResource loggedInUser,
                                   HttpServletRequest request,
                                   HttpServletResponse response,
                                   Model model) {
        List<Long> attachments = loadAttachmentsFromCookie(request, projectId, organisationId);
        if (attachments.contains(attachmentId)) {
            attachments.remove(attachments.indexOf(attachmentId));
            financeCheckService.deleteFile(attachmentId);
        }
        saveAttachmentsToCookie(response, attachments, projectId, organisationId);

        FinanceChecksQueriesAddQueryViewModel viewModel = populateQueriesViewModel(projectId, organisationId, querySection, attachments);
        model.addAttribute("model", viewModel);
        model.addAttribute("form", form);
        return "project/financecheck/new-query";
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_QUERIES_SECTION')")
    @RequestMapping(value="/cancel", method = GET)
    public String cancelNewForm(@PathVariable Long projectId,
                                @PathVariable Long organisationId,
                                @RequestParam(value = "query_section", required = false) String querySection,
                                @ModelAttribute("loggedInUser") UserResource loggedInUser,
                                HttpServletRequest request,
                                HttpServletResponse response) {
        List<Long> attachments = loadAttachmentsFromCookie(request, projectId, organisationId);
        attachments.forEach(( id -> financeCheckService.deleteFile(id)));

        cookieUtil.removeCookie(response, getCookieName(projectId, organisationId));

        return redirectToQueryPage(projectId, organisationId, querySection);
    }

    private FinanceChecksQueriesAddQueryViewModel populateQueriesViewModel(Long projectId, Long organisationId, String querySection, List<Long> attachmentFileIds) {

        ProjectResource project = projectService.getById(projectId);

        OrganisationResource organisation = organisationService.getOrganisationById(organisationId);
        OrganisationResource leadOrganisation = projectService.getLeadOrganisation(projectId);
        boolean leadPartnerOrganisation = leadOrganisation.getId().equals(organisation.getId());

        Optional<ProjectUserResource> financeContact = getFinanceContact(projectId, organisationId);

        Map<Long, String> attachmentLinks = new HashMap<>();
        attachmentFileIds.forEach(id -> {
            ServiceResult<AttachmentResource> file = financeCheckService.getAttachment(id);
            if(file.isSuccess()) {
                attachmentLinks.put(id, file.getSuccessObject().name);
            }
        });

        return new FinanceChecksQueriesAddQueryViewModel(
                organisation.getName(),
                leadPartnerOrganisation,
                financeContact.isPresent() ? financeContact.get().getUserName() : UNKNOWN_FIELD,
                financeContact.isPresent() ? financeContact.get().getEmail() : UNKNOWN_FIELD,
                financeContact.isPresent() ? financeContact.get().getPhoneNumber() : UNKNOWN_FIELD,
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

    private String getCookieName(Long projectId, Long organisationId) {
        return ATTACHMENT_COOKIE + "_" + projectId + "_" + organisationId;
    }

    private void saveAttachmentsToCookie(HttpServletResponse response, List<Long> attachmentFileIds, Long projectId, Long organisationId) {
        String jsonState = JsonUtil.getSerializedObject(attachmentFileIds);
        cookieUtil.saveToCookie(response, getCookieName(projectId, organisationId), jsonState);
    }

    private List<Long> loadAttachmentsFromCookie(HttpServletRequest request, Long projectId, Long organisationId) {

        List<Long> attachments = new LinkedList<>();
        String json = cookieUtil.getCookieValue(request, getCookieName(projectId, organisationId));

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
