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

    @Autowired
    UserService userService;

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_SECTION_EXTERNAL')")
    @RequestMapping(method = GET)
    public String viewFinanceChecks(Model model,
                                               @PathVariable("projectId") final Long projectId,
                                               @PathVariable("organisationId") final Long organisationId) {

        ProjectOrganisationCompositeId projectComposite = new ProjectOrganisationCompositeId(projectId, organisationId);

        model.addAttribute("model", buildFinanceChecksLandingPage(projectComposite));

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

    private ProjectFinanceChecksViewModel buildFinanceChecksLandingPage(final ProjectOrganisationCompositeId compositeId) {
        ProjectResource projectResource = projectService.getById(compositeId.getProjectId());
        OrganisationResource organisationResource = organisationService.getOrganisationById(compositeId.getOrganisationId());
        List<ThreadViewModel> queriesViewModel = buildThreadViewModel(getQueries(compositeId));
        boolean approved = isApproved(compositeId);

        return new ProjectFinanceChecksViewModel(projectResource, organisationResource, loadQueryModel(compositeId.getProjectId(), compositeId.getOrganisationId()), approved);
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


    private List<ThreadViewModel> loadQueryModel(Long projectId, Long organisationId) {
        // Dummy test data
        ThreadResource thread = new ThreadResource();
        PostResource firstPost = new PostResource();
        PostResource firstResponse = new PostResource();
        thread.setCreatedOn(LocalDateTime.now());
        thread.setAwaitingResponse(false);
        thread.setOrganisationId(22L);
        thread.setProjectId(3L);
        thread.setTitle("Query title");
        thread.setSectionType(FinanceChecksSectionType.ELIGIBILITY);
        thread.setId(1L);
        firstPost.setCreatedOn(LocalDateTime.now().plusMinutes(10L));
        firstPost.setUserId(18L);
        firstPost.setPostBody("Question");
        firstResponse.setCreatedOn(LocalDateTime.now().plusMinutes(20L));
        firstResponse.setUserId(55L);
        firstResponse.setPostBody("Response");
        firstResponse.setAttachments(new LinkedList<>());
        PostAttachmentResource att1 = new PostAttachmentResource();
        att1.setFileEntryId(23L);
        firstPost.setAttachments(Arrays.asList(att1));
        thread.setPosts(Arrays.asList(firstPost, firstResponse));

        ThreadResource thread2 = new ThreadResource();
        PostResource firstPost2 = new PostResource();
        thread2.setCreatedOn(LocalDateTime.now());
        thread2.setAwaitingResponse(true);
        thread2.setOrganisationId(22L);
        thread2.setProjectId(3L);
        thread2.setTitle("Query2 title");
        thread2.setSectionType(FinanceChecksSectionType.ELIGIBILITY);
        thread2.setId(3L);
        firstPost2.setCreatedOn(LocalDateTime.now().plusMinutes(10L));
        firstPost2.setUserId(18L);
        firstPost2.setPostBody("Question2");
        firstPost2.setAttachments(new LinkedList<>());
        thread2.setPosts (Arrays.asList(firstPost2));
        List<ThreadResource> queries = Arrays.asList(thread2, thread);

        // TODO read data from service

        // order queries by most recent post
        List<ThreadResource> sortedQueries = queries.stream().
                flatMap(t -> t.getPosts().stream()
                        .map(p -> new AbstractMap.SimpleImmutableEntry<>(t, p)))
                .sorted((e1, e2) -> e2.getValue().getCreatedOn().compareTo(e1.getValue().getCreatedOn()))
                .map(m -> m.getKey())
                .distinct()
                .collect(Collectors.toList());

        List<ThreadViewModel> queryModel = new LinkedList<>();
        Long attachmentIndex = 0L;
        for (ThreadResource t : sortedQueries) {
            List<ThreadPostViewModel> posts = new LinkedList<>();
            for (PostResource p : t.getPosts()) {
                List<ThreadPostAttachmentResourceViewModel> attachments = new LinkedList<>();
                for (PostAttachmentResource a : p.getAttachments()) {
                    ThreadPostAttachmentResourceViewModel attachment = new ThreadPostAttachmentResourceViewModel();
                    // TODO get file details from service
                    attachment.setFileEntryId(a.getFileEntryId());
                    attachment.setPostId(a.getPostId());
                    attachment.setFilename("file" + attachmentIndex.toString());
                    attachment.setLocalFileId(attachmentIndex);
                    attachments.add(attachment);
                    attachmentIndex++;
                    //FileEntryResource f;
                    //f.getName();
                }
                UserResource user = userService.findById(p.getUserId());
                OrganisationResource organisation = organisationService.getOrganisationForUser(p.getUserId());
                ThreadPostViewModel post = new ThreadPostViewModel();
                post.setViewModelAttachments(attachments);
                post.setUsername(user.getName() + " - " + organisation.getName() + (user.hasRole(UserRoleType.PROJECT_FINANCE)?  " (Finance team)" : ""));
                post.setCreatedOn(p.getCreatedOn());
                post.setPostBody(p.getPostBody());
                post.setUserId(p.getUserId());
                post.setAttachments(p.getAttachments());
                posts.add(post);
            }
            ThreadViewModel detail = new ThreadViewModel();
            detail.setViewModelPosts(posts);
            detail.setSectionType(t.getSectionType());
            detail.setCreatedOn(t.getCreatedOn());
            detail.setAwaitingResponse(t.isAwaitingResponse());
            detail.setTitle(t.getTitle());
            detail.setId(t.getId());
            detail.setProjectId(t.getProjectId());
            detail.setOrganisationId(t.getOrganisationId());
            detail.setPosts(t.getPosts());
            queryModel.add(detail);
        }
        return queryModel;
    }

}
