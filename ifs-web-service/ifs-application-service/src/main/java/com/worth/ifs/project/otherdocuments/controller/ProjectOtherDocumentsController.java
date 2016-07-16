package com.worth.ifs.project.otherdocuments.controller;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.controller.ValidationHandler;
import com.worth.ifs.exception.UnableToReadUploadedFile;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.project.ProjectService;
import com.worth.ifs.project.otherdocuments.form.ProjectOtherDocumentsForm;
import com.worth.ifs.project.otherdocuments.viewmodel.ProjectOtherDocumentsViewModel;
import com.worth.ifs.project.resource.ProjectResource;
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

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.function.Supplier;

import static com.worth.ifs.controller.ErrorToObjectErrorConverterFactory.toField;
import static com.worth.ifs.file.controller.FileDownloadControllerUtils.getFileResponseEntity;
import static java.util.Arrays.asList;
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
    public String viewOtherDocumentsPage(@PathVariable("projectId") Long projectId, Model model) {

        ProjectOtherDocumentsViewModel viewModel = getOtherDocumentsViewModel(projectId);
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

    @RequestMapping(value = "/exploitation-plan", method = GET)
    public @ResponseBody ResponseEntity<ByteArrayResource> downloadExploitationPlanFile(
            @PathVariable("projectId") final Long projectId) {

        // TODO DW - remove these gets()
        final ByteArrayResource resource = projectService.getExploitationPlanFile(projectId).get();
        final FileEntryResource fileDetails = projectService.getExploitationPlanFileDetails(projectId).get();
        return getFileResponseEntity(resource, fileDetails);
    }

    @RequestMapping(params = "uploadCollaborationAgreementClicked", method = POST)
    public String uploadCollaborationAgreementFile(
            @PathVariable("projectId") final Long projectId,
            @ModelAttribute(FORM_ATTR) ProjectOtherDocumentsForm form,
            @SuppressWarnings("unused") BindingResult bindingResult,
            ValidationHandler validationHandler,
            Model model, HttpServletRequest request) {

        Supplier<String> failureView = () -> viewOtherDocumentsPage(projectId, model);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            ServiceResult<FileEntryResource> uploadFileResult = uploadFormInput(projectId, form.getCollaborationAgreement());

            return validationHandler.
                    addAnyErrors(uploadFileResult, toField("collaborationAgreement")).
                    failNowOrSucceedWith(failureView, () -> redirectToOtherDocumentsPage(projectId));
        });
    }

    @RequestMapping(params = "removeCollaborationAgreementClicked", method = POST)
    public String removeAssessorFeedbackFile(@PathVariable("projectId") final Long projectId,
                                             @ModelAttribute(FORM_ATTR) ProjectOtherDocumentsForm applicationForm,
                                             @SuppressWarnings("unused") BindingResult bindingResult,
                                             ValidationHandler validationHandler,
                                             Model model) {

        Supplier<String> failureView = () -> viewOtherDocumentsPage(projectId, model);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {

            ServiceResult<Void> removeFileResult = projectService.removeCollaborationAgreementDocument(projectId);

            return validationHandler.
                    addAnyErrors(removeFileResult, toField("collaborationAgreement")).
                    failNowOrSucceedWith(failureView, () -> redirectToOtherDocumentsPage(projectId));
        });
    }

    private ServiceResult<FileEntryResource> uploadFormInput(Long projectId, MultipartFile file) {

        try {

            return projectService.addCollaborationAgreementDocument(projectId,
                    file.getContentType(), file.getSize(), file.getOriginalFilename(), file.getBytes());

        } catch (IOException e) {
            LOG.error(e);
            throw new UnableToReadUploadedFile();
        }
    }

    private ProjectOtherDocumentsViewModel getOtherDocumentsViewModel(Long projectId) {

        ProjectResource project = projectService.getById(projectId);
        return new ProjectOtherDocumentsViewModel(projectId, project.getName(),
                null, null,
                asList("Partner Org 1", "Partner Org 2", "Partner Org 3"), asList("No documents for you!"), true, false, false
        );
    }

    private String redirectToOtherDocumentsPage(Long projectId) {
        return "redirect:/project/" + projectId + "/other-documents";
    }
}
