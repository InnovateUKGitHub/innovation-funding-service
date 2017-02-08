package org.innovateuk.ifs.project.financechecks.controller;

import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.finance.ProjectFinanceService;
import org.innovateuk.ifs.project.financecheck.FinanceCheckService;
import org.innovateuk.ifs.project.financechecks.form.FinanceChecksQueryConstraints;
import org.innovateuk.ifs.project.financechecks.form.FinanceChecksQueryResponseForm;
import org.innovateuk.ifs.project.financechecks.viewmodel.ProjectFinanceChecksViewModel;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.project.resource.ProjectPartnerStatusResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.thread.viewmodel.ThreadPostViewModel;
import org.innovateuk.ifs.thread.viewmodel.ThreadViewModel;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.innovateuk.threads.resource.PostResource;
import org.innovateuk.threads.resource.QueryResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.project.constant.ProjectActivityStates.COMPLETE;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;
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
    ProjectFinanceService projectFinanceService;

    @Autowired
    FinanceCheckService financeCheckService;

    @Autowired
    OrganisationService organisationService;

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_SECTION_EXTERNAL')")
    @RequestMapping(method = GET)
    public String viewFinanceChecks(Model model,
                                    @PathVariable("projectId") final Long projectId,
                                    @PathVariable("organisationId") final Long organisationId,
                                    @ModelAttribute("loggedInUser") UserResource loggedInUser) {

        ProjectOrganisationCompositeId projectComposite = new ProjectOrganisationCompositeId(projectId, organisationId);

        model.addAttribute("model", buildFinanceChecksLandingPage(projectComposite, loggedInUser, null, null));

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

    private ProjectFinanceChecksViewModel buildFinanceChecksLandingPage(final ProjectOrganisationCompositeId compositeId, final UserResource loggedInUser, List<Long> attachments, Long queryId) {
        ProjectResource projectResource = projectService.getById(compositeId.getProjectId());
        OrganisationResource organisationResource = organisationService.getOrganisationById(compositeId.getOrganisationId());
        OrganisationResource organisation = organisationService.getOrganisationById(compositeId.getOrganisationId());

        Optional<ProjectUserResource> financeContact = getFinanceContact(compositeId.getProjectId(), compositeId.getOrganisationId());

        Map<Long, String> attachmentLinks = new HashMap<>();
        if(attachments != null) {
            attachments.forEach(id -> {
                ServiceResult<FileEntryResource> file = financeCheckService.getFileInfo(id);
                if (file.isSuccess()) {
                    attachmentLinks.put(id, file.getSuccessObject().getName());
                }
            });
        }

        boolean approved = isApproved(compositeId);

        return new ProjectFinanceChecksViewModel(projectResource,
                organisationResource,
                loadQueryModel(compositeId.getProjectId(),compositeId.getOrganisationId(),loggedInUser),
                approved,
                attachmentLinks,
                FinanceChecksQueryConstraints.MAX_QUERY_WORDS,
                FinanceChecksQueryConstraints.MAX_QUERY_CHARACTERS,
                queryId);
    }

    private boolean isApproved(final ProjectOrganisationCompositeId compositeId) {
        Optional<ProjectPartnerStatusResource> organisationStatus = projectService.getProjectTeamStatus(compositeId.getProjectId(), Optional.empty()).getPartnerStatusForOrganisation(compositeId.getOrganisationId());
        return COMPLETE.equals(organisationStatus.map(ProjectPartnerStatusResource::getFinanceChecksStatus).orElse(null));
    }

    private Optional<ProjectUserResource> getFinanceContact(Long projectId, Long organisationId){
        List<ProjectUserResource> projectUsers = projectService.getProjectUsersForProject(projectId);
        return simpleFindFirst(projectUsers, pr -> pr.isFinanceContact() && organisationId.equals(pr.getOrganisation()));
    }

    private List<ThreadViewModel> loadQueryModel(Long projectId, Long organisationId, UserResource user) {

        List<ThreadViewModel> queryModel = new LinkedList<>();

        ProjectFinanceResource projectFinance = projectFinanceService.getProjectFinance(projectId, organisationId);

        ServiceResult<List<QueryResource>> queries = financeCheckService.loadQueries(projectFinance.getId());
        if (queries.isSuccess()) {
            // order queries by most recent post
            List<QueryResource> sortedQueries = queries.getSuccessObject().stream().
                    flatMap(t -> t.posts.stream()
                            .map(p -> new AbstractMap.SimpleImmutableEntry<>(t, p)))
                    .sorted((e1, e2) -> e2.getValue().createdOn.compareTo(e1.getValue().createdOn))
                    .map(m -> m.getKey())
                    .distinct()
                    .collect(Collectors.toList());

            for (QueryResource query : sortedQueries) {
                List<ThreadPostViewModel> posts = new LinkedList<>();
                for (PostResource p : query.posts) {
                    ThreadPostViewModel post = new ThreadPostViewModel(p.id, p.author, p.body, p.attachments, p.createdOn);
                    post.setUsername(user.getName() + " - " + (user.hasRole(UserRoleType.PROJECT_FINANCE) ? "Finance team" : organisationService.getOrganisationForUser(user.getId()).getName()));
                    posts.add(post);
                }
                ThreadViewModel detail = new ThreadViewModel();
                detail.setViewModelPosts(posts);
                detail.setSectionType(query.section);
                detail.setCreatedOn(query.createdOn);
                detail.setAwaitingResponse(query.awaitingResponse);
                detail.setTitle(query.title);
                detail.setId(query.id);
                detail.setProjectId(projectId);
                detail.setOrganisationId(organisationId);
                detail.setPosts(query.posts);
                queryModel.add(detail);
            }
        }
        return queryModel;
    }
}
