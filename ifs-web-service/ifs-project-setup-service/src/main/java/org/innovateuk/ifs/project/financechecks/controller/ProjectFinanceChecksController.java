package org.innovateuk.ifs.project.financechecks.controller;

import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.notesandqueries.resource.post.PostAttachmentResource;
import org.innovateuk.ifs.notesandqueries.resource.post.PostResource;
import org.innovateuk.ifs.notesandqueries.resource.thread.FinanceChecksSectionType;
import org.innovateuk.ifs.notesandqueries.resource.thread.ThreadResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.financechecks.form.FinanceChecksQueryResponseForm;
import org.innovateuk.ifs.project.financechecks.viewmodel.ProjectFinanceChecksViewModel;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.project.resource.ProjectPartnerStatusResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.thread.viewmodel.ThreadPostAttachmentResourceViewModel;
import org.innovateuk.ifs.thread.viewmodel.ThreadPostViewModel;
import org.innovateuk.ifs.thread.viewmodel.ThreadViewModel;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.project.constant.ProjectActivityStates.COMPLETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * This controller will handle requests related to finance checks
 */
@Controller
@RequestMapping("/" + ProjectFinanceChecksController.BASE_DIR + "/{projectId}/partner-organisation/{organisationId}/finance-checks")
public class ProjectFinanceChecksController {

    public static final String BASE_DIR = "project";

    @Autowired
    ProjectService projectService;

    @Autowired
    OrganisationService organisationService;

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_SECTION_EXTERNAL')")
    @RequestMapping(method = GET)
    public String viewFinanceChecks(Model model,
                                    @PathVariable("projectId") final Long projectId,
                                    @PathVariable("organisationId") final Long organisationId,
                                    @ModelAttribute("loggedInUser") UserResource loggedInUser) {

        ProjectOrganisationCompositeId projectComposite = new ProjectOrganisationCompositeId(projectId, organisationId);

        model.addAttribute("model", buildFinanceChecksLandingPage(projectComposite, loggedInUser));

        return "project/finance-checks";
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_SECTION_EXTERNAL')")
    @PostMapping("/response")
    public String respondToQuery(Model model,
                                 FinanceChecksQueryResponseForm form,
                                 @PathVariable("projectId") final long projectId,
                                 @PathVariable("organisationId") final Long organisationId) {
        ProjectOrganisationCompositeId projectComposite = new ProjectOrganisationCompositeId(projectId, organisationId);
        //Call to core service to add post to query.
        return null;
    }

    private ProjectFinanceChecksViewModel buildFinanceChecksLandingPage(final ProjectOrganisationCompositeId compositeId, final UserResource loggedInUser) {
        ProjectResource projectResource = projectService.getById(compositeId.getProjectId());
        OrganisationResource organisationResource = organisationService.getOrganisationById(compositeId.getOrganisationId());
        List<ThreadViewModel> queriesViewModel = buildThreadViewModel(getQueries(compositeId));
        boolean approved = isApproved(compositeId);

        return new ProjectFinanceChecksViewModel(projectResource, organisationResource, queriesViewModel, approved);
    }

    private boolean isApproved(final ProjectOrganisationCompositeId compositeId) {
        Optional<ProjectPartnerStatusResource> organisationStatus = projectService.getProjectTeamStatus(compositeId.getProjectId(), Optional.empty()).getPartnerStatusForOrganisation(compositeId.getOrganisationId());
        return COMPLETE.equals(organisationStatus.get().getFinanceChecksStatus());
    }

    private List<ThreadViewModel> buildThreadViewModel(Optional<List<ThreadResource>> queries) {
        return Collections.emptyList();
    }

    private Optional<List<ThreadResource>> getQueries(final ProjectOrganisationCompositeId compositeId) {
        //service call to get queries for project_finance
        return Optional.empty();
    }
}
