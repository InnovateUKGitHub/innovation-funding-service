package org.innovateuk.ifs.project.queries.controller;

import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.service.FailingOrSucceedingResult;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.queries.form.FinanceChecksQueriesForm;
import org.innovateuk.ifs.project.queries.viewmodel.FinanceChecksQueriesViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;
import static org.innovateuk.ifs.file.controller.FileDownloadControllerUtils.getFileResponseEntity;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * This Controller handles finance check queries activity for the finance team members
 */
@Controller
@RequestMapping("/project/{projectId}/finance-check/organisation/{organisationId}/queries")
@SessionAttributes(FinanceChecksQueriesController.POST_ATTACHMENTS)
public class FinanceChecksQueriesController {

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private ProjectService projectService;

    private static final String FORM_ATTR = "form";
    public static final String POST_ATTACHMENTS = "postAttachments";

    @ModelAttribute(POST_ATTACHMENTS)
    public Map<Long, String> getEmptyAttachments() {
        return new HashMap<Long, String>();
    }

    private Map<Long, String> localAttachments;

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_SECTION')")
    @RequestMapping(method = GET)
    public String viewQuery(@PathVariable Long projectId,
                            @PathVariable Long organisationId,
                            @RequestParam(value = "query_section", required = false) String querySection,
                            Model model,
                            @ModelAttribute("loggedInUser") UserResource loggedInUser,
                            @ModelAttribute(POST_ATTACHMENTS) Map<Long, String> attachments,
                            SessionStatus status) {
        status.setComplete(); // clear postAttachments
        FinanceChecksQueriesForm form = new FinanceChecksQueriesForm();
        return doViewQueries(projectId, organisationId, model, form, false, querySection, attachments);
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_SECTION')")
    @RequestMapping(value="/new-query", method = GET)
    public String viewNewQuery(@PathVariable Long projectId,
                               @PathVariable Long organisationId,
                               @RequestParam(value = "query_section", required = false) String querySection,
                               Model model,
                               @ModelAttribute("loggedInUser") UserResource loggedInUser,
                               @ModelAttribute(POST_ATTACHMENTS) Map<Long, String> attachments,
                               SessionStatus status) {
        status.setComplete(); // clear postAttachments
        FinanceChecksQueriesForm form = new FinanceChecksQueriesForm();
        return doViewQueries(projectId, organisationId, model, form, true, querySection, attachments);
    }

    @RequestMapping(value="/save-new-query", method = POST, params = "uploadAttachment")
    public String saveNewQueryAttachment(Model model,
                            @PathVariable("projectId") final Long projectId,
                            @PathVariable Long organisationId,
                            @RequestParam(value = "query_section", required = false) String querySection,
                            @ModelAttribute(POST_ATTACHMENTS) Map<Long, String> attachments,
                            @ModelAttribute(FORM_ATTR) FinanceChecksQueriesForm form,
                            @SuppressWarnings("unused") BindingResult bindingResult,
                            ValidationHandler validationHandler) {
        MultipartFile file = form.getAttachment();

        return validationHandler.performActionOrBindErrorsToField("attachment",
                () -> doViewQueries(projectId, organisationId, model, form, true, querySection, attachments),
                () -> doViewQueriesLocalAttachments(projectId, organisationId, model, form, true, querySection),
                () -> {
                    if(file == null) {
                        return ServiceResult.serviceFailure(CommonFailureKeys.FINANCE_CHECKS_POST_ATTACH_NOT_UPLOADED);
                    }
                    //validationHandler.addAnyErrors(projectService.addGrantOfferLetter(projectId, file.getContentType(), file.getSize(),
                    //        file.getOriginalFilename(), getMultipartFileBytes(file)));

                    // save file entry ID in model
                    attachments.put(1L, "dummy_file1.txt");
                    localAttachments = attachments;
                    return ServiceResult.serviceSuccess();
                    });
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_SECTION')")
    @RequestMapping(value="/save-new-query", method = POST)
    public String postQuery(Model model,
                            @PathVariable("projectId") final Long projectId,
                            @PathVariable Long organisationId,
                            @RequestParam(value = "query_section", required = false) String querySection,
                            @ModelAttribute(POST_ATTACHMENTS) Map<Long, String> attachments,
                            @Valid @ModelAttribute(FORM_ATTR) FinanceChecksQueriesForm form,
                            @SuppressWarnings("unused") BindingResult bindingResult,
                            ValidationHandler validationHandler)
    {
        Supplier<String> failureView = () -> doViewQueries(projectId, organisationId, model, form, true, querySection, attachments);
        Supplier<String> successView = () -> doViewQueries(projectId, organisationId, model, form, false, querySection, attachments);

        return validationHandler.performActionOrBindErrorsToField("queryForm", failureView, successView, () -> {
            // get file entry IDs from model
            //attachments.forEach();
        /*return validationHandler.failNowOrSucceedWith(view, () -> {
            ServiceResult<Void> updateResult = ServiceResult.serviceSuccess();
            return validationHandler.addAnyErrors(updateResult, fieldErrorsToFieldErrors(), asGlobalErrors())
                    .failNowOrSucceedWith(view, () -> redirectToQueryPage(projectId, organisationId, querySection));
        });*/
             return ServiceResult.serviceSuccess();
        });
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_SECTION')")
    @RequestMapping(value = "/new-query/attachment/{attachmentId}", method = GET)
    public
    @ResponseBody
    ResponseEntity<ByteArrayResource> viewDownloadNewQueryAttachment(@PathVariable Long projectId,
                                                                     @PathVariable Long organisationId,
                                                                     @PathVariable Long attachmentId,
                                                                     @ModelAttribute("loggedInUser") UserResource loggedInUser,
                                                                     @ModelAttribute(POST_ATTACHMENTS) Map<Integer, String> attachments) {
        final Optional<ByteArrayResource> content = Optional.empty();//projectService.getSignedGrantOfferLetterFile(projectId);
        final Optional<FileEntryResource> fileDetails = Optional.empty(); //projectService.getSignedGrantOfferLetterFileDetails(projectId);
        if (attachments.containsKey(attachmentId)) {
            attachments.get(attachmentId);
            //fileDetails = Optional.of();
        }

        return returnFileIfFoundOrThrowNotFoundException(content, fileDetails);
    }
    private String performActionOrBindErrorsToField(Long projectId, Long organisationId, ValidationHandler validationHandler, Model model, String fieldName, FinanceChecksQueriesForm form, String querySection, Supplier<FailingOrSucceedingResult<?, ?>> actionFn, Map<Long, String> attachments) {

        Supplier<String> successView = () -> redirectToQueryPage(projectId, organisationId, querySection);
        Supplier<String> failureView = () -> doViewQueries(projectId, organisationId, model, form, true, querySection, attachments);

        return validationHandler.performActionOrBindErrorsToField(fieldName, failureView, successView, actionFn);
    }

    private String doViewQueries(Long projectId, Long organisationId, Model model, FinanceChecksQueriesForm form, boolean showNewQuery, String querySection, Map<Long, String> attachments) {

        FinanceChecksQueriesViewModel viewModel = populateQueriesViewModel(projectId, organisationId, showNewQuery, querySection, attachments);

        model.addAttribute("model", viewModel);
        model.addAttribute(FORM_ATTR, form);

        return "project/financecheck/queries";
    }

    private String doViewQueriesLocalAttachments(Long projectId, Long organisationId, Model model, FinanceChecksQueriesForm form, boolean showNewQuery, String querySection) {

        FinanceChecksQueriesViewModel viewModel = populateQueriesViewModel(projectId, organisationId, showNewQuery, querySection, localAttachments);

        model.addAttribute("model", viewModel);
        model.addAttribute(FORM_ATTR, form);

        return "project/financecheck/queries";
    }

    private FinanceChecksQueriesViewModel populateQueriesViewModel(Long projectId, Long organisationId, boolean showNewQuery, String querySection, Map<Long, String> attachments) {

        ProjectResource project = projectService.getById(projectId);

        OrganisationResource organisation = organisationService.getOrganisationById(organisationId);
        OrganisationResource leadOrganisation = projectService.getLeadOrganisation(projectId);
        boolean leadPartnerOrganisation = leadOrganisation.getId().equals(organisation.getId());

        Optional<ProjectUserResource> financeContact = getFinanceContact(projectId, organisationId);

        return new FinanceChecksQueriesViewModel(
                organisation.getName(),
                leadPartnerOrganisation,
                financeContact.isPresent() ? financeContact.get().getUserName() : "unknown",
                financeContact.isPresent() ? financeContact.get().getEmail() : "unknown",
                financeContact.isPresent() ? financeContact.get().getPhoneNumber() : "unknown",
                showNewQuery,
                querySection == null ? "unknown" : querySection,
                project.getId(),
                project.getName(),
                attachments,
                FinanceChecksQueriesForm.MAX_QUERY_WORDS,
                FinanceChecksQueriesForm.MAX_QUERY_CHARACTERS,
                FinanceChecksQueriesForm.MAX_TITLE_CHARACTERS
        );
    }

    private Optional<ProjectUserResource> getFinanceContact(Long projectId, Long organisationId){
        List<ProjectUserResource> projectUsers = projectService.getProjectUsersForProject(projectId);
        return simpleFindFirst(projectUsers, pr -> pr.isFinanceContact() && organisationId.equals(pr.getOrganisation()));
    }

    private String redirectToQueryPage(Long projectId, Long organisationId, String querySection) {
        return "redirect:/project/" + projectId + "/finance-check/organisation/" + organisationId + "/queries?query_section=" + querySection;
    }

    private ResponseEntity<ByteArrayResource> returnFileIfFoundOrThrowNotFoundException(Optional<ByteArrayResource> content, Optional<FileEntryResource> fileDetails) {
        if (content.isPresent() && fileDetails.isPresent()) {
            return getFileResponseEntity(content.get(), fileDetails.get());
        } else {
            return new ResponseEntity<>(null, null, HttpStatus.NO_CONTENT);
        }
    }
}
