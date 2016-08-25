package com.worth.ifs.project.otherdocuments.controller;

import com.worth.ifs.commons.error.exception.ObjectNotFoundException;
import com.worth.ifs.file.controller.viewmodel.FileDetailsViewModel;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.project.ProjectService;
import com.worth.ifs.project.otherdocuments.form.ProjectOtherDocumentsForm;
import com.worth.ifs.project.otherdocuments.viewmodel.ProjectOtherDocumentsViewModel;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.resource.ProjectUserResource;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.worth.ifs.file.controller.FileDownloadControllerUtils.getFileResponseEntity;
import static com.worth.ifs.user.resource.UserRoleType.PROJECT_MANAGER;
import static com.worth.ifs.util.CollectionFunctions.simpleFindFirst;
import static java.util.Collections.singletonList;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Controller backing the Other Documents page
 */
@Controller
@RequestMapping("/project/{projectId}/partner/documents")
public class ProjectOtherDocumentsController {

    private static final String FORM_ATTR = "form";

    @Autowired
    private ProjectService projectService;


    private void checkInCorrectStateToAdministerOtherDocumentsPage(Long projectId) {
        ProjectResource project = projectService.getById(projectId);

        //check what test should be made (same test that sets the dashboard flag ?)
//        if (!project.isProjectDetailsSubmitted()) {
//            throw new ForbiddenActionException("This project is not ready for documentation checking.  Not all the documents have been submitted");
//        }
    }


    @RequestMapping(method = GET)
    public String viewOtherDocumentsPage(Model model, @PathVariable("projectId") Long projectId,
                                         @ModelAttribute("loggedInUser") UserResource loggedInUser) {

        checkInCorrectStateToAdministerOtherDocumentsPage(projectId);

        return doViewOtherDocumentsPage(model, null, projectId, loggedInUser);

    }

    @RequestMapping(method = POST)
    public String acceptOrRejectOtherDocuments(Model model, @ModelAttribute(FORM_ATTR) ProjectOtherDocumentsForm form,
                                               @PathVariable("projectId") Long projectId,
                                               //  @ModelAttribute("observations") String  rejectionReason,
                                               @ModelAttribute("loggedInUser") UserResource loggedInUser) {

        checkInCorrectStateToAdministerOtherDocumentsPage(projectId);

        //TODO:  Check for cancelled action ?
        boolean rejected = form.isRejected();
        boolean approved = form.isApproved();
        List<String> rejectionReasons = form.getRejectionReasons();

        //TODO: Add these attributes to the project / project documents object when we have created it


        return doViewOtherDocumentsPage(model, form, projectId, loggedInUser);

    }

    @RequestMapping(value = "/collaboration-agreement", method = GET)
    public
    @ResponseBody
    ResponseEntity<ByteArrayResource> downloadCollaborationAgreementFile(
            @PathVariable("projectId") final Long projectId) {

        final Optional<ByteArrayResource> content = projectService.getCollaborationAgreementFile(projectId);
        final Optional<FileEntryResource> fileDetails = projectService.getCollaborationAgreementFileDetails(projectId);

        return returnFileIfFoundOrThrowNotFoundException(projectId, content, fileDetails);
    }

    @RequestMapping(value = "/exploitation-plan", method = GET)
    public
    @ResponseBody
    ResponseEntity<ByteArrayResource> downloadExploitationPlanFile(
            @PathVariable("projectId") final Long projectId) {

        final Optional<ByteArrayResource> content = projectService.getExploitationPlanFile(projectId);
        final Optional<FileEntryResource> fileDetails = projectService.getExploitationPlanFileDetails(projectId);
        return returnFileIfFoundOrThrowNotFoundException(projectId, content, fileDetails);
    }

    private String doViewOtherDocumentsPage(Model model, ProjectOtherDocumentsForm form, Long projectId, UserResource loggedInUser) {

        ProjectOtherDocumentsViewModel viewModel = getOtherDocumentsViewModel(form, projectId, loggedInUser);
        model.addAttribute("model", viewModel);

        if (viewModel.isApproved()) {
            return "project/other-documents-approved";
        } else if (viewModel.isRejected()) {
            return "project/other-documents-rejected";
        } else {
            form = new ProjectOtherDocumentsForm();
            model.addAttribute("form", form);
            return "project/other-documents-review";
        }
    }

    private ProjectOtherDocumentsViewModel getOtherDocumentsViewModel(ProjectOtherDocumentsForm form, Long projectId, UserResource loggedInUser) {

        ProjectResource project = projectService.getById(projectId);
        Optional<FileEntryResource> collaborationAgreement = projectService.getCollaborationAgreementFileDetails(projectId);
        Optional<FileEntryResource> exploitationPlan = projectService.getExploitationPlanFileDetails(projectId);

        String leadPartnerOrganisationName = projectService.getLeadOrganisation(projectId).getName();

        Optional<ProjectUserResource> projectManager = getProjectManagerResource(project);
        String projectManagerName = projectManager.map(ProjectUserResource::getUserName).orElse("");
        String projectManagerTelephone = projectManager.map(ProjectUserResource::getPhoneNumber).orElse("");
        String projectManagerEmail = projectManager.map(ProjectUserResource::getEmail).orElse("");

        List<String> partnerOrganisationNames = projectService.getPartnerOrganisationsForProject(projectId).stream()
                .map(OrganisationResource::getName)
                .filter(s -> s != leadPartnerOrganisationName)
                .collect(Collectors.toList());

        return new ProjectOtherDocumentsViewModel(projectId, project.getName(), leadPartnerOrganisationName,
                projectManagerName, projectManagerTelephone, projectManagerEmail,
                collaborationAgreement.map(FileDetailsViewModel::new).orElse(null),
                exploitationPlan.map(FileDetailsViewModel::new).orElse(null),
                partnerOrganisationNames, form != null && form.isApproved(),
                form != null && form.isRejected(), form != null ? form.getRejectionReasons() : null);
    }

    private ResponseEntity<ByteArrayResource> returnFileIfFoundOrThrowNotFoundException(Long projectId, Optional<ByteArrayResource> content, Optional<FileEntryResource> fileDetails) {
        if (content.isPresent() && fileDetails.isPresent()) {
            return getFileResponseEntity(content.get(), fileDetails.get());
        } else {
            throw new ObjectNotFoundException("Could not find Collaboration Agreement for project " + projectId, singletonList(projectId));
        }
    }

    private String redirectToOtherDocumentsPage(Long projectId) {
        return "redirect:/project/" + projectId + "/partner/documents";
    }


    private Optional<ProjectUserResource> getProjectManagerResource(ProjectResource project) {
        List<ProjectUserResource> projectUsers = projectService.getProjectUsersForProject(project.getId());
        Optional<ProjectUserResource> projectManager = simpleFindFirst(projectUsers, pu -> PROJECT_MANAGER.getName().equals(pu.getRoleName()));

        return projectManager;
    }

}
