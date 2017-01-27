package org.innovateuk.ifs.project.queries.controller;

import org.innovateuk.ifs.application.service.OrganisationService;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;


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
    public Map<Integer, String> getEmptyAttachments() {
        return new HashMap<Integer, String>();
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_SECTION')")
    @RequestMapping(method = GET)
    public String viewQuery(@PathVariable Long projectId,
                            @PathVariable Long organisationId,
                            @RequestParam(value = "query_section", required = false) String querySection,
                            Model model,
                            @ModelAttribute("loggedInUser") UserResource loggedInUser,
                            @ModelAttribute(POST_ATTACHMENTS) Map<Integer, String> attachments,
                            SessionStatus status) {
        status.setComplete(); // clear postAttachments
        FinanceChecksQueriesForm form = new FinanceChecksQueriesForm();
        return doViewQueries(projectId, organisationId, model, form, false, querySection);
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_SECTION')")
    @RequestMapping(value="new-query", method = GET)
    public String viewNewQuery(@PathVariable Long projectId,
                               @PathVariable Long organisationId,
                               @RequestParam(value = "query_section", required = false) String querySection,
                               Model model,
                               @ModelAttribute("loggedInUser") UserResource loggedInUser,
                               @ModelAttribute(POST_ATTACHMENTS) Map<Integer, String> attachments,
                               SessionStatus status) {
        status.setComplete(); // clear postAttachments
        FinanceChecksQueriesForm form = new FinanceChecksQueriesForm();
        return doViewQueries(projectId, organisationId, model, form, true, querySection);
    }


    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_SECTION')")
    @RequestMapping(params = "uploadAttachment", method = POST)
    public String uploadQueryAttachment(@PathVariable("projectId") final Long projectId,
                                        @PathVariable Long organisationId,
                                        @RequestParam(value = "query_section", required = false) String querySection,
                                        @ModelAttribute(FORM_ATTR) FinanceChecksQueriesForm form,
                                        @ModelAttribute(POST_ATTACHMENTS) Map<Integer, String> attachments,
                                        @SuppressWarnings("unused") BindingResult bindingResult,
                                        ValidationHandler validationHandler,
                                        Model model)
    {
        return performActionOrBindErrorsToField(projectId, organisationId, validationHandler, model, "attachment", form, querySection, () -> {

            MultipartFile file = form.getAttachment();
            //return projectService.addGrantOfferLetter(projectId, file.getContentType(), file.getSize(),
            //        file.getOriginalFilename(), getMultipartFileBytes(file));

            // save file entry ID in model
            attachments.put(1, "dummy_file.txt");

            return ServiceResult.serviceSuccess();
        });
    }
    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_SECTION')")
    @RequestMapping(value="save-new-query", method = POST)
    public String postQuery(@PathVariable("projectId") final Long projectId,
                            @PathVariable Long organisationId,
                            @RequestParam(value = "query_section", required = false) String querySection,
                            @RequestParam(value = "uploadAttachment", required = false) String uploadAttachment,
                            @ModelAttribute(FORM_ATTR) FinanceChecksQueriesForm form,
                            @ModelAttribute(POST_ATTACHMENTS) Map<Integer, String> attachments,
                            @SuppressWarnings("unused") BindingResult bindingResult,
                            ValidationHandler validationHandler,
                            Model model)
    {
        Supplier<String> failureView = () -> doViewQueries(projectId, organisationId, model, form, true, querySection);

        if (uploadAttachment != null) {
            return performActionOrBindErrorsToField(projectId, organisationId, validationHandler, model, "attachment", form, querySection, () -> {

                MultipartFile file = form.getAttachment();
                //return projectService.addGrantOfferLetter(projectId, file.getContentType(), file.getSize(),
                //        file.getOriginalFilename(), getMultipartFileBytes(file));

                // save file entry ID in model
                attachments.put(1, "dummy_file.txt");

                return ServiceResult.serviceSuccess();
            });
        } else {
            // get file entry IDs from model
            //attachments.forEach();
            return validationHandler.failNowOrSucceedWith(failureView, () -> {
                ServiceResult<Void> updateResult = ServiceResult.serviceSuccess();
                return validationHandler.addAnyErrors(updateResult, fieldErrorsToFieldErrors(), asGlobalErrors())
                        .failNowOrSucceedWith(failureView, () -> redirectToQueryPage(projectId, organisationId, querySection));
            });
        }
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_SECTION')")
    @RequestMapping(value = "new-query/attachment/{attachmentId}", method = GET)
    @ResponseBody
    ResponseEntity<ByteArrayResource> viewDownloadNewQueryAttachment(@PathVariable Long projectId,
                                                                     @PathVariable Long organisationId,
                                                                     @PathVariable Long attachmentId,
                                                                     @RequestParam(value = "query_section", required = false) String querySection,
                                                                     Model model,
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
    private String performActionOrBindErrorsToField(Long projectId, Long organisationId, ValidationHandler validationHandler, Model model, String fieldName, FinanceChecksQueriesForm form, String querySection, Supplier<FailingOrSucceedingResult<?, ?>> actionFn) {

        Supplier<String> successView = () -> redirectToQueryPage(projectId, organisationId, querySection);
        Supplier<String> failureView = () -> doViewQueries(projectId, organisationId, model, form, true, querySection);

        return validationHandler.performActionOrBindErrorsToField(fieldName, failureView, successView, actionFn);
    }

    private String doViewQueries(Long projectId, Long organisationId, Model model, FinanceChecksQueriesForm form, boolean showNewQuery, String querySection) {
        Map<Long, String> attachments = new HashMap<>();
        if (model.asMap().containsKey("attachments")) {
            attachments = (Map<Long, String>) model.asMap().get("attachments");
        }
        FinanceChecksQueriesViewModel viewModel = populateQueriesViewModel(projectId, organisationId, showNewQuery, querySection, attachments);

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
