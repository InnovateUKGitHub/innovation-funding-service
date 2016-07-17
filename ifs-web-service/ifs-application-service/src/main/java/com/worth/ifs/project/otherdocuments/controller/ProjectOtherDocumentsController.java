package com.worth.ifs.project.otherdocuments.controller;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.controller.ValidationHandler;
import com.worth.ifs.exception.UnableToReadUploadedFile;
import com.worth.ifs.file.controller.viewmodel.FileDetailsViewModel;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.project.ProjectService;
import com.worth.ifs.project.otherdocuments.form.ProjectOtherDocumentsForm;
import com.worth.ifs.project.otherdocuments.viewmodel.ProjectOtherDocumentsViewModel;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.UserResource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static com.worth.ifs.controller.ErrorToObjectErrorConverterFactory.toField;
import static com.worth.ifs.file.controller.FileDownloadControllerUtils.getFileResponseEntity;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static java.util.Collections.emptyList;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Controller backing the Other Documents page
 */
@Controller
@RequestMapping("/project/{projectId}/other-documents")
public class ProjectOtherDocumentsController {

    private static final Log LOG = LogFactory.getLog(ProjectOtherDocumentsController.class);
    private static final String FORM_ATTR = "form";

    @Autowired
    private ProjectService projectService;

    @RequestMapping(method = GET)
    public String viewOtherDocumentsPage(@PathVariable("projectId") Long projectId, Model model,
                                         @ModelAttribute("loggedInUser") UserResource loggedInUser) {

        ProjectOtherDocumentsViewModel viewModel = getOtherDocumentsViewModel(projectId, loggedInUser);
        model.addAttribute("model", viewModel);
        model.addAttribute("form", new ProjectOtherDocumentsForm());
        return "project/other-documents";
    }

    @RequestMapping(value = "/collaboration-agreement", method = GET)
    public @ResponseBody ResponseEntity<ByteArrayResource> downloadCollaborationAgreementFile(
            @PathVariable("projectId") final Long projectId) {

        // TODO DW - remove these gets()
        final ByteArrayResource resource = projectService.getCollaborationAgreementFile(projectId).get();
        final FileEntryResource fileDetails = projectService.getCollaborationAgreementFileDetails(projectId).get();
        return getFileResponseEntity(resource, fileDetails);
    }

    @RequestMapping(params = "uploadCollaborationAgreementClicked", method = POST)
    public String uploadCollaborationAgreementFile(
            @PathVariable("projectId") final Long projectId,
            @ModelAttribute(FORM_ATTR) ProjectOtherDocumentsForm form,
            @SuppressWarnings("unused") BindingResult bindingResult,
            ValidationHandler validationHandler,
            Model model,
            @ModelAttribute("loggedInUser") UserResource loggedInUser) {

        Supplier<String> failureView = () -> viewOtherDocumentsPage(projectId, model, loggedInUser);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            ServiceResult<FileEntryResource> uploadFileResult = uploadCollaborationAgreementFormInput(projectId, form.getCollaborationAgreement());

            return validationHandler.
                    addAnyErrors(uploadFileResult, toField("collaborationAgreement")).
                    failNowOrSucceedWith(failureView, () -> redirectToOtherDocumentsPage(projectId));
        });
    }

    @RequestMapping(params = "removeCollaborationAgreementClicked", method = POST)
    public String removeCollaborationAgreementFile(@PathVariable("projectId") final Long projectId,
                                             @ModelAttribute(FORM_ATTR) ProjectOtherDocumentsForm form,
                                             @SuppressWarnings("unused") BindingResult bindingResult,
                                             ValidationHandler validationHandler,
                                             Model model,
                                             @ModelAttribute("loggedInUser") UserResource loggedInUser) {

        Supplier<String> failureView = () -> viewOtherDocumentsPage(projectId, model, loggedInUser);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {

            ServiceResult<Void> removeFileResult = projectService.removeCollaborationAgreementDocument(projectId);

            return validationHandler.
                    addAnyErrors(removeFileResult, toField("collaborationAgreement")).
                    failNowOrSucceedWith(failureView, () -> redirectToOtherDocumentsPage(projectId));
        });
    }

    @RequestMapping(value = "/exploitation-plan", method = GET)
    public @ResponseBody ResponseEntity<ByteArrayResource> downloadExploitationPlanFile(
            @PathVariable("projectId") final Long projectId) {

        // TODO DW - remove these gets()
        final ByteArrayResource resource = projectService.getExploitationPlanFile(projectId).get();
        final FileEntryResource fileDetails = projectService.getExploitationPlanFileDetails(projectId).get();
        return getFileResponseEntity(resource, fileDetails);
    }

    @RequestMapping(params = "uploadExploitationPlanClicked", method = POST)
    public String uploadExploitationPlanFile(
            @PathVariable("projectId") final Long projectId,
            @ModelAttribute(FORM_ATTR) ProjectOtherDocumentsForm form,
            @SuppressWarnings("unused") BindingResult bindingResult,
            ValidationHandler validationHandler,
            Model model,
            @ModelAttribute("loggedInUser") UserResource loggedInUser) {

        Supplier<String> failureView = () -> viewOtherDocumentsPage(projectId, model, loggedInUser);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            ServiceResult<FileEntryResource> uploadFileResult = uploadExploitationPlanFormInput(projectId, form.getExploitationPlan());

            return validationHandler.
                    addAnyErrors(uploadFileResult, toField("exploitationPlan")).
                    failNowOrSucceedWith(failureView, () -> redirectToOtherDocumentsPage(projectId));
        });
    }

    @RequestMapping(params = "removeExploitationPlanClicked", method = POST)
    public String removeExploitationPlanFile(@PathVariable("projectId") final Long projectId,
                                             @ModelAttribute(FORM_ATTR) ProjectOtherDocumentsForm form,
                                             @SuppressWarnings("unused") BindingResult bindingResult,
                                             ValidationHandler validationHandler,
                                             Model model,
                                             @ModelAttribute("loggedInUser") UserResource loggedInUser) {

        Supplier<String> failureView = () -> viewOtherDocumentsPage(projectId, model, loggedInUser);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {

            ServiceResult<Void> removeFileResult = projectService.removeExploitationPlanDocument(projectId);

            return validationHandler.
                    addAnyErrors(removeFileResult, toField("exploitationPlan")).
                    failNowOrSucceedWith(failureView, () -> redirectToOtherDocumentsPage(projectId));
        });
    }

    private ServiceResult<FileEntryResource> uploadCollaborationAgreementFormInput(Long projectId, MultipartFile file) {

        try {

            return projectService.addCollaborationAgreementDocument(projectId,
                    file.getContentType(), file.getSize(), file.getOriginalFilename(), file.getBytes());

        } catch (IOException e) {
            LOG.error(e);
            throw new UnableToReadUploadedFile();
        }
    }

    // TODO DW = refactor!
    private ServiceResult<FileEntryResource> uploadExploitationPlanFormInput(Long projectId, MultipartFile file) {

        try {

            return projectService.addExploitationPlanDocument(projectId,
                    file.getContentType(), file.getSize(), file.getOriginalFilename(), file.getBytes());

        } catch (IOException e) {
            LOG.error(e);
            throw new UnableToReadUploadedFile();
        }
    }

    private ProjectOtherDocumentsViewModel getOtherDocumentsViewModel(Long projectId, UserResource loggedInUser) {

        ProjectResource project = projectService.getById(projectId);
        Optional<FileEntryResource> collaborationAgreement = projectService.getCollaborationAgreementFileDetails(projectId);
        Optional<FileEntryResource> exploitationPlan = projectService.getExploitationPlanFileDetails(projectId);
        List<OrganisationResource> partnerOrganisations = projectService.getPartnerOrganisationsForProject(projectId);

        List<String> partnerOrganisationNames = simpleMap(partnerOrganisations, OrganisationResource::getName);

        boolean leadPartner = projectService.isUserLeadPartner(projectId, loggedInUser.getId());

        // TODO DW - these rejection messages to be covered in other stories
        List<String> rejectionReasons = emptyList();

        // TODO DW - these flags to be covered in other stories
        boolean otherDocumentsSubmitted = false;
        boolean otherDocumentsApproved = false;

        return new ProjectOtherDocumentsViewModel(projectId, project.getName(),
                collaborationAgreement.map(FileDetailsViewModel::new).orElse(null),
                exploitationPlan.map(FileDetailsViewModel::new).orElse(null),
                partnerOrganisationNames, rejectionReasons,
                leadPartner, otherDocumentsSubmitted, otherDocumentsApproved
        );
    }

    private String redirectToOtherDocumentsPage(Long projectId) {
        return "redirect:/project/" + projectId + "/other-documents";
    }
}
