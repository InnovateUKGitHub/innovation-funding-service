package org.innovateuk.ifs.project.otherdocuments.controller;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.commons.error.exception.ObjectNotFoundException;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.file.controller.viewmodel.FileDetailsViewModel;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.otherdocuments.OtherDocumentsService;
import org.innovateuk.ifs.project.otherdocuments.form.OtherDocumentsForm;
import org.innovateuk.ifs.project.otherdocuments.viewmodel.OtherDocumentsViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.file.controller.FileDownloadControllerUtils.getFileResponseEntity;
import static org.innovateuk.ifs.user.resource.UserRoleType.PROJECT_MANAGER;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;

/**
 * Controller backing the Other Documents page
 */
@Controller
@RequestMapping("/project/{projectId}/partner/documents")
public class OtherDocumentsController {

    private static final String FORM_ATTR = "form";

    @Autowired
    private ProjectService projectService;

    @Autowired
    private OtherDocumentsService otherDocumentsService;

    @Autowired
    private ApplicationService applicationService;

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_OTHER_DOCUMENTS_SECTION')")
    @GetMapping
    public String viewOtherDocumentsPage(Model model, @ModelAttribute(name = FORM_ATTR, binding = false) OtherDocumentsForm form,
                                         @PathVariable("projectId") Long projectId) {
        return doViewOtherDocumentsPage(model, form, projectId);
    }

    private String doViewOtherDocumentsPage(Model model, OtherDocumentsForm form, Long projectId) {

        OtherDocumentsViewModel viewModel = getOtherDocumentsViewModel(projectId);
        model.addAttribute("model", viewModel);
        model.addAttribute(FORM_ATTR, form);
        return "project/other-documents";
    }

    private OtherDocumentsViewModel getOtherDocumentsViewModel(Long projectId) {

        ProjectResource project = projectService.getById(projectId);
        List<OrganisationResource> partnerOrganisations = projectService.getPartnerOrganisationsForProject(projectId);
        boolean collaborationAgreementRequired = partnerOrganisations.size() > 1;

        Optional<FileEntryResource> collaborationAgreement = Optional.empty();
        if (collaborationAgreementRequired) {
            collaborationAgreement = otherDocumentsService.getCollaborationAgreementFileDetails(projectId);
        }
        Optional<FileEntryResource> exploitationPlan = otherDocumentsService.getExploitationPlanFileDetails(projectId);

        OrganisationResource leadPartnerOrganisation = projectService.getLeadOrganisation(projectId);
        ApplicationResource applicationResource = applicationService.getById(project.getApplication());

        Optional<ProjectUserResource> projectManager = getProjectManagerResource(project);
        String projectManagerName = projectManager.map(ProjectUserResource::getUserName).orElse("");
        String projectManagerTelephone = projectManager.map(ProjectUserResource::getPhoneNumber).orElse("");
        String projectManagerEmail = projectManager.map(ProjectUserResource::getEmail).orElse("");

        List<String>partnerOrganisationNames = partnerOrganisations.stream()
                .filter(org -> !org.getId().equals(leadPartnerOrganisation.getId()))
                .map(OrganisationResource::getName)
                .collect(Collectors.toList());

        return new OtherDocumentsViewModel(projectId, applicationResource.getId(), project.getName(), applicationResource.getCompetition(), leadPartnerOrganisation.getName(),
                projectManagerName, projectManagerTelephone, projectManagerEmail,
                collaborationAgreementRequired ? collaborationAgreement.map(FileDetailsViewModel::new).orElse(null) : null,
                exploitationPlan.map(FileDetailsViewModel::new).orElse(null),
                partnerOrganisationNames, project.getOtherDocumentsApproved(), collaborationAgreementRequired);
    }

    private Optional<ProjectUserResource> getProjectManagerResource(ProjectResource project) {
        List<ProjectUserResource> projectUsers = projectService.getProjectUsersForProject(project.getId());
        return simpleFindFirst(projectUsers, pu -> PROJECT_MANAGER.getName().equals(pu.getRoleName()));
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_OTHER_DOCUMENTS_SECTION')")
    @PostMapping
    public String acceptOrRejectOtherDocuments(Model model, @ModelAttribute(FORM_ATTR) OtherDocumentsForm form,
                                               ValidationHandler validationhandler,
                                               @PathVariable("projectId") Long projectId) {

        return validationhandler.performActionOrBindErrorsToField("approved",
                () -> doViewOtherDocumentsPage(model, form, projectId),
                () -> doViewOtherDocumentsPage(model, form, projectId),
                () -> otherDocumentsService.acceptOrRejectOtherDocuments(projectId, form.isApproved()));
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_OTHER_DOCUMENTS_SECTION')")
    @GetMapping("/collaboration-agreement")
    public
    @ResponseBody
    ResponseEntity<ByteArrayResource> downloadCollaborationAgreementFile(
            @PathVariable("projectId") final Long projectId) {

        final Optional<ByteArrayResource> content = otherDocumentsService.getCollaborationAgreementFile(projectId);
        final Optional<FileEntryResource> fileDetails = otherDocumentsService.getCollaborationAgreementFileDetails(projectId);

        return returnFileIfFoundOrThrowNotFoundException(projectId, content, fileDetails);
    }

    private ResponseEntity<ByteArrayResource> returnFileIfFoundOrThrowNotFoundException(Long projectId, Optional<ByteArrayResource> content, Optional<FileEntryResource> fileDetails) {
        if (content.isPresent() && fileDetails.isPresent()) {
            return getFileResponseEntity(content.get(), fileDetails.get());
        } else {
            throw new ObjectNotFoundException("Could not find Collaboration Agreement for project " + projectId, singletonList(projectId));
        }
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_OTHER_DOCUMENTS_SECTION')")
    @GetMapping("/exploitation-plan")
    public
    @ResponseBody
    ResponseEntity<ByteArrayResource> downloadExploitationPlanFile(@PathVariable("projectId") final Long projectId) {

        final Optional<ByteArrayResource> content = otherDocumentsService.getExploitationPlanFile(projectId);
        final Optional<FileEntryResource> fileDetails = otherDocumentsService.getExploitationPlanFileDetails(projectId);
        return returnFileIfFoundOrThrowNotFoundException(projectId, content, fileDetails);
    }
}
